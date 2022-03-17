package org.nwolfhub.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManualDatabase {
    private String url;
    private Connection con;
    private Integer errors;

    /**
     * ManualDatabase is used to simplify usage of jdbc
     * @param ip
     * @param dbName
     * @param username
     * @param password
     * @throws SQLException
     */
    public ManualDatabase(String ip, String dbName, String username, String password) throws SQLException {
        url = "jdbc:postgresql://" + ip + "/" + dbName + "?user=" + username + "&password=" + password;
        errors = 0;
        con = DriverManager.getConnection(url);
    }

    public void reinitDB() throws SQLException {
        con.close();
        con = DriverManager.getConnection(url);
    }

    public boolean makeRequest(String sql) throws SQLException {
        try {
            return con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).execute(sql);
        } catch (SQLException e) {
            errors++;
            if(errors>9) {
                errors = 0;
                this.reinitDB();
            }
            throw e;
        }
    }

    public ResultSet makeResultRequest(String sql) throws SQLException {
        try {
            return con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
        } catch (SQLException e) {
            errors++;
            if(errors>9) {
                errors = 0;
                this.reinitDB();
            }
            throw e;
        }
    }

}
