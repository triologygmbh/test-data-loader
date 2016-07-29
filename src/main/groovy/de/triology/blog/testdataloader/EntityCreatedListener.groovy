package de.triology.blog.testdataloader

/**
 * Listener for entity creation. Gets notified every time an entity is completely created.
 */
interface EntityCreatedListener {

    /**
     * Is called every time an entity is completely created, including all referenced entities.
     * @param entity the created entity
     */
    void entityCreated(Object entity)

}