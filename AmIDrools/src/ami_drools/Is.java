package ami_drools;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Collection;

import org.drools.core.rule.FactType;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import sharedFacts.Lampadina;

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
     * considered subscribed to.
     */
	private static final long serialVersionUID = 1L;
    private Vector<WoisRegistration> woises; 
    private Vector<RuleRunner> runners;
    /** Remote object, used for communication between ISs */
    private IsRemote remoteObject;
    /** Name of the Is */
    private String name;
    /** Private fact */
    private Position pos;
    /**
     * Shared facts vector
     */
    private Vector<Fact> privateFacts;
    /**
     * Map of the device and their ID
     */
    private Map mDevices = new HashMap();
    /**Graphic elements*/
    JPanel p;
	JButton bManager;
	JButton bLocal;
	JLabel lInfo;
	JLabel lError;
	JTextArea txtServer;
	/**Rule engine elements*/
	KieServices ks ;
    KieContainer kContainer ;
	KieSession kSession ;
	
	RuleRunner runner;

    //
    
    
	public Is(String name) throws RemoteException
	{	
		woises = new Vector<WoisRegistration>();
		runners = new Vector<RuleRunner>();
		privateFacts=new Vector<Fact>();
		remoteObject = new IsRemote( this );
		this.name=name;
		pos = new Position("id1",1,"Soggiorno");
		
		p=new JPanel();
    	lInfo=new JLabel();
    	lError=new JLabel();
    	bManager=new JButton("Connect To Manager");
    	bLocal=new JButton("Run Engine");
    	txtServer=new JTextArea();
    	bManager.addActionListener((ActionListener) this);
    	bLocal.addActionListener((ActionListener) this);
    	p.add(lInfo);
    	p.add(bManager);
    	p.add(bLocal);
    	p.add(lError);
    	//p.add(txtServer);
    	this.add(p);
    	pos=new Position("1p", 1, "soggiorno");
    	mDevices.put(pos.getId(), pos);
    	runners.add(createEngine());
    	
	}
	/**
	 * add the private fact to the WM and run the engine
	 */
	private RuleRunner createEngine()
	{
		runner = new RuleRunner(name);
		Fact privateFact = new Fact("1p","Position");
		privateFact.insertAttributeValue("location", "int", "1");
		privateFact.insertAttributeValue("codice", "String", "soggiorno");
		privateFact.insertAttributeValue("_privateVisibility", "Boolean", "true");
		privateFacts.add(privateFact);
    	runner.runRules(privateFacts);
    	return runner;
	}
	
	public IsIntf getRemoteProxy()
    {
        return remoteObject;
    }
	
	/**
	 * Register to a given wois
	 * @param wois
	 * @param name
	 * @throws RemoteException
	 */
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
        runner.setWois(wois);
	}
	
	public void unregister( Wois wois) throws RemoteException, NotRegisteredException
    {
       
        WoisRegistration rw = null;
        try {
            synchronized (woises) {
                rw = findRegWois( wois );
                if (rw == null || ! rw.isRegistered()) throw new NotRegisteredException( wois.getName() );
                rw.state = WoisRegistration.EXITING;
            }
            try {
                try {
                	
                } finally {
                    wois.unregister( this );
                }
            } finally {
                
            }
        } finally {
            synchronized (woises) {
                if (rw != null && rw.state == WoisRegistration.EXITING)
                    rw.state = WoisRegistration.OUT;
            }
        }
    }
	/**
     * Returns the WoIS registration associated to a given <code>WoIS</code>.
     * Returns <code>null</code> if there is no such registration.
     * @param wois a WoIS.
     * @return the WoIS registration or <code>null</code>.
     */
    private WoisRegistration findRegWois( Wois wois )
    {
        synchronized (woises) {
            Iterator i = woises.iterator();
            while (i.hasNext()) {
                WoisRegistration rw = (WoisRegistration)i.next();
                if (rw.wois.getName().equals( wois.getName() ))
                    return rw;
            }
        }
        return null;
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
            Iterator<WoisRegistration> i = woises.iterator();
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
    	if (event.getSource()==bLocal)
	    {
            //String host = (args.length < 1) ? null : args[0];
            try {
            	lInfo.setText("avvio motore");
              
            	runner.matchResolveAct(this.name, privateFacts);
              
                //get the value of the private fact
            	privateFacts=runner.getPrivateFacts();
            	for (Fact fact : privateFacts) {
            		updatePrivateFact(fact);
				}
            	System.out.println(pos.toString());
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();}
	    	
    	}
    	//manage the connection and the registration of the wois
    	if (event.getSource()==bManager)
	    {
            try {
            	//TODO se sono gi� connesso a quel manager salta
            	if (runner.wois!=null)
            	{
            		lInfo.setText("gi� connesso");
            	}else{
            		Wois wois = new Wois("prova");
                	register(wois, name);
                	lInfo.setText("connesso");
            	}
            	
                //woises.add( wois );
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();}
	    	
    	}
    }
    
    public void updatePrivateFact(Fact fact)
    {
    	try {
    		Vector <String> tempAttr = fact.getAttributes();
    		Vector <String> tempVal = fact.getValues();
    		String tempFactType = fact.getFactType();
    		for (int i=0;i<tempAttr.size();i++){//update all the attribute, even if not modified
    			switch(tempFactType){
    			case "Position" :	Class cls = Class.forName("ami_drools." + tempFactType);
    								Position l = (Position) cls.cast(mDevices.get(fact.getId()));
    								l.updateField(tempAttr.get(i), tempVal.get(i));
    								break;
    			}
    		}
		} catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
		}
    	
    }
}
