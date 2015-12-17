package at.sync.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Author: nschoch
 * Date: 17.12.15
 * Time: 09:43
 */
public class ConnectionManager {

    //region Singleton
    private static ConnectionManager _instance;

    private ConnectionManager() {

    }

    public static ConnectionManager getInstance() {
        if (_instance == null) {
            _instance = new ConnectionManager();
        }

        return _instance;
    }
    //endregion

    //region JDBC

    //TODO: extract to properties file
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost/fhvgis";

    //  Database credentials
    private static final String USER = "username";
    private static final String PASS = "password";

    private static Connection _connection;

    /**
     * Get JDBC connection
     *
     * @return Connection
     * @throws Exception
     */
    public Connection getConnection() throws Exception {
        if (_connection == null) {
            try {
                Class.forName(JDBC_DRIVER);
                _connection = DriverManager.getConnection(DB_URL, USER, PASS);
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw e;
            }
        }

        return _connection;
    }
    //endregion

}
