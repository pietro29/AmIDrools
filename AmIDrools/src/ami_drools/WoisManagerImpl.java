package ami_drools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import sharedFacts.Lampadina;

public class WoisManagerImpl extends UnicastRemoteObject implements WoisManager {

	/**
     * Version ID used by deserialization in J2SE >= 1.5.0.
     */
    private static final long serialVersionUID = 1;

    /**
     * Table containing all the members of this WoIS. It maps names ({@link String}s) to engines ({@link IsIntf}s).
     */
    private Hashtable members = new Hashtable();
    
    /**
     * Table containing all the lock of the facts in this WoIS
     */
    private Hashtable<String, Lock> locks = new Hashtable<String, Lock>();
    
    /**
     * Map from engines ({@link IsIntf}s) to their names ({@link String}s). For data consistency,
     * access should be synchronized on {@link #members}.
     */
    private Map mNames = new HashMap(); 
    /**
     * Map of the fact and their ID
     */
    private Map mFacts = new HashMap();
    /**
     * Map of the device and their ID
     */
    private Map mDevices = new HashMap();
    /**
     * Map of the registered users
     */
    private Map mUsers = new HashMap();
    /**
     * Map of the priorities
     */
    private Map mPriorities = new HashMap();
    /**
     * Name of the controlled WoIS
     */
    private final String woisName;
    
    /**
     * URL used for binding to the RMI registry.
     */
    private final String bindingUrl;
    
    /**
     * Shared facts vector
     */
    private Vector<Fact> sharedFacts;
    
    /**
     * Assertion object vector
     */
    private Vector<Assertion> assertions;
    
    private Thread checklockdate;
    
    private Thread checkIs;
    
    /**
     * Constructor that requires the name of the new WoIS.
     * @param name      the name for this WoIS.  It can be also a full URL (//host:port/name).
     * @throws RemoteException
     * @throws AlreadyBoundException    if <code>name</code> is already used in the RMI registry
     * @throws MalformedURLException    if <code>name</code> is not valid
     */
    public WoisManagerImpl( String name ) throws RemoteException, AlreadyBoundException, MalformedURLException
    {
        super();
        woisName = Wois.stripHost( name );
        bindingUrl = name;
        sharedFacts = new Vector<Fact>();
        assertions = new Vector<Assertion>();
        
        boolean done = false;
        try {
            Naming.rebind( name, this ); // FIXME: must be bind, not rebind
            done = true;
        } finally {
            // If bind fails, unexport this object
            if (! done)
                try {
                    UnicastRemoteObject.unexportObject( this, true );
                } catch (NoSuchObjectException ee) {
                }
        }
        //DEVICE 1
        Lampadina lampadina = new Lampadina("1","lampadina1",true,true);
        mDevices.put(lampadina.getId(), lampadina);
        
        Fact fatto = new Fact("1","Lampadina");
        fatto.insertAttributeValue("codice", "String", "lampadina1");
        fatto.insertAttributeValue("accesa", "Boolean", "true");
        fatto.insertAttributeValue("spenta", "Boolean", "true");
        
      //Aggiungo gli oggetti al vettore dei fatti condivisi
        sharedFacts.add(fatto);
        mFacts.put(fatto.getId(), fatto);
        
        locks.put(fatto.getId(),new Lock(fatto.getId()) );
        
        //DEVICE 2
        Lampadina lampadina2 = new Lampadina("2","lampadina2",true,true);
        mDevices.put(lampadina2.getId(), lampadina2);
        
        Fact fatto2 = new Fact("2","Lampadina");
        fatto2.insertAttributeValue("codice", "String", "lampadina2");
        fatto2.insertAttributeValue("accesa", "Boolean", "true");
        fatto2.insertAttributeValue("spenta", "Boolean", "true");
        
        sharedFacts.add(fatto2);
        mFacts.put(fatto2.getId(), fatto2);
        
        locks.put(fatto2.getId(),new Lock(fatto2.getId()) );
        
        //Read priority config. file
        getPrioritiesTable();
        
        
        //Start lock thread
        checklockdate=new Thread("Check lock"){
        	public void run(){
        		try {
					checkLockProcess();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        };
        checklockdate.start();
        
        //Start IS thread
        checkIs = new Thread("Check IS"){
        	public void run(){
        		try {
					checkIsProcess();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotRegisteredException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        };
        checkIs.start();
    }
    
    private void checkLockProcess() throws InterruptedException{
    	while(true){
    		checkLockStatus();
        	checklockdate.sleep(5000);
    	}
    	
    }
    private void checkLockStatus(){
    	Iterator<Map.Entry<String, Lock>> it = locks.entrySet().iterator();
    	Date dateToCompare=new Date();
    	//System.out.println(dateToCompare.getTime());
    	while (it.hasNext()) {
    		Map.Entry<String, Lock> entry = it.next();
    		if(entry.getValue().getLock()){
    			//System.out.println(entry.getValue().getDateLocked().getTime());
    			if(dateToCompare.getTime()-entry.getValue().getDateLocked().getTime()>5000){
    				entry.getValue().unLock();
    			}
    		}
    	}
    }
    private void checkIsProcess() throws InterruptedException, NotRegisteredException{
    	while(true){
    		checkISStatus();
    		checkIs.sleep(600000);
    	}
    	
    }
    private void checkISStatus() {
    	Iterator<Map.Entry<String, IsIntf>> it = members.entrySet().iterator();

    	while (it.hasNext()) {
    		Map.Entry<String, IsIntf> entry = it.next();
    		try {
				if(! entry.getValue().getIsName().equals(entry.getKey().toString())){
					removeMember(entry.getValue());
				} else {
					System.out.println("Il dispositivo è connesso");
				}
			} catch (RemoteException e) {
				//e.printStackTrace();
				System.out.println("Il dispositivo non risponde");
				try {
					removeMember(entry.getValue());
				} catch (RemoteException | NotRegisteredException e1) {
					e1.printStackTrace();
				}
			} catch (NotRegisteredException e) {
				System.out.println("Il dispositivo non è registrato");
			}
    	}
    }
    /**
     * Destroys this WoIS.
     * @throws RemoteException
     */
    public void destroy() throws RemoteException
    {
        // TODO: Kick out all the ISs
        try {
            Naming.unbind( bindingUrl );
        } catch (MalformedURLException e) {
        } catch (NotBoundException e) {
        } finally {
            try {
                UnicastRemoteObject.unexportObject( this, true );
            } catch (NoSuchObjectException e) {
            }
        }
    }
    
    /**
     * Insert a new engine in the member of this WoIS.
     * @param inf the engine.
     * @param name the name of the engine in this WoIS.
     * @throws AlreadyRegisteredException if the name is already in use by another IS or if
     *             <code>dr</code> was already registered with another name.
     */
    protected void insertMember( IsIntf inf, String name ) throws AlreadyRegisteredException
    {
        synchronized (members) {
            Object v1 = members.get( name );
            if (v1 != null && ! v1.equals( inf ) ){
            	throw new AlreadyRegisteredException( name + " is already in use" );
            }
            Object v2 = mNames.get( inf ); 
            if (v2 != null && ! v2.equals( name )){
                throw new AlreadyRegisteredException( inf + " is already subscriped with another name" );
            }
            Object old = members.put( name, inf );
            if (old != null) {
                // assert v1 == dr;
            }
            mNames.put( inf, name );
            User user = new User(name,name, getUserPriority(name) );
            mUsers.put(name, user);
        }
    }
    public void addMember( IsIntf inf, String name ) throws RemoteException
    {
        // IMPORTANT: update members before getting templates and facts */
        try {
			insertMember( inf, name );
		} catch (AlreadyRegisteredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public int getUserPriority(String infName){
    	return (int) mPriorities.get(infName);
    }
    public IsIntf[] getMemberList() throws RemoteException
    {
        IsIntf[] ret;
        synchronized (members) {
            Enumeration en = members.elements();
            ret = new IsIntf[ members.size() ];
            for (int i = 0; en.hasMoreElements(); ++i) {
                ret[i] = (IsIntf)en.nextElement();
            }
        }
        return ret;
    }
    
    public void removeMember( IsIntf inf ) throws RemoteException, NotRegisteredException
    {
        synchronized (members) {
            Object name = mNames.remove( inf );
            if (name == null)
                throw new NotRegisteredException( "IS not found: " + inf );
            Object old = members.remove( name );
            if (old == null) {
                throw new RuntimeException( "Internal error: incosistent maps while removing " + name + " " + inf );
            }
            removeIsData(name.toString());
        }
    }
    private void removeIsData(String isName){
    	//Remove all IS assertions
    	for(int i=0; i<assertions.size();i++){
    		if(assertions.get(i).getUser().getName().equals(isName)){
    			assertions.remove(i);
    		}
    	}
    	//Remove from the registered user
    	mUsers.remove(isName);
    	//Remove all user lock
    	Iterator<Map.Entry<String, Lock>> it = locks.entrySet().iterator();
    	while (it.hasNext()) {
    		Map.Entry<String, Lock> entry = it.next();
    		if (entry.getValue().getIsId().equals(isName)){
    			entry.getValue().unLock();
    		}
    	}
    }
    /**
     * return the vector of the shared facts of the WoIS
     */
    public synchronized Vector <Fact> getSharedFacts(){
    	for (Fact fact: sharedFacts){
    		fact.removeAllModifiedAttributed();
    	}
    	return sharedFacts;
    }
    /**
     * @throws ClassNotFoundException 
     * 
     */
    public synchronized void setSharedFacts(Vector <Fact> sharedFactUpdate, String isName) throws ClassNotFoundException{
    	Vector <String> tempAttr;
    	Vector <String> tempVal;
    	List tempModified;
    	String tempId;
    	String tempFactType;
    	Fact factToUpdate;
    	Boolean update = true;
    	User tempUsr;
    	
    	
    		
	    	for (Fact fact : sharedFactUpdate){
	    		
	    		tempAttr = fact.getAttributes();
	    		tempVal = fact.getValues();
	    		tempModified = fact.getModified();
	    		tempFactType = fact.getFactType();
	    		factToUpdate = (Fact) mFacts.get(fact.getId());
	    		tempUsr =(User) mUsers.get(isName);
	    		for (int i=0;i<tempAttr.size();i++){
	    			//If the array of the modified attribute, check the priority table and then (if check returns true) update the object (and the priority table)
	    			if (tempModified.contains(tempAttr.get(i))){
	    				
	    				//se trovo un asserzione precedente devo controllare la priorità dell'utente che tenta di modificare il valore dell'attributo
	    				update = checkAssertionPriority( fact.getId(), tempAttr.get(i),tempUsr.getPriority());
	    				
	    				if (update){
	    					
	    					//aggiorno il valore dell'attributo
		    				factToUpdate.updateAttributeValue(tempAttr.get(i), tempVal.get(i));
		    				//gestisco la tabella delle asserzioni
		    				updateAssertionTable(tempAttr.get(i),tempUsr,fact);
		    				//aggiorno i device specifici
			    			switch(tempFactType){
			    			case "Lampadina" : Class cls = Class.forName("sharedFacts." + tempFactType);
			    								Lampadina l = (Lampadina) cls.cast(mDevices.get(fact.getId()));
			    								l.updateField(tempAttr.get(i), tempVal.get(i));
			    								break;
			    			}
			    			//Set lock to false
			    			locks.get(fact.getId()).unLock();
	    				}	
	    			}
	    		}
	    	}
    }
    /**
     * Check if the Is can update an attribute value
     * @param idDevice 
     * @param attribute
     * @param priority
     * @return
     */
    public boolean checkAssertionPriority(String idDevice, String attribute, int priority){
    	boolean check = true;
    	for (int i=0;i<assertions.size();i++){
    		Assertion asrt = assertions.get(i);
    		Fact asserctionFact = asrt.getFact();
    		User asserctionUser = asrt.getUser();
    		String asserctionAttribute = asrt.getAttribute();
    		if (asserctionFact.getId()==idDevice  && asserctionAttribute == attribute && asserctionUser.getPriority()>priority){
    			check=false;
    		}
    	}
    	return check;
    }
    /**
     * Manage the assertions vector. Delete lower priority assertion, insert new assertion.
     * @param attribute
     * @param user
     * @param fact
     */
    public void updateAssertionTable( String attribute, User user, Fact fact){
    	boolean insert = true;
    	
    	for (int i=0;i<assertions.size();i++){
    		Assertion asrt = assertions.get(i);
    		Fact asserctionFact = asrt.getFact();
    		User asserctionUser = asrt.getUser();
    		String asserctionAttribute = asrt.getAttribute();
    		
    		if (asserctionFact.getId()==fact.getId()  && asserctionAttribute == attribute){
    			if (asserctionUser.getPriority()<user.getPriority()){
    				assertions.remove(i);
    			}
    			if (asserctionUser.getPriority()==user.getPriority()){
    				if(asserctionUser.getId()==user.getId()){
    					insert=false;
    				}
    			}
    		}
    		
    	}
    	if(insert){
    		Assertion newAsrt = new Assertion(user, fact, attribute);
    		assertions.add(newAsrt);
    	}
    }
    
    public boolean getLock(String idFact, String isId){
    	synchronized (locks) {
    		if (!locks.get(idFact).getLock())
    			return false;
    		else{
    			if (locks.get(idFact).getIsId().equals(isId))
    				return false;
    			else
    				return true;
    		}
    	}
    }
    public boolean setLock(String idFact, String isId){
    	synchronized (locks) {
    		if (!getLock(idFact, isId)){
        		locks.get(idFact).setLock(isId);
        		return true;
        	} else 
        		return false;
		}
    	
    }
    public String getSharedTemplates(){
    	return getStringFromFile("/shared_declare.txt");
    }
    public String getSharedFunctions(){
    	return getStringFromFile("/shared_function.txt");
    }
    public String getSharedRules(){
    	return getStringFromFile("/shared_rules.txt");
    }
    private String getStringFromFile(String fileName) {
		String s = "" ;
		try {
			BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + fileName ));
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
		//System.out.println(s);
		return s;
	}
    private void getPrioritiesTable(){
    	String s="";
    	try {
			BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/wois_priorities.txt"));
			StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    String[] parts;
		    while (line != null) {
		         sb.append(line);
		         sb.append(System.lineSeparator());
		         
		         parts = line.split(":");
		         mPriorities.put(parts[0], Integer.parseInt(parts[1]));
		            line = br.readLine();
		    }
		    s = sb.toString();
		    br.close();
		    } catch (Throwable t) {
		    	System.err.println(t.toString());
		    	s="";
		    }
		//System.out.println(mPriorities.get("pippo"));
    	
    }
    /**
     * Main function
     * @param args  <code>args[0]</code> is the name of the new WoIS
     */
    public static void main( String[] args ) throws Exception
    {
        BufferedReader bf = new BufferedReader( new InputStreamReader( System.in ) );
        WoisManagerImpl mw = new WoisManagerImpl( args[0] );
        
        System.out.print( "Manager of " + args[0] + " started. Press <enter> to see a list of members.\n\n" );

        while (true) {
            bf.readLine();
            
            IsIntf[] parts = mw.getMemberList();
            if (parts.length > 0) {
                System.out.println( "\nMembers:" );
                for (int i = 0; i < parts.length; ++i)
                    System.out.println( (i+1) + ". " + mw.mNames.get( parts[i] ) + "  " + parts[i] );
            } else
                System.out.println( "\nMember list is empty");
            
        }
        // nothing else to do
    }
}
