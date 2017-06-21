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
package de.triology.blog.testdataloader;

import de.triology.blog.testdataloader.testentities.AnotherTestEntity;
import de.triology.blog.testdataloader.testentities.BasicTestEntity;
import de.triology.blog.testdataloader.testentities.TestEntityWithToManyRelationship;
import de.triology.blog.testdataloader.testentities.TestEntityWithToOneRelationship;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.Assert.*;

public class EntityBuilderTest {

    private EntityBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = EntityBuilder.instance();
        builder.clear();
        callBuildEntitiesWithFile("tests/testEntityDefinitions.groovy");
    }

    private void callBuildEntitiesWithFile(String file) throws FileNotFoundException {
        builder.buildEntities(FileReader.create(file));
    }

    @After
    public void tearDown() throws Exception {
        builder.clear();
    }

    @Test
    public void getsCreatedEntityByName() throws Exception {
        BasicTestEntity entity = builder.getEntityByName("basicEntity", BasicTestEntity.class);
        assertNotNull(entity);
    }

    @Test(expected = NoSuchElementException.class)
    public void getEntityByNameFailsForNonexistingEntity() throws Exception {
        builder.getEntityByName("notExisting", BasicTestEntity.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEntityByNameFailsIfPassesClassDoesNotMatch() throws Exception {
        builder.getEntityByName("basicEntity", AnotherTestEntity.class);
    }

    @Test
    public void createsEntityFromDsl() throws Exception {
        assertEntityOfClassWasBuilt("basicEntity", BasicTestEntity.class);
    }

    @Test
    public void createsMultipleEntitiesFromDsl() throws Exception {
        assertEntityOfClassWasBuilt("secondBasicEntity", BasicTestEntity.class);
        assertEntityOfClassWasBuilt("entityOfAnotherClass", AnotherTestEntity.class);
    }

    private <T> void assertEntityOfClassWasBuilt(String entityName, Class<T> clazz) {
        T entity = builder.getEntityByName(entityName, clazz);
        assertNotNull("entity of name " + entityName + " was not built", entity);
        assertEquals(clazz, entity.getClass());
    }

    @Test
    public void setsStringProperty() throws Exception {
        BasicTestEntity entity = builder.getEntityByName("basicEntity", BasicTestEntity.class);
        assertEquals("a string value", entity.getStringProperty());
    }

    @Test
    public void setsIntegerProperty() throws Exception {
        BasicTestEntity entity = builder.getEntityByName("basicEntity", BasicTestEntity.class);
        assertEquals(5, (int) entity.getIntegerProperty());
    }

    @Test
    public void setProgrammaticallyCreatedDateProperty() throws Exception {
        BasicTestEntity entity = builder.getEntityByName("basicEntity", BasicTestEntity.class);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 18);
        calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        calendar.set(Calendar.YEAR, 2015);
        assertTrue(DateUtils.isSameDay(calendar.getTime(), entity.getDateProperty()));
    }

    @Test
    public void buildsReferencedEntityInPlace() throws Exception {
        TestEntityWithToOneRelationship entity = builder.getEntityByName("entityWithToOneRelationship",
                TestEntityWithToOneRelationship.class);
        assertEntityOfClassWasBuilt("referencedInstance", BasicTestEntity.class);
        BasicTestEntity referencedEntity = builder.getEntityByName("referencedInstance", BasicTestEntity.class);
        assertSame(referencedEntity, entity.getReferencedEntity());
    }

    @Test
    public void setsPropertiesOfEntitiesBuiltInPlace() throws Exception {
        TestEntityWithToOneRelationship entity = builder.getEntityByName("entityWithToOneRelationship",
                TestEntityWithToOneRelationship.class);
        BasicTestEntity referencedEntity = entity.getReferencedEntity();
        assertEquals("string in referenced entity", referencedEntity.getStringProperty());
        assertEquals(222, (int) referencedEntity.getIntegerProperty());
    }

    @Test
    public void buildEntitiesWithArbitraryNesting() throws Exception {
        TestEntityWithToOneRelationship entity = builder.getEntityByName("deeplyNestedEntities",
                TestEntityWithToOneRelationship.class);
        assertSame(builder.getEntityByName("nest1", TestEntityWithToOneRelationship.class),
                entity.getTestEntityWithToOneRelationship());
        assertSame(builder.getEntityByName("nest2", TestEntityWithToOneRelationship.class),
                entity.getTestEntityWithToOneRelationship().getTestEntityWithToOneRelationship());
        assertSame(builder.getEntityByName("nest3", BasicTestEntity.class),
                entity.getTestEntityWithToOneRelationship().getTestEntityWithToOneRelationship().getReferencedEntity());
        assertEquals("deeply nested string", builder.getEntityByName("nest3", BasicTestEntity.class).getStringProperty());
    }

    @Test
    public void resolvesReferences() throws Exception {
        TestEntityWithToOneRelationship entity = builder.getEntityByName("entityReferencingPreviouslyCreatedEntity",
                TestEntityWithToOneRelationship.class);
        assertSame(builder.getEntityByName("secondBasicEntity", BasicTestEntity.class), entity.getReferencedEntity());
    }

    @Test(expected = EntityBuildingException.class)
    public void failsIfReferencedEntityDoesNotExist() throws Exception {
        callBuildEntitiesWithFile("tests/failingBecauseOfMissingReferencedEntity.groovy");
    }


    @Test(expected = EntityBuildingException.class)
    public void failsIfAnEntityNameHasAlreadyBeenUsed() throws Exception {
        callBuildEntitiesWithFile("tests/failingBecauseOfReusedName.groovy");
    }

    @Test
    public void setsCollectionOfReferencedEntities() throws Exception {
        TestEntityWithToManyRelationship entity = builder.getEntityByName("entityWithCollection",
                TestEntityWithToManyRelationship.class);
        Collection<BasicTestEntity> toManyRelationship = entity.getToManyRelationship();
        assertEquals(2, toManyRelationship.size());
        BasicTestEntity referencedEntity1 = null;
        BasicTestEntity referencedEntity2 = null;
        for (BasicTestEntity basicTestEntity : toManyRelationship) {
            if (referencedEntity1 == null) {
                referencedEntity1 = basicTestEntity;
            } else {
                referencedEntity2 = basicTestEntity;
            }
        }
        assertNotSame(referencedEntity1, referencedEntity2);
        assert referencedEntity1 != null;
        assertEquals(5, (int) referencedEntity1.getIntegerProperty());
        assert referencedEntity2 != null;
        assertEquals(5, (int) referencedEntity2.getIntegerProperty());
    }

    @Test
    public void notifiesEntityCreatedListenerInTheOrderOfEntityCreation() throws Exception {
        builder.clear();
        final List<Object> entitiesInOrderOfCreation = new ArrayList<Object>();
        builder.addEntityCreatedListener(new EntityCreatedListener() {
            public void entityCreated(Object entity) {
                entitiesInOrderOfCreation.add(entity);
            }
        });
        callBuildEntitiesWithFile("tests/testOrderOfEntityCreatedListenerNotifications.groovy");
        assertEquals(4, entitiesInOrderOfCreation.size());
        assertEquals(BasicTestEntity.class, entitiesInOrderOfCreation.get(0).getClass());
        assertEquals(TestEntityWithToOneRelationship.class, entitiesInOrderOfCreation.get(1).getClass());
        assertEquals(AnotherTestEntity.class, entitiesInOrderOfCreation.get(2).getClass());
        assertEquals(TestEntityWithToOneRelationship.class, entitiesInOrderOfCreation.get(3).getClass());
    }

    @Test (expected = NoSuchElementException.class)
    public void clearsEntities() throws Exception {
        try {
            builder.getEntityByName("basicEntity", BasicTestEntity.class);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            fail("basicEntity already was not available before calling clearEntityCacheAndDatabase");
        }

        builder.clear();
        builder.getEntityByName("basicEntity", BasicTestEntity.class);
    }
}