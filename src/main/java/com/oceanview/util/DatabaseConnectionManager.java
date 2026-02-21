package com.oceanview.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread-safe Singleton Database Connection Manager
 * Implements connection pooling and resource management
 * Follows Singleton Design Pattern with lazy initialization
 */
public class DatabaseConnectionManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnectionManager.class.getName());

    private static volatile DatabaseConnectionManager instance;

    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClass;

    private final ConcurrentHashMap<Connection, Boolean> connectionPool;
    private static final int MAX_POOL_SIZE = 10;

    /**
     * Private constructor to prevent instantiation
     * Loads database configuration from properties file
     */
    private DatabaseConnectionManager() {
        this.connectionPool = new ConcurrentHashMap<>();
        loadDatabaseConfiguration();
        initializeDriver();
    }

    /**
     * Double-checked locking for thread-safe singleton
     * 
     * @return Singleton instance of DatabaseConnectionManager
     */
    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionManager.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionManager();
                }
            }
        }
        return instance;
    }

    /**
     * Load database configuration from properties file
     */
    private void loadDatabaseConfiguration() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (input == null) {
                // Default configuration
                this.jdbcUrl = "jdbc:mysql://localhost:3306/oceanview_resort";
                this.username = "root";
                this.password = "";
                this.driverClass = "com.mysql.cj.jdbc.Driver";
                LOGGER.warning("Using default database configuration");
                return;
            }

            props.load(input);
            this.jdbcUrl = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
            this.driverClass = props.getProperty("db.driver");

            LOGGER.info("Database configuration loaded successfully");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading database configuration", e);
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    /**
     * Initialize JDBC driver
     */
    private void initializeDriver() {
        try {
            Class.forName(driverClass);
            LOGGER.info("JDBC Driver loaded: " + driverClass);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "JDBC Driver not found", e);
            throw new RuntimeException("JDBC Driver not found: " + driverClass, e);
        }
    }

    /**
     * Get database connection from pool or create new one
     * 
     * @return Active database connection
     * @throws SQLException if connection cannot be established
     */
    public synchronized Connection getConnection() throws SQLException {
        // Try to find available connection in pool
        for (Connection conn : connectionPool.keySet()) {
            if (Boolean.TRUE.equals(connectionPool.get(conn)) &&
                    !conn.isClosed() && conn.isValid(2)) {
                connectionPool.put(conn, false);
                LOGGER.fine("Reusing pooled connection");
                return conn;
            }
        }

        // Create new connection if pool not full
        if (connectionPool.size() < MAX_POOL_SIZE) {
            Connection newConn = createNewConnection();
            connectionPool.put(newConn, false);
            LOGGER.info("Created new database connection. Pool size: " + connectionPool.size());
            return newConn;
        }

        // Pool exhausted - wait and retry
        LOGGER.warning("Connection pool exhausted. Creating additional connection.");
        return createNewConnection();
    }

    /**
     * Create a new database connection
     * 
     * @return New database connection
     * @throws SQLException if connection fails
     */
    private Connection createNewConnection() throws SQLException {
        try {
            Properties connProps = new Properties();
            connProps.put("user", username);
            connProps.put("password", password);
            connProps.put("useSSL", "false");
            connProps.put("serverTimezone", "UTC");
            connProps.put("allowPublicKeyRetrieval", "true");

            Connection conn = DriverManager.getConnection(jdbcUrl, connProps);
            conn.setAutoCommit(true);

            LOGGER.info("New database connection established");
            return conn;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to create database connection", e);
            throw e;
        }
    }

    /**
     * Release connection back to pool
     * 
     * @param connection Connection to release
     */
    public synchronized void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.setAutoCommit(true);
                    connectionPool.put(connection, true);
                    LOGGER.fine("Connection released back to pool");
                } else {
                    connectionPool.remove(connection);
                    LOGGER.warning("Removed closed connection from pool");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error releasing connection", e);
                connectionPool.remove(connection);
            }
        }
    }

    /**
     * Close specific connection and remove from pool
     * 
     * @param connection Connection to close
     */
    public synchronized void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                connectionPool.remove(connection);
                LOGGER.fine("Connection closed and removed from pool");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection", e);
            }
        }
    }

    /**
     * Close all connections and shutdown pool
     */
    public synchronized void shutdown() {
        LOGGER.info("Shutting down connection pool...");
        for (Connection conn : connectionPool.keySet()) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection during shutdown", e);
            }
        }
        connectionPool.clear();
        LOGGER.info("Connection pool shutdown complete");
    }

    /**
     * Get current pool statistics
     * 
     * @return Pool status information
     */
    public String getPoolStatus() {
        long available = connectionPool.values().stream().filter(b -> b).count();
        return String.format("Pool Status: Total=%d, Available=%d, In-use=%d",
                connectionPool.size(), available, connectionPool.size() - available);
    }

    /**
     * Test database connectivity
     * 
     * @return true if connection successful
     */
    public boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            return conn != null && conn.isValid(5);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Connection test failed", e);
            return false;
        } finally {
            if (conn != null) {
                releaseConnection(conn);
            }
        }
    }
}
