package de.triology.blog.testdataloader.testentities;

import java.util.Collection;

public class TestEntityWithToManyRelationship {

    private Long id;
    Collection<BasicTestEntity> toManyRelationship;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<BasicTestEntity> getToManyRelationship() {
        return toManyRelationship;
    }

    public void setToManyRelationship(Collection<BasicTestEntity> toManyRelationship) {
        this.toManyRelationship = toManyRelationship;
    }
}
