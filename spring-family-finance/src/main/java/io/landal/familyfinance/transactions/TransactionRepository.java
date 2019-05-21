package io.landal.familyfinance.transactions;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByDescriptionLike(String description);

}
