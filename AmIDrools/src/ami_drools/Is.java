package ami_drools;

import java.applet.Applet;
import java.lang.reflect.InvocationTargetException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.WeakHashMap;


class IsRemote extends UnicastRemoteObject implements IsIntf 
{
	/** Version ID used by deserialization in J2SE >= 1.5.0. */
    private static final long serialVersionUID = 1;
    
    private Is parent;
    
    IsRemote(Is parentIs) throws RemoteException
    {
    	super();
    	parent=parentIs;
    }
    
}

public class Is {
	
	 /**
     * WoISs this object belongs to (if any). Elements are of type {@link WoisRegistration}. This
     * field is very important, as this collection contains the information about which WoISs are
     * considered subscribed to. External notifications (through
     * {@link #addGhostFact(GhostFact, boolean) addGhostFact} and so on) regarding any WoIS not
     * subscribed to are ignored.
     */
    private Vector woises;
    /** Remote object, used for communication between ISs */
    private IsRemote remoteObject;
    
	public Is() throws RemoteException
	{
		woises = new Vector();
		remoteObject = new IsRemote( this );
	}
	
	public IsIntf getRemoteProxy()
    {
        return remoteObject;
    }
	
	//Meccanismo di registrazione di un Is, ricordarsi di aggiungere un controllo per evitare di duplicare le registrazioni
	public void register( Wois wois, String name ) throws RemoteException
	{
		// Create a module, if needed
        final String modName = wois.getName();
        
     // Update the internal data structures
        WoisRegistration rw;
        synchronized (woises) {
            rw = findRegWoISName( wois.getName() );
            //if (rw != null && rw.isRegistered()) throw new AlreadyRegisteredException();
            if (rw == null) {
                rw = new WoisRegistration( wois, modName, name );
                woises.add( rw );
            }
            rw.state = WoisRegistration.ENTERING;
        }
        wois.register( this, rw );
        
	}
	/**
     * Returns the WoIS registration associated with the WoIS with the given name. Returns
     * <code>null</code> if there is no such WoIS.
     * 
     * @param name name of the WoIS to search for.
     * @return the registration for the WoIS with the given name or <code>null</code>.
     */
    WoisRegistration findRegWoISName( String name )
    {
        synchronized (woises) {
            Iterator i = woises.iterator();
            while (i.hasNext()) {
                WoisRegistration rw = (WoisRegistration)i.next();
                if (name.equals( rw.defmodule ))
                    return rw;
            }
        }
        return null;
    }
}
