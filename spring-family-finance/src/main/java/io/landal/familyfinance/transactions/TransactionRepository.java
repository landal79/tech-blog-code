package io.landal.familyfinance.transactions;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {

	List<Transaction> findByDescriptionLike(String description);

}
