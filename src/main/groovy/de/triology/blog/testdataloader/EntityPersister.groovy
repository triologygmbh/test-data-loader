package de.triology.blog.testdataloader

import javax.persistence.EntityManager

/**
 * An EntityCreatedListener that persists created entities.
 */
class EntityPersister implements EntityCreatedListener {

    private EntityManager entityManager

    EntityPersister(EntityManager entityManager) {
        this.entityManager = entityManager
    }

    /**
     * Persists the passed entity using this instance's {@link javax.persistence.EntityManager}.
     * @param entity
     */
    void entityCreated(Object entity) {
        entityManager.persist(entity)
    }
}
