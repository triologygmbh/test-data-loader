package de.triology.blog.testdataloader.testentities;

public class TestEntityWithToOneRelationship {

    private Long id;
    private BasicTestEntity referencedEntity;
    private TestEntityWithToOneRelationship testEntityWithToOneRelationship;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BasicTestEntity getReferencedEntity() {
        return referencedEntity;
    }

    public void setReferencedEntity(BasicTestEntity referencedEntity) {
        this.referencedEntity = referencedEntity;
    }

    public TestEntityWithToOneRelationship getTestEntityWithToOneRelationship() {
        return testEntityWithToOneRelationship;
    }

    public void setTestEntityWithToOneRelationship(TestEntityWithToOneRelationship testEntityWithToOneRelationship) {
        this.testEntityWithToOneRelationship = testEntityWithToOneRelationship;
    }
}
