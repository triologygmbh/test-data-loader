/**
 * MIT License
 *
 * Copyright (c) 2016 TRIOLOGY GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.triology.testdata.builder

import de.triology.testdata.builder.EntitiesScriptExecutor
import de.triology.testdata.builder.EntityBuilder
import spock.lang.Specification

class EntityBuilderTest extends Specification {

    static class SimpleClass {
        String prop;
    }

    static class ComplexClass {
        SimpleClass simple
        String prop
        ComplexClass complex
    }

    EntitiesScriptExecutor executor
    EntityBuilder builder

    def setup() {
        executor = Mock()
        builder = new EntityBuilder(executor)
    }

    def "should create single instance and set single property" () {
        given: "dsl configuration from setup"

        when: "a simple entity class is created"
        builder.create(SimpleClass, "simple", { prop = "Value" })

        then: "the builder receives an event with the specified name for the entity and the built entity"
        1 * executor.fireEntityCreated("simple", { it.prop == "Value" })
    }

    def "should create multiple instances and set given properties" () {
        given: "dsl configuration from setup"
        SimpleClass capturedSimple

        when: "two entites, a simple and a complex one are created"
        builder.create(ComplexClass, "complex", {
            prop = "ComplexValue"
            simple = create(SimpleClass, "simple", { prop = "SimpleValue" })
            complex = complex
        })

        then: "the builder receives exactly two events, both with the specified name for the entity and the built entity"
        1 * executor.fireEntityCreated("simple", {
            it.prop == "SimpleValue"
            capturedSimple = it
        })
        1 * executor.fireEntityCreated("complex", {
            it.prop == "ComplexValue"
            it.complex == it
            it.simple == capturedSimple
        })

        0 * executor._
    }

    def "should throw exception when the referenced entity is not available" () {
        given: "dsl configuration from setup"

        when: "an attempt is made to create an entity by specifying another not existing entity"
        builder.create(ComplexClass, "complex", { simple = foo })

        then: "an exception is thrown"
        EntityBuilderException e = thrown()
        e.message.contains("'foo' cannot be resolved")
    }

    def "should throw exception when entity name is reused" () {
        given: "dsl configuration from setup"

        when: "an attempt is made to create an entity using an already used name"
        builder.create(ComplexClass, "simple", { prop = "Value1" })
        builder.create(ComplexClass, "simple", { prop = "Value2" })

        then: "an exception is thrown"
        EntityBuilderException e = thrown()
        e.message.contains("entity with that name already exists")
    }
}
