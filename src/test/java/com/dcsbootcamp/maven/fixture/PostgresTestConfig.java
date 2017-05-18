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
package com.dcsbootcamp.maven.fixture;

import com.fitbur.testify.need.NeedInstance;
import com.github.dockerjava.api.command.InspectContainerResponse;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static javax.persistence.Persistence.createEntityManagerFactory;
import javax.sql.DataSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.cfg.Environment;
import org.postgresql.ds.PGSimpleDataSource;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import static org.springframework.transaction.support.TransactionSynchronizationManager.getResource;

/**
 * <p>
 * Postgres Database Configuration for testing.</p>
 * <p>
 * NOTE/XXX/WARN: This class must NOT be locatable by the production code. That
 * is to say it can not be placed in root package of your application otherwise
 * it will be picked up by the Spring component scanner. Test fixtures such as
 * this must be placed at least one package above your root package where
 * component/package scanning is performed.
 * </p>
 *
 * @author saden
 */
@Primary
@Configuration
public class PostgresTestConfig {

    /**
     * Provdes a PosgresSQL data source based on the database running inside of
     * a Docker container.
     *
     * @param instance container supplied need instance
     * @return the data source
     */
    @Bean
    DataSource dataSourceProvider(NeedInstance<InspectContainerResponse> instance) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(instance.getHost());
        dataSource.setPortNumber(instance.findFirstPort().get());
        //Default postgres image database name, user and postword
        dataSource.setDatabaseName("postgres");
        dataSource.setUser("postgres");
        dataSource.setPassword("mysecretpassword");

        return dataSource;
    }

    /**
     * Provides an entity manager factory based on the data source.
     *
     * @param ds the data source
     * @return the entity manager factory
     */
    @Bean
    EntityManagerFactory entityManagerFactoryProvider(DataSource ds) {
        Map<String, Object> props = new HashMap<>();
        props.put(Environment.DATASOURCE, ds);
        props.put(Environment.PHYSICAL_NAMING_STRATEGY, new PhysicalNamingStrategyStandardImpl());
        props.put(Environment.IMPLICIT_NAMING_STRATEGY, new ImplicitNamingStrategyComponentPathImpl());

        return createEntityManagerFactory("example.junit.spring.integrationtest", props);
    }

    /**
     * Provides a proxy instance of the current transaction's entity manager
     * which is ready to inject via constructor injection and guarantees
     * immutability.
     *
     * @param emf the entity manager factory
     * @return a proxy of entity manager
     */
    @Bean
    @Scope(SCOPE_PROTOTYPE)
    EntityManager entityManagerProvider(EntityManagerFactory emf) {
        EntityManagerHolder holder = (EntityManagerHolder) getResource(emf);

        if (holder == null) {
            throw new IllegalStateException("Transaction not available. Is your service annotated with"
                    + " @Transactional? Did you enable @EnableTransactionManagement?");
        }

        return holder.getEntityManager();
    }

    /**
     * Provides JPA based Spring transaction manager.
     *
     * @param emf the entity manager factory
     * @return jpa transaction manager
     */
    @Bean
    JpaTransactionManager jpaTransactionManagerProvider(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager(emf);

        return transactionManager;
    }
}
