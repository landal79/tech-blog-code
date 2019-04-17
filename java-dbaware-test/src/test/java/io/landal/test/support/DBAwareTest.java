package io.landal.test.support;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.junit.After;
import org.junit.Before;

public abstract class DBAwareTest {

	private final static List<Integer> ROLLBACK_STATUSES = Arrays.asList(Status.STATUS_MARKED_ROLLBACK, Status.STATUS_ROLLING_BACK, Status.STATUS_ROLLEDBACK);

	@Inject
	protected UserTransaction transaction;

	@Before
	public void startTransaction() throws Exception {
		int status = transaction.getStatus();
		if (status == Status.STATUS_NO_TRANSACTION) {
			transaction.begin();
		}
	}

	@After
	public void stopTransaction() throws Exception {
		int status = transaction.getStatus();
		if (ROLLBACK_STATUSES.contains(status)) {
			transaction.rollback();
		} else if (status != Status.STATUS_NO_TRANSACTION) {
			transaction.commit();
		}

	}

}
