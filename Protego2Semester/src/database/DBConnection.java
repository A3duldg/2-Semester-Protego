package database;

import java.lang.reflect.*;
import java.sql.*;
import java.util.concurrent.*;

public class DBConnection {
	private static DBConnection instance;

	private final BlockingQueue<Connection> pool;
	private final int poolSize;

	// Connection info
	private static final String DBNAME = "DMA-CSD-V252_10665998";
	private static final String SERVERNAME = "hildur.ucn.dk";
	private static final int PORTNUMBER = 1433;
	private static final String USERNAME = "DMA-CSD-V252_10665998";
	private static final String PASSWORD = "Password1!";

	private DBConnection(int poolSize) throws DataAccessException {
		this.poolSize = poolSize;
		this.pool = new ArrayBlockingQueue<>(poolSize);
		initPool();
	}

	private void initPool() throws DataAccessException {
		try {
			for (int i = 0; i < poolSize; i++) {
				pool.offer(createPhysicalConnection());
			}
			System.out.println("DBConnection: Pool initialized with " + poolSize + " connections");
		} catch (SQLException e) {
			throw new DataAccessException("Failed to initialize connection pool", e);
		}
	}

	private Connection createPhysicalConnection() throws SQLException {
		String urlString = String.format("jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=false", SERVERNAME, PORTNUMBER,
				DBNAME);
		return DriverManager.getConnection(urlString, USERNAME, PASSWORD);
	}

	public static synchronized DBConnection getInstance() throws DataAccessException {
		if (instance == null) {
			instance = new DBConnection(10); // ✅ 10 er mere realistisk end 100
		}
		return instance;
	}

	public Connection getConnection() throws DataAccessException {
		try {
			Connection physical = pool.poll(5, TimeUnit.SECONDS);
			if (physical == null) {
				throw new DataAccessException("Connection pool exhausted (timeout waiting for a connection)",
						new SQLException("Timeout"));
			}

			// makes a new one if the physical is dead
			if (physical.isClosed()) {
				physical = createPhysicalConnection();
			}

			final Connection physicalFinal = physical;

			// Proxy: when something calls close(), then we return it to the pool instead of
			// closing it.
			return (Connection) Proxy.newProxyInstance(DBConnection.class.getClassLoader(),
					new Class<?>[] { Connection.class }, new InvocationHandler() {
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							if ("close".equals(method.getName())) {
								releaseConnection(physicalFinal);
								return null;
							}
							return method.invoke(physicalFinal, args);
						}
					});

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new DataAccessException("Interrupted while waiting for a connection", e);
		} catch (SQLException e) {
			throw new DataAccessException("Error getting connection", e);
		}
	}

	// Return the physical connection to the pool instead of closing it.
	public void releaseConnection(Connection physical) {
		if (physical == null)
			return;
		try {
			if (!physical.isClosed()) {
				// Important: reset state so the next user does not inherit transaction
				// settings.
				physical.setAutoCommit(true);
				pool.offer(physical);
			}
		} catch (SQLException ignore) {
		}
	}

	// Transaction helpers
	public void startTransaction(Connection conn) throws DataAccessException {
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			throw new DataAccessException("Could not start transaction.", e);
		}
	}

	public void commitTransaction(Connection conn) throws DataAccessException {
		try {
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new DataAccessException("Could not commit transaction", e);
		}
	}

	public void rollbackTransaction(Connection conn) throws DataAccessException {
		try {
			conn.rollback();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new DataAccessException("Could not rollback transaction", e);
		}
	}

	// Optional: invoke to shut down the entire pool at program termination.
	public void shutdown() {
		while (!pool.isEmpty()) {
			try {
				Connection c = pool.poll();
				if (c != null)
					c.close();
			} catch (SQLException ignore) {
			}
		}
	}
}
