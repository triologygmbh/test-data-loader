package de.triology.blog.testdataloader.testentities;

import java.util.Date;

public class BasicTestEntity {

    private Long id;
    private String stringProperty;
    private Integer integerProperty;
    private Date dateProperty;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public Integer getIntegerProperty() {
        return integerProperty;
    }

    public void setIntegerProperty(Integer integerProperty) {
        this.integerProperty = integerProperty;
    }

    public Date getDateProperty() {
        return dateProperty;
    }

    public void setDateProperty(Date dateProperty) {
        this.dateProperty = dateProperty;
    }
}
