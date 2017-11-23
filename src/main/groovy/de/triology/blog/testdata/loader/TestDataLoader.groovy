/**
 * MIT License
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
package de.triology.blog.testdata.loader

import javax.persistence.EntityManager

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.triology.blog.testdata.builder.EntityBuilder
import de.triology.blog.testdata.builder.EntityBuilderListener
import de.triology.blog.testdata.util.FileReader

/**
 * Loads test data from entity definition files, saves them to a database via a specified {@link EntityManager} and
 * makes the entities available by their names as defined in the entity definition files.
 */
class TestDataLoader implements EntityBuilderListener {

    /**
     * Defines the transaction type that the {@link EntityManager} passed to the {@code TestDataLoader} is configured with.
     */
    public enum TransactionType {
        RESOURCE_LOCAL, JTA
    }

    private static final Logger LOG = LoggerFactory.getLogger(TestDataLoader)

    private Map<String, ?> loadedEntities = [:].withDefault {
        throw new NoSuchElementException("an entity named '$it' has not been created")
    }

    private EntityManager entityManager
    private EntityDeleter entityDeleter
    private TransactionType transactionType;

    /**
     * Creates a new TestDataLoader that uses the specified JPA EntityManager to save and delete entities.
     * The EntityManager is expected to be fully initialized and ready to use.<br>
     * <br>
     * The {@code TestDataLoader} will assume a {@code TransactionType.RESOURCE_LOCAL} configuration when created with
     * this constructor and therefore try an manage transactions itself. If this is undesired, e. g. in a container
     * environment with managed persistence, use the {@code TestDataLoader(EntityManager, TransactionType)}
     * constructor with {@code TransactionType.JTA} .
     *
     * @param entityManager {@link EntityManager}
     */
    TestDataLoader(EntityManager entityManager) {
        this(entityManager, TransactionType.RESOURCE_LOCAL)
    }

    /**
     * Creates a new TestDataLoader that uses the specified JPA EntityManager to save and delete entities.
     * The EntityManager is expected to be configured with the specified transaction type and to be fully initialized
     * and ready to use.<br>
     * <br>
     * Note that when specifying {@code TransactionType.RESOURCE_LOCAL}, the {@code TestDataLoader} will try and manage
     * transactions itself. This might not be desired and even lead to runtime exceptions, e. g. in a container
     * environment with managed persistence. Specify {@code TransactionType.JTA} in this case, the {@code TestDataLoader}
     * will assume transactions to be managed by client code then.
     *
     * @param entityManager {@link EntityManager}
     * @param transactionType {@link TransactionType}
     */
    TestDataLoader(EntityManager entityManager, TransactionType transactionType) {
        if (transactionType == null) {
            throw new IllegalArgumentException("transactionType must not be null")
        }
        checkTransactionType(entityManager, transactionType)
        this.entityManager = entityManager
        entityDeleter = new EntityDeleter(entityManager)
        this.transactionType = transactionType;
    }

    private void checkTransactionType(EntityManager entityManager, TransactionType transactionType) {
        if (transactionType == TransactionType.RESOURCE_LOCAL) {
            makeSureTransactionsAreResourceLocal(entityManager)
        }
    }

    private static void makeSureTransactionsAreResourceLocal(EntityManager entityManager) {
        try {
            entityManager.getTransaction()
        } catch (IllegalStateException e) {
            throw new IllegalStateException(
            'TestDataLoader is configured with RESOURCE_LOCAL transactions but the supplied EntityManager uses ' +
            'JTA transactions. Use the TestDataLoader#TestDataLoader(EntityManager, TransactionType) ' +
            'constructor to specify that JTA transactions are being used. Note that the client code ' +
            'needs to take care of transaction management in this case.', e)
        }
    }

    /**
     * Loads the entities defined in the passed {@code entityDefinitionFiles} into the database.
     *
     * @param entityDefinitionFiles {@link Collection} of Strings - the names of files containing the entity
     * definitions. The files are expected to be UTF-8 encoded.
     */
    void loadTestData(Collection<String> entityDefinitionFiles) {
        EntityPersister persister = new EntityPersister(entityManager)
        EntityBuilder entityBuilder = new EntityBuilder()
                .addEntityBuilderListener(this)
                .addEntityBuilderListener(persister)
                .addEntityBuilderListener(entityDeleter)

        withTransaction {
            entityDefinitionFiles.each {
                entityBuilder.build(FileReader.create(it))
            }
        }
    }

    /**
     * Gets the entity with the specified name from the set of entities created.
     *
     * If this {@code EntityBuilder} has not created an entity by the passed name a
     * {@link NoSuchElementException} is thrown. If an entity is found but has a different type than
     * the passed {@code entityClass}, an {@link IllegalArgumentException} is thrown.
     *
     * @param name
     *            {@link String} - the requested entity's name
     * @param entityClass
     *            the requested entity's {@link Class}
     * @return the requested entity
     */
    public <T> T getEntityByName(String name, Class<T> entityClass) {
        def entity = loadedEntities[name]

        if (entityClass != entity.class) {
            throw new IllegalArgumentException(
            "The class of the requested entity named '$name' does not match the requested class. Requested: $entityClass, Actual: ${entity.class}")
        }

        return (T) entity;
    }

    /**
     * Clears all previously built entities so that they are no longer available through the {@code getEntityByName}
     * method and deletes all data from the database.
     */
    void clearEntityCacheAndDatabase() {
        withTransaction { entityDeleter.deleteAllEntities() }
        clearEntityCache()
    }

    /**
     * Clears all previously built entities so that they are no longer available through the {@code getEntityByName}
     * method.
     */
    void clearEntityCache() {
        loadedEntities.clear();
    }

    private void withTransaction(Closure doWithinTransaction) {
        if (newTransactionRequired()) {
            withNewTransaction(doWithinTransaction)
        } else {
            // Someone else is taking care of transaction handling
            doWithinTransaction();
        }
    }

    private boolean newTransactionRequired() {
        return transactionType == TransactionType.RESOURCE_LOCAL &&
                !entityManager.getTransaction().isActive()
    }

    private void withNewTransaction(Closure doWithinTransaction) {
        try {
            entityManager.getTransaction().begin()
            doWithinTransaction()
            entityManager.getTransaction().commit()
        } catch (Exception e) {
            e.printStackTrace()
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void onEntityCreated(String name, Object entity) {
        loadedEntities[name]=entity
    }

    public void clear() {
        loadedEntities.clear()
    }
}
