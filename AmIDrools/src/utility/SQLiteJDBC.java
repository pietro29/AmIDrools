package utility;

import java.io.ObjectInputStream.GetField;
import java.sql.*;

public class SQLiteJDBC {
	
	public SQLiteJDBC() {
		
	}
	
	public Connection getConnection(){
		Connection c=null;
		try {
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:amidrools.db");
		    } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		    }
		    System.out.println("Opened database successfully");
		    return c;
	}

	
	public boolean executeUpdate(String sql){
		Statement stmt = null;
		Connection conn = this.getConnection();
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		    stmt.close();
		    conn.close();
		    System.out.println("Query executed");
		    return true;
		} catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      return false;
		} 
	}
	public ResultSet retrieveData(String statement){
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
				conn = this.getConnection();
				System.out.println("Connected to database");
			} catch (Exception e) {
				System.out.println("ERROR: Could not connect to the database");
				e.printStackTrace();
			}
		try{
			stmt = conn.createStatement();
		    rs = stmt.executeQuery(statement);
		    return rs;
		} catch (Exception e) {
			System.out.println("ERROR: Could not executed query");
			e.printStackTrace();
		}
		return null;
	}
}

