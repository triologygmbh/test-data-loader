# test-data-loader
A Groovy DSL for creating test data via JPA

## Contents
This project implements a Groovy DSL that can be used to create test data using JPA entities. The DSL is implemented in Groovy but can be used from pure Java.
Entities are defined in separate .groovy files using the DSL syntax. Those entitiy definition files can then be loaded as needed using the `de.triology.blog.testdataloader.TestDataLoader`.

## Usage
An example entity definition can be found here: https://github.com/triologygmbh/test-data-loader/blob/master/src/test/resources/demo/testData.groovy

And [`de.triology.blog.testdataloader.demo.Demo`](https://github.com/triologygmbh/test-data-loader/blob/master/src/test/java/de/triology/blog/testdataloader/demo/Demo.java) shows how to load that file. (Notice that `Demo` is a Java class.)

### Entity Definitions
Use the following syntax in a separate .groovy file to define a `User` entity. The entitiy will be created, persisted and registered under the name "Peter" when the definition file is loaded. _**Note:** Entity definition files are expected to be UTF-8 encoded._
```Groovy
import static de.triology.blog.testdataloader.EntityBuilder.create
import de.triology.blog.testdataloader.demo.User

create User, 'Peter', {
    id = 123
    firstName = 'Peter'
    lastName = 'Pan'
    login = 'pete'
    email = 'peter.pan@example.com'
}
```
Create nested entities by simply nesting their definitions:
```Groovy
import static de.triology.blog.testdataloader.EntityBuilder.create
import de.triology.blog.testdataloader.demo.User
import de.triology.blog.testdataloader.demo.Department

create User, 'Peter', {
    // ...
    department = create Department, 'lostBoys', {
        id = 999
        name = 'The Lost Boys'
    }
}
```
And reference previously created entities by their name like so: 
```Groovy
import static de.triology.blog.testdataloader.EntityBuilder.create
import de.triology.blog.testdataloader.demo.User
import de.triology.blog.testdataloader.demo.Department

create User, 'Peter', {
    // ...
    department = create Department, 'lostBoys', {
        // ...
        head = Peter
    }
}

create User, 'Tinker', {
    id = 555
    firstName = 'Tinker'
    lastName = 'Bell'
    department = lostBoys
}
```

### Loading entity definitions
Use the `de.triology.blog.testdataloader.TestDataLoader` to load entitiy definition files and persist the defined entities. 
The `TestDataLoader` requires a fully initialized, ready-to-use entitiy manager and can then be used to load entity definition files and access the persisted entities.
```Java
EntityManager entityManager = // ... init EntityManager
TestDataLoader testDataLoader = new TestDataLoader(entityManager);
testDataLoader.loadTestData(Collections.singletonList("demo/testData.groovy"));

User peter = entityManager.find(User.class, 123L);
assert "Pan".equals(peter.getLastName());

User tinker = testDataLoader.getEntityByName("Tinker", User.class);
assert "Bell".equals(tinker.getLastName());
```

### Clean up afterwards
To reset the database as well as the TestDataLoader to a clean state after a test case simply call `testDataLoader.clear()`. That will delete all created entites from the database and from TestDataLoader's entity cache.
