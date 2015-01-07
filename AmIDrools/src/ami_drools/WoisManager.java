package ami_drools;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;


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
	//Per ora non ritorna nulla perchè bisogna decidere cosa ritornare (fatti, template, ...)
    void addMember( IsIntf inf, String name ) throws RemoteException;
	
    //Metodo per il passaggio dei fatti condivisi
    Vector <Fact> getSharedFacts() throws RemoteException;
}
