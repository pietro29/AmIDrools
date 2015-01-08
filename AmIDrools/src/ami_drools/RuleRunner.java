package ami_drools;

import java.lang.reflect.Field;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Vector;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
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
		// create a temporary file .drl for support the KB, is important that the path begins with src/main/resources
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
		for (Object fact : facts) {
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
		String ft = new String("");// fact.getFactType
		FactType factType = kContainer.getKieBase().getFactType("rules", ft);
		Object NewFactType = factType.newInstance();
		// extract attributes from the array and put the value in the
		// newFactType
		Vector<String> attributes = null;
		Vector<String> values = null;
		for (int i = 0; i < attributes.size(); i++) {
			try {
				factType.set(NewFactType, attributes.get(i), values.get(i));
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		String id = new String("");
		// insert data outside the attributes, like the id
		try {
			factType.set(NewFactType, "id", id);
		} catch (Throwable t) {
			t.printStackTrace();
		}

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
		String s = ""
				+ "package rules \n"
				+ "declare Lampadina \n"
				+ "	id		: String \n"
				+ "	codice	: String \n"
				+ "	accesa	: Boolean \n"
				+ "	spenta	: Boolean \n"
				+ "end \n"
				+
				// "import prova.Lampadina \n" +
				"rule \"rule 1\" when \n"
				+ "    $f:Lampadina(accesa==true) \n"
				+ "then \n"
				+ "   System.out.println( \"Lampadina accesa da nuovo drl!\" ); \n"
				+ "   $f.setAccesa(false); \n"
				+ "end \n"
				+ "rule \"rule 2\" when \n"
				+ "    $f:Lampadina(spenta==true) \n"
				+ "then \n"
				+ "   System.out.println( \"Lampadina spenta da nuovo drl!\" ); \n"
				+ "end \n" + "rule \"rule 3\" when \n" + "    eval(true) \n"
				+ "then \n"
				+ "   System.out.println( \"funge da nuovo drl!\" ); \n"
				+ "end \n";

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
	public void matchResolveAct() throws RemoteException,
			IllegalArgumentException, IllegalAccessException {
		sharedFacts = wois.getSharedFacts();

		System.out.println("inserisco i fatti");
		// iterate all the shared fact
		for (Fact ogg : sharedFacts) {// insert the fact
			try {
				this.addFact(ogg);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		fireAllRules();

		System.out.println("recupero i fatti");
		// import all the fact from the working memory
		Collection<? extends Object> oggettiDaWM = this.getFacts();
		for (Object ogg : oggettiDaWM) {
			System.out.println(ogg.toString());
			Field[] attributes = ogg.getClass().getDeclaredFields();
			for (Field field : attributes) {
				field.setAccessible(true);
				// Dynamically read Attribute Name
				System.out.println("ATTRIBUTE NAME: " + field.getName()
						+ "; VALUE: " + field.get(ogg).toString());
			}
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
