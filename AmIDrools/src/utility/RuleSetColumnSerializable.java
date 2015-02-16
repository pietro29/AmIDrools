package utility;

import java.io.Serializable;
import java.util.Vector;

public class RuleSetColumnSerializable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<String> values;
	private String columnName;
	private String columnType;
	
	public RuleSetColumnSerializable(Vector<String> values,String columnName,String columnType) {
		this.values=values;
		this.columnName=columnName;
		this.columnType=columnType;
	}
	
	public Vector<String> getValues(){
		return values;
	}
	
	public void addValues(String value){
		values.add(value);
	}
	
	public String getColumnName(){
		return columnName;
	}
	
	public String getColumnType(){
		return columnType;
	}

}
