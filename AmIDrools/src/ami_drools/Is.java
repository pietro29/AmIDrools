package ami_drools;


import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Collection;

import org.apache.poi.hssf.util.HSSFColor.RED;
import org.drools.core.rule.FactType;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import sharedFacts.Lampadina;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.BorderLayout;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.Font;



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
    public String getIsName(){
    	return parent.getIsName();
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
	JButton bManager;
	JButton bLocal;
	JLabel picLabel;
	/**Rule engine elements*/
	KieServices ks ;
    KieContainer kContainer ;
	KieSession kSession ;
	
	RuleRunner runner;
	private TextArea textArea;
	private JPanel panel;
	private JLabel label;
	private JLabel label_2;
	private JLabel label_3;
	private JTabbedPane tabbedPane;
	private JPanel panelFireRule;
	private JPanel panelNewRule;
	private JPanel panelLogo;
	private JLabel label_1;

    //
    
    
	public Is(String name) throws RemoteException
	{	
		woises = new Vector<WoisRegistration>();
		runners = new Vector<RuleRunner>();
		privateFacts=new Vector<Fact>();
		remoteObject = new IsRemote( this );
		this.name=name;
		pos = new Position("id1",1,"Soggiorno");
    	
    	getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
    	
    	tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    	getContentPane().add(tabbedPane);
    	   	
    	panelFireRule = new JPanel();
    	//tabbedPane.addTab("New tab", null, panelFireRule, null);
    	panelFireRule.setLayout(new GridLayout(0, 1, 0, 0));
    	
    	panelLogo = new JPanel();
    	panelFireRule.add(panelLogo);
    	panelLogo.setLayout(new GridLayout(0, 3, 0, 0));
    	
    	label_1 = new JLabel("");
    	panelLogo.add(label_1);
    	picLabel = new JLabel();
    	panelLogo.add(picLabel);
    	
    	panel = new JPanel();
    	panelFireRule.add(panel);
    	panel.setLayout(new GridLayout(0, 5, 0, 0));
    	
    	label = new JLabel("");
    	panel.add(label);
    	bManager=new JButton("");
    	
    	panel.add(bManager);
    	bManager.addActionListener((ActionListener) this);
    	//this.setImageButton(bManager, System.getProperty("user.dir") + "/images/connect.png");
    	label_2 = new JLabel("");
    	panel.add(label_2);
    	bLocal=new JButton("");
    	//this.setImageButton(bLocal, System.getProperty("user.dir") + "/images/engine.png");
    	panel.add(bLocal);
    	bLocal.addActionListener((ActionListener) this);
    	
    	label_3 = new JLabel("");
    	panel.add(label_3);
    	
    	textArea = new TextArea();
    	panelFireRule.add(textArea);
    	textArea.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 14));
    	textArea.setEditable(false);
    	textArea.setForeground(UIManager.getColor("ToolBar.dockingForeground"));
    	textArea.setBackground(UIManager.getColor("InternalFrame.activeTitleBackground"));
    	
    	panelNewRule = new JPanel();
    	//tabbedPane.addTab("New tab", null, panelNewRule, null);
    	
    	//this.setIconImage(new ImageIcon(System.getProperty("user.dir") + "/images/drools.png").getImage());
    	//this.add(pTextArea);
    	
    	ImageIcon iconPanel1 = new ImageIcon("images/gear32.png", "users");
    	ImageIcon iconPanel2 = new ImageIcon("images/folderplus32.png", "users");
    	tabbedPane.addTab("Fire", iconPanel1, panelFireRule, "Fire Rules");
    	tabbedPane.addTab("New", iconPanel2, panelNewRule, "New Rules");
    	pos=new Position("1p", 1, "soggiorno");
    	mDevices.put(pos.getId(), pos);
    	runners.add(createEngine());
    	
	}
	public String getIsName(){
		return this.name;
	}
	
	public void resizeButton()
	{
		this.setImageButton(bManager, System.getProperty("user.dir") + "/images/connect.png");
		this.setImageButton(bLocal, System.getProperty("user.dir") + "/images/engine.png");
	}
	
	public void setImageButton(JButton bt, String pathImage){
		bt.setIcon(new ImageIcon(pathImage));
        Image img = new ImageIcon(pathImage).getImage();
        int minDimension=bt.getWidth();
        if(minDimension>bt.getHeight())
        	minDimension=bt.getHeight();
        System.err.println(bt.getWidth());
        //minDimension=80;
        bt.setPreferredSize(new Dimension(minDimension,minDimension));
        Image newimg = img.getScaledInstance(minDimension, minDimension,  java.awt.Image.SCALE_SMOOTH);  
        bt.setIcon(new ImageIcon(newimg));  
        bt.setBorderPainted(false);
        bt.setFocusPainted(false);
        bt.setContentAreaFilled(false);
	}
	
	public void resizeLogoUnibs(String pathImage, int width, int height){
		BufferedImage img =  new BufferedImage(100, 100,BufferedImage.TYPE_INT_RGB);
    	try {
    	    img = ImageIO.read(new File(pathImage));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	Image newimg = img.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH);
    	picLabel.setIcon(new ImageIcon(newimg));
    	picLabel.setHorizontalAlignment(JLabel.CENTER);
    
	}
	
	/**
	 * add the private fact to the WM and run the engine
	 */
	private RuleRunner createEngine()
	{
		runner = new RuleRunner(name, textArea);
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
            	if (runner.wois!=null)
            	{
            		unregister(runner.wois);
            		runner.wois=null;
            		runner.runRules(privateFacts);
            		this.setImageButton(bManager, System.getProperty("user.dir") + "/images/connect.png");
            		textArea.append("Non connesso\n");
            	}else{
            	
            		try {
						Wois wois = new Wois("prova");
						register(wois, name);
						runner.runRules(privateFacts);
						textArea.append("Connesso\n");
						this.setImageButton(bManager, System.getProperty("user.dir") + "/images/disconnect.png");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						textArea.append("Errore, connessione fallita\n");}
				}
            	
                //woises.add( wois );
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
                textArea.append("Errore Server\n");}
	    	
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
