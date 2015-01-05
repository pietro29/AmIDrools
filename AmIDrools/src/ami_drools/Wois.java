package ami_drools;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;


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
     * Registers the given <code>Is</code> object in this WoIS. Also adds templates and facts
     * shared in the WoIS to <code>inf</code> and adds a <code>wois-member</code> fact for the
     * new member.
     * 
     * @param inf a <code>Is</code> object to register
     * @param rWois info about this Wois registration. The {@link WoisRegistration#membershipFact}
     *            field is filled by this method; the other fields should be already initialized.
     * @throws RemoteException if there are network problems
     * @throws AlreadyRegisteredException if the name is already in use by another IS or if
     *             <code>dr</code> was already registered with another name
     * @throws JessException
     */
    void register( Is inf, WoisRegistration rWois ) throws RemoteException
    {
      woisManager.addMember(inf.getRemoteProxy(), rWois.name);
    }
    
    
    public byte[] downloadFile(String fileName) throws RemoteException{
    	return woisManager.downloadFile(fileName);
    }
    
    public Vector <Object> getSharedFacts() throws RemoteException{
    	return woisManager.getSharedFacts();
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
