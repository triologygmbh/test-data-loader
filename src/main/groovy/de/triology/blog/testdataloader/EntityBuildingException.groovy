package de.triology.blog.testdataloader

/**
 * A {@link RuntimeException} that can be used while building entities
 */
class EntityBuildingException extends RuntimeException {

    EntityBuildingException(String message) {
        super(message)
    }

}
