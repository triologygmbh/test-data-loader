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
import de.triology.blog.testdataloader.testentities.AnotherTestEntity
import de.triology.blog.testdataloader.testentities.BasicTestEntity
import de.triology.blog.testdataloader.testentities.TestEntityWithToManyRelationship
import de.triology.blog.testdataloader.testentities.TestEntityWithToOneRelationship

import java.text.SimpleDateFormat

import static de.triology.blog.testdataloader.EntityBuilder.create

create BasicTestEntity, 'basicEntityCp', {
    stringProperty = 'a string value'
    integerProperty = 5
    dateProperty = new SimpleDateFormat('dd.MM.yyyy').parse('18.11.2015')
}

create BasicTestEntity, 'secondBasicEntityCp'

create AnotherTestEntity, 'entityOfAnotherClassCp'

create TestEntityWithToOneRelationship, 'entityWithToOneRelationshipCp', {
    referencedEntity = create BasicTestEntity, 'referencedInstanceCp',  {
        stringProperty = 'string in referenced entity'
        integerProperty = 222
    }
}

create TestEntityWithToOneRelationship, 'deeplyNestedEntitiesCp', {
    testEntityWithToOneRelationship = create TestEntityWithToOneRelationship, 'nest1Cp', {
        testEntityWithToOneRelationship = create TestEntityWithToOneRelationship, 'nest2Cp', {
            referencedEntity = create BasicTestEntity, 'nest3Cp', {
                stringProperty = 'deeply nested string'
            }
        }
    }
}

create TestEntityWithToOneRelationship, 'entityReferencingPreviouslyCreatedEntityCp', {
    referencedEntity = secondBasicEntity
}

create TestEntityWithToManyRelationship, 'entityWithCollectionCp', {
    toManyRelationship = [
            create(BasicTestEntity, 'createdInPlaceCp', {
                integerProperty = 5
            }),
            basicEntity
    ]
}