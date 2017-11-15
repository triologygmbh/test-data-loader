import de.triology.blog.testdata.loader.testentities.AnotherTestEntity
import de.triology.blog.testdata.loader.testentities.BasicTestEntity
import de.triology.blog.testdata.loader.testentities.TestEntityWithToOneRelationship

create TestEntityWithToOneRelationship, 'entityWithToOneRelationship', {
    referencedEntity = create BasicTestEntity, 'referencedInstance', {
        stringProperty = 'string in referenced entity'
        integerProperty = 222
    }
}

create AnotherTestEntity, 'entityOfAnotherClass', {}

create TestEntityWithToOneRelationship, 'anotherEntityWithToOneRelationship', {
    referencedEntity = referencedInstance
}