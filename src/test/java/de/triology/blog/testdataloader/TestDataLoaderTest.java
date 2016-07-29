package de.triology.blog.testdataloader;

import de.triology.blog.testdataloader.testentities.AnotherTestEntity;
import de.triology.blog.testdataloader.testentities.BasicTestEntity;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestDataLoaderTest {

    @Test
    public void getsCreatedEntityByName() throws Exception {
        BasicTestEntity entity = loadDefaultTestDataAndCallGetEntityByName("basicEntity", BasicTestEntity.class);
        assertNotNull(entity);
    }

    @Test(expected = NoSuchElementException.class)
    public void getEntityByNameFailsForNonexistingEntity() throws Exception {
        loadDefaultTestDataAndCallGetEntityByName("notExisting", BasicTestEntity.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEntityByNameFailsIfPassesClassDoesNotMatch() throws Exception {
        loadDefaultTestDataAndCallGetEntityByName("basicEntity", AnotherTestEntity.class);
    }

    private <T> T loadDefaultTestDataAndCallGetEntityByName(String entityName, Class<T> entityClass) {
        EntityManager entityManagerMock = createTransactionalEntityManagerMock();
        TestDataLoader testDataLoader = new TestDataLoader(entityManagerMock);
        testDataLoader.loadTestData(Arrays.asList("testEntityDefinitions.groovy"));
        return testDataLoader.getEntityByName(entityName, entityClass);
    }

    @Test(expected = NoSuchElementException.class)
    public void clearsEntities() throws Exception {
        EntityManager entityManagerMock = createTransactionalEntityManagerMock();
        when(entityManagerMock.createNativeQuery(anyString())).thenReturn(mock(Query.class));
        TestDataLoader testDataLoader = new TestDataLoader(entityManagerMock);
        testDataLoader.loadTestData(Arrays.asList("testEntityDefinitions.groovy"));
        try {
            testDataLoader.getEntityByName("basicEntity", BasicTestEntity.class);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            fail("basicEntity already was not available before calling clear");
        }

        testDataLoader.clear();
        testDataLoader.getEntityByName("basicEntity", BasicTestEntity.class);
    }

    private EntityManager createTransactionalEntityManagerMock() {
        EntityManager entityManagerMock = mock(EntityManager.class);
        when(entityManagerMock.getTransaction()).thenReturn(mock(EntityTransaction.class));
        return entityManagerMock;
    }
}