package io.landal.test.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.hibernate.cfg.Environment;
import org.hibernate.dialect.H2Dialect;
import org.jboss.weld.exceptions.UnsupportedOperationException;
import org.jboss.weld.injection.spi.JpaInjectionServices;
import org.jboss.weld.injection.spi.ResourceReference;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;
import org.jboss.weld.injection.spi.helpers.SimpleResourceReference;

/**
 *
 * JPA integration, it makes possible to use the annotations: {@link PersistenceContext} and {@link PersistenceUnit}.
 * Beware both {@code EntityManagerFactory} and {@code EntityManager} are singleton for the current container instance.
 */
public class TestingJpaInjectionServices implements JpaInjectionServices {

	private Map<String, EntityManagerFactory> entityManagerFactoryMap = new HashMap<>();
	private Map<String, EntityManager> entityManagerMap = new HashMap<>();

	@Override
	public void cleanup() {
		entityManagerFactoryMap.values().forEach(emf -> emf.close());
		entityManagerMap.values().forEach(em -> em.close());
	}

	@Override
	public ResourceReferenceFactory<EntityManager> registerPersistenceContextInjectionPoint(InjectionPoint injectionPoint) {

		return new ResourceReferenceFactory<EntityManager>() {

			@Override
			public ResourceReference<EntityManager> createResource() {
				final PersistenceContext pcAnnotation = injectionPoint.getAnnotated().getAnnotation(PersistenceContext.class);
				final String unitName = pcAnnotation.unitName();
				final EntityManager entityManager = entityManagerMap.computeIfAbsent(unitName, (un) -> {
					final EntityManagerFactory entityManagerFactory = entityManagerFactoryMap.computeIfAbsent(un, (key) -> createEntityManagerFactory(key));
					return entityManagerFactory.createEntityManager();
				});

				return new SimpleResourceReference<EntityManager>(entityManager);
			}
		};
	}

	@Override
	public ResourceReferenceFactory<EntityManagerFactory> registerPersistenceUnitInjectionPoint(InjectionPoint injectionPoint) {

		return new ResourceReferenceFactory<EntityManagerFactory>() {
			@Override
			public ResourceReference<EntityManagerFactory> createResource() {
				final PersistenceUnit puAnnotation = injectionPoint.getAnnotated().getAnnotation(PersistenceUnit.class);
				final String unitName = puAnnotation.unitName();
				final EntityManagerFactory entityManagerFactory = entityManagerFactoryMap.computeIfAbsent(unitName, (un) -> createEntityManagerFactory(un));
				return new SimpleResourceReference<EntityManagerFactory>(entityManagerFactory);
			}
		};
	}

	@Override
	public EntityManager resolvePersistenceContext(InjectionPoint injectionPoint) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityManagerFactory resolvePersistenceUnit(InjectionPoint injectionPoint) {
		throw new UnsupportedOperationException();
	}

	private EntityManagerFactory createEntityManagerFactory(String unitName) {
		Objects.requireNonNull(unitName);

		Map<String, Object> props = new HashMap<>();
		props.put("javax.persistence.bean.manager", CDI.current().getBeanManager());
		props.put(Environment.HBM2DDL_AUTO, "create");
		props.put("hibernate.validator.apply_to_ddl", false);
		props.put(Environment.SCANNER_DISCOVERY, "class");
		props.put(Environment.USE_NEW_ID_GENERATOR_MAPPINGS, true);
		props.put(Environment.IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS, true);
		props.put(Environment.DIALECT, H2Dialect.class.getName());
		// envers integration disabled
		props.put("hibernate.integration.envers.enabled", false);

		// JNDI Connection provider
		props.put(Environment.CONNECTION_PROVIDER, org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl.class.getName());
		props.put(Environment.JTA_PLATFORM, "JBossTS");

		return Persistence.createEntityManagerFactory(unitName.trim().length() == 0 ? null : unitName, props);
//		return Persistence.createEntityManagerFactory("testPU", props);
	}

}
