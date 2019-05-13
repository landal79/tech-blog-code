package io.landal.familyfinance.transactions;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;

import io.landal.familyfinance.user.UserRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransactionRepositoryTest {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	public void test_insert() {

		Transaction t = new Transaction();
		t.setDescription("desc");
		t.setDate(LocalDate.now());
		t.setAmount(new BigDecimal(10));
		t.setUser(userRepository.findByUsername("user"));
		Transaction saved = transactionRepository.save(t);
		assertThat(saved).isNotNull();
		assertThat(saved.getId()).isNotNull();

		TestTransaction.flagForCommit();
		TestTransaction.end();
		TestTransaction.start();

		Optional<Transaction> fromDb = transactionRepository.findById(saved.getId());
		assertThat(fromDb.isPresent()).isTrue();

		TestTransaction.end();

	}


	@Test
	public void test_findByDescriptionLike() {

		transactionRepository.deleteAll();

		TestTransaction.flagForCommit();
		TestTransaction.end();
		TestTransaction.start();

		Transaction t = new Transaction();
		t.setDescription("desc");
		t.setDate(LocalDate.now());
		t.setAmount(new BigDecimal(10));
		t.setUser(userRepository.findByUsername("user"));
		transactionRepository.save(t);

		TestTransaction.flagForCommit();
		TestTransaction.end();
		TestTransaction.start();

		List<Transaction> result = transactionRepository.findByDescriptionLike("desc");

		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
		Transaction transaction = result.get(0);
		assertThat(transaction).isEqualTo(t);

		TestTransaction.end();

	}

}
