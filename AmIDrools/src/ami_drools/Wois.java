package ami_drools;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.Vector;

import utility.ResultSetSerializable;
import utility.rulesSQLManager;



public class Wois implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * The manager of this WoIS.  This field is a remote reference and cannot be <code>null</code>.
     */
    private WoisManager woisManager;
    
    /**
     * Name of this WoIS.
     */
    private String name;
    
    /**
     * Creates a <code>Wois</code> object that points to a WoIS identified by <code>id</code>. 
     * @param id    identification (name) of the WoIS. It can be a full URL (//host:port/name).
     * @throws RemoteException          if there are problems with RMI registry
     * @throws NotBoundException        if <code>id</code> is not currently bound.
     * @throws MalformedURLException    if <code>id</code> is not valid.
     */
    public Wois( String id ) throws RemoteException, NotBoundException, MalformedURLException
    {
        try {
            woisManager = (WoisManager)java.rmi.Naming.lookup( id );
            name = stripHost( id );
        } catch (ClassCastException e) {
            throw new RemoteException( "RMI registry corrupted", e );
        }
    }
    
    /**
     * Registers the given <code>Is</code> object in this WoIS. 
     * @param inf a <code>Is</code> object to register
     * @param rWois info about this Wois registration. The {@link WoisRegistration#membershipFact}
     *            field is filled by this method; the other fields should be already initialized.
     * @throws RemoteException if there are network problems
     * @throws AlreadyRegisteredException if the name is already in use by another IS or if
     *             <code>inf</code> was already registered with another name
     */
    void register( Is inf, WoisRegistration rWois ) throws RemoteException
    {
      woisManager.addMember(inf.getRemoteProxy(), rWois.name);
    }
    void unregister(Is inf) throws RemoteException,NotRegisteredException{
    	woisManager.removeMember(inf.getRemoteProxy());
    }
    String getSharedFactsTemplates() throws RemoteException{
    	return woisManager.getSharedTemplates();
    }
    String getSharedFactsRules(String des_user) throws RemoteException{
    	return woisManager.getSharedRules(des_user);
    }
    String getSharedFactsFunctions() throws RemoteException{
    	return woisManager.getSharedFunctions();
    }
    public boolean getLock(String idFact, String isId) throws RemoteException{
    	return woisManager.getLock(idFact, isId);
    }
    public boolean setLock(String idFact, String isId) throws RemoteException{
    	return woisManager.setLock(idFact, isId);
    }
    
    public Vector <Fact> getSharedFacts() throws RemoteException{
    	return woisManager.getSharedFacts();
    }
    
    public Vector <String> getRulesNames(String ISName) throws RemoteException, NotRegisteredException{
    	return woisManager.getRulesNames(ISName);
    }
    public ResultSetSerializable getUsersModels(String des_model, Integer id_user) throws RemoteException, NotRegisteredException{
    	return woisManager.getUsersModels(des_model, id_user);
    }
    public boolean newModelWithAttribute(String des_model, int id_user,
			boolean if_model, boolean then_model, Vector<String> des_attribute,
			Vector<String> type_attribute) throws RemoteException{
    	return woisManager.newModelWithAttribute(des_model, id_user, if_model, then_model, des_attribute, type_attribute);
    }
    public boolean newRule(String SQL) throws RemoteException{
    	return woisManager.newRule(SQL);
    }
    public boolean deleteRules(String ruleName, String ISName) throws RemoteException{
    	return woisManager.deleteRules(ruleName, ISName);
    }
    public ResultSetSerializable getPublicModelsIf() throws RemoteException{
    	return woisManager.getPublicModelsIf();
    }
    public ResultSetSerializable getPublicModelsThen() throws RemoteException{
    	return woisManager.getPublicModelsThen();
    }
    public ResultSetSerializable getAttributesFromModel(int id_model) throws RemoteException{
    	return woisManager.getAttributesFromModel(id_model);
    }
    public ResultSetSerializable getTypeOfAttributes(int id_attribute) throws RemoteException{
    	return woisManager.getTypeOfAttributes(id_attribute);
    }
    public ResultSetSerializable getPublicModel(int id_model) throws RemoteException{
    	return woisManager.getPublicModel(id_model);
    }
    public void setSharedFacts(Vector <Fact> sharedFactsUpdate, String isName) throws RemoteException, ClassNotFoundException{
    	woisManager.setSharedFacts(sharedFactsUpdate, isName);
    	//System.out.println(sharedFactsUpdate.get(0).getAttributes().get(1) + " - "+ sharedFactsUpdate.get(0).getValues().get(1));
    	//System.out.println(sharedFactsUpdate.get(0).getAttributes().get(2) + " - "+ sharedFactsUpdate.get(0).getValues().get(2));
    	//System.out.println(sharedFactsUpdate.get(0).getModified().size());
    }
    
    public String RulesThenFactsDetailsInsert_manager(String des_attribute,String des_model, String operation, String value) throws RemoteException{
		return woisManager.RulesThenFactsDetailsInsert_manager(des_attribute,des_model,operation,value);
	}
    
    public String RulesIfFactsDetailsInsert_manager(String des_attribute,String des_model, String operation, String value) throws RemoteException{
		return woisManager.RulesIfFactsDetailsInsert_manager(des_attribute,des_model,operation,value);
	}
    
    public String RulesThenFactsInsert_manager(String des_model, String var_name) throws RemoteException{
		return woisManager.RulesThenFactsInsert_manager(des_model,var_name);
	}
    
    public String RulesIfFactsInsert_manager(String des_model, String var_name) throws RemoteException{
		return woisManager.RulesIfFactsInsert_manager(des_model,var_name);
	}
    
    public String RulesInsert_manager(String name, String des_user, boolean no_loop, Integer salience, boolean _public) {
		return rulesSQLManager.RulesInsert_manager(name,des_user,no_loop,salience,_public);
	}
    /**
     * Extracts the name of a resource from an RMI URL.
     * @param url a URL as in //host:port/name or just a name.
     * @return the name of the RMI resource indentified by the given URL.
     * @throws MalformedURLException if <code>url</code> doesn't conform to the specifications.
     */
    public static String stripHost( String url ) throws MalformedURLException
    {
        if (url.startsWith( "//" )) {
            int s = url.indexOf( '/', 2 );
            if (s == -1 || s+1 >= url.length()) throw new MalformedURLException( url );
            return url.substring( s + 1 );
        } else
            return url;
    }
    /**
     * Returns the name of this WoIS.
     * @return the name of this WoIS.
     */
    public String getName()
    {
        return name;
    }
}
