package de.triology.blog.testdataloader;

import org.junit.Test;

import javax.persistence.EntityManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EntityPersisterTest {

    @Test
    public void persistsCreatedEntities() throws Exception {
        EntityManager entityManagerMock = mock(EntityManager.class);
        EntityPersister entityPersister = new EntityPersister(entityManagerMock);
        Object entity = new Object();
        entityPersister.entityCreated(entity);
        verify(entityManagerMock).persist(entity);
    }
}