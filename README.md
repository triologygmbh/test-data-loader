# test-data-loader 
[![Build Status](https://opensource.triology.de/jenkins/buildStatus/icon?job=triologygmbh-github/test-data-loader/master)](https://opensource.triology.de/jenkins/blue/organizations/jenkins/triologygmbh-github%2Ftest-data-loader/branches)
[![Maven Central](https://img.shields.io/maven-central/v/de.triology.test-data-loader/test-data-loader.svg)](http://search.maven.org/#search|gav|1|g%3A%22de.triology.test-data-loader%22%20AND%20a%3A%22test-data-loader%22)

## Overview
This project implements a Groovy DSL that can be used to populate a database using JPA entities. Its indented use is testing but it could be used in other scenarios as well.
The DSL is implemented in Groovy but can be used from pure Java. Entities are modularly defined in separate .groovy files using the DSL syntax. Those entity definition files can then be loaded as needed using the `de.triology.blog.testdata.loader.TestDataLoader`, which also provides access to loaded entities. Thus, the client code does not need to deal with any database or JPA specific concerns other than providing an initialized EntityManager.

This project was started while working on an article published in [Java aktuell](http://www.ijug.eu/java-aktuell/das-magazin.html) 03/2017: 
[A Groovy DSL for the Creation of Test Data using JPA](https://www.triology.de/en/blog-entries/groovy-dsl-test-data).  
The original article (🇩🇪) can be found here: [Eine Groovy-DSL zum Erzeugen von Testdaten über JPA](https://www.triology.de/wp-content/uploads/2017/09/Eine-Groovy-DSL-zum-Erzeugen-von-Testdaten-ueber-JPA.pdf).

Please note that from version 1.x the implementation as described in the article referenced above has changed. For more information about the changes, please refer to the release notes of each [release](https://github.com/triologygmbh/test-data-loader/releases).

## Configuration

Add the [latest stable version of test-data-loader](http://search.maven.org/#search|gav|1|g%3A%22de.triology.test-data-loader%22%20AND%20a%3A%22test-data-loader%22) to the dependency management tool of your choice.

E.g. for maven

```XML
<dependency>
    <groupId>de.triology.test-data-loader</groupId>
    <artifactId>test-data-loader</artifactId>
    <version>1.0.0</version>
</dependency>
```
Current version is [![Maven Central](https://img.shields.io/maven-central/v/de.triology.test-data-loader/test-data-loader.svg)](http://search.maven.org/#search|gav|1|g%3A%22de.triology.test-data-loader%22%20AND%20a%3A%22test-data-loader%22)

You can get snapshot versions from maven central (for the most recent commit on develop branch) or via [JitPack](https://jitpack.io/#triologygmbh/test-data-loader) (note that JitPack uses different maven coordinates).  

## Usage
An example entity definition Groovy script file can be found here: https://github.com/triologygmbh/test-data-loader/blob/master/src/test/resources/tests/itTestData.groovy

The [`de.triology.blog.testdata.loader.TestDataLoaderIT`](https://github.com/triologygmbh/test-data-loader/blob/master/src/test/java/de/triology/blog/testdata/loader/TestDataLoaderIT.java) integration test shows how to load that file.

### Entity Definitions
Use the following syntax in a separate .groovy file to define a `User` entity. The entity will be created, persisted and registered under the name "Peter" when the definition file is loaded. _**Note:** Entity definition files are expected to be UTF-8 encoded._
```Groovy
import de.triology.blog.testdata.loader.testentities.User

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
import de.triology.blog.testdata.loader.testentities.User
import de.triology.blog.testdata.loader.testentities.Department

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
import de.triology.blog.testdata.loader.testentities.User
import de.triology.blog.testdata.loader.testentities.Department

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
Since entity definition files are just plain Groovy scripts, you are free to use any control structures, like loops and conditions, e.g.:
```Groovy
import de.triology.blog.testdata.loader.testentities.User

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

**Private fields from superclasses**

For now, we must work around this, by setting the field to `protected` or creating a `protected` setter method.
See [this issue](https://github.com/triologygmbh/test-data-loader/issues/7).

### Loading entity definitions
Use the `de.triology.blog.testdata.loader.TestDataLoader` to load entity definition files (from classpath or file system) and persist the defined entities. 
The `TestDataLoader` requires a fully initialized, ready-to-use `EntityManager` and can then be used to load entity definition files and access the persisted entities.
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
To reset the database as well as the TestDataLoader to a clean state after a test case simply call `testDataLoader.clear()`. That will delete all created entities from the database and from TestDataLoader's entity cache.

## Tested with...

We have approved TestDataLoader in multiple projects and use cases including

* "unit" tests with H2 and JUnit
* Integration test with arquillian, WildFly (Swarm) and Postgresql
* Integration tests with arquillian, IBM WebSphere Liberty Profile and IBM DB2

## Contributions
The test-data-loader has been derived and generalized from real world development projects but has yet to prove itself as stand-alone library. **Any feedback or contributions are highly welcome!**
