package io.landal.test.model;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CdiTestRunner.class)
public class PersistenceWithContextTest2 {

	@Inject
	protected UserTransaction transaction;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	public void test_write() throws Exception {

		this.transaction.begin();

		Person person = new Person().setName("john").setSurname("doe");
		this.entityManager.persist(person);

		person = new Person().setName("kevin").setSurname("chung");
		this.entityManager.persist(person);

		person = new Person().setName("molly").setSurname("sanders");
		this.entityManager.persist(person);

		this.transaction.commit();
		this.transaction.begin();

		Person fromDb = this.entityManager.find(Person.class, person.getId());
		assertNotNull(fromDb);
		assertNotNull(fromDb.getId());

//		this.transaction.commit();

	}

}
