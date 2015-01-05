package ami_drools;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.Vector;
import java.util.Collection;
import org.drools.core.rule.FactType;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import javax.swing.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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

public class Is extends JFrame implements ActionListener{
	
	 /**
     * WoISs this object belongs to (if any). Elements are of type {@link WoisRegistration}. This
     * field is very important, as this collection contains the information about which WoISs are
     * considered subscribed to. External notifications (through
     * {@link #addGhostFact(GhostFact, boolean) addGhostFact} and so on) regarding any WoIS not
     * subscribed to are ignored.
     */
	private static final long serialVersionUID = 1L;
    private Vector woises;
    private Vector runners;
    /** Remote object, used for communication between ISs */
    private IsRemote remoteObject;
    
    //Papu
    JPanel p;
	JButton b;
	JLabel lInfo;
	JLabel lError;
	JTextArea txtServer;
	
	KieServices ks ;
    KieContainer kContainer ;
	KieSession kSession ;
	
	RuleRunner runner;
	//specifico il drl
	String[] rules=new String[1];
	//specifico i fatti
	Object[] facts=new Object[5];
    //
    
    
	public Is() throws RemoteException
	{
		woises = new Vector();
		runners = new Vector();
		remoteObject = new IsRemote( this );
		
		p=new JPanel();
    	lInfo=new JLabel();
    	lError=new JLabel();
    	b=new JButton("Chiedi");
    	txtServer=new JTextArea();
    	b.addActionListener((ActionListener) this);
    	p.add(lInfo);
    	p.add(b);
    	p.add(lError);
    	//p.add(txtServer);
    	this.add(p);
    	
    	
		
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
        //Istanzio un runner per la rete a cui mi registro
        try {
        	//gestisce la KB e le regole, ora non è usato
        	runner = new RuleRunner(wois);
        	String DRL = new String("PrivateRule.drl");
        	rules[0] = DRL;
            runner.runRules(rules,facts);
        	
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
    @Override
    public void actionPerformed(ActionEvent event){
    	if (event.getSource()==b)
	    {
            //String host = (args.length < 1) ? null : args[0];
            try {
            	lInfo.setText("dentro");
              
            	runner.matchResolveAct();
                
                //gli oggetti sono caricati correttamente
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();}
	    	
    	}
    }
}
