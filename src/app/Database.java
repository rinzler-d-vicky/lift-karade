package app;

import java.sql.*;

public class Database {
	final static String URL = "jdbc:oracle:thin:@OSCTrain1DB01.oneshield.com:1521:train1";
	final static String USERNAME = "vvicky";
	final static String PASSWORD = "password";

	private static Connection connection;
	private static boolean connected = false;

	public static Connection getConnection() throws SQLException {
		Database.initialize();
		if(connection==null) throw new SQLException();
		return connection;
	}
	
	private static void initialize() throws SQLException {
		if(!connected){
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				Database.connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
				connected = true;
			}
			catch (ClassNotFoundException e) { e.printStackTrace(); }
		}
	}

	public static ResultSet fetchResultSet(String sql) throws SQLException {
		Database.initialize();
		Statement stmt = Database.connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		rs.next();
		return rs;
	}
}