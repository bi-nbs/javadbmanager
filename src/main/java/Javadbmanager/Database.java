package Javadbmanager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;

 abstract class Database {

    private static Logger logger = LogManager.getLogger();

    private String hostname;
    private String port;
    private String username;
    private String password;
    private String database;

    public Database() {
    }

    public Database(String hostname, String port, String username, String password, String database) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    private Connection makeConnection() throws SQLException {
        String connectionString = "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database +
                "?" + "useSSL=false" + "&serverTimezone=Europe/Copenhagen";
        //logger.debug("Connection to database with following JDBC string: " + connectionString);
        return DriverManager.getConnection(connectionString, this.username, this.password);
    }

    protected ResultSet executeQuery(String queryString){
        //logger.debug("Executing following string: " + queryString);
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            conn = this.makeConnection();
            statement = conn.prepareStatement(queryString, ResultSet.TYPE_FORWARD_ONLY , ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery();

            // if (!conn.isClosed()) conn.close();

        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return resultSet;
    }

    protected ResultSet execute(String queryString){
        //logger.debug("Executing following string: " + queryString);
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            conn = this.makeConnection();
            statement = conn.prepareStatement(queryString, ResultSet.TYPE_FORWARD_ONLY , ResultSet.CONCUR_UPDATABLE);
            if (statement.execute()){
                resultSet = statement.getResultSet();
            }

            // if (!conn.isClosed()) conn.close();

        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return resultSet;
    }

    protected void executeMultipleQueries(List<String> queries){
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = this.makeConnection();

            for (String query: queries) {
                if (!query.isEmpty()){
                    //logger.debug("Executing following string: " + query);
                    statement = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY , ResultSet.CONCUR_UPDATABLE);
                    statement.execute();
                   // logger.info("QUERY: " + query);
                }
            }


            if (!conn.isClosed()) conn.close();

        } catch (SQLException e) {
            logger.error(e.getMessage());
        }



    }

    protected void executeUpdate(String queryString){
        //logger.debug("Executing following string: " + queryString);
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = this.makeConnection();
            statement = conn.prepareStatement(queryString);
            statement.executeUpdate();

            // if (!conn.isClosed()) conn.close();

        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    protected void logResultSet(ResultSet resultSet){
            try {
                while (resultSet.next()){
                    logger.info(resultSet.getString(1));
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }

    }

    @Override
    public String toString() {
        return "Javadbmanager.Database{" +
                "hostname='" + hostname + '\'' +
                ", port='" + port + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", database='" + database + '\'' +
                '}';
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
