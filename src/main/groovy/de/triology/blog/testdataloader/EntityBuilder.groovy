/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 TRIOLOGY GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.triology.blog.testdataloader

import groovy.transform.PackageScope
import org.codehaus.groovy.control.CompilerConfiguration

/**
 * Builder that takes the name of a file containing entity definitions and builds the entities accordingly.
 */
@PackageScope
class EntityBuilder {

    private static EntityBuilder singletonInstance;

    private Map<String, ?> entitiesByName = [:]
    private List<EntityCreatedListener> entityCreatedListeners = []

    private EntityBuilder() {}

    /**
     * Gets the EntityBuilder singleton instance
     * @return EntityBuilder
     */
    protected static EntityBuilder instance() {
        singletonInstance = singletonInstance ?: new EntityBuilder();
        return singletonInstance
    }

    /**
     * Builds the entities defined in the provided by the passed Reader.
     *
     * @param entityDefinitionReader Reader - a Reader for the file containing the entity definitions
     */
    protected void buildEntities(Reader entityDefinitionReader) {
        DelegatingScript script = createExecutableScriptFromEntityDefinition(entityDefinitionReader)
        script.setDelegate(this)
        script.run()
    }

    private DelegatingScript createExecutableScriptFromEntityDefinition(Reader entityDefinitionReader) {
        GroovyShell shell = createGroovyShell()
        return (DelegatingScript) shell.parse(entityDefinitionReader)
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
    protected void addEntityCreatedListener(EntityCreatedListener listener) {
        entityCreatedListeners += listener
    }

    /**
     * Removes the {@link EntityCreatedListener}
     * @param listener {@link EntityCreatedListener}
     */
    protected void removeEntityCreatedListener(EntityCreatedListener listener) {
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
    protected void clear() {
        entitiesByName.clear()
    }
}

