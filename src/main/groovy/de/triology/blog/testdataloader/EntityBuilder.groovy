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
package de.triology.blog.testdataloader

import org.codehaus.groovy.control.CompilerConfiguration

/**
 * Builder that takes the name of a file containing entity definitions and builds the entities accordingly.
 */
class EntityBuilder {

    private List<EntityBuilderListener> listeners = []

    /**
     * Builds the entities defined in the provided by the passed Reader.
     *
     * @param reader - a Reader for the file containing the entity definitions
     */
    public void build(Reader reader) {
        CompilerConfiguration compilerConfiguration = new CompilerConfiguration()
        compilerConfiguration.scriptBaseClass = EntityBuilderScript.class.name

        Binding binding = new Binding()
        binding.dsl = new EntityBuilderDsl(this)

        GroovyShell shell = new GroovyShell(this.class.classLoader, binding, compilerConfiguration)
        Script script =  shell.parse(reader)
        script.run()
    }

    /**
     * Adds an {@link EntityBuilderListener} that gets notified every time an entity is completely created.
     * @param listener {@link EntityBuilderListener}
     */
    public EntityBuilder addEntityBuilderListener(EntityBuilderListener listener) {
        listeners += listener
        return this
    }

    protected void fireEntityCreated(String entityName, Object entity) {
        listeners*.onEntityCreated(entityName, entity)
    }
}

