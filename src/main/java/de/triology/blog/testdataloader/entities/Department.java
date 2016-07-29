package de.triology.blog.testdataloader.entities;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Department {

    @Id
    private Long id;

    private String name;

    @ManyToOne
    private User head;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getHead() {
        return head;
    }

    public void setHead(User head) {
        this.head = head;
    }
}
