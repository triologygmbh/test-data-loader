import de.triology.blog.testdata.loader.testentities.TestEntityWithToOneRelationship

create TestEntityWithToOneRelationship, 'entityReferencingANonexistingEntity', {
    referencedEntity = notExistingReference
}