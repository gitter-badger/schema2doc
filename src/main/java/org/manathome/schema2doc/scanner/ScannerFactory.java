package org.manathome.schema2doc.scanner;

import org.manathome.schema2doc.scanner.impl.GenericDbScanner;
import org.manathome.schema2doc.scanner.impl.MockScanner;
import org.manathome.schema2doc.scanner.impl.OracleScanner;
import org.manathome.schema2doc.util.Ensure;
import org.manathome.schema2doc.util.LogWriter;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

/**
 * creates a jdbc connection an its suitable scanner.
 * @author man-from-home
 * @since  2015-09-05
 */
public class ScannerFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScannerFactory.class);
	
	private ScannerFactory() {}
	
	/** get new factory, not garantied to be a singleton (at the moment: no). */
	@NotNull public static ScannerFactory getInstance() { 
		return new ScannerFactory(); 
	}

	/** get a connected database metadata scanner. */
	@NotNull public IScanner getScanner(@NotNull String scannerName, String driverClassName, String jdbcUrl, String user, String pw) {
		return getScanner(scannerName, driverClassName, jdbcUrl, user, pw, false);
	}
	
	/** get a connected database metadata scanner. */
	@NotNull public IScanner getScanner(@NotNull String scannerName, String driverClassName, 
			                            String jdbcUrl, String user, String pw,
			                            boolean isVerbose) {

		IScanner scanner = null;
		DataSource dataSource = null;
		
		try {
		
			if (!"Mock".equalsIgnoreCase(Require.notNull(scannerName, "scannerName"))) {
				Class<?> clazz = Class.forName(driverClassName); // load database
																// driver..
				LOG.debug("using jdbc driver: " + clazz.getName());
				LOG.debug("connecting to " + jdbcUrl + 
						  " as " + user + " " + (pw == null ? "without pw" : " with pw"));
				
				if (isVerbose) {
					DriverManager.setLogWriter(
							new LogWriter(LoggerFactory.getLogger("org.manathome.schema2doc.SQLTRACE"))
							);
				}	
				
				dataSource = new HikariDataSource();	// using connection pooling
				((HikariDataSource) dataSource).setJdbcUrl(jdbcUrl);
				((HikariDataSource) dataSource).setUsername(user);
				((HikariDataSource) dataSource).setPassword(pw);
				((HikariDataSource) dataSource).setReadOnly(true);
				((HikariDataSource) dataSource).setMaximumPoolSize(3);
				
				if ("Oracle".equalsIgnoreCase(scannerName)) {
					((HikariDataSource) dataSource).addDataSourceProperty("remarksReporting", "true");
					scanner = new OracleScanner(dataSource); 					
				} else {		
					scanner = new GenericDbScanner(dataSource);					
				}
			} else {
				scanner = new MockScanner();
			}
			
			Ensure.notNull(scanner, "scanner");
			LOG.debug("using scanner: " + scanner.getClass().getName());
			return scanner;
			
		} catch (final Exception ex) {
			LOG.error("could not create scanner: " + ex.getMessage(), ex);

			try {				
				if (dataSource != null) {
					((HikariDataSource) dataSource).close();
				}
			} catch (Exception nestedex) {
				// nothing to do here..
			}			
			try {				
				if (scanner != null) {
					scanner.close();
				}
			} catch (Exception nestedex) {
				// nothing to do here..
			}
			throw new ScannerException(ex.getMessage(), ex);
		}		
	}
	
	
}
