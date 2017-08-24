# test-data-loader 
 [![Build Status](https://travis-ci.org/triologygmbh/test-data-loader.svg?branch=master)](https://travis-ci.org//triologygmbh/test-data-loader)
[![JitPack](https://jitpack.io/v//triologygmbh/test-data-loader.svg)](https://jitpack.io/#/triologygmbh/test-data-loader)

## Overview
This project implements a Groovy DSL that can be used to populate a database using JPA entities. Its indented use is testing but it could be used in other scenarios as well.
The DSL is implemented in Groovy but can be used from pure Java. Entities are modularly defined in separate .groovy files using the DSL syntax. Those entitiy definition files can then be loaded as needed using the `de.triology.blog.testdataloader.TestDataLoader`, which also provides access to loaded entities. Thus, the client code does not need to deal with any database or JPA specific concerns other than providing an initialized EntityManager.

This project was started while working on an article published in [Java aktuell](http://www.ijug.eu/java-aktuell/das-magazin.html) 03/2017: 
[A Groovy DSL for the Creation of Test Data using JPA](https://www.triology.de/en/blog-entries/groovy-dsl-test-data).  
The original article (🇩🇪) can be found here: [Eine Groovy-DSL zum Erzeugen von Testdaten über JPA](https://www.triology.de/blog/groovy-dsl-testdaten). 

## Configuration
You can use JitPack to configure the test-data-loader as a dependency in your project.<br/>
For example when using maven, define the JitPack repository:
```XML
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
And the test-data-loader dependency:
```XML
<dependency>
    <groupId>com.github.triologygmbh</groupId>
    <artifactId>test-data-loader</artifactId>
    <version>0.2.1</version>
</dependency>
```
Current version is [![](https://jitpack.io/v/triologygmbh/test-data-loader.svg)](https://jitpack.io/#triologygmbh/test-data-loader).<br/> 
For further details and options refer to the [JitPack website](https://jitpack.io/#triologygmbh/test-data-loader).

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
Since entity definition files are just plain Groovy scripts you are free to use any control structures like loops and conditions, e.g.:
```Groovy
import static de.triology.blog.testdataloader.EntityBuilder.create
import de.triology.blog.testdataloader.demo.User

5.times { count ->
    create User, "user_$count", {
        id = 1000 + count
        if(count % 2 == 0) {
            firstName = "even_$count"
        } else {
            firstName = "odd_$count"
        }
    }
}
```

### Loading entity definitions
Use the `de.triology.blog.testdataloader.TestDataLoader` to load entitiy definition files (from classpath or file system) and persist the defined entities. 
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

## Tested with...

We have approved TestDataLoader in multiple projects and use cases including

* "unit" tests with H2 and JUnit
* Integration test with arquillian, WildFly (Swarm) and Postgresql
* Integration tests with arquillian, IBM WebSphere Liberty Profile and IBM DB2

## Contributions
The test-data-loader has been derived and generalized from real world development projects but has yet to prove itself as standalone library. **Any feedback or contributions are highly welcome!**
