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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntityDeleterTest {

    @Mock
    EntityManager entityManager;
    private EntityDeleter entityDeleter;

    @Before
    public void setUp() throws Exception {
        entityDeleter = new EntityDeleter(entityManager);
    }

    @Test
    public void deletesAllCreatedEntitiesInReversedOrder() throws Exception {
        Object entity1 = new Object();
        Object entity2 = new Object();
        Object entity3 = new Object();

        entityDeleter.entityCreated(entity1);
        entityDeleter.entityCreated(entity2);
        entityDeleter.entityCreated(entity3);

        when(entityManager.merge(entity1)).thenReturn(entity1);
        when(entityManager.merge(entity2)).thenReturn(entity2);
        when(entityManager.merge(entity3)).thenReturn(entity3);

        entityDeleter.deleteAllEntities();

        InOrder inOrder = inOrder(entityManager);
        inOrder.verify(entityManager).remove(entity3);
        inOrder.verify(entityManager).remove(entity2);
        inOrder.verify(entityManager).remove(entity1);
    }

    @Test
    public void deletesAnEntityOnlyOnce() throws Exception {
        EntityDeleter entityDeleter = new EntityDeleter(entityManager);
        Object entity = new Object();
        entityDeleter.entityCreated(entity);

        entityDeleter.deleteAllEntities();
        entityDeleter.deleteAllEntities();

        verify(entityManager, times(1)).remove(any());
    }

    @Test
    public void mergesEntitiesBeforeRemovingThem() throws Exception {
        EntityDeleter entityDeleter = new EntityDeleter(entityManager);
        Object entity = new Object();
        entityDeleter.entityCreated(entity);

        when(entityManager.merge(entity)).thenReturn(entity);

        entityDeleter.deleteAllEntities();

        InOrder inOrder = inOrder(entityManager);
        inOrder.verify(entityManager).merge(entity);
        inOrder.verify(entityManager).remove(entity);
    }
}