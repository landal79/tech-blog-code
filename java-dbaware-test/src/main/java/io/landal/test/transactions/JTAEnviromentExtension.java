package io.landal.test.transactions;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.h2.jdbcx.JdbcDataSource;
import org.jnp.server.NamingBeanImpl;

import com.arjuna.ats.jta.utils.JNDIManager;

/**
 * CDI extension to boot and stop JNDI server, and bind JTA transaction manager
 * and database connection to it.
 *
 */
public class JTAEnviromentExtension implements Extension {

	private NamingBeanImpl NAMING_BEAN;

	void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) throws Throwable {
		NAMING_BEAN = new NamingBeanImpl();
		NAMING_BEAN.start();

		JNDIManager.bindJTAImplementation();
		H2DataSource.bindDataSource();
	}

	public void beforeShutdown(@Observes BeforeShutdown adv, BeanManager bm) {
		NAMING_BEAN.stop();
	}

}

/**
 * Datasource.
 *
 */
class H2DataSource {

	private static final String DATASOURCE_JNDI = "java:testDS";
	private static final String USERNAME = "sa";
	private static final String PASSWORD = "";

	public static void bindDataSource() {

		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL("jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1");
		dataSource.setUser(USERNAME);
		dataSource.setPassword(PASSWORD);

		try {
			InitialContext initialContext = new InitialContext();
			initialContext.bind(DATASOURCE_JNDI, dataSource);
			initialContext.close();
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

}
