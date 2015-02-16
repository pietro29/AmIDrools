package utility;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public class ResultSetSerializable implements Serializable{
	/**
	 * 
	 */
	private Vector<RuleSetColumnSerializable> columns;
	private static final long serialVersionUID = 1L;
	private int index = -1;
	public ResultSetSerializable(ResultSet rs) {
		columns=new Vector<RuleSetColumnSerializable>();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int i=1;i<=rsmd.getColumnCount();i++){//insert column name
				columns.add(new RuleSetColumnSerializable(new Vector<String>(),rsmd.getColumnName(i).toString(),rsmd.getColumnTypeName(i)));
			}
			
			while(rs.next()){
				for(int i=1;i<=rsmd.getColumnCount();i++){
					System.err.println(rsmd.getColumnName(i).toString());
					System.err.println(rsmd.getColumnTypeName(i));					
					columns.get(i-1).addValues(rs.getObject(i).toString());
					System.err.println(columns.get(i-1));
				}
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean next(){
		index+=1;
		if (index>=columns.get(0).getValues().size())
			return false;
		else
			return true;
	}
	
	public String getString(String columnName){
		try {
			for(int i=0;i<columns.size();i++)
			{
				if (columns.get(i).getColumnName().equals(columnName))
				{
					return columns.get(i).getValues().get(index).toString();
				}
			}
		} catch (Exception e) {
			
		}
		return null;
	}

}
