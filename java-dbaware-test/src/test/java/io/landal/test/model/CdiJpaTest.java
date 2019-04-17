package io.landal.test.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.TransactionalException;
import javax.transaction.UserTransaction;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.landal.test.support.DBAwareTest;

@RunWith(CdiTestRunner.class)
public class CdiJpaTest extends DBAwareTest {

	@Inject
	private EntityManager entityManager;

	@Inject
	private UserTransaction transaction;

	@Inject
	private ObserverTestBean observerTestBean;

	@Inject
	private TestService testService;

	@Inject
	private TransactionalTestService transactionalTestService;

	@Before
	public void before() throws Exception {
		assertThat(entityManager).isNotNull();
		entityManager.createQuery("delete from TestEntity te").executeUpdate();

		transaction.commit();
		transaction.begin();
	}

	@Test
	public void canInjectEntityManager() throws Exception {

		TestEntity te = new TestEntity();
		te.id = UUID.randomUUID();
		te.name = "Test 1";
		entityManager.persist(te);

		te = new TestEntity();
		te.id = UUID.randomUUID();
		te.name = "Test 2";
		entityManager.persist(te);

		transaction.commit();
		transaction.begin();

		List<TestEntity> loaded = entityManager.createQuery("FROM TestEntity te", TestEntity.class).getResultList();
		assertThat(loaded).hasSize(2);
	}

	@Test
	public void canInjectUserTransaction() throws Exception {

		TestEntity te = new TestEntity();
		te.id = UUID.randomUUID();
		te.name = "Test 1";
		entityManager.persist(te);

		te = new TestEntity();
		te.id = UUID.randomUUID();
		te.name = "Test 2";
		entityManager.persist(te);

		transaction.commit();
		transaction.begin();

		List<TestEntity> loaded = entityManager.createQuery("FROM TestEntity te", TestEntity.class).getResultList();
		assertThat(loaded).hasSize(2);
		transaction.commit();
	}

	@Test
	public void shouldProcessTransactionalObservers() throws Exception {
		observerTestBean.work();

		transaction.commit();

		assertThat(observerTestBean.getResult()).isEqualTo("321");
	}

	@Test
	public void canUseDiInEntityListener() throws Exception {

		TestEntity te = new TestEntity();
		te.id = UUID.randomUUID();
		te.name = "Test 1";
		entityManager.persist(te);

		te = new TestEntity();
		te.id = UUID.randomUUID();
		te.name = "Test 2";
		entityManager.persist(te);

		transaction.commit();

		assertThat(testService.getTestEntityNames()).contains("Test 1", "Test 2");
	}

	@Test
	public void canUseDeclarativeTxControl() throws Exception {

		transaction.commit();

		try {
			transactionalTestService.doSomething();
			fail("Exception raised due to missing yet required transaction wasn't raised");
		} catch (TransactionalException e) {
			assertThat(e.getMessage().contains("ARJUNA016110"));
		}

		transaction.begin();
		assertThat(transactionalTestService.doSomething()).isEqualTo("Success");
		transaction.rollback();
	}
}
