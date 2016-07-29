import de.triology.blog.testdataloader.testentities.TestEntityWithToOneRelationship

import static de.triology.blog.testdataloader.EntityBuilder.create

create TestEntityWithToOneRelationship, 'entityReferencingANonexistingEntity', {
    referencedEntity = notExistingReference
}