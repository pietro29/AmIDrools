package ami_drools;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Vector;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
 
public class RuleRunner
{
	String DRLPath=new String("src/main/resources/PrivateRules/");
	KieSession kSession;//lo metto globale per poter inserire altri oggetti
	KieServices kieServices;
	KieResources kieResources;
	KieFileSystem kieFileSystem;
	KieRepository kieRepository;
	KieBuilder kb;
	KieContainer kContainer;
	
	Wois wois;
	Vector sharedFacts;
	
    public RuleRunner(Wois wois)
    {
    	this.wois=wois;
    	sharedFacts = new Vector();
    }
 
    public void runRules(String[] rules, Object[] facts)
    {
 
    	
        kieServices = KieServices.Factory.get();
        kieResources = kieServices.getResources();
        kieFileSystem = kieServices.newKieFileSystem();
        kieRepository = kieServices.getRepository();
  
        /*for(String ruleFile : rules)
        {
            Resource resource = kieResources.newClassPathResource(ruleFile);
            
            // path has to start with src/main/resources
            // append it with the package from the rule
            System.out.println(DRLPath + ruleFile);
            kieFileSystem.write(DRLPath + ruleFile, resource);
           
        }*/
        
        
        
        kieFileSystem.write("src/main/resources/PrivateRules/PrivateRules.drl", getRule());
        kb = kieServices.newKieBuilder(kieFileSystem);
        kb.buildAll();
  
        if (kb.getResults().hasMessages(Level.ERROR))
        {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }
        kContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
	    //kContainer = kieServices.getKieClasspathContainer();
    	//kSession = kContainer.newKieSession("PrivateSession");
        kSession = kContainer.newKieSession();
        for (Object fact : facts)
        {
            kSession.insert(fact);
        }
        kSession.fireAllRules();
    }
    
    public void addFact(Object fact){
	    try{
	    	Object f=fact;
	    	//System.out.println(f.getClass().toString());
	    	System.out.println(f.toString());
	    	//FactType personType = kbase.getFactType( "org.drools.examples","Person" );

 	    	kSession.insert(f);
	        //kSession.fireAllRules();
	     } catch (Throwable t) {
	        t.printStackTrace();
	    }
    }
    
    public Collection<? extends Object> getFacts(){
	    try{
	    	return kSession.getObjects();
	     } catch (Throwable t) {
	        t.printStackTrace();
	    }
		return null;
    }
    //prova a importare un DRL da stringa
    private String getRule() {
       String s = "" +
       "package PrivateRules \n" +
       "declare Lampadina \n" +
       "codice	: String \n" +
       "accesa	: Boolean \n" +
       "spenta	: Boolean \n" +
       "end \n"+
       "rule \"rule 1\" when \n" +
       "    $f:Lampadina(accesa==true) \n" +
       "then \n" +
        "   System.out.println( \"Lampadina accesa da nuovo drl!\" ); \n" +
       "end \n" + 
       "rule \"rule 2\" when \n" +
       "    $f:Lampadina(spenta==true) \n" +
       "then \n" +
        "   System.out.println( \"Lampadina spenta da nuovo drl!\" ); \n" +
       "end \n" + 
       "rule \"rule 3\" when \n" +
       "    eval(true) \n" +
       "then \n" +
        "   System.out.println( \"funge da nuovo drl!\" ); \n" +
       "end \n";
       
      System.out.println(s);
      return s;
    }
    //Chiedi i fatti al manager, spara le regole, aggiorna fatti sul manager
    public void matchResolveAct() throws RemoteException{
    	sharedFacts = wois.getSharedFacts();
    	
    	Object fact = sharedFacts.get(0);
        
        
        //devo inserire il fatto che ho ricevuto
        this.addFact(fact);
        
        fireAllRules();
        
        Collection<? extends Object> oggettiDaWM = this.getFacts();
        for (Object ogg : oggettiDaWM)
        {
        	System.out.println(ogg.getClass().toString());
	    	System.out.println(ogg.toString());
        }
    }
    public void fireAllRules(){
    	kSession.fireAllRules();
    }
}
