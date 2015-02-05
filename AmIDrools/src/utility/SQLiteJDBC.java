package utility;

import java.sql.*;

public final class SQLiteJDBC {
	
	private SQLiteJDBC() {
		
	}
	
	public static Connection getConnection(){
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

	
	public static boolean executeUpdate(String sql){
		Statement stmt = null;
		Connection conn = SQLiteJDBC.getConnection();
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
	public static int executeUpdate_Insert(String sql){
		Statement stmt = null;
		Connection conn = SQLiteJDBC.getConnection();
		ResultSet rs=null;
		int id=0;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();
		    if (rs.next()){
		    	id=rs.getInt(1);
		    }
		    stmt.close();
		    conn.close();
		    System.out.println("Query executed");
		    return id;
		} catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      return id;
		} 
	}
	public static ResultSet retrieveData(String statement){
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
				conn = SQLiteJDBC.getConnection();
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

