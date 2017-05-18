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
package com.dcsbootcamp.maven.integrationtest;

import com.fitbur.testify.Cut;
import com.fitbur.testify.Module;
import com.fitbur.testify.Real;
import com.dcsbootcamp.maven.integrationtest.entity.GreetingEntity;
import com.dcsbootcamp.maven.integrationtest.service.Greeter;
import com.fitbur.testify.integration.SpringIntegrationTest;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 * @author saden
 */
@Module(GreeterConfig.class)
@RunWith(SpringIntegrationTest.class)
public class GreeterDelegateTest {

    @Cut
    Greeter cut;

    @Real(true)
    Provider<EntityManager> entityManagerProvider;

    @Inject
    EntityManagerFactory entityManagerFactory;

    @Test
    public void givenHelloSaveShouldSavePhrase() {
        //Arrange
        String phrase = "Hello";

        //Act
        cut.save(phrase);

        //Assert
        EntityManager em = entityManagerFactory.createEntityManager();
        Query query = em.createQuery("SELECT e FROM GreetingEntity e");
        assertThat(query).isNotNull();
        List<GreetingEntity> entities = query.getResultList();
        assertThat(entities).hasSize(1);

        GreetingEntity entity = entities.get(0);
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getPhrase()).isEqualTo(phrase);
        em.close();

        //you can verify calls to delegated real instances
        verify(entityManagerProvider).get();

    }

    @Test
    public void givenHayeSaveShouldSavePhrase() {
        //Arrange
        String phrase = "Hello";
        GreetingEntity entity = new GreetingEntity(phrase);
        EntityManager entityManager = mock(EntityManager.class);

        given(entityManagerProvider.get()).willReturn(entityManager);
        willThrow(RuntimeException.class).given(entityManager).persist(entity);

        try {
            //Act
            cut.save(phrase);

        } catch (RuntimeException e) {

            //Assert
            verify(entityManagerProvider).get();
            verify(entityManager).persist(entity);
        }
    }
}
