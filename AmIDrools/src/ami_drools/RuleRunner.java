package ami_drools;

import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import utility.rulesSQLIS;

public class RuleRunner {
	KieSession kSession;
	KieServices kieServices;
	KieResources kieResources;
	KieFileSystem kieFileSystem;
	KieRepository kieRepository;
	KieBuilder kb;
	KieContainer kContainer;
	FactHandle handleWois;
	String ISName;
	TextArea txtArea;
	Wois wois;
	Vector<Fact> sharedFacts;
	Vector<Fact> privateFacts;
	Vector<Fact> sharedFactsSend;
	Vector<Fact> privateFactsSend;
	/**
	 * Constructor
	 * 
	 * @param wois the Wois which is connected to the session of the rule engine
	 * @see Wois
	 */
	public RuleRunner(String ISName, TextArea txtArea) {
		sharedFacts = new Vector<Fact>();
		privateFacts = new Vector<Fact>();
		this.ISName=ISName;
		this.txtArea=txtArea;
	}
	
	/**
	 * set the local wois associated to the ruleRunner
	 * @param wois object that represent the wois
	 */
	public void setWois(Wois wois)
	{
		this.wois = wois;
	}
	
	/**
	 * Create the knowledge base from the String and add the fact that are
	 * already inside the client. Then start the rule engine and create the
	 * session associated.
	 *
	 * @param rules the string that create the knowledge base
	 * @param facts the facts that are already inside the client
	 */
	public void CreateKnowlegdeBase(Vector<Fact> facts) {
		try {
			privateFacts=new Vector<Fact>();
			kieServices = KieServices.Factory.get();
			kieResources = kieServices.getResources();
			kieFileSystem = kieServices.newKieFileSystem();
			kieRepository = kieServices.getRepository();
			// create a temporary file .drl for support the KB, 
			//is important that the path begins with src/main/resources
			kieFileSystem.write("src/main/resources/rules/tempKB.drl", getRule());

			kb = kieServices.newKieBuilder(kieFileSystem);
			// compile the KB
			kb.buildAll();
			if (kb.getResults().hasMessages(Level.ERROR)) {
				throw new RuntimeException("Build Errors:\n"
						+ kb.getResults().toString());
			}
			kContainer = kieServices.newKieContainer(kieRepository
					.getDefaultReleaseId());
			// create the session
			//kSession = kContainer.newKieSession();
			for (Fact fact : facts) {
				// insert the fact
				privateFacts.add(fact);
			}
			//fireAllRules();
			//cleanSession();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
			cleanSession();
		}
		
	}
	
	

	/**
	 * Add a fact into the current session.
	 *
	 * @param fact the fact that as to be insert into the knowledge base
	 * @see Fact
	 */
	@SuppressWarnings("rawtypes")
	public void addFact(Fact fact) throws Throwable {
		String ft = new String(fact.getFactType());// fact.getFactType
		FactType factType = kContainer.getKieBase().getFactType("rules", ft);
		Object NewFactType = factType.newInstance();
		// extract attributes from the array and put the value in the
		// newFactType
		Vector<String> attributes = fact.getAttributes();
		Vector<String> values = fact.getValues();
		Vector<String> attributesType = fact.getAttributesType();
		System.out.println("PRIMA--------------------");
		for (int i = 0; i < attributes.size(); i++) {
			try {
				System.out.println(attributes.get(i) + ", " + values.get(i));
				//extract the type of attribute and transform it into a Class object for casting
				Class<?> theClass=null;
				try {
					theClass = Class.forName("java.lang." + attributesType.get(i));
				} catch (Exception  e) {
					// TODO: handle exception
				}
				if (theClass==null)
				{
					try {
						theClass = Class.forName(attributesType.get(i));
					} catch (Exception e) {
		
					}
				}
				
				//System.out.println(theClass.toString());
				if (attributesType.get(i).toLowerCase().equals("boolean"))
				{//if the type is boolean the cast is done observing the single string and putting the true/false value
					if (values.get(i).toLowerCase().equals("true"))
					{
						factType.set(NewFactType, attributes.get(i),true);
					}else{
						factType.set(NewFactType, attributes.get(i), false);
					}
				}
				else
				{
					if (attributesType.get(i).toLowerCase().equals("int"))
					{
						factType.set(NewFactType, attributes.get(i), Integer.parseInt(values.get(i)));
					}else if(attributesType.get(i).equals("java.util.Date")){
						SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
						Date date = formatter.parse(values.get(i));
						factType.set(NewFactType, attributes.get(i), date);
					}
					else
					{
						//in other case we can use the dynamic cast
						factType.set(NewFactType, attributes.get(i), theClass.cast(values.get(i)));
					}
				}
				
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		String id = new String(fact.getId());
		// insert data outside the attributes, like the id
		try {
			factType.set(NewFactType, "id", id);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		factType.set(NewFactType,"modificati", new java.util.ArrayList());
		kSession.insert(NewFactType);

	}

	/**
	 * Returns all the fact inside the working memory of the current session
	 * 
	 * @return the collection of the object inside the session
	 */
	public Collection<? extends Object> getFacts() {
		try {
			return kSession.getObjects();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the string that represent a knowledge base and for create the
	 * temporary file .drl
	 * 
	 * @return the string of the knowledge base
	 * @throws RemoteException 
	 */
	private String getRule() throws RemoteException {
		String s = "" ;
		//s+=getStringFromFile("resources/local_import.txt");//import the local class and the package
		s+=getPackageVariableforDRL();
		s+=getModelsFromDBForDRL();//import the local class and the package
		if (s.equals(""))
		{
			s="KB non caricato";
		}else{
			s+="\n";
		//s+=getStringFromFile("/resources/shared_declare.txt");
		if (wois!=null){
			s+=wois.getSharedFactsTemplates();//import the template/declare of the shared fact
			
		}
		s+="\n";
		//s+=getStringFromFile("/resources/shared_function.txt");//import the shared function
		if (wois!=null) 
			s+=wois.getSharedFactsFunctions();
		s+="\n";
		//s+=getStringFromFile("resources/local_rules.txt");//import the rules that use local variable (no declare needed)
		s+=getRulesFromDBForDRL();//import the rules that use local variable (no declare needed)
		s+="\n";
		//s+=getStringFromFile("/resources/shared_rules.txt");//import the rules that use shared variable (declare needed)
		if (wois!=null)
			s+=wois.getSharedFactsRules(ISName);
			//s+=getStringFromFile("resources/shared_rules.txt");
		}
			
		System.out.println(s);
		return s;
	}

	/**
	 * @return the default header of the Knowledge Base
	 */
	private String getPackageVariableforDRL()
	{
		String s = new String("");
		s+="package rules \n";
		s+="import ami_drools.Wois; \n";
		s+="global String ISName; \n";
		s+="global java.awt.TextArea txtArea; \n";
		return s;
	}
	
	/**
	 * @return return a string filled with the models of the client
	 */
	private String getModelsFromDBForDRL(){
		String s = new String("");
		ResultSet rs;
		rs=rulesSQLIS.getModels();
		if (rs==null){
    		System.out.println("Models not found");
    	} else {
    		try {
				while (rs.next()) {
					s+="declare " + rs.getString("des_model") +"\n";
					ResultSet rsAtt;
					rsAtt=rulesSQLIS.getAttributeFromModels(rs.getInt("id_model"));
					if (rsAtt==null){
			    		System.out.println("Attribute of the model not found");
			    	} else {
			    		try {
							while (rsAtt.next()) {
								s+="\t " + rsAtt.getString("des_attribute") + ":\t "+rsAtt.getString("type_attribute")+"\n";
							}
							s+="\t modificati:\t java.util.List\n";
							s+="\t _privateVisibility:\t Boolean\n";
							s+="end\n";
			    		}catch (Exception e) {
							// TODO: handle exception
							return "";
						}
			    	}rsAtt.close();
				}rs.close();
    		}
			catch (Exception e) {
					// TODO: handle exception
				return "";
			}
		}
		return s;
	}
	
	/**
	 * @return the string filled with the formatted rules of the client
	 */
	private String getRulesFromDBForDRL(){
		String s = new String("");
		ResultSet rs;
		rs=rulesSQLIS.getRules();
		if (rs==null){
    		System.out.println("Rules not found");
    	} else {
    		try {
				while (rs.next()) {
					s+="rule \"" + rs.getString("name") +"\"\n";
					try {
						if(rs.getBoolean("no_loop")) s+="no-loop\n";
						if(rs.getInt("salience")>0) s+="salience "+ rs.getInt("salience") +"\n";
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					s+="when\n";//load condition
					ResultSet rsCond;
					rsCond=rulesSQLIS.getRulesConditionsFacts(rs.getInt("id_rule"));
					if (rsCond==null){
			    		System.out.println("Condition of the rule not found");
			    		return "";
			    	} else {
			    		try {
							while (rsCond.next()) {
								s+="\t $" + rsCond.getString("var_name") + ":"+rsCond.getString("des_model")+"(";
								ResultSet rsCondDetails;
								rsCondDetails=rulesSQLIS.getRulesConditionsFactsDetails(rsCond.getInt("id_ruleiffact"));
								if (rsCondDetails==null){
						    		System.out.println("Condition of the rule not found");
						    		return "";
						    	} else {
						    		try {int i=0;
										while (rsCondDetails.next()) {
											String des_attribute = new String();
											try {
												des_attribute=rsCondDetails.getString("des_attribute");
											} catch (Exception e) {
												des_attribute="";
											}
											if (i>0) s+=",";
											if(des_attribute==null) des_attribute="";
											s+=des_attribute +rsCondDetails.getString("operation")+rsCondDetails.getString("value");
											i=1;
										}
										s+=")\n";
						    		}catch (Exception e) {
										// TODO: handle exception
						    			e.printStackTrace();
										return "";
									}
						    	}rsCondDetails.close();
								
							}
							s+="\n";
			    		}catch (Exception e) {
							// TODO: handle exception
			    			e.printStackTrace();
							return "";
						}
			    	}rsCond.close();
					s+="then\n";//load action
					ResultSet rsAct;
					rsAct=rulesSQLIS.getRulesActionsFacts(rs.getInt("id_rule"));
					if (rsAct==null){
			    		System.out.println("Condition of the rule not found");
			    		return "";
			    	} else {
			    		try {
							while (rsAct.next()) {
								
								ResultSet rsActDetails;
								rsActDetails=rulesSQLIS.getRulesActionsFactsDetails(rsAct.getInt("id_rulethenfact"));
								if (rsActDetails==null){
						    		System.out.println("Condition of the rule not found");
						    		return "";
						    	} else {
						    		try {int i=0;
										while (rsActDetails.next()) {
											String var=new String("$"+rsAct.getString("var_name"));
											String value=new String(rsActDetails.getString("value"));
											String att=new String(rsActDetails.getString("des_attribute"));
											if(rsActDetails.getString("operation").equals("notify"))
								        	{ //print some text
												s+="\t txtArea.append(\""+ value +"\"+"+var+".get"+att.substring(0,1).toUpperCase()+att.substring(1,att.length())+"()+\"\\n\");\n";
								        	}else{//modify an attribute
								        		/*
								        		 * if(setLock($f.getId(),$wi,ISName))
													{
														modify($f) {setAccesa(false)};
														$f.getModificati().add(new String("accesa"));
													}*/
								        		s+="\t\t modify("+var+") {set"+att.substring(0,1).toUpperCase()+att.substring(1,att.length())+"("+value+")};\n";
								        		s+="\t\t "+var+".getModificati().add(new String(\""+att+"\"));\n";
								        	}
										}
										rsActDetails.close();
						    		}catch (Exception e) {
										// TODO: handle exception
						    			e.printStackTrace();
										s="";
									}
						    	}
								
							}rsAct.close();
			    		}catch (Exception e) {
							// TODO: handle exception
			    			e.printStackTrace();
							s="";
						}
			    	}
					s+="end\n";
					
				}rs.close();
				
    		}
			catch (Exception e) {
					// TODO: handle exception
				return "";
			}
		}
		return s;
	}
	
	/**
	 * Returns the string that represent a the content of a specific file
	 * 
	 * @return the string of the file
	 */
	private String getStringFromFile(String fileName) {
		String s = "" ;
		try {
			InputStream in=ClassLoader.getSystemResourceAsStream(fileName);
    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    while (line != null) {
		         sb.append(line);
		         sb.append(System.lineSeparator());
		            line = br.readLine();
		    }
		    s = sb.toString();
		    br.close();
		    } catch (Exception t) {
		    	System.err.println(t.toString());
		    	s="";
		    }
		return s;
	}
	
	/**
	 * Resolve one cycle for the client, where a cycle is composed of three
	 * stages, then first import the fact from the manager, the second add the
	 * fact to the working memory and fire the rules and the third export the
	 * object that represent the fact to the manager
	 *
	 * @param ISname name of the actual IS
	 * @param privateFactsReceived list of the private facts
	 * @throws RemoteException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void matchResolveAct(String ISname, Vector<Fact> privateFactsReceived) throws RemoteException,
			IllegalArgumentException, IllegalAccessException {
		//recreate the KB in case of changes
		//is important that the path begins with src/main/resources
		
		kieFileSystem.write("src/main/resources/rules/tempKB.drl", getRule());

		kb = kieServices.newKieBuilder(kieFileSystem);
		// compile the KB
		kb.buildAll();
		if (kb.getResults().hasMessages(Level.ERROR)) {
			throw new RuntimeException("Build Errors:\n"
					+ kb.getResults().toString());
		}
		kContainer = kieServices.newKieContainer(kieRepository
				.getDefaultReleaseId());
		
		//insert the shared fact
		sharedFacts = new Vector<Fact>();
		// create new session
		kSession = kContainer.newKieSession();
		//set global variable
		kSession.setGlobal("ISName", ISName);
		kSession.setGlobal("txtArea", txtArea);
		try {
			sharedFacts = wois.getSharedFacts();
	
			// iterate all the shared fact
			for (Fact ogg : sharedFacts) {
				try {// insert the fact
					this.addFact(ogg);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		handleWois = kSession.insert(wois);
		} catch (Exception e) { 
			// TODO: handle exception
		}
		privateFacts=new Vector<Fact>();
		privateFacts=privateFactsReceived;
		//insert the private fact
		for (Fact ogg : privateFacts) {
			try {// insert the fact
				this.addFact(ogg);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		/*
		kSession.addEventListener(new RuleRuntimeEventListener() {
			@Override
			public void objectUpdated(ObjectUpdatedEvent arg0) {
				// 
				
			}
			
			@Override
			public void objectInserted(ObjectInsertedEvent arg0) {
				// 
				//wois.toString();
				
			}
			
			@Override
			public void objectDeleted(ObjectDeletedEvent arg0) {
				// 
			}
		});*/
		System.setProperty("drools.dateformat", "dd-MM-yyyy HH:mm:ss");//setting date format for drools
		System.setProperty( "drools.defaultcountry" ,"IT" );
		System.setProperty( "drools.defaultlanguage", "it" );
		System.out.println("********************************");
		kSession.fireAllRules(new AgendaFilter()
		{//define the condition to fire the rule
			public boolean accept(Match match)
			{
				//String rulename = match.getRule().getName();
				Collection<Object> oggettiDaWM = match.getObjects();
				for (Object ogg : oggettiDaWM) {
					Field[] attributes = ogg.getClass().getDeclaredFields();
					//check if is private
					boolean priv=false;
					for (Field field : attributes) {
						field.setAccessible(true);
						if(field.getName().equals("_privateVisibility"))
						{
							try {
									if(field.get(ogg).toString().equals("true"))
									{// the object is locked
										priv=true;
									}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					//check the id
					for (Field field : attributes) {
						field.setAccessible(true);
						if(field.getName().toLowerCase().equals("id") && !priv)
						{
							if(wois!=null)
							{
								try {
										if(wois.getLock(field.get(ogg).toString(),ISName))
										{// the object is locked
											return false;
										}
								} catch (IllegalArgumentException
										| IllegalAccessException | RemoteException e) {
									e.printStackTrace();
									return false;
								}
							}
						}
					}
				}
				return true;
			}
		});
		try {
			kSession.delete(handleWois);
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("********************************");
		System.out.println("DOPO--------------------");
		// import all the fact from the working memory
		Collection<? extends Object> oggettiDaWM = this.getFacts();
		sharedFactsSend = new Vector<Fact>();
		privateFactsSend = new Vector<Fact>();
		for (Object ogg : oggettiDaWM) {
			System.out.println(ogg.toString());
			Field[] attributes = ogg.getClass().getDeclaredFields();
			int index=-1;
			//search the index of the fact in the vector of the shared facts
			for (Field field : attributes) {
				field.setAccessible(true);
				if(field.getName().toLowerCase().equals("id"))
				{
					for(int j=0;j<sharedFacts.size() && index<0;j++)
					{//if the id is found in the shared fact save the index j
						if(sharedFacts.get(j).getId().equals(field.get(ogg).toString()))
						{
							index=j;
						}
					}
				}
				if (index>0) break;
			}
			Fact factToSend=null;
			if (index>=0)
			{
				factToSend = sharedFacts.get(index);
				for (Field field : attributes) {
					field.setAccessible(true);
					// Dynamically read Attribute Name
					System.out.println(field.getName() + ", " + field.get(ogg).toString());
					if (field.getName().equals("modificati"))
					{
						factToSend.insertModifiedAttribute(field.get(ogg).toString());
					}
					else
					{
						factToSend.updateAttributeValue(field.getName(), field.get(ogg).toString());
					}
				}
				sharedFactsSend.add(factToSend);
			}else{
				//System.err.println("id of the object not found inside the array of the fact");
				//private fact
				System.out.println("fatto privato, non verrà inviato");
				//search the index of the fact in the vector of the private facts
				for (Field field : attributes) {
					field.setAccessible(true);
					
					if(field.getName().toLowerCase().equals("id"))
					{
						for(int j=0;j<privateFacts.size() && index<0;j++)
						{//if the id is found in the shared fact save the index j
							
							if(privateFacts.get(j).getId().equals(field.get(ogg).toString()))
							{
								index=j;
							}
						}
					}
					if (index>=0) break;
				}
				if(index>=0)
				{//update the private facts
					factToSend = privateFacts.get(index);
					for (Field field : attributes) {
						field.setAccessible(true);
						// Dynamically read Attribute Name
						System.out.println(field.getName() + ", " + field.get(ogg).toString());
						if (field.getName().equals("modificati"))
						{
							factToSend.insertModifiedAttribute(field.get(ogg).toString());
						}
						else
						{
							factToSend.updateAttributeValue(field.getName(), field.get(ogg).toString());
						}
					}
					privateFactsSend.add(factToSend);
				}
			}
		}
		//send here
		try {
			if(wois!=null && sharedFactsSend!=null)
			{
				wois.setSharedFacts(sharedFactsSend, ISname);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// clean the session
		cleanSession();
		
	}

	/**
	 * fire all the rules inside the knowledge base that satisfy the condition and
	 * add the Wois object for the lock's control
	 * 
	 */
	public void fireAllRules() {
		try {
			try {
				handleWois = kSession.insert(wois);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			kSession.fireAllRules();
			try {
				if (handleWois!=null)kSession.delete(handleWois);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	/**
	 * dispose the session, all the facts inside the working memory are deleted
	 */
	public void cleanSession() {
		try {
			if (kSession!=null)
				kSession.dispose();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @return facts to send to the manager
	 */
	public Vector<Fact> getPrivateFacts()
	{
		return privateFactsSend;
	}
}
