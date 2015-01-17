package ami_drools;

import java.rmi.Remote;
import java.rmi.RemoteException;



public interface IsIntf extends Remote {

	String getIsName() throws RemoteException;
	
}
