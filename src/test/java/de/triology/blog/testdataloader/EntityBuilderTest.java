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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import de.triology.blog.testdataloader.testentities.AnotherTestEntity;
import de.triology.blog.testdataloader.testentities.BasicTestEntity;
import de.triology.blog.testdataloader.testentities.InheritingEntity;
import de.triology.blog.testdataloader.testentities.TestEntityWithToManyRelationship;
import de.triology.blog.testdataloader.testentities.TestEntityWithToOneRelationship;

public class EntityBuilderTest {

    private EntityBuilder builder;
    private EntityStore entityStore;

    @Before
    public void setUp() throws Exception {
        builder = EntityBuilder.newBuilder();
        callBuildEntitiesWithFile("tests/testEntityDefinitions.groovy");
    }

    private void callBuildEntitiesWithFile(final String file) throws FileNotFoundException {
        entityStore = builder.build(FileReader.create(file));
    }

    @Test
    public void getsCreatedEntityByName() throws Exception {
        final BasicTestEntity entity = entityStore.getEntityByName("basicEntity", BasicTestEntity.class);
        assertNotNull(entity);
    }

    @Test(expected = NoSuchElementException.class)
    public void getEntityByNameFailsForNonexistingEntity() throws Exception {
        entityStore.getEntityByName("notExisting", BasicTestEntity.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEntityByNameFailsIfPassesClassDoesNotMatch() throws Exception {
        entityStore.getEntityByName("basicEntity", AnotherTestEntity.class);
    }

    @Test
    public void createsEntityFromDsl() throws Exception {
        assertEntityOfClassWasBuilt("basicEntity", BasicTestEntity.class);
    }

    @Test
    public void createsEntityThatUsesFieldFromMappedSuperclass() throws Exception {
        callBuildEntitiesWithFile("tests/mappedSuperclass.groovy");
        assertEntityOfClassWasBuilt("inherited", InheritingEntity.class);
    }

    @Test
    public void createsMultipleEntitiesFromDsl() throws Exception {
        assertEntityOfClassWasBuilt("secondBasicEntity", BasicTestEntity.class);
        assertEntityOfClassWasBuilt("entityOfAnotherClass", AnotherTestEntity.class);
    }

    private <T> void assertEntityOfClassWasBuilt(final String entityName, final Class<T> clazz) {
        final T entity = entityStore.getEntityByName(entityName, clazz);
        assertNotNull("entity of name " + entityName + " was not built", entity);
        assertEquals(clazz, entity.getClass());
    }

    @Test
    public void setsStringProperty() throws Exception {
        final BasicTestEntity entity = entityStore.getEntityByName("basicEntity", BasicTestEntity.class);
        assertEquals("a string value", entity.getStringProperty());
    }

    @Test
    public void setsIntegerProperty() throws Exception {
        final BasicTestEntity entity = entityStore.getEntityByName("basicEntity", BasicTestEntity.class);
        assertEquals(5, (int) entity.getIntegerProperty());
    }

    @Test
    public void setProgrammaticallyCreatedDateProperty() throws Exception {
        final BasicTestEntity entity = entityStore.getEntityByName("basicEntity", BasicTestEntity.class);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 18);
        calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        calendar.set(Calendar.YEAR, 2015);
        assertTrue(DateUtils.isSameDay(calendar.getTime(), entity.getDateProperty()));
    }

    @Test
    public void buildsReferencedEntityInPlace() throws Exception {
        final TestEntityWithToOneRelationship entity = entityStore.getEntityByName("entityWithToOneRelationship",
                TestEntityWithToOneRelationship.class);
        assertEntityOfClassWasBuilt("referencedInstance", BasicTestEntity.class);
        final BasicTestEntity referencedEntity = entityStore.getEntityByName("referencedInstance", BasicTestEntity.class);
        assertSame(referencedEntity, entity.getReferencedEntity());
    }

    @Test
    public void setsPropertiesOfEntitiesBuiltInPlace() throws Exception {
        final TestEntityWithToOneRelationship entity = entityStore.getEntityByName("entityWithToOneRelationship",
                TestEntityWithToOneRelationship.class);
        final BasicTestEntity referencedEntity = entity.getReferencedEntity();
        assertEquals("string in referenced entity", referencedEntity.getStringProperty());
        assertEquals(222, (int) referencedEntity.getIntegerProperty());
    }

    @Test
    public void buildEntitiesWithArbitraryNesting() throws Exception {
        final TestEntityWithToOneRelationship entity = entityStore.getEntityByName("deeplyNestedEntities",
                TestEntityWithToOneRelationship.class);
        assertSame(entityStore.getEntityByName("nest1", TestEntityWithToOneRelationship.class),
                entity.getTestEntityWithToOneRelationship());
        assertSame(entityStore.getEntityByName("nest2", TestEntityWithToOneRelationship.class),
                entity.getTestEntityWithToOneRelationship().getTestEntityWithToOneRelationship());
        assertSame(entityStore.getEntityByName("nest3", BasicTestEntity.class),
                entity.getTestEntityWithToOneRelationship().getTestEntityWithToOneRelationship().getReferencedEntity());
        assertEquals("deeply nested string", entityStore.getEntityByName("nest3", BasicTestEntity.class).getStringProperty());
    }

    @Test
    public void resolvesReferences() throws Exception {
        final TestEntityWithToOneRelationship entity = entityStore.getEntityByName("entityReferencingPreviouslyCreatedEntity",
                TestEntityWithToOneRelationship.class);
        assertSame(entityStore.getEntityByName("secondBasicEntity", BasicTestEntity.class), entity.getReferencedEntity());
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
        final TestEntityWithToManyRelationship entity = entityStore.getEntityByName("entityWithCollection",
                TestEntityWithToManyRelationship.class);
        final Collection<BasicTestEntity> toManyRelationship = entity.getToManyRelationship();
        assertEquals(2, toManyRelationship.size());
        BasicTestEntity referencedEntity1 = null;
        BasicTestEntity referencedEntity2 = null;
        for (final BasicTestEntity basicTestEntity : toManyRelationship) {
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
        final List<Object> entitiesInOrderOfCreation = new ArrayList<Object>();
        builder.addEntityCreatedListener(new EntityCreatedListener() {
            public void onEntityCreated(final String name, final Object entity) {
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

    @Test(expected = NoSuchElementException.class)
    public void clearsEntities() throws Exception {
        try {
            entityStore.getEntityByName("basicEntity", BasicTestEntity.class);
        } catch (final NoSuchElementException e) {
            e.printStackTrace();
            fail("basicEntity already was not available before calling clearEntityCacheAndDatabase");
        }

        entityStore.clear();
        entityStore.getEntityByName("basicEntity", BasicTestEntity.class);
    }

    @Test
    public void builderCreatesNewStoreForEachBuildAttempt() throws FileNotFoundException {
        final EntityStore oldStore = EntityBuilder.newBuilder().build(FileReader.create("tests/testEntityDefinitions.groovy"));
        final EntityStore newStore = EntityBuilder.newBuilder().build(FileReader.create("tests/testEntityDefinitions.groovy"));

        assertNotEquals(oldStore, newStore);

        final BasicTestEntity oldEntity = oldStore.getEntityByName("basicEntity", BasicTestEntity.class);
        assertNotNull(oldEntity);

        final BasicTestEntity newEntity = newStore.getEntityByName("basicEntity", BasicTestEntity.class);
        assertNotNull(newEntity);

        assertNotEquals(oldEntity, newEntity);
    }
}