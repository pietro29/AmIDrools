package ami_drools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

public class WoisManagerImpl extends UnicastRemoteObject implements WoisManager {

	/**
     * Version ID used by deserialization in J2SE >= 1.5.0.
     */
    private static final long serialVersionUID = 1;

    /**
     * Table containing all the members of this WoIS. It maps names ({@link String}s) to engines ({@link DjReteIntf}s).
     */
    private Hashtable members = new Hashtable();
    
    /**
     * Map from engines ({@link DjReteIntf}s) to their names ({@link String}s). For data consistency,
     * access should be synchronized on {@link #members}.
     */
    private Map mNames = new HashMap(); 
    
    /**
     * Name of the controlled WoIS
     */
    private final String woisName;
    
    /**
     * URL used for binding to the RMI registry.
     */
    private final String bindingUrl;
    
    /** All the templates shared in this WoIS. It contains objects of type {@link SharedTemplate}. */
    private Vector templates;
    
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
        templates = new Vector();
        //templates.add( new SharedTemplate( woisName + "::" + SharedRuleProxy.templateName, null, SharedRuleProxy.class.getName(), SharedRuleProxy.defaultValues ) );
        //templates.add( new SharedTemplate( woisName + "::" + WoisMemberProxy.templateName, null, WoisMemberProxy.class.getName(), WoisMemberProxy.defaultValues ) );
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
     * @param dr the engine.
     * @param name the name of the engine in this WoIS.
     * @throws AlreadyRegisteredException if the name is already in use by another IS or if
     *             <code>dr</code> was already registered with another name.
     */
    protected void insertMember( IsIntf inf, String name )
    {
        synchronized (members) {
            Object v1 = members.get( name );
            if (v1 != null && ! v1.equals( inf ) ){
            	//throw new AlreadyRegisteredException( name + " is already in use" );
            }
            Object v2 = mNames.get( inf ); 
            if (v2 != null && ! v2.equals( name )){
                //throw new AlreadyRegisteredException( inf + " is already subscriped with another name" );
            }
            Object old = members.put( name, inf );
            if (old != null) {
                // assert v1 == dr;
            }
            mNames.put( inf, name );
        }
    }
    public void addMember( IsIntf inf, String name ) throws RemoteException
    {
        // IMPORTANT: update members before getting templates and facts */
        insertMember( inf, name );
        //SharedMemoryImage ret = getMemoryImage();
        //DebugIO.fprintlnDebug( "Returning " + ret );
        //return ret;
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
