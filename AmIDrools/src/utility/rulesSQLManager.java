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
/**
 * @author MattiaEvent
 *
 */
public final class rulesSQLManager {

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
	 * Insert new Rule header
	 * @param name rule name
	 * @param id_user id of the user owner
	 * @param no_loop optional no loop attribute
	 * @param salience optional salience 
	 * @param _public public or not rule
	 * @return String that represent the insert of the rule header
	 */
	public static String RulesInsert_manager(String name, String des_user, boolean no_loop, Integer salience, boolean _public){
		Integer no_loopInt = 0;
		Integer _publicInt = 0;
		if(no_loop) no_loopInt=1;
		if(_public) _publicInt=1;
		String SQL=new String("");
		SQL+="insert into rules ";
		SQL+="(name,id_user,no_loop,salience,public) ";
		SQL+="values ";
		SQL+="(\""+name+"\",(select id_user from users where des_user=\""+des_user+"\" LIMIT 1),"+no_loopInt+","+salience+","+_publicInt+");";
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
	 * insert condition fact manager side
	 * @param des_model description of the model associated to the condition
	 * @param var_name name for the variable associated to the condition
	 * @return string represented the SQL command
	 */
	public static String RulesIfFactsInsert_manager(String des_model, String var_name){
		String SQL=new String("");
		SQL+="insert into rulesiffacts";
		SQL+="(id_ruleif,id_model,var_name)";
		SQL+="values";
		SQL+="((select max(id_ruleif) from rulesif),(select id_model from models where des_model=\""+des_model+"\" LIMIT 1),\""+var_name+"\");";
		
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
	 * insert action fact manager side
	 * @param id_model id of the model associated to the action
	 * @param var_name name for the variable associated to the action
	 * @return string represented the SQL command
	 */
	public static String RulesThenFactsInsert_manager(String des_model, String var_name){
		String SQL=new String("");
		SQL+="insert into rulesthenfacts";
		SQL+="(id_rulethen,id_model,var_name)";
		SQL+="values";
		SQL+="((select max(id_rulethen) from rulesthen),(select id_model from models where des_model=\""+des_model+"\" LIMIT 1),\""+var_name+"\");";
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
	 * insert condition on the attribute manager side
	 * @param id_attribute id of the attribute associated to the condition
	 * @param operation operation of the condition
	 * @param value value of the condition
	 * @return string represented the SQL command
	 */
	public static String RulesIfFactsDetailsInsert_manager(String des_attribute, String des_model, String operation, String value){
		String SQL=new String("");
		SQL+="insert into rulesiffactsdetails";
		SQL+="(id_ruleiffact,id_attribute,operation,value)";
		SQL+="values";
		SQL+="((select max(id_ruleiffact) from rulesiffacts),"
				+ "(select A.id_attribute from attributes A left join models M on A.id_model=M.id_model "
				+ "where A.des_attribute=\""+des_attribute+"\" and M.des_model=\""+des_model+"\" LIMIT 1),\""+operation+"\",\""+value+"\");";
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
	 * insert action on the attribute
	 * @param id_attribute id of the attribute associated to the action
	 * @param operation operation of the action
	 * @param value value of the action
	 * @return string represented the SQL command
	 */
	public static String RulesThenFactsDetailsInsert_manager(String des_attribute,String des_model, String operation, String value){
		String SQL=new String("");
		SQL+="insert into rulesthenfactsdetails";
		SQL+="(id_rulethenfact,id_attribute,operation,value)";
		SQL+="values";
		SQL+="((select max(id_rulethenfact) from rulesthenfacts),"
				+ "(select A.id_attribute from attributes A left join models M on A.id_model=M.id_model "
				+ "where A.des_attribute=\""+des_attribute+"\" and M.des_model=\""+des_model+"\" LIMIT 1),\""+operation+"\",\""+value+"\");";
		//SQLiteJDBC.executeUpdate(SQL,1);
		return SQL;
	}
	
	/**
	 * fire a SQL command
	 * @param SQL contains all the SQL command to fire
	 */
	public static void fireSQLInsertCommand(String SQL){
		System.err.println(SQL);
		SQLiteJDBC.executeUpdate(SQL,0);
	}
	
	/**
	 * Insert a new model in the manager db
	 * @param des_model description of the model to insert
	 * @param id_user id of the user that want to insert the model
	 * @param if_model true if the model can be choose in the condition
	 * @param then_model true if the model can be choose in the action
	 * @return the string with the SQL command for the insert
	 */
	public static String ModelInsert(String des_model, int id_user, boolean if_model, boolean then_model){
		Integer if_modelInt = 0;
		Integer then_modelInt = 0;
		if(if_model) if_modelInt=1;
		if(then_model) then_modelInt=1;
		String SQL=new String("");
		SQL+="insert into models";
		SQL+="(des_model,id_user,if_model,then_model)";
		SQL+="values";
		SQL+="(\""+des_model+"\","+id_user+","+if_modelInt+","+then_modelInt+");";
		//SQLiteJDBC.executeUpdate(SQL,0);
		return SQL;
	}
	
	/**
	 * insert the attribute of a model in the manager db
	 * @param des_attribute description of the attribute to insert
	 * @param type_attribute type of the attribute to insert
	 * @return the string with the SQL command for the insert
	 */
	public static String AttributeInsert(String des_attribute, String type_attribute){
		String SQL=new String("");
		SQL+="insert into attributes";
		SQL+="(id_model,des_attribute,type_attribute)";
		SQL+="values";
		SQL+="((select max(id_model) from models),\""+des_attribute+"\",\""+type_attribute+"\");";
		//SQLiteJDBC.executeUpdate(SQL,0);
		return SQL;
	}
	
	/**
	 * @return all the shared models
	 */
	public static ResultSet getModels()
	{
		String SQL = new String("");
		SQL+="select id_model, des_model, ifnull(id_user,0) as id_user from models";
		return SQLiteJDBC.retrieveData(SQL,0);
	}
	
	/**
	 * @return the private models stored in the manager
	 */
	public static ResultSet getUsersModels(String des_model, Integer id_user)
	{
		String SQL = new String("");
		SQL+="select id_model, des_model, ifnull(id_user,0) as id_user from models where des_model=\""+des_model+"\" and id_user="+id_user+";";
		return SQLiteJDBC.retrieveData(SQL,0);
	}
	
	/**
	 * @return all the shared models usable for the condition
	 */
	public static ResultSet getModelsIF()
	{
		String SQL = new String("");
		SQL+="select id_model, des_model, ifnull(id_user,0) as id_user from models where ifnull(if_model,1)=1 and ifnull(id_user,0)=0";
		return SQLiteJDBC.retrieveData(SQL,0);
	}
	
	/**
	 * @return all the shared models usable for the action
	 */
	public static ResultSet getModelsTHEN()
	{
		String SQL = new String("");
		SQL+="select id_model, des_model, ifnull(id_user,0) as id_user from models where ifnull(then_model,1)=1 and ifnull(id_user,0)=0";
		return SQLiteJDBC.retrieveData(SQL,0);
	}
	
	/**
	 * @param id_model id of the model to search
	 * @return a specific model
	 */
	public static ResultSet getModel(Integer id_model)
	{
		String SQL = new String("");
		SQL+="select id_model, des_model, ifnull(id_user,0) as id_user from models where id_model=" + id_model;
		return SQLiteJDBC.retrieveData(SQL,0);
	}
	
	/**
	 * @param id_model id of the model 
	 * @return all the attribute associated to a specific model
	 */
	public static ResultSet getAttributeFromModels(int id_model)
	{
		String SQL = new String("");
		SQL+="select id_attribute, des_attribute, type_attribute from attributes where id_model="+id_model;
		return SQLiteJDBC.retrieveData(SQL,0);
	}
	
	/**
	 * @param id_attribute id of the attribute
	 * @return type of the specific attribute
	 */
	public static ResultSet getTypeOfAttributes(int id_attribute)
	{
		String SQL = new String("");
		SQL+="select type_attribute from attributes where id_attribute="+id_attribute;
		return SQLiteJDBC.retrieveData(SQL,0);
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
		return SQLiteJDBC.retrieveData(SQL,0);
	}
	
	/**
	 * @param id_rule id of the specific rule
	 * @return all the actions for a specific rule
	 */
	public static ResultSet getRulesActions(Integer id_rule)
	{
		String SQL = new String("");
		SQL+="select R.name,M.des_model,RTF.var_name,A.des_attribute,RTFD.operation,RTFD.value "
				+ "from rules R "
				+ "left join rulesthen RT on RT.id_rule=R.id_rule "
				+ "left join rulesthenfacts RTF on RTF.id_rulethen=RT.id_rulethen "
				+ "left join rulesthenfactsdetails RTFD on RTFD.id_rulethenfact=RTF.id_rulethenfact "
				+ "left join models M on M.id_model=RTF.id_model "
				+ "left join attributes A on A.id_attribute=RTFD.id_attribute ";
		if (id_rule>0) SQL+="where R.id_rule="+id_rule;
		return SQLiteJDBC.retrieveData(SQL,0);
	}
	
	/**
	 * delete a specific rule
	 * @param ruleName name of the rule
	 */
	public static void deleteRules(String ruleName)
	{
		String SQL = new String("");
		SQL+="delete from rules where name=\""+ruleName+"\"";
		SQLiteJDBC.executeUpdate(SQL,0);
	}
	
	/**
	 * delete a specific rule
	 * @param ruleName name of the rule
	 * @param ISName name of the owner
	 */
	public static void deleteRules_manager(String ruleName, String ISName)
	{
		String SQL = new String("");
		SQL+="delete from rules "
				+ "where name=\""+ruleName+"\" and id_rule not in "
				+ "(select id_rule from rules R left join users U on U.id_user=R.id_user where des_user=\""+ISName+"\")";
		SQLiteJDBC.executeUpdate(SQL,0);
	}
	
	/**
	 * get the rules of a specific user
	 * @return the shared rules of the user
	 */
	public static ResultSet getRules(String des_user)
	{
		String SQL = new String("");
		SQL+="select R.id_rule, R.name, R.no_loop, R.salience "
				+ "from rules R "
				+ "left join users U on U.id_user=R.id_user "
				+ "where U.des_user=\""+des_user+"\";";
		return SQLiteJDBC.retrieveData(SQL,0);
	}
	
	/**
	 * get the fact of a rule's condition
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
		return SQLiteJDBC.retrieveData(SQL,0);
	}
	
	/**
	 * get the fact details of a rule's condition
	 * @param id_ruleiffact id of a fact of a rule
	 * @return all the conditions details for a specific rule fact
	 */
	public static ResultSet getRulesConditionsFactsDetails(Integer id_ruleiffact)
	{
		String SQL = new String("");
		SQL+="select ifnull(A.des_attribute,\"\") as des_attribute,ifnull(RIFD.operation,\"\") as operation,ifnull(RIFD.value,\"\") as value "
				+ "from rulesiffacts RIF "
				+ "left join rulesiffactsdetails RIFD on RIFD.id_ruleiffact=RIF.id_ruleiffact "
				+ "left join attributes A on A.id_attribute=RIFD.id_attribute "
				+ "where RIF.id_ruleiffact="+id_ruleiffact;
		return SQLiteJDBC.retrieveData(SQL,0);
	}
	
	/**
	 * get the fact of a rule's action
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
		return SQLiteJDBC.retrieveData(SQL,0);
	}
	
	/**
	 * get the fact details of a rule's action
	 * @param id_rulethenfact id of a fact of a rule
	 * @return all the actions details for a specific rule fact
	 */
	public static ResultSet getRulesActionsFactsDetails(Integer id_rulethenfact)
	{
		String SQL = new String("");
		SQL+="select ifnull(A.des_attribute,\"\") as des_attribute,ifnull(RTFD.operation,\"\") as operation,ifnull(RTFD.value,\"\") as value "
				+ "from rulesthenfacts RTF "
				+ "left join rulesthenfactsdetails RTFD on RTFD.id_rulethenfact=RTF.id_rulethenfact "
				+ "left join attributes A on A.id_attribute=RTFD.id_attribute "
				+ "where RTF.id_rulethenfact="+id_rulethenfact;
		return SQLiteJDBC.retrieveData(SQL,0);
	}
	
}
