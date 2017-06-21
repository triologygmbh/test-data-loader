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
package de.triology.blog.testdataloader.demo;

import de.triology.blog.testdataloader.TestDataLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class Demo {

    private EntityManager entityManager;
    private TestDataLoader testDataLoader;

    @Before
    public void setUp() throws Exception {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("testdataloader");
        entityManager = entityManagerFactory.createEntityManager();
        testDataLoader = new TestDataLoader(entityManager);
        testDataLoader.loadTestData(Collections.singletonList("demo/testData.groovy"));
    }

    @After
    public void tearDown() throws Exception {
        testDataLoader.clearEntityCacheAndDatabase();
        assertEquals(0L, entityManager.createQuery("select count(u) from User u").getSingleResult());
        assertEquals(0L, entityManager.createQuery("select count(d) from Department d").getSingleResult());
    }

    @Test
    public void createsTwoUsersInDatabase() throws Exception {
        User peter = entityManager.find(User.class, 123L);
        assertEquals("Pan", peter.getLastName());

        User captainHook = entityManager.find(User.class, 987L);
        assertEquals("James", captainHook.getFirstName());
    }

    @Test
    public void providesUsersUnderTheirNames() throws Exception {
        User peter = testDataLoader.getEntityByName("Peter", User.class);
        assertEquals("Pan", peter.getLastName());
    }

    @Test
    public void createsANestedDeparment() throws Exception {
        User peter = entityManager.find(User.class, 123L);
        assertEquals("The Lost Boys", peter.getDepartment().getName());
    }

    @Test
    public void referencesPreviouslyCreatedEntities() throws Exception {
        User peter = entityManager.find(User.class, 123L);
        User tinker = entityManager.find(User.class, 555L);
        assertSame(peter.getDepartment(), tinker.getDepartment());
    }

    @Test
    public void resolvesNestedReferences() throws Exception {
        User peter = entityManager.find(User.class, 123L);
        Department lostBoys = entityManager.find(Department.class, 999L);
        assertSame(peter, lostBoys.getHead());
    }
}
