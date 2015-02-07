package utility;

import java.sql.ResultSet;

public final class rulesSQL {

	public static void RulesInsert(String name, int id_user, int no_loop, int saliance, int _public){
		String SQL=new String("");
		SQL+="insert into rules ";
		SQL+="(name,date_creation,id_user,no_loop,saliance,public) ";
		SQL+="values ";
		SQL+="(_name,now(),_id_user,_no_loop,_saliance,_public);";
		SQL+="insert into rulesif ";
		SQL+="(id_rule)";
		SQL+="values";
		SQL+="((select max(id_rule) from rules));";
		SQL+="insert into rulesthen ";
		SQL+="(id_rule)";
		SQL+="values";
		SQL+="((select max(id_rule) from rules));";
		SQLiteJDBC.executeUpdate(SQL,1);
	}
	public static void RulesIfFactsInsert(int id_model, String var_name){
		String SQL=new String("");
		SQL+="insert into rulesiffacts";
		SQL+="(id_ruleif,id_model,var_name)";
		SQL+="values";
		SQL+="((select max(id_ruleif) from rulesif),"+id_model+",\""+var_name+"\");";
		SQLiteJDBC.executeUpdate(SQL,1);
	}
	public static void RulesIfFactsDetailsInsert(int id_template, String operation, String value){
		String SQL=new String("");
		SQL+="insert into rulesiffactsdetails";
		SQL+="(id_ruleiffact,id_template,operation,value)";
		SQL+="values";
		SQL+="((select max(id_ruleiffact) from rulesiffacts),"+id_template+",\""+operation+"\",\""+value+"\");";
		SQLiteJDBC.executeUpdate(SQL,1);
	}
	
	public static ResultSet getModels()
	{
		String SQL = new String("");
		SQL+="select id_model, des_model from models";
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	public static ResultSet getAttributeFromModels(int id_model)
	{
		String SQL = new String("");
		SQL+="select id_attribute, des_attribute, type_attribute from attributes where id_model="+id_model;
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	public static ResultSet getTypeOfAttributes(int id_template)
	{
		String SQL = new String("");
		SQL+="select type_attribute from attributes where id_attribute="+id_template;
		return SQLiteJDBC.retrieveData(SQL,1);
	}
}
