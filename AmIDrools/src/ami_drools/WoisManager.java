package ami_drools;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface WoisManager extends Remote {

	/**
     * Adds a {@link Is Is} to this WoIS. Current shared facts and templates are returned.
     * 
     * @param dr a <code>DjRete</code> object to add to this WoIS
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
	
    //Metodo con cui un Is scarica il file delle regole condivise per la rete a cui è collegato
    byte[] downloadFile(String fileName) throws RemoteException;
}
