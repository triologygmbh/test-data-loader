package de.triology.blog.testdataloader

import org.codehaus.groovy.control.CompilerConfiguration

/**
 * Builder that takes the name of a file containing entity definitions and builds the entities accordingly.
 */
class EntityBuilder {

    private static EntityBuilder singletonInstance;

    private Map<String, ?> entitiesByName = [:]
    private List<EntityCreatedListener> entityCreatedListeners = []

    private EntityBuilder() {}

    static EntityBuilder instance() {
        singletonInstance = singletonInstance ?: new EntityBuilder();
        return singletonInstance
    }

    /**
     * Builds the entities defined in the specified file.
     *
     * @param entityDefinitionFile String - the name of the file containing the entity definitions; the file must be in
     * the classpath
     */
    void buildEntities(String entityDefinitionFile) {
        DelegatingScript script = createExecutableScriptFromEntityDefinition(entityDefinitionFile)
        script.setDelegate(this)
        script.run()
    }

    private DelegatingScript createExecutableScriptFromEntityDefinition(String entityDefinitionFile) {
        InputStreamReader entityDefinitionReader = createReaderForEntityDefinitionFile(entityDefinitionFile)
        GroovyShell shell = createGroovyShell()
        return (DelegatingScript) shell.parse(entityDefinitionReader)
    }

    private InputStreamReader createReaderForEntityDefinitionFile(String entityDefinitionFile) {
        URI entityDefinitionFileUri = getUriForEntityDefinition(entityDefinitionFile)
        return createUtf8Reader(entityDefinitionFileUri)
    }

    private URI getUriForEntityDefinition(String entityDefinitionFile) {
        URL entityDefinitionFileUrl = getClass().getClassLoader().getResource(entityDefinitionFile)
        if (entityDefinitionFileUrl == null) {
            throw new RuntimeException("entity definition File cannot be found in classpath: " + entityDefinitionFile);
        }
        return entityDefinitionFileUrl.toURI()
    }

    private InputStreamReader createUtf8Reader(URI entityDefinitionFileUri) {
        InputStream inputStream = new FileInputStream(new File(entityDefinitionFileUri))
        return new InputStreamReader(inputStream, "UTF-8");
    }

    private GroovyShell createGroovyShell() {
        CompilerConfiguration compilerConfiguration = new CompilerConfiguration()
        compilerConfiguration.scriptBaseClass = DelegatingScript.class.name
        return new GroovyShell(this.class.classLoader, compilerConfiguration)
    }

    /**
     * Creates an Instance of the specified  entityClass, registers it under the specified entityName and applies the
     * specified entityData definition
     *
     * @param entityClass
     * @param entityName
     * @param entityData
     * @return the created entity
     */
    static <T> T create(@DelegatesTo.Target Class<T> entityClass, String entityName,
                        @DelegatesTo(strategy = Closure.DELEGATE_FIRST, genericTypeIndex = 0) Closure entityData = {}) {
        return instance().createEntity(entityClass, entityName, entityData);
    }

    private <T> T createEntity(Class<T> entityClass, String entityName, Closure entityData) {
        T entity = createEntityInstance(entityName, entityClass)
        executeEntityDataDefinition(entityData, entity)
        notifyEntityCreatedListeners(entity)
        return entity
    }

    private <T> T createEntityInstance(String entityName, Class<T> entityClass) {
        ensureNameHasNotYetBeenAssigned(entityName, entityClass)
        T entity = entityClass.newInstance()
        entitiesByName[entityName] = entity;
        return entity
    }

    private void ensureNameHasNotYetBeenAssigned(String entityName, Class requestedEntityClass) {
        if (entitiesByName[entityName]) {
            throw new EntityBuildingException(
                    "attempt to create an instance of $requestedEntityClass under the name of '$entityName' but an " +
                            "entity with that name already exists: ${entitiesByName[entityName]}")
        }
    }

    private void notifyEntityCreatedListeners(Object entity) {
        entityCreatedListeners.each {
            it.entityCreated(entity)
        }
    }

    private void executeEntityDataDefinition(Closure entityDataDefinition, Object entity) {
        entityDataDefinition = entityDataDefinition.rehydrate(entity, this, this)
        entityDataDefinition.call()
    }

    /**
     * Implementation of Groovy's {@code propertyMissing} that returns the entity previously created under the property
     * name. This Method will be called during entity creation, when an entity is referenced.
     *
     * @param name String
     * @return a previously created entity
     */
    private def propertyMissing(String name) {
        if (entitiesByName[name]) {
            return entitiesByName[name]
        }
        throw new EntityBuildingException("requested reference for entity with name '$name' cannot be resolved")
    }

    /**
     * Adds an {@link EntityCreatedListener} that gets notified every time an entity is completely created.
     * @param listener {@link EntityCreatedListener}
     */
    void addEntityCreatedListener(EntityCreatedListener listener) {
        entityCreatedListeners += listener
    }

    /**
     * Removes the {@link EntityCreatedListener}
     * @param listener {@link EntityCreatedListener}
     */
    void removeEntityCreatedListener(EntityCreatedListener listener) {
        entityCreatedListeners -= listener
    }

    /**
     * Gets the entity with the specified name from the set of entities created.
     *
     * If this {@code EntityBuilder} has not created an entity by the passed name a {@link NoSuchElementException} is
     * thrown. If an entity is found but has a different type than the passed {@code entityClass}, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param name {@link String} - the requested entity's name
     * @param entityClass the requested entity's {@link Class}
     * @return the requested entity
     */
    protected <T> T getEntityByName(String name, Class<T> entityClass) {
        def entity = findEntity(name)
        ensureTypesMatch(entityClass, entity, name)
        return (T) entity;
    }

    private Object findEntity(String name) {
        def entity = entitiesByName[name]
        if (!entity) {
            throw new NoSuchElementException("an entity named '$name' has not been created by the EntityBuilder")
        }
        return entity
    }

    private void ensureTypesMatch(Class entityClass, entity, String name) {
        if (entityClass != entity.class) {
            throw new IllegalArgumentException(
                    "The class of the requested entity named '$name' does not match the requested class. Requested: $entityClass, Actual: ${entity.class}")
        }
    }

    /**
     * Clears all previously built entities.
     */
    void clear() {
        entitiesByName.clear()
    }
}

