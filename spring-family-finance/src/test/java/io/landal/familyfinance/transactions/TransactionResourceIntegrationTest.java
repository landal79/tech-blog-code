package io.landal.familyfinance.transactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import io.landal.familyfinance.user.User;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TransactionResourceIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;


	@Test
	public void test_create() throws Exception {

		Transaction t = new Transaction();
		t.setAmount(new BigDecimal(100.00));
		t.setDate(LocalDate.of(2010, 5, 15));
		t.setDescription("an expense");
		t.setUser(new User(1L));

		ResponseEntity<Void> responseEntity = restTemplate.postForEntity(TransactionResource.BASE_PATH, t, Void.class);
		assertEquals(201, responseEntity.getStatusCodeValue());
		URI location = responseEntity.getHeaders().getLocation();
		assertNotNull(location);

		ResponseEntity<Transaction> getResponse = restTemplate.getForEntity(location.toString(), Transaction.class);
		assertEquals(201, responseEntity.getStatusCodeValue());
		Transaction body = getResponse.getBody();
		assertNotNull(body);
		assertEquals(t.getAmount().intValue(), body.getAmount().intValue());
		assertEquals(t.getDate(), body.getDate());
		assertEquals(t.getDescription(), body.getDescription());

	}


}
