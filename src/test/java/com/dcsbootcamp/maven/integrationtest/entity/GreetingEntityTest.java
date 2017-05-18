/*
 * Copyright 2015 Sharmarke Aden.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dcsbootcamp.maven.integrationtest.entity;

import com.fitbur.testify.Module;
import com.dcsbootcamp.maven.integrationtest.GreeterConfig;
import com.fitbur.testify.integration.SpringIntegrationTest;
import com.fitbur.testify.need.Need;
import com.fitbur.testify.need.hsql.InMemoryHSQL;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author saden
 */
@Module(GreeterConfig.class)
@Need(InMemoryHSQL.class)
@RunWith(SpringIntegrationTest.class)
public class GreetingEntityTest {

    @Inject
    EntityManagerFactory entityManagerFactory;

    @Test(expected = PersistenceException.class)
    public void givenNullPhrasePersistShouldThrowException() {
        //Arrange
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            //Act
            GreetingEntity entity = new GreetingEntity(null);

            entityManager.persist(entity);
        } finally {
            entityManager.close();
        }
    }

    @Test
    public void givenEmptyStringPersistShouldPersistEmptyString() {
        //Arrange
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();

        GreetingEntity entity = new GreetingEntity("");

        //Act
        tx.begin();
        entityManager.persist(entity);
        tx.commit();
        entityManager.close();

        //Assert
        assertThat(entity.getId()).isNotNull();
    }

    @Test
    public void givenHelloPersistShouldPersistPhrase() {
        //Arrange
        GreetingEntity entity = new GreetingEntity("Hello");

        //Act
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(entity);
        tx.commit();
        entityManager.close();

        //Assert
        assertThat(entity.getId()).isNotNull();
    }

    @Test
    public void givenExistingPhrasePersistShouldPersist() {
        //Arrange
        GreetingEntity entity = new GreetingEntity("");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(entity);
        tx.commit();
        assertThat(entity.getId()).isNotNull();

        String phrase = "Hello";
        GreetingEntity updateEntity = new GreetingEntity(entity.getId(), phrase);

        //Act
        entityManager = entityManagerFactory.createEntityManager();
        tx = entityManager.getTransaction();
        tx.begin();
        GreetingEntity result = entityManager.merge(updateEntity);
        tx.commit();
        entityManager.close();

        //Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getPhrase()).isEqualTo(phrase);
    }

    @Test
    public void givenEqualEntitiesTheyShouldBeEqual() {
        //Arrange
        GreetingEntity entity1 = new GreetingEntity("Hello");
        GreetingEntity entity2 = new GreetingEntity("Hello");

        //Assert
        assertThat(entity1).isEqualTo(entity2);
    }

    @Test
    public void givenNullAndAnEntityTheyShouldNotBeEqual() {
        //Arrange
        GreetingEntity entity1 = new GreetingEntity("Hello");
        GreetingEntity entity2 = null;

        //Assert
        assertThat(entity1).isNotEqualTo(entity2);
    }

    @Test
    public void givenDifferentObjectAndAnEntityTheyShouldNotBeEqual() {
        //Arrange
        GreetingEntity entity1 = new GreetingEntity("Hello");
        Object entity2 = new Object();

        //Assert
        assertThat(entity1).isNotEqualTo(entity2);
    }

    @Test
    public void givenSameEntityItShouldBeEqualToItself() {
        //Arrange
        GreetingEntity entity = new GreetingEntity("Hello");

        //Assert
        assertThat(entity).isEqualTo(entity);
    }

    @Test
    public void givenEqualEntitiesTheyShouldHaveEqualHashCode() {
        //Arrange
        GreetingEntity entity1 = new GreetingEntity("Hello");
        GreetingEntity entity2 = new GreetingEntity("Hello");

        //Assert
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
    }

    @Test
    public void callToToStringShouldReturnEntityDescription() {
        //Arrange
        GreetingEntity entity = new GreetingEntity(1L, "Hello");

        //Assert
        assertThat(entity.toString()).contains("1", "Hello");
    }

}
