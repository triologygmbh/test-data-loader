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
package de.triology.blog.testdata.loader

import javax.persistence.EntityManager

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.triology.blog.testdata.builder.EntityBuilderListener
import groovy.transform.PackageScope

/**
 * If added as {@link EntityCreatedListener} during entity creation, the EntityDeleter keeps track of created entities
 * for later deletion.
 */
@PackageScope
class EntityDeleter implements EntityBuilderListener {

    private static final Logger LOG = LoggerFactory.getLogger(EntityDeleter)

    private EntityManager entityManager
    private Stack entities;

    /**
     * Creates an EntityDeleter that uses the specified EntityManager to delete entities.
     *
     * @param entityManager EntityManager
     */
    protected EntityDeleter(EntityManager entityManager) {
        this.entityManager = entityManager
        entities = new Stack()
    }

    @Override
    public void onEntityCreated(String name, Object entity) {
        entities.push(entity)
    }

    /**
     * Deletes all previously created entities from the database using the instance's EntityManager.
     */
    protected deleteAllEntities() {
        while(!entities.empty()) {
            def entity = prepareNextEntityForDeletion()
            if(entity) {
                entityManager.remove(entity)
            }
        }
    }

    private prepareNextEntityForDeletion() {
        def entity = entities.pop()
        try {
            return mergeNextEntityIfNotAttached(entity)
        } catch (IllegalArgumentException e) {
            LOG.debug("caught IllegalArgumentException when merging entity $entity, assuming it to be already removed", e)
            return null
        }
    }

    private mergeNextEntityIfNotAttached(Object entity) {
        if(!entityManager.contains(entity)) {
            return entityManager.merge(entity)
        }
        return entity
    }
}
