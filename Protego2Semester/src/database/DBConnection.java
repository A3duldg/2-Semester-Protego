package database;

import java.sql.*;

import database.DBConnection;
import database.DataAccessException;

public class DBConnection {
	private Connection connection = null;
	private static DBConnection dbConnection;

	private static final String DBNAME = "Protego_system";
	private static final String SERVERNAME = "localhost";
	private static final int PORTNUMBER = 1433;
	private static final String USERNAME = "sa";
	private static final String PASSWORD = "secret2025*";

	private DBConnection() throws DataAccessException {
		try {
			this.connection = createNewConnection();
		} catch (SQLException e) {
			throw new DataAccessException(String.format("Could not connect to database %s@%s:%d user %s", DBNAME,
					SERVERNAME, PORTNUMBER, USERNAME), e);
		}
	}

	private Connection createNewConnection() throws SQLException {
		String urlString = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=false", SERVERNAME, PORTNUMBER,
				DBNAME);
		return  DriverManager.getConnection(urlString, USERNAME, PASSWORD);
		
	}

	public static synchronized DBConnection getInstance() throws DataAccessException {
		if (dbConnection == null) {
			dbConnection = new DBConnection();
		}
		return dbConnection;
	}

	
	
	public Connection getConnection() throws DataAccessException {
        if (connection == null) {
            try {
				this.connection = createNewConnection();
			} catch (SQLException e) {
				throw new DataAccessException(String.format("Could not connect to database %s@%s:%d user %s", DBNAME,
						SERVERNAME, PORTNUMBER, USERNAME), e);
			}
        }
        return connection;
	}


	public void disconnect() throws DataAccessException {
		if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
				throw new DataAccessException(String.format("Could not close the connection to database %s@%s:%d", DBNAME,
						SERVERNAME, PORTNUMBER), e);
            } finally {
                connection = null;
            }
        }
	}

		
	
	public void startTransaction() throws DataAccessException {
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new DataAccessException("Could not start transaction.", e);
		}
	}

	public void commitTransaction() throws DataAccessException {
		try {
			try {
				connection.commit();
			} catch (SQLException e) {
				throw e;
				// e.printStackTrace();
			} finally {
				connection.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new DataAccessException("Could not commit transaction", e);
		}
	}

	public void rollbackTransaction() throws DataAccessException {
		try {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw e;
				// e.printStackTrace();
			} finally {
				connection.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new DataAccessException("Could not rollback transaction", e);
		}
	}

	public int executeInsertWithIdentity(String sql) throws DataAccessException {
		int res = -1;
		try (Statement s = connection.createStatement()) {
			res = s.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (res > 0) {
				ResultSet rs = s.getGeneratedKeys();
				rs.next();
				res = rs.getInt(1);
			}
			// s.close(); -- the try block does this for us now

		} catch (SQLException e) {
			// e.printStackTrace();
			throw new DataAccessException("Could not execute insert (" + sql + ").", e);
		}
		return res;
	}

	public int executeInsertWithIdentity(PreparedStatement ps) throws DataAccessException {
		// requires prepared statement to be created with the additional argument PreparedStatement.RETURN_GENERATED_KEYS  
		int res = -1;
		try {
			res = ps.executeUpdate();
			if (res > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				rs.next();
				res = rs.getInt(1);
			}
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new DataAccessException("Could not execute insert", e);
		}
		return res;
	}


	
}
