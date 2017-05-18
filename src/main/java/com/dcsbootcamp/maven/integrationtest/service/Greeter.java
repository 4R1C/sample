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
package com.dcsbootcamp.maven.integrationtest.service;

import com.dcsbootcamp.maven.integrationtest.entity.GreetingEntity;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * Greeter Service.
 *
 * @author saden
 */
@Named
@Singleton
@Transactional
public class Greeter {

    private final Provider<EntityManager> entityManagerProvider;

    @Inject
    Greeter(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    public void save(String phrase) {
        EntityManager entityManager = entityManagerProvider.get();
        entityManager.persist(new GreetingEntity(phrase));
    }

}
