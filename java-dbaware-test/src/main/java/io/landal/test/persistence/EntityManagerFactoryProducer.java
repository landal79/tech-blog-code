package io.landal.test.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.cfg.Environment;
import org.hibernate.dialect.H2Dialect;


@ApplicationScoped
public class EntityManagerFactoryProducer {

    @Inject
    private BeanManager beanManager;

    @Produces
    @ApplicationScoped
    public EntityManagerFactory produceEntityManagerFactory() {
    	Map<String, Object> props = new HashMap<>();
        props.put("javax.persistence.bean.manager", beanManager);
        props.put(Environment.HBM2DDL_AUTO, "create");
        props.put("hibernate.validator.apply_to_ddl", false);
        props.put(Environment.SCANNER_DISCOVERY, "class");
        props.put(Environment.USE_NEW_ID_GENERATOR_MAPPINGS, true);
        props.put(Environment.IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS, true);
        props.put(Environment.DIALECT, H2Dialect.class.getName());
        //envers integration disabled
        props.put("hibernate.integration.envers.enabled", false);

        //JNDI Connection provider
        props.put(Environment.CONNECTION_PROVIDER, org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl.class.getName());
        props.put(Environment.JTA_PLATFORM, "JBossTS");

        return Persistence.createEntityManagerFactory("testPU", props);
    }

    public void close(@Disposes EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }

}
