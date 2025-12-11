package database;

import java.sql.*;
import java.util.concurrent.*;

public class DBConnection {
    private static DBConnection instance;
    private final BlockingQueue<Connection> connectionPool;
    private static final int POOL_SIZE = 100;
    
    // Connection info
    private static final String DBNAME = "DMA-CSD-V252_10666000";
    private static final String SERVERNAME = "hildur.ucn.dk";
    private static final int PORTNUMBER = 1433;
    private static final String USERNAME = "DMA-CSD-V252_10666000";
    private static final String PASSWORD = "Password1!";
    
    private DBConnection() throws DataAccessException {
        connectionPool = new LinkedBlockingQueue<>(POOL_SIZE);
        initializePool();
    }
    
    private void initializePool() throws DataAccessException {
        try {
            for (int i = 0; i < POOL_SIZE; i++) {
                Connection conn = createNewConnection();
                connectionPool.offer(conn);
            }
            System.out.println("Connection pool initialized with " + POOL_SIZE + " connections");
        } catch (SQLException e) {
            throw new DataAccessException("Failed to initialize pool", e);
        }
    }
    
    private Connection createNewConnection() throws SQLException {
        String urlString = String.format(
            "jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=false",
            SERVERNAME, PORTNUMBER, DBNAME
        );
        return DriverManager.getConnection(urlString, USERNAME, PASSWORD);
    }
    
    public static synchronized DBConnection getInstance() throws DataAccessException {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
    
    public Connection getConnection() throws DataAccessException {
        try {
            Connection conn = connectionPool.poll(5, TimeUnit.SECONDS);
            
            if (conn == null) {
                throw new DataAccessException(
                    "Connection pool exhausted - no connections available",
                    new SQLException("Timeout waiting for connection")
                );
            }
            
            if (conn.isClosed()) {
                conn = createNewConnection();
            }
            
            return conn;
            
        } catch (SQLException | InterruptedException e) {
            throw new DataAccessException("Error getting connection", e);
        }
    }
    
    public void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    connectionPool.offer(conn);
                } else {
                    System.err.println("Warning: Attempted to release closed connection");
                }
            } catch (SQLException e) {
                System.err.println("Error checking connection: " + e.getMessage());
            }
        }
    }

		
	
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

	public int executeInsertWithIdentity(String sql) throws DataAccessException {
		int res = -1;
		try (Statement s = ((Connection) instance).createStatement()) {
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
