package utility;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class rulesSQL {

	public static String RulesInsert(String name, Integer id_user, Integer no_loop, Integer saliance, Integer _public){
		String SQL=new String("");
		SQL+="insert into rules ";
		SQL+="(name,id_user,no_loop,saliance,public) ";
		SQL+="values ";
		SQL+="(\""+name+"\","+id_user+","+no_loop+","+saliance+","+_public+");";
		//SQLiteJDBC.executeUpdate(SQL,1);
		SQL+="insert into rulesif ";
		SQL+="(id_rule)";
		SQL+="values";
		SQL+="((select max(id_rule) from rules));";
		//SQLiteJDBC.executeUpdate(SQL,1);
		SQL+="insert into rulesthen ";
		SQL+="(id_rule)";
		SQL+="values";
		SQL+="((select max(id_rule) from rules));";
		//SQLiteJDBC.executeUpdate(SQL,1);
		return SQL;
	}
	public static String RulesIfFactsInsert(int id_model, String var_name){
		String SQL=new String("");
		SQL+="insert into rulesiffacts";
		SQL+="(id_ruleif,id_model,var_name)";
		SQL+="values";
		SQL+="((select max(id_ruleif) from rulesif),"+id_model+",\""+var_name+"\");";
		
		//SQLiteJDBC.executeUpdate(SQL,1);
		return SQL;
	}
	
	public static String RulesThenFactsInsert(int id_model, String var_name){
		String SQL=new String("");
		SQL+="insert into rulesthenfacts";
		SQL+="(id_rulethen,id_model,var_name)";
		SQL+="values";
		SQL+="((select max(id_rulethen) from rulesthen),"+id_model+",\""+var_name+"\");";
		//SQLiteJDBC.executeUpdate(SQL,1);
		return SQL;
	}
	public static String RulesIfFactsDetailsInsert(int id_attribute, String operation, String value){
		String SQL=new String("");
		SQL+="insert into rulesiffactsdetails";
		SQL+="(id_ruleiffact,id_attribute,operation,value)";
		SQL+="values";
		SQL+="((select max(id_ruleiffact) from rulesiffacts),"+id_attribute+",\""+operation+"\",\""+value+"\");";
		//SQLiteJDBC.executeUpdate(SQL,1);
		return SQL;
	}
	
	public static String RulesThenFactsDetailsInsert(int id_attribute, String operation, String value){
		String SQL=new String("");
		SQL+="insert into rulesthenfactsdetails";
		SQL+="(id_rulethenfact,id_attribute,operation,value)";
		SQL+="values";
		SQL+="((select max(id_rulethenfact) from rulesthenfacts),"+id_attribute+",\""+operation+"\",\""+value+"\");";
		//SQLiteJDBC.executeUpdate(SQL,1);
		return SQL;
	}
	
	public static void fireSQLInsertPrivateRule(String SQL){
		System.err.println(SQL);
		SQLiteJDBC.executeUpdate(SQL,1);
	}
	
	public static ResultSet getModels()
	{
		String SQL = new String("");
		SQL+="select id_model, des_model, id_user from models";
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	public static ResultSet getModel(Integer id_model)
	{
		String SQL = new String("");
		SQL+="select id_model, des_model, id_user from models where id_model=" + id_model;
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
	
	public static ResultSet getRulesConditions(Integer id_rule)
	{
		String SQL = new String("");
		SQL+="select R.name,M.des_model,RIF.var_name,A.des_attribute,RIFD.operation,RIFD.value "
				+ "from rules R "
				+ "left join rulesif RI on RI.id_rule=R.id_rule "
				+ "left join rulesiffacts RIF on RIF.id_ruleif=RI.id_ruleif "
				+ "left join rulesiffactsdetails RIFD on RIFD.id_ruleiffact=RIF.id_ruleiffact "
				+ "left join models M on M.id_model=RIF.id_model "
				+ "left join attributes A on A.id_attribute=RIFD.id_attribute ";
		if (id_rule>0) SQL+="where R.id_rule="+id_rule;
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	public static ResultSet getRulesActions(Integer id_rule)
	{
		String SQL = new String("");
		SQL+="select R.name,M.des_model,RTF.var_name,A.des_attribute,RTFD.operation,RTFD.value"
				+ "from rules R "
				+ "left join rulesthen RT on RT.id_rule=R.id_rule "
				+ "left join rulesthenfacts RTF on RTF.id_rulethen=RT.id_rulethen "
				+ "left join rulesthenfactsdetails RTFD on RTFD.id_rulethenfact=RTF.id_rulethenfact "
				+ "left join models M on M.id_model=RTF.id_model "
				+ "left join attributes A on A.id_attribute=RTFD.id_attribute ";
		if (id_rule>0) SQL+="where R.id_rule="+id_rule;
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	public static void deleteRules(String ruleName)
	{
		String SQL = new String("");
		SQL+="delete from rules where name=\""+ruleName+"\"";
		SQLiteJDBC.executeUpdate(SQL,1);
	}
	
	public static ResultSet getRules()
	{
		String SQL = new String("");
		SQL+="select id_rule, name from rules";
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	public static ResultSet getRulesConditionsFacts(Integer id_rule)
	{
		String SQL = new String("");
		SQL+="select M.des_model,RIF.var_name,RIF.id_ruleiffact "
				+ "from rules R "
				+ "left join rulesif RI on RI.id_rule=R.id_rule "
				+ "left join rulesiffacts RIF on RIF.id_ruleif=RI.id_ruleif "
				+ "left join models M on M.id_model=RIF.id_model "
				+ "where R.id_rule="+id_rule;
		System.err.println(SQL);
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	public static ResultSet getRulesConditionsFactsDetails(Integer id_ruleiffact)
	{
		String SQL = new String("");
		SQL+="select A.des_attribute,RIFD.operation,RIFD.value "
				+ "from rulesiffacts RIF "
				+ "left join rulesiffactsdetails RIFD on RIFD.id_ruleiffact=RIF.id_ruleiffact "
				+ "left join attributes A on A.id_attribute=RIFD.id_attribute "
				+ "where RIF.id_ruleiffact="+id_ruleiffact;
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	public static ResultSet getRulesActionsFacts(Integer id_rule)
	{
		String SQL = new String("");
		SQL+="select R.name,M.des_model,RTF.var_name,RTF.id_rulethenfact "
				+ "from rules R "
				+ "left join rulesthen RT on RT.id_rule=R.id_rule "
				+ "left join rulesthenfacts RTF on RTF.id_rulethen=RT.id_rulethen "
				+ "left join models M on M.id_model=RTF.id_model "
				+ "where R.id_rule="+id_rule;
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	public static ResultSet getRulesActionsFactsDetails(Integer id_rulethenfact)
	{
		String SQL = new String("");
		SQL+="select A.des_attribute,RTFD.operation,RTFD.value "
				+ "from rulesthenfacts RTF "
				+ "left join rulesthenfactsdetails RTFD on RTFD.id_rulethenfact=RTF.id_rulethenfact "
				+ "left join attributes A on A.id_attribute=RTFD.id_attribute "
				+ "where RTF.id_rulethenfact="+id_rulethenfact;
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
}
