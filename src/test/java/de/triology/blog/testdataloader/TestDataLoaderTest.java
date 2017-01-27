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
package de.triology.blog.testdataloader;

import de.triology.blog.testdataloader.testentities.AnotherTestEntity;
import de.triology.blog.testdataloader.testentities.BasicTestEntity;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestDataLoaderTest {

    private TestDataLoader testDataLoader;
    private EntityManager entityManagerMock;

    @Before
    public void setUp() throws Exception {
        entityManagerMock = createTransactionalEntityManagerMock();
        testDataLoader = new TestDataLoader(entityManagerMock);
    }

    private EntityManager createTransactionalEntityManagerMock() {
        EntityManager entityManagerMock = mock(EntityManager.class);
        when(entityManagerMock.getTransaction()).thenReturn(mock(EntityTransaction.class));
        return entityManagerMock;
    }

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
        testDataLoader.loadTestData(Collections.singletonList("tests/testEntityDefinitions.groovy"));
        return testDataLoader.getEntityByName(entityName, entityClass);
    }

    @Test(expected = NoSuchElementException.class)
    public void clearsEntitiesFromMemory() throws Exception {
        testDataLoader.loadTestData(Collections.singletonList("tests/testEntityDefinitions.groovy"));
        try {
            testDataLoader.getEntityByName("basicEntity", BasicTestEntity.class);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            fail("basicEntity already was not available before calling clearEntityCacheAndDatabase");
        }

        testDataLoader.clearEntityCacheAndDatabase();
        testDataLoader.getEntityByName("basicEntity", BasicTestEntity.class);
    }

    @Test
    public void clearsEntitiesFromDatabase() throws Exception {
        when(entityManagerMock.merge(any())).then(returnsFirstArg());
        testDataLoader.loadTestData(Collections.singletonList("tests/testEntityDefinitions.groovy"));
        testDataLoader.clearEntityCacheAndDatabase();
        verify(entityManagerMock, times(12)).remove(any());
    }

    @Test(expected = IllegalStateException.class)
    public void expectsResourceLocalTransactionsWhenCreatedWithSingleArgConstructor() throws Exception {
        //noinspection unchecked
        when(entityManagerMock.getTransaction()).thenThrow(IllegalStateException.class);
        new TestDataLoader(entityManagerMock);
    }

    @Test(expected = IllegalStateException.class)
    public void expectsResourceLocalTransactionsWhenSpecified() throws Exception {
        //noinspection unchecked
        when(entityManagerMock.getTransaction()).thenThrow(IllegalStateException.class);
        new TestDataLoader(entityManagerMock, TestDataLoader.TransactionType.RESOURCE_LOCAL);
    }

    @Test
    public void doesNotManageTransactionsIfTransactionTypeIsJTA() throws Exception {
        //noinspection unchecked
        when(entityManagerMock.getTransaction()).thenThrow(IllegalStateException.class);
        testDataLoader = new TestDataLoader(entityManagerMock, TestDataLoader.TransactionType.JTA);
        testDataLoader.loadTestData(Collections.singletonList("tests/testEntityDefinitions.groovy"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void transactionTypeMustNotBeNull() throws Exception {
        new TestDataLoader(entityManagerMock, null);
    }
}