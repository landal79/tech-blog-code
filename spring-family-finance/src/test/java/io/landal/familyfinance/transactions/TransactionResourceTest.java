package io.landal.familyfinance.transactions;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.landal.familyfinance.user.User;

/**
 * Rest controller test without dependencies,
 * the dependecies are mocked.
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
public class TransactionResourceTest {

	private MockMvc mvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private TransactionRepository transactionRepository;

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void test_get() throws Exception {

		Transaction t = new Transaction();
		t.setId(1L);
		t.setAmount(new BigDecimal(100.00));
		t.setDate(LocalDate.of(2010, 5, 15));
		t.setDescription("an expense");
		t.setUser(new User(1L));

		given(transactionRepository.findById(anyLong())).willReturn(Optional.<Transaction>of(t));

		mvc.perform(MockMvcRequestBuilders.get(TransactionResource.BASE_PATH + "/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(t.getId().intValue())))
				.andExpect(jsonPath("$.description", is(t.getDescription())))
				.andExpect(jsonPath("$.date", is(t.getDate().toString())))
				.andExpect(jsonPath("$.amount", is(t.getAmount().intValue())));

	}

	@Test
	public void test_create() throws Exception {

		Transaction t = new Transaction();
		t.setId(1L);
		t.setAmount(new BigDecimal(100.00));
		t.setDate(LocalDate.of(2010, 5, 15));
		t.setDescription("an expense");

		given(transactionRepository.save(any())).willReturn(t);

		String inputJson = "{\"date\":\"2019-04-20T00:00:00.000Z\",\"description\":\"deeeeeee\",\"amount\": \"100.00\"}";

		mvc.perform(MockMvcRequestBuilders.post(TransactionResource.BASE_PATH)
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andExpect(status().isCreated())
				.andExpect(header().string("Location", TransactionResource.BASE_PATH + "/" + 1)).andReturn();

	}

}
