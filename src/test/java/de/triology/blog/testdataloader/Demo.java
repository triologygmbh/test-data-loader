package de.triology.blog.testdataloader;

import de.triology.blog.testdataloader.entities.Department;
import de.triology.blog.testdataloader.entities.User;
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
        entityManager.getTransaction().begin();
        testDataLoader = new TestDataLoader(entityManager);
        testDataLoader.loadTestData(Collections.singletonList("testData.groovy"));
    }

    @After
    public void tearDown() throws Exception {
        entityManager.flush();
        entityManager.getTransaction().rollback();
        testDataLoader.clear();
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
