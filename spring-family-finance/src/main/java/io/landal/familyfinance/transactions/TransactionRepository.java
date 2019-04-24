package io.landal.familyfinance.transactions;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
	
	List<Transaction> findByDescriptionLike(String description);

}
