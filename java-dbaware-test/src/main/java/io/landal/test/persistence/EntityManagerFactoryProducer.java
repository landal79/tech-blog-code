package it.softecheng.gwe.core.test.persistence;

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

import it.softecheng.gwe.core.test.extensions.TransactionalConnectionProvider;

@ApplicationScoped
public class EntityManagerFactoryProducer {

    @Inject
    private BeanManager beanManager;

    @Produces
    @ApplicationScoped
    public EntityManagerFactory produceEntityManagerFactory() {
    	Map<String, Object> props = new HashMap<>();
        props.put("javax.persistence.bean.manager", beanManager);
//        props.put(Environment.HBM2DDL_AUTO, "create-drop"); drop non dovrebbe servire
        props.put(Environment.HBM2DDL_AUTO, "create");
        props.put("hibernate.validator.apply_to_ddl", false);
        props.put(Environment.SCANNER_DISCOVERY, "class");
        props.put(Environment.USE_NEW_ID_GENERATOR_MAPPINGS, true);
        props.put(Environment.DEFAULT_BATCH_FETCH_SIZE, 100);
        props.put(Environment.IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS, true);
        props.put(Environment.DIALECT, H2Dialect.class.getName());

        //2nd level cache disabled
        props.put(Environment.USE_SECOND_LEVEL_CACHE, false);
        props.put(Environment.USE_QUERY_CACHE, false);

        // Batch config
        props.put(Environment.STATEMENT_BATCH_SIZE, 20);
        props.put(Environment.ORDER_INSERTS, true);
        props.put(Environment.ORDER_UPDATES, true);

        //envers integration disabled
        props.put("hibernate.integration.envers.enabled", false);

        //Connection provider
        props.put(Environment.CONNECTION_PROVIDER, TransactionalConnectionProvider.class.getName());
        props.put(Environment.JTA_PLATFORM, "JBossTS");

        return Persistence.createEntityManagerFactory("testPU", props);
    }

    public void close(@Disposes EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.close();
    }

}
