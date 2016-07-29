import de.triology.blog.testdataloader.testentities.AnotherTestEntity
import de.triology.blog.testdataloader.testentities.BasicTestEntity
import de.triology.blog.testdataloader.testentities.TestEntityWithToOneRelationship

import static de.triology.blog.testdataloader.EntityBuilder.create

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