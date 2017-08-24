package de.triology.blog.testdataloader.testentities;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseClass {

    // Making this field protected is current workaround for making test-data-loader work with fields of base classes
    protected String inheritedField;

    public String getInheritedField() {
        return inheritedField;
    }
}
