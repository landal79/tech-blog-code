package io.landal.test.persistence;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanAttributes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.Decorator;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.InjectionTargetFactory;
import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.ProducerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.cfg.Environment;
import org.hibernate.dialect.H2Dialect;
import org.jboss.weld.exceptions.UnsupportedOperationException;
import org.jboss.weld.injection.spi.JpaInjectionServices;
import org.jboss.weld.injection.spi.ResourceReference;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;
import org.jboss.weld.injection.spi.helpers.SimpleResourceReference;

public class TestingJpaInjectionServices implements JpaInjectionServices {

	private EntityManagerFactory entityManagerFactory;

	public TestingJpaInjectionServices() {

	}

	@Override
	public void cleanup() {
		this.entityManagerFactory.close();
	}

	@Override
	public ResourceReferenceFactory<EntityManager> registerPersistenceContextInjectionPoint(InjectionPoint injectionPoint) {

		if (this.entityManagerFactory == null) {
			this.entityManagerFactory = createEntityManagerFactory();
		}

		return new ResourceReferenceFactory<EntityManager>() {
			@Override
			public ResourceReference<EntityManager> createResource() {
				return new SimpleResourceReference<EntityManager>(entityManagerFactory.createEntityManager());
			}
		};
	}

	@Override
	public ResourceReferenceFactory<EntityManagerFactory> registerPersistenceUnitInjectionPoint(InjectionPoint injectionPoint) {

		if (this.entityManagerFactory == null) {
			this.entityManagerFactory = createEntityManagerFactory();
		}

		return new ResourceReferenceFactory<EntityManagerFactory>() {
			@Override
			public ResourceReference<EntityManagerFactory> createResource() {
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

	private EntityManagerFactory createEntityManagerFactory() {
		Map<String, Object> props = new HashMap<>();
       props.put("javax.persistence.bean.manager", new BeanManagerDelegate());
		props.put(Environment.HBM2DDL_AUTO, "create");
		props.put("hibernate.validator.apply_to_ddl", false);
		props.put(Environment.SCANNER_DISCOVERY, "class");
		props.put(Environment.USE_NEW_ID_GENERATOR_MAPPINGS, true);
		props.put(Environment.IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS, true);
		props.put(Environment.DIALECT, H2Dialect.class.getName());
		// envers integration disabled
		props.put("hibernate.integration.envers.enabled", false);

		// JNDI Connection provider
		props.put(Environment.CONNECTION_PROVIDER,
				org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl.class.getName());
		props.put(Environment.JTA_PLATFORM, "JBossTS");

		return Persistence.createEntityManagerFactory("testPU", props);
	}

}

class BeanManagerDelegate implements BeanManager {

	private BeanManager beanManager;

	private BeanManager beanManager() {
		if(beanManager == null) {
			this.beanManager = CDI.current().getBeanManager();
		}

		return beanManager;
	}

	public Object getReference(Bean<?> bean, Type beanType, CreationalContext<?> ctx) {
		return beanManager().getReference(bean, beanType, ctx);
	}

	public Object getInjectableReference(InjectionPoint ij, CreationalContext<?> ctx) {
		return beanManager().getInjectableReference(ij, ctx);
	}

	public <T> CreationalContext<T> createCreationalContext(Contextual<T> contextual) {
		return beanManager().createCreationalContext(contextual);
	}

	public Set<Bean<?>> getBeans(Type beanType, Annotation... qualifiers) {
		return beanManager().getBeans(beanType, qualifiers);
	}

	public Set<Bean<?>> getBeans(String name) {
		return beanManager().getBeans(name);
	}

	public Bean<?> getPassivationCapableBean(String id) {
		return beanManager().getPassivationCapableBean(id);
	}

	public <X> Bean<? extends X> resolve(Set<Bean<? extends X>> beans) {
		return beanManager().resolve(beans);
	}

	public void validate(InjectionPoint injectionPoint) {
		beanManager().validate(injectionPoint);
	}

	public void fireEvent(Object event, Annotation... qualifiers) {
		beanManager().fireEvent(event, qualifiers);
	}

	public <T> Set<ObserverMethod<? super T>> resolveObserverMethods(T event, Annotation... qualifiers) {
		return beanManager().resolveObserverMethods(event, qualifiers);
	}

	public List<Decorator<?>> resolveDecorators(Set<Type> types, Annotation... qualifiers) {
		return beanManager().resolveDecorators(types, qualifiers);
	}

	public List<Interceptor<?>> resolveInterceptors(InterceptionType type, Annotation... interceptorBindings) {
		return beanManager().resolveInterceptors(type, interceptorBindings);
	}

	public boolean isScope(Class<? extends Annotation> annotationType) {
		return beanManager().isScope(annotationType);
	}

	public boolean isNormalScope(Class<? extends Annotation> annotationType) {
		return beanManager().isNormalScope(annotationType);
	}

	public boolean isPassivatingScope(Class<? extends Annotation> annotationType) {
		return beanManager().isPassivatingScope(annotationType);
	}

	public boolean isQualifier(Class<? extends Annotation> annotationType) {
		return beanManager().isQualifier(annotationType);
	}

	public boolean isInterceptorBinding(Class<? extends Annotation> annotationType) {
		return beanManager().isInterceptorBinding(annotationType);
	}

	public boolean isStereotype(Class<? extends Annotation> annotationType) {
		return beanManager().isStereotype(annotationType);
	}

	public Set<Annotation> getInterceptorBindingDefinition(Class<? extends Annotation> bindingType) {
		return beanManager().getInterceptorBindingDefinition(bindingType);
	}

	public Set<Annotation> getStereotypeDefinition(Class<? extends Annotation> stereotype) {
		return beanManager().getStereotypeDefinition(stereotype);
	}

	public boolean areQualifiersEquivalent(Annotation qualifier1, Annotation qualifier2) {
		return beanManager().areQualifiersEquivalent(qualifier1, qualifier2);
	}

	public boolean areInterceptorBindingsEquivalent(Annotation interceptorBinding1, Annotation interceptorBinding2) {
		return beanManager().areInterceptorBindingsEquivalent(interceptorBinding1, interceptorBinding2);
	}

	public int getQualifierHashCode(Annotation qualifier) {
		return beanManager().getQualifierHashCode(qualifier);
	}

	public int getInterceptorBindingHashCode(Annotation interceptorBinding) {
		return beanManager().getInterceptorBindingHashCode(interceptorBinding);
	}

	public Context getContext(Class<? extends Annotation> scopeType) {
		return beanManager().getContext(scopeType);
	}

	public ELResolver getELResolver() {
		return beanManager().getELResolver();
	}

	public ExpressionFactory wrapExpressionFactory(ExpressionFactory expressionFactory) {
		return beanManager().wrapExpressionFactory(expressionFactory);
	}

	public <T> AnnotatedType<T> createAnnotatedType(Class<T> type) {
		return beanManager().createAnnotatedType(type);
	}

	public <T> InjectionTarget<T> createInjectionTarget(AnnotatedType<T> type) {
		return beanManager().createInjectionTarget(type);
	}

	public <T> InjectionTargetFactory<T> getInjectionTargetFactory(AnnotatedType<T> annotatedType) {
		return beanManager().getInjectionTargetFactory(annotatedType);
	}

	public <X> ProducerFactory<X> getProducerFactory(AnnotatedField<? super X> field, Bean<X> declaringBean) {
		return beanManager().getProducerFactory(field, declaringBean);
	}

	public <X> ProducerFactory<X> getProducerFactory(AnnotatedMethod<? super X> method, Bean<X> declaringBean) {
		return beanManager().getProducerFactory(method, declaringBean);
	}

	public <T> BeanAttributes<T> createBeanAttributes(AnnotatedType<T> type) {
		return beanManager().createBeanAttributes(type);
	}

	public BeanAttributes<?> createBeanAttributes(AnnotatedMember<?> type) {
		return beanManager().createBeanAttributes(type);
	}

	public <T> Bean<T> createBean(BeanAttributes<T> attributes, Class<T> beanClass,
			InjectionTargetFactory<T> injectionTargetFactory) {
		return beanManager().createBean(attributes, beanClass, injectionTargetFactory);
	}

	public <T, X> Bean<T> createBean(BeanAttributes<T> attributes, Class<X> beanClass,
			ProducerFactory<X> producerFactory) {
		return beanManager().createBean(attributes, beanClass, producerFactory);
	}

	public InjectionPoint createInjectionPoint(AnnotatedField<?> field) {
		return beanManager().createInjectionPoint(field);
	}

	public InjectionPoint createInjectionPoint(AnnotatedParameter<?> parameter) {
		return beanManager().createInjectionPoint(parameter);
	}

	public <T extends Extension> T getExtension(Class<T> extensionClass) {
		return beanManager().getExtension(extensionClass);
	}



}
