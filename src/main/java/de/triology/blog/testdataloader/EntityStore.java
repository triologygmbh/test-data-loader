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

package de.triology.blog.testdataloader;

import java.util.NoSuchElementException;

/**
 * An entity store which maintains the set of loaded entities.
 *
 */
interface EntityStore {

    /**
     * Gets the entity with the specified name from the set of entities created.
     *
     * If this {@code EntityBuilder} has not created an entity by the passed name a
     * {@link NoSuchElementException} is thrown. If an entity is found but has a different type than
     * the passed {@code entityClass}, an {@link IllegalArgumentException} is thrown.
     *
     * @param name
     *            {@link String} - the requested entity's name
     * @param entityClass
     *            the requested entity's {@link Class}
     * @return the requested entity
     */
    <T> T getEntityByName(String name, Class<T> entityClass);

    /**
     * Removes all entities from the store, so that they are no longer available through the
     * {@code getEntityByName} method.
     */
    void clear();
}
