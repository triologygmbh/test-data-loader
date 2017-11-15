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
package de.triology.blog.testdata.builder

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

    EntityBuilder builder = new EntityBuilder();

    def "should fire event for each registered listener" () {
        given: "multiple listeners, which have to be notified when the builder's method fireEntityCreated is called"
        String builtObject = "obj"
        String objName = "ObjectName"

        EntityBuilderListener listener1 = Mock()
        EntityBuilderListener listener2 = Mock()

        builder
                .addEntityBuilderListener(listener1)
                .addEntityBuilderListener(listener2)

        when: "the builder's method fireEntityCreated is called"
        builder.fireEntityCreated(objName, builtObject)

        then: "the onEntityCreated method of each listener is invoked"
        2 * _.onEntityCreated(objName, builtObject)
    }

    def "should create entities from given definition" () {
        given: "a groovy script defining entitied to be build"
        SimpleClass capturedSimple

        def entityDefinition = """
            import de.triology.blog.testdata.builder.EntityBuilderTest.SimpleClass
            import de.triology.blog.testdata.builder.EntityBuilderTest.ComplexClass

            create ComplexClass, "complex", {
                prop = "ComplexValue"
                simple = create SimpleClass, "simple", { prop = "SimpleValue" }
                complex = complex
            }
        """

        and: "an entity builder instance with registered listeners"
        EntityBuilderListener listener = Mock()
        builder.addEntityBuilderListener(listener)

        when: "the script is processed"
        builder.build(new StringReader(entityDefinition))

        then: "the registered listener get notified for each created object"
        1 * listener.onEntityCreated("simple", {
            it.prop == "SimpleValue"
            capturedSimple = it
        })
        1 * listener.onEntityCreated("complex", {
            it.prop == "ComplexValue"
            it.complex == it
            it.simple == capturedSimple
        })

        0 * listener._
    }
}
