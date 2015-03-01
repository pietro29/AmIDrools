package utility;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class that contain SQL command to manage the rules
 * @author 
 *
 */
public final class rulesSQLIS {

	/**
	 * Insert new Rule header
	 * @param name rule name
	 * @param id_user id of the user owner
	 * @param no_loop optional no loop attribute
	 * @param salience optional salience 
	 * @param _public public or not rule
	 * @return String that represent the insert of the rule header
	 */
	public static String RulesInsert(String name, Integer id_user, boolean no_loop, Integer salience, boolean _public){
		Integer no_loopInt = 0;
		Integer _publicInt = 0;
		if(no_loop) no_loopInt=1;
		if(_public) _publicInt=1;
		String SQL=new String("");
		SQL+="insert into rules ";
		SQL+="(name,id_user,no_loop,salience,public) ";
		SQL+="values ";
		SQL+="(\""+name+"\","+id_user+","+no_loopInt+","+salience+","+_publicInt+");";
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
	/**
	 * insert condition fact
	 * @param id_model id of the model associated to the condition
	 * @param var_name name for the variable associated to the condition
	 * @return string represented the SQL command
	 */
	public static String RulesIfFactsInsert(int id_model, String var_name){
		String SQL=new String("");
		SQL+="insert into rulesiffacts";
		SQL+="(id_ruleif,id_model,var_name)";
		SQL+="values";
		SQL+="((select max(id_ruleif) from rulesif),"+id_model+",\""+var_name+"\");";
		
		//SQLiteJDBC.executeUpdate(SQL,1);
		return SQL;
	}
	
	/**
	 * insert action fact
	 * @param id_model id of the model associated to the action
	 * @param var_name name for the variable associated to the action
	 * @return string represented the SQL command
	 */
	public static String RulesThenFactsInsert(int id_model, String var_name){
		String SQL=new String("");
		SQL+="insert into rulesthenfacts";
		SQL+="(id_rulethen,id_model,var_name)";
		SQL+="values";
		SQL+="((select max(id_rulethen) from rulesthen),"+id_model+",\""+var_name+"\");";
		//SQLiteJDBC.executeUpdate(SQL,1);
		return SQL;
	}
	/**
	 * insert condition on the attribute
	 * @param id_attribute id of the attribute associated to the condition
	 * @param operation operation of the condition
	 * @param value value of the condition
	 * @return string represented the SQL command
	 */
	public static String RulesIfFactsDetailsInsert(int id_attribute, String operation, String value){
		String SQL=new String("");
		SQL+="insert into rulesiffactsdetails";
		SQL+="(id_ruleiffact,id_attribute,operation,value)";
		SQL+="values";
		SQL+="((select max(id_ruleiffact) from rulesiffacts),"+id_attribute+",\""+operation+"\",\""+value+"\");";
		//SQLiteJDBC.executeUpdate(SQL,1);
		return SQL;
	}
	/**
	 * insert action on the attribute
	 * @param id_attribute id of the attribute associated to the action
	 * @param operation operation of the action
	 * @param value value of the action
	 * @return string represented the SQL command
	 */
	public static String RulesThenFactsDetailsInsert(int id_attribute, String operation, String value){
		String SQL=new String("");
		SQL+="insert into rulesthenfactsdetails";
		SQL+="(id_rulethenfact,id_attribute,operation,value)";
		SQL+="values";
		SQL+="((select max(id_rulethenfact) from rulesthenfacts),"+id_attribute+",\""+operation+"\",\""+value+"\");";
		//SQLiteJDBC.executeUpdate(SQL,1);
		return SQL;
	}
	
	/**
	 * fire a SQL command
	 * @param SQL contains all the SQL command to fire
	 */
	public static void fireSQLInsertPrivateRule(String SQL){
		System.err.println(SQL);
		SQLiteJDBC.executeUpdate(SQL,1);
	}
	
	/**
	 * @return all the private models
	 */
	public static ResultSet getModels()
	{
		String SQL = new String("");
		SQL+="select id_model, des_model, id_user from models";
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	/**
	 * @return all the private models usable for the condition
	 */
	public static ResultSet getModelsIF()
	{
		String SQL = new String("");
		SQL+="select id_model, des_model, id_user from models where if_model=1";
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	/**
	 * @return all the private models usable for the action
	 */
	public static ResultSet getModelsTHEN()
	{
		String SQL = new String("");
		SQL+="select id_model, des_model, id_user from models where then_model=1";
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	/**
	 * @param id_model id of the model to search
	 * @return a specific model
	 */
	public static ResultSet getModel(Integer id_model)
	{
		String SQL = new String("");
		SQL+="select id_model, des_model, id_user, if_model, then_model from models where id_model=" + id_model;
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	/**
	 * @param id_model id of the model 
	 * @return all the attribute associated to a specific model
	 */
	public static ResultSet getAttributeFromModels(Integer id_model)
	{
		String SQL = new String("");
		SQL+="select id_attribute, des_attribute, type_attribute from attributes where id_model="+id_model;
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	/**
	 * @param id_attribute id of the attribute
	 * @return type of the specific attribute
	 */
	public static ResultSet getTypeOfAttributes(int id_attribute)
	{
		String SQL = new String("");
		SQL+="select type_attribute from attributes where id_attribute="+id_attribute;
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	/**
	 * @param id_rule id of the specific rule
	 * @return all the conditions for a specific rule
	 */
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
	
	/**
	 * @param id_rule id of the specific rule
	 * @return all the actions for a specific rule
	 */
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
	
	/**
	 * delete a specific rule
	 * @param ruleName name of the rule
	 */
	public static void deleteRules(String ruleName)
	{
		String SQL = new String("");
		SQL+="delete from rules where name=\""+ruleName+"\"";
		SQLiteJDBC.executeUpdate(SQL,1);
	}
	
	/**
	 * @return all the private rules
	 */
	public static ResultSet getRules()
	{
		String SQL = new String("");
		SQL+="select id_rule, name, no_loop, salience from rules";
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	/**
	 * @param id_rule id of a specific rule
	 * @return all the conditions fact for a specific rule
	 */
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
	
	/**
	 * @param id_ruleiffact id of a fact of a rule
	 * @return all the conditions details for a specific rule fact
	 */
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
	
	/**
	  * @param id_rule id of a specific rule
	 * @return all the actions fact for a specific rule
	 */
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
	
	/**
	 * @param id_rulethenfact id of a fact of a rule
	 * @return all the actions details for a specific rule fact
	 */
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
	
	/**
	 * get all the instances of private fact
	 * @return the ruleSet that contains the models
	 */
	public static ResultSet getModelsInsatnces()
	{
		String SQL = new String("");
		SQL+="select mi.id_modelinstance " +
				",mi.des_modelinstance " +
				", m.des_model " +
				"from modelsinstances mi " +
				"join models m on m.id_model=mi.id_model;";
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
	/**
	 * get all the attribute instances of private fact
	 * @param id_modelinstance id of the model instances
	 * @return the ruleSet that contains the attribute's model
	 */
	public static ResultSet getModelsAttributesInstances(Integer id_modelinstance)
	{
		String SQL = new String("");
		SQL+="select mi.id_modelinstance, " +
	    		"mi.id_model, " +
	    		"mi.des_modelinstance, " +
	    		"mi.ip_model, " +
	    		"m.des_model, " +
	    		"a.id_attribute, " +
	    		"a.des_attribute, " +
	    		"a.type_attribute, " +
	    		"ai.id_attributeinstance, " +
	    		"ai.value_attribute " +
	    		"from modelsinstances as mi " +
	    		"join models as m on m.id_model=mi.id_model " +
	    		"join attributes as a on a.id_model=mi.id_model " +
	    		"join attributesinstances ai on ai.id_modelinstance=mi.id_modelinstance and ai.id_attribute=a.id_attribute " +
	    		"where mi.id_modelinstance=" + id_modelinstance + ";";
		return SQLiteJDBC.retrieveData(SQL,1);
	}
	
}
