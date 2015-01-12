package ami_drools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Collection;
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

public class RuleRunner {
	String DRLPath = new String("src/main/resources/PrivateRules/");
	KieSession kSession;
	KieServices kieServices;
	KieResources kieResources;
	KieFileSystem kieFileSystem;
	KieRepository kieRepository;
	KieBuilder kb;
	KieContainer kContainer;

	Wois wois;
	Vector<Fact> sharedFacts;
	Vector<Fact> sharedFactsSend;

	/**
	 * Constructor
	 * 
	 * @param wois the Wois which is connected to the session of the rule engine
	 * @see Wois
	 */
	public RuleRunner(Wois wois) {
		this.wois = wois;
		sharedFacts = new Vector<Fact>();
	}

	/**
	 * Create the knowledge base from the String and add the fact that are
	 * already inside the client. Then start the rule engine and create the
	 * session associated.
	 *
	 * @param rules the string that create the knowledge base
	 * @param facts the facts that are already inside the client
	 */
	public void runRules(String[] rules, Object[] facts) {

		kieServices = KieServices.Factory.get();
		kieResources = kieServices.getResources();
		kieFileSystem = kieServices.newKieFileSystem();
		kieRepository = kieServices.getRepository();
		// create a temporary file .drl for support the KB, 
		//is important that the path begins with src/main/resources
		kieFileSystem.write("src/main/resources/rules/p.drl", getRule());

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
		kSession = kContainer.newKieSession();
		for (Object fact : facts) { // TODO use typeFact to import the private fact
			// insert the fact
			kSession.insert(fact);
		}

		kSession.fireAllRules();
	}

	/**
	 * Add a fact into the current session.
	 *
	 * @param fact the fact that as to be insert into the knowledge base
	 * @see Fact
	 */
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
				Class<?> theClass = Class.forName("java.lang." + attributesType.get(i));
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
				{//in other case we can use the dynamic cast
					factType.set(NewFactType, attributes.get(i), theClass.cast(values.get(i)));
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
		// kSession.fireAllRules();

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
	 */
	private String getRule() {
		String s = "" //TODO da cancellare
				+ "package rules \n"
				+ "declare Lampadina \n"
				+ "	id		: String \n"
				+ "	codice	: String \n"
				+ "	accesa	: Boolean \n"
				+ "	spenta	: Boolean \n"
				+ "	modificati	: java.util.List \n"
				+ "end \n"
				//declare an object to notify the update
				+ "declare Notifica \n"
				+ "	id		: String \n"
				+ "end \n"

				//funzione per controllare se il manager ha messo un lock sul fatto
				/*
				+ "function void newFunction(){ \n"
				+ "System.out.println( \"dentro alla funzione!\" );"
				+ "if(true) \n"
				+ "		System.out.println( \"Lampadina sia accesa che spenta!\" ); \n"
				+ "else \n"
				+ "		System.out.println( \"merda!\" );}\n"
				*/
				/*
				+ "rule \"rule f\" when \n"
				+ "    eval(true) \n"
				+ "then \n"
				+ "   newFunction(); \n"
				+ "end \n"
				 */
				+ "rule \"rule 1\" \n when \n"
				+ "    $f:Lampadina(accesa==true) \n"
				+ "	   $damodificare: Lampadina(id==$f.getId())\n"
				+ "then \n"
				+ " \n"
				+ "   System.out.println( \"Lampadina accesa da nuovo drl!\" ); \n"
				+ "   modify($f) {setAccesa(false)}; \n"
				//+ "   $f.setModificati(new java.util.ArrayList()); \n"
				+ "   $f.getModificati().add(new String(\"accesa\")); \n"
				+ "end \n"
				
				+ "rule \"rule 2\" when \n"
				+ "    $f:Lampadina(spenta==true) \n"
				+ "then \n"
				+ "   System.out.println( \"Lampadina spenta da nuovo drl!\" ); \n"
				+ "end \n" 
				
				+ "rule \"rule 3\" when \n" 
				+ "    eval(true) \n"
				+ "then \n"
				+ "   System.out.println( \"funge da nuovo drl!\" ); \n"
				+ "end \n";
		s="";
			try {
				BufferedReader br = new BufferedReader(new FileReader("C:/Users/MattiaEvent/workspace/AmIDrools/drl.txt"));
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();
		        while (line != null) {
		            sb.append(line);
		            sb.append(System.lineSeparator());
		            line = br.readLine();
		        }
		        s = sb.toString();
		        br.close();
		    } catch (Throwable t) {
		    	System.err.println(t.toString());
		    	s="";
		    }
			System.out.println(s);
			return s;
	}

	/**
	 * Resolve one cycle for the client, where a cycle is composed of three
	 * stages, then first import the fact from the manager, the second add the
	 * fact to the working memory and fire the rules and the third export the
	 * object that represent the fact to the manager
	 *
	 */
	public void matchResolveAct(String ISname) throws RemoteException,
			IllegalArgumentException, IllegalAccessException {
		sharedFacts = new Vector<Fact>();
		sharedFacts = wois.getSharedFacts();

		// iterate all the shared fact
		for (Fact ogg : sharedFacts) {
			try {// insert the fact
				this.addFact(ogg);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		kSession.setGlobal("wois", wois);
		//block the rule that have some facts locked in the WM
		
		kSession.addEventListener(new RuleRuntimeEventListener() {
			@Override
			public void objectUpdated(ObjectUpdatedEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void objectInserted(ObjectInsertedEvent arg0) {
				// TODO Auto-generated method stub
				//wois.toString();
				
			}
			
			@Override
			public void objectDeleted(ObjectDeletedEvent arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		kSession.fireAllRules(new AgendaFilter()
		{//define the condition to fire the rule
			public boolean accept(Match match)
			{
				String rulename = match.getRule().getName();
				
				//check if there's a lock for the ID inside the metadata
				/*Map<String, Object> metaData = match.getRule().getMetaData();
				if (metaData.containsKey("LampToLock")) {
				      System.out.println("id da verificare : " + metaData.get("LampToLock"));
				    }*/
				
				Collection<? extends FactHandle> oggettiDaWM = match.getFactHandles();
				//search for the obj which name is damodificare
				for (FactHandle ogg : oggettiDaWM) {
					//System.err.println(ogg);
					Field[] attributes = ogg.getClass().getDeclaredFields();
					for (Field field : attributes) {
						// TODO check if the id is in the lock table
						//System.out.println(field.toString());
						field.setAccessible(true);
						if(field.getName().toLowerCase().equals("codice"))
						{
							try {
								if(field.get(ogg).toString().equals("lampadina2"))
								{
									return false;
								}
							} catch (IllegalArgumentException
									| IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					}
				
				for (Object ogg : oggettiDaWM) {
				Field[] attributes = ogg.getClass().getDeclaredFields();
				for (Field field : attributes) {
					// TODO check if the id is in the lock table
					//System.out.println(field.toString());
					field.setAccessible(true);
					if(field.getName().toLowerCase().equals("codice"))
					{
						try {
							if(field.get(ogg).toString().equals("lampadina2"))
							{
								return false;
							}
						} catch (IllegalArgumentException
								| IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				}
				return true;
			}
		});
		
		
		System.out.println("DOPO--------------------");
		// import all the fact from the working memory
		Collection<? extends Object> oggettiDaWM = this.getFacts();
		sharedFactsSend = new Vector<Fact>();
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
			// TODO if index<0 then error message
			Fact factToSend = sharedFacts.get(index);
			
			for (Field field : attributes) {
				field.setAccessible(true);
				// Dynamically read Attribute Name
				System.out.println(field.getName() + ", " + field.get(ogg).toString());
				if (field.getName().equals("modificati"))
				{
					factToSend.updateAttributeValue(field.getName(), "new java.util.ArrayList()" );
				}
				else
				{
					factToSend.updateAttributeValue(field.getName(), field.get(ogg).toString());
				}
			}
			sharedFactsSend.add(factToSend);
		}
		//send here
		try {
			wois.setSharedFacts(sharedFactsSend, ISname);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// clean the session
		cleanSession();
		// create new session
		kSession = kContainer.newKieSession();
	}

	/**
	 * fire all the rules inside the knowledge base that satisfy the condition
	 * 
	 */
	public void fireAllRules() {
		kSession.fireAllRules();
	}

	/**
	 * dispose the session, all the facts inside the working memory are deleted
	 */
	public void cleanSession() {
		kSession.dispose();
	}
}
