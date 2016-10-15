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

import org.junit.Test;
import org.mockito.InOrder;

import javax.persistence.EntityManager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EntityDeleterTest {

    @Test
    public void deletesAllCreatedEntitiesInReversedOrder() throws Exception {
        EntityManager entityManager = mock(EntityManager.class);
        EntityDeleter entityDeleter = new EntityDeleter(entityManager);

        Object entity1 = new Object();
        Object entity2 = new Object();
        Object entity3 = new Object();

        entityDeleter.entityCreated(entity1);
        entityDeleter.entityCreated(entity2);
        entityDeleter.entityCreated(entity3);
        entityDeleter.deleteAllEntities();

        InOrder inOrder = inOrder(entityManager);
        inOrder.verify(entityManager).remove(entity3);
        inOrder.verify(entityManager).remove(entity2);
        inOrder.verify(entityManager).remove(entity1);
    }

    @Test
    public void deletesAnEntityOnlyOnce() throws Exception {
        EntityManager entityManager = mock(EntityManager.class);
        EntityDeleter entityDeleter = new EntityDeleter(entityManager);
        Object entity = new Object();
        entityDeleter.entityCreated(entity);

        entityDeleter.deleteAllEntities();
        entityDeleter.deleteAllEntities();

        verify(entityManager, times(1)).remove(any());
    }

}