import de.triology.blog.testdata.loader.testentities.AnotherTestEntity
import de.triology.blog.testdata.loader.testentities.BasicTestEntity
import de.triology.blog.testdata.loader.testentities.TestEntityWithToManyRelationship
import de.triology.blog.testdata.loader.testentities.TestEntityWithToOneRelationship

import java.text.SimpleDateFormat

create BasicTestEntity, 'basicEntity', {
    stringProperty = 'a string value'
    integerProperty = 5
    dateProperty = new SimpleDateFormat('dd.MM.yyyy').parse('18.11.2015')
}

create BasicTestEntity, 'secondBasicEntity'

create AnotherTestEntity, 'entityOfAnotherClass'

create TestEntityWithToOneRelationship, 'entityWithToOneRelationship', {
    referencedEntity = create BasicTestEntity, 'referencedInstance',  {
        stringProperty = 'string in referenced entity'
        integerProperty = 222
    }
}

create TestEntityWithToOneRelationship, 'deeplyNestedEntities', {
    testEntityWithToOneRelationship = create TestEntityWithToOneRelationship, 'nest1', {
        testEntityWithToOneRelationship = create TestEntityWithToOneRelationship, 'nest2', {
            referencedEntity = create BasicTestEntity, 'nest3', {
                stringProperty = 'deeply nested string'
            }
        }
    }
}

create TestEntityWithToOneRelationship, 'entityReferencingPreviouslyCreatedEntity', {
    referencedEntity = secondBasicEntity
}

create TestEntityWithToManyRelationship, 'entityWithCollection', {
    toManyRelationship = [
            create(BasicTestEntity, 'createdInPlace', {
                integerProperty = 5
            }),
            basicEntity
    ]
}