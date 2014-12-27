package ami_drools;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

//test

public class Wois implements Serializable {

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
