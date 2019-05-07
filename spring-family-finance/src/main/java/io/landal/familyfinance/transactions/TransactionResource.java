package io.landal.familyfinance.transactions;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(TransactionResource.BASE_PATH)
public class TransactionResource {

	public static final String BASE_PATH = "/api/transactions";

	private TransactionRepository transactionRepository;

	public TransactionResource(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Transaction> findById(@PathVariable Long id) {
		Optional<Transaction> transaction = transactionRepository.findById(id);
		if (!transaction.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.of(transaction);
	}

	@PostMapping
	public ResponseEntity<Void> create(@Valid @RequestBody Transaction transaction) throws URISyntaxException {
		if (transaction.getId() != null) {
			throw new IllegalArgumentException();
		}

		Transaction saved = transactionRepository.save(transaction);

		UriComponents components = UriComponentsBuilder.fromUriString(BASE_PATH + "/{id}")
				.buildAndExpand(Collections.singletonMap("id", saved.getId()));

		return ResponseEntity.created(components.toUri()).build();
	}

	@PutMapping
	public ResponseEntity<Void> update(@Valid @RequestBody Transaction transaction) {
		if (transaction.getId() == null) {
			throw new IllegalArgumentException();
		}

		transactionRepository.save(transaction);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/")
	public ResponseEntity<Iterable<Transaction>> findAll() {
		return new ResponseEntity<>(transactionRepository.findAll(), HttpStatus.OK);
	}

}
