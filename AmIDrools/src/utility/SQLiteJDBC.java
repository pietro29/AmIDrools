package utility;

import java.sql.*;

import javax.swing.JOptionPane;
/**
 * SQLite management class
 * @author 
 *
 */
public final class SQLiteJDBC {
	/**
	 * empty constructor
	 */
	private SQLiteJDBC() {
		
	}
	/**
	 * Get the connection with the local SQLite db
	 * @return Connection object
	 */
	public static Connection getConnection(int db){
		Connection c=null;
		try {
		      Class.forName("org.sqlite.JDBC");
		      if (db==0)
		    	  c = DriverManager.getConnection("jdbc:sqlite:amidroolsManager.db");
		      else if (db==1)
		    	  c = DriverManager.getConnection("jdbc:sqlite:amidroolsIs.db");
		    } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		    }
		    System.out.println("Opened database successfully");
		    return c;
	}

	/**
	 * Run a SQL command which does not return a recordset:
	 * CREATE/UPDATE/DELETE/DROP/etc.
	 * @param sql
	 * @return true when query is executed
	 */
	public static boolean executeUpdate(String sql, int db){
		Statement stmt = null;
		Connection conn = SQLiteJDBC.getConnection(db);
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
		} finally{
			try {
				stmt.close();
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
	/**
	 * Run a SQL command which return last inserted id; INSERT statement
	 * @param sql
	 * @return last id of the inserted row
	 */
	public static int executeUpdate_Insert(String sql, int db){
		Statement stmt = null;
		Connection conn = SQLiteJDBC.getConnection(db);
		//ResultSet rs=null;
		int id=0;
		try {
			
			stmt = conn.createStatement();
			/*stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();
		    if (rs.next()){
		    	id=rs.getInt(1);
		    }*/
			//conn.setAutoCommit(false);
			System.err.println(sql);
			stmt.executeUpdate(sql);
			//conn.commit();
			System.err.println(sql);
		    stmt.close();
		    conn.close();
		    System.out.println("Query executed");
		    JOptionPane.showMessageDialog(null, "Rule saved!");
		    return id;
		} catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      return id;
		}  finally{
			try {
				stmt.close();
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
	/**
	 * Run a SQL command which return a dataset; SELECT statement
	 * @param statement
	 * @return ResultSet object
	 */
	public static ResultSet retrieveData(String statement, int db){
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
				conn = SQLiteJDBC.getConnection(db);
				System.out.println("Connected to database");
			} catch (Exception e) {
				System.out.println("ERROR: Could not connect to the database");
				e.printStackTrace();
			}
		try{
			stmt = conn.createStatement();
		    rs = stmt.executeQuery(statement);
		    //conn.close();
		    return rs;
		} catch (Exception e) {
			System.out.println("ERROR: Could not executed query");
			e.printStackTrace();
		} finally{
			try {
				//stmt.close();
				//conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return null;
	}
}

