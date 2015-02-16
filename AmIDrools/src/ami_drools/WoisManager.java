package ami_drools;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.Vector;

import utility.ResultSetSerializable;


public interface WoisManager extends Remote {

	/**
     * Adds a {@link Is Is} to this WoIS. Current shared facts and templates are returned.
     * 
     * @param inf a <code>Is</code> object to add to this WoIS
     * @param name name of the object in this WoIS
     * @return the state of the shared working memory of this WoIS.
     * @throws RemoteException
     * @throws AlreadyRegisteredException if the name is already in use by another IS or if
     *             <code>dr</code> was already registered with another name
     * @throws JessException
     * @see DjReteIntf for a discussion on asynchronous registration
     */
	/**
	 * Add an IS to a WoIS
	 * @param inf
	 * @param name
	 * @throws RemoteException
	 */
    void addMember( IsIntf inf, String name ) throws RemoteException;
	
    /**
     * Shared Facts of the WoIs.
     * @return
     * @throws RemoteException
     */
    Vector <Fact> getSharedFacts() throws RemoteException;
    
    /**
     * Update shared facts of the WoIS
     * @param sharedFacts
     * @throws RemoteException
     */
    void setSharedFacts(Vector <Fact> sharedFactsUpdate, String isName) throws RemoteException, ClassNotFoundException;
    /**
     * Return true if lock is active
     * @param idFact
     * @return
     * @throws RemoteException
     */
    boolean getLock(String idFact, String isId) throws RemoteException;
    /**
     * Return true if the method success
     * @param idFact
     * @return
     * @throws RemoteException
     */
    boolean setLock(String idFact, String isId) throws RemoteException;
    /**
     * 
     * @return a String containing the list of shared facts templates
     * @throws RemoteException
     */
    String getSharedTemplates() throws RemoteException;
    
    String getSharedFunctions() throws RemoteException;
    
    String getSharedRules(String des_user) throws RemoteException;
    /**
     * 
     * @param inf
     * @throws RemoteException
     * @throws NotRegisteredException
     */
    void removeMember( IsIntf inf ) throws RemoteException, NotRegisteredException;
    
    boolean newModelWithAttribute(String des_model, int id_user, boolean if_model, boolean then_model, Vector<String> des_attribute, Vector<String> type_attribute) throws RemoteException;
    String RulesThenFactsDetailsInsert_manager(String des_attribute,String des_model, String operation, String value) throws RemoteException;
    String RulesIfFactsDetailsInsert_manager(String des_attribute,String des_model, String operation, String value) throws RemoteException;
    String RulesThenFactsInsert_manager(String des_model, String var_name) throws RemoteException;
    String RulesIfFactsInsert_manager(String des_model, String var_name) throws RemoteException;
    String RulesInsert_manager(String name, String des_user, boolean no_loop, Integer salience, boolean _public) throws RemoteException;
    
    Vector<String> getRulesNames(String ISName) throws RemoteException, NotRegisteredException;
    
    boolean newRule(String SQL) throws RemoteException;
    boolean deleteRules(String ruleName, String ISName) throws RemoteException;
    
    String say() throws RemoteException;
    
    ResultSetSerializable getUsersModels(String des_model, Integer id_user) throws RemoteException;
    ResultSetSerializable getPublicModelsIf() throws RemoteException;
    ResultSetSerializable getPublicModelsThen() throws RemoteException;
    ResultSetSerializable getPublicModel(int id_model) throws RemoteException;
    ResultSetSerializable getAttributesFromModel(int id_model) throws RemoteException;
    ResultSetSerializable getTypeOfAttributes(int id_attribute) throws RemoteException;
    
    
}
