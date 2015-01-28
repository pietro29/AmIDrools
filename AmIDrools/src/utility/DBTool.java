package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.mysql.jdbc.PreparedStatement;

public class DBTool {
	/** The name of the MySQL account to use (or empty for anonymous) */
	private final String userName = "root";

	/** The password for the MySQL account (or empty for anonymous) */
	private final String password = "root";

	/** The name of the computer running MySQL */
	private final String serverName = "127.0.0.1";

	/** The port of the MySQL server (default is 3306) */
	private final int portNumber = 3306;

	/** The name of the database we are testing with (this default is installed with MySQL) */
	private final String dbName = "amidrools";
	

	/**
	 * Get a new database connection
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);

		conn = DriverManager.getConnection("jdbc:mysql://"
				+ this.serverName + ":" + this.portNumber + "/" + this.dbName,
				connectionProps);

		return conn;
	}
	
	/**
	 * Run a SQL command which does not return a recordset:
	 * CREATE/INSERT/UPDATE/DELETE/DROP/etc.
	 * 
	 * @throws SQLException If something goes wrong
	 */
	public boolean executeUpdate(Connection conn, String command) throws SQLException {
	    Statement stmt = null;
	    try {
	        stmt = conn.createStatement();
	        stmt.executeUpdate(command); // This will throw a SQLException if it fails
	        return true;
	    } finally {

	    	// This will run whether we throw an exception or not
	        if (stmt != null) { stmt.close(); }
	    }
	}
	/**
	 * 
	 * @param tablename
	 * @param statement select from tablename
	 * @return a result set 
	 */
	public ResultSet retrieveData(String tablename, String statement){
		// Connect to MySQL
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
				conn = this.getConnection();
				System.out.println("Connected to database");
			} catch (SQLException e) {
				System.out.println("ERROR: Could not connect to the database");
				e.printStackTrace();
			}
		try {
			pst=(PreparedStatement) conn.prepareStatement(statement);
			rs=pst.executeQuery();
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public DBTool() {
		// TODO Auto-generated constructor stub
	}

}
