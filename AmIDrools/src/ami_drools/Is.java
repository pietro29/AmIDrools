package ami_drools;


import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
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
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.BorderLayout;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;

import java.awt.Font;

import javax.swing.UIManager.*;

import java.awt.SystemColor;

import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;


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
	SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	private static final long serialVersionUID = 1L;
    private Vector<WoisRegistration> woises; 
    private Vector<RuleRunner> runners;
    /** Remote object, used for communication between ISs */
    private IsRemote remoteObject;
    /** Name of the Is */
    private String name;
    private String nomeServer;
    /** Private fact */
    private Position position;
    private Battery battery;
    private Clock clock;
    boolean Cucina;
    boolean Soggiorno;
    boolean CameraLetto;
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
	private JPanel panel_1;
	private JTextField txtServerIP;
	private JPanel panelStatus;
	private JPanel panelPosizione;
	private JButton btCucina;
	private JButton btCameraLetto;
	private JButton btSoggiorno;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_3;
	private JLabel lblNewLabel_4;
	private JPanel panelBatteria;
	private JLabel label_4;
	private JLabel lblBattetr;
	private JLabel label_6;
	private JButton btAggiornaBatteria;
	private JTextField txtBatteria;
	private JTextPane lbBatteryError;
	private JButton btRegolaCondivisa;
	private JButton btRegolaPrivata;
	private JTable tbSharedRules;
	private JTable tbPrivateRules;
	private JPanel panelSharedTableButton;
	private JPanel panelPrivateTableButton;
	private JButton btCancellaRegolaPrivata;

    //
    
    
	public Is(String name, String nomeServer) throws RemoteException
	{	
		
			woises = new Vector<WoisRegistration>();
			runners = new Vector<RuleRunner>();
			this.nomeServer=nomeServer;
			privateFacts=new Vector<Fact>();
		try {
			setGraphics();
			remoteObject = new IsRemote( this );
			this.name=name;
			position = new Position("idp1",1,"Soggiorno");
			battery = new Battery("idb1",100);
			clock=new Clock("idc1");
			
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
			
			panel_1 = new JPanel();
			panel.add(panel_1);
			panel_1.setLayout(new GridLayout(2, 1, 0, 0));
			bManager=new JButton("");
			panel_1.add(bManager);
			
			txtServerIP = new JTextField(nomeServer);
			panel_1.add(txtServerIP);
			txtServerIP.setColumns(10);
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
			
			this.setIconImage(new ImageIcon(ClassLoader.getSystemResource("images/drools.png")).getImage());
			//this.add(pTextArea);
			ImageIcon iconPanel1 = new ImageIcon(ClassLoader.getSystemResource("images/gear32.png"), "users");
			ImageIcon iconPanel2 = new ImageIcon(ClassLoader.getSystemResource("images/folderplus32.png"), "users");
			ImageIcon iconPanel3 = new ImageIcon(ClassLoader.getSystemResource("images/spanner32.png"), "users");
			
			panelStatus = new JPanel();
			
			panelStatus.setLayout(new GridLayout(3, 1, 0, 0));
			
			panelPosizione = new JPanel();
			panelPosizione.setBorder(new LineBorder(new Color(0, 0, 255), 2, true));
			panelStatus.add(panelPosizione);
			panelPosizione.setLayout(new GridLayout(2, 3, 0, 0));
			
			lblNewLabel = new JLabel("");
			panelPosizione.add(lblNewLabel);
			
			lblNewLabel_3 = new JLabel("POSIZIONE");
			lblNewLabel_3.setFont(new Font("Trebuchet MS", Font.PLAIN, 20));
			lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
			panelPosizione.add(lblNewLabel_3);
			
			lblNewLabel_4 = new JLabel("");
			panelPosizione.add(lblNewLabel_4);
			
			btCucina = new JButton("");
			panelPosizione.add(btCucina);
			btCucina.addActionListener((ActionListener) this);
			
			btCameraLetto = new JButton("");
			panelPosizione.add(btCameraLetto);
			btCameraLetto.addActionListener((ActionListener) this);
			
			btSoggiorno = new JButton("");
			panelPosizione.add(btSoggiorno);
			btSoggiorno.addActionListener((ActionListener) this);
			
			tabbedPane.addTab("Fire", iconPanel1, panelFireRule, "Fire Rules");
			tabbedPane.addTab("New", iconPanel2, panelNewRule, "New Rules");
			panelNewRule.setLayout(new GridLayout(0, 2, 0, 0));
			
			btRegolaCondivisa = new JButton("Regola con fatti condivisi");
			panelNewRule.add(btRegolaCondivisa);
			btRegolaCondivisa.addActionListener((ActionListener) this);
			
			btRegolaPrivata = new JButton("Regola con fatti privati");
			panelNewRule.add(btRegolaPrivata);
			tbPrivateRules = new JTable();
			tbPrivateRules.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
				}
			));
			extractRuleFromFile("resources/local_rules.txt");
			
			tbSharedRules = new JTable();
			tbSharedRules.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
				}
			));
			extractRuleFromFile("resources/shared_rules.txt");
			tbSharedRules.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
			panelNewRule.add(tbSharedRules);
			tbPrivateRules.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
			panelNewRule.add(tbPrivateRules);
			
			panelSharedTableButton = new JPanel();
			panelNewRule.add(panelSharedTableButton);
			
			panelPrivateTableButton = new JPanel();
			panelNewRule.add(panelPrivateTableButton);
			panelPrivateTableButton.setLayout(new GridLayout(1, 0, 0, 0));
			
			btCancellaRegolaPrivata = new JButton("Cancella");
			panelPrivateTableButton.add(btCancellaRegolaPrivata);
			btCancellaRegolaPrivata.addActionListener((ActionListener) this);
			
			btRegolaPrivata.addActionListener((ActionListener) this);
			//tabbedPane.addTab("Status", iconPanel3, panelStatus, "Status");
			tabbedPane.addTab("Status", iconPanel3, panelStatus, "Status");
			
			panelBatteria = new JPanel();
			panelBatteria.setBorder(new LineBorder(new Color(0, 128, 0), 2, true));
			panelStatus.add(panelBatteria);
			panelBatteria.setLayout(new GridLayout(2, 3, 0, 0));
			
			label_4 = new JLabel("");
			panelBatteria.add(label_4);
			
			lblBattetr = new JLabel("BATTERIA");
			lblBattetr.setHorizontalAlignment(SwingConstants.CENTER);
			lblBattetr.setFont(new Font("Trebuchet MS", Font.PLAIN, 20));
			panelBatteria.add(lblBattetr);
			
			label_6 = new JLabel("");
			panelBatteria.add(label_6);
			
			btAggiornaBatteria = new JButton("");
			panelBatteria.add(btAggiornaBatteria);
			btAggiornaBatteria.addActionListener((ActionListener) this);
			
			txtBatteria = new JTextField();
			txtBatteria.setFont(new Font("Trebuchet MS", Font.BOLD, 37));
			panelBatteria.add(txtBatteria);
			txtBatteria.setColumns(10);
			
			txtBatteria.setText(new Integer(battery.getLevel()).toString());
			
			lbBatteryError = new JTextPane();
			lbBatteryError.setBackground(SystemColor.control);
			lbBatteryError.setFont(new Font("Tahoma", Font.PLAIN, 13));
			panelBatteria.add(lbBatteryError);
			
			lbBatteryError.setEditable(false);
			
			mDevices.put(position.getId(), position);
			mDevices.put(battery.getId(), battery);
			mDevices.put(clock.getId(), clock);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	runners.add(createEngine());
    	
	}
	
	public void setGraphics(){
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
	}
	
	public String getIsName(){
		return this.name;
	}
	
	public void resizeButton()
	{
		this.setImageButton(bManager, "images/connect.png");
		this.setImageButton(bLocal, "images/engine.png");
		this.setImageButton(btSoggiorno, "images/soggiorno.png",50,50);
		this.setImageButton(btCameraLetto, "images/cameralettoBN.png",50,50);
		this.setImageButton(btCucina, "images/cucinaBN.png",50,50);
		this.setImageButton(btAggiornaBatteria, "images/exchange32.png",50,50);
	}
	
	public void setImageButton(JButton bt, String pathImage){
		bt.setIcon(new ImageIcon(ClassLoader.getSystemResource(pathImage)));
        Image img = new ImageIcon(ClassLoader.getSystemResource(pathImage)).getImage();
        int minDimension=bt.getWidth();
        if(minDimension>bt.getHeight())
        	minDimension=bt.getHeight();
        //minDimension=80;
        bt.setPreferredSize(new Dimension(minDimension,minDimension));
        Image newimg = img.getScaledInstance(minDimension, minDimension,  java.awt.Image.SCALE_SMOOTH);  
        bt.setIcon(new ImageIcon(newimg));  
        bt.setBorderPainted(false);
        bt.setFocusPainted(false);
        bt.setContentAreaFilled(false);
	}
	
	public void setImageButton(JButton bt, String pathImage, int dimensioX, int dimensionY){
		bt.setIcon(new ImageIcon(ClassLoader.getSystemResource(pathImage)));
        Image img = new ImageIcon(ClassLoader.getSystemResource(pathImage)).getImage();
        bt.setPreferredSize(new Dimension(dimensioX,dimensionY));
        Image newimg = img.getScaledInstance(dimensioX, dimensionY,  java.awt.Image.SCALE_SMOOTH);  
        bt.setIcon(new ImageIcon(newimg));  
        bt.setBorderPainted(false);
        bt.setFocusPainted(false);
        bt.setContentAreaFilled(false);
	}
	
	public void resizeLogoUnibs(String pathImage, int width, int height){
		BufferedImage img =  new BufferedImage(100, 100,BufferedImage.TYPE_INT_RGB);
    	try {
    	    img = ImageIO.read(ClassLoader.getSystemResource(pathImage));
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
		insertPrivateFacts();
    	runner.runRules(privateFacts);
    	return runner;
	}
	
	private void insertPrivateFacts(){
		privateFacts=new Vector<Fact>();
		//insert position fact
		Fact privateFactPos = new Fact(position.getId(),"Position");
		privateFactPos.insertAttributeValue("location", "int", new Integer(position.getLocation()).toString());
		privateFactPos.insertAttributeValue("codice", "String", position.getCodice());
		privateFactPos.insertAttributeValue("_privateVisibility", "Boolean", "true");
		//insert battery fact
		Fact privateFactBat = new Fact(battery.getId(),"Battery");
		privateFactBat.insertAttributeValue("level", "int", new Integer(battery.getLevel()).toString());
		privateFactBat.insertAttributeValue("_privateVisibility", "Boolean", "true");
		//insert clock fact
		Fact privateFactClo = new Fact(clock.getId(),"Clock");
		privateFactClo.insertAttributeValue("dateTime", "java.util.Date",dateFormat.format(clock.getDateUpdated()));
		privateFactClo.insertAttributeValue("_privateVisibility", "Boolean", "true");
		privateFacts.add(privateFactBat);
		privateFacts.add(privateFactPos);
		privateFacts.add(privateFactClo);
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
            	updateInternalState();
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
            		this.setImageButton(bManager, "images/connect.png");
            		textArea.append("Non connesso\n");
            	}else{
            	
            		try {
            			textArea.append("Inizio Connessione\n");
            			nomeServer=txtServerIP.getText();
						Wois wois = new Wois(nomeServer);
						register(wois, name);
						runner.runRules(privateFacts);
						textArea.append("Connesso\n");
						this.setImageButton(bManager, "images/disconnect.png");
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
	    	
    	}//manage the change of the position
    	if (event.getSource()==btSoggiorno)
	    {
            try {//if i choose living room then disable the other position
            		Soggiorno=true;
            		Cucina=false;
            		CameraLetto=false;
            		position.setCodice("Soggiorno");
            		position.setLocation(1);
            		this.setImageButton(btSoggiorno, "images/soggiorno.png",50,50);
            		this.setImageButton(btCameraLetto, "images/cameralettoBN.png",50,50);
            		this.setImageButton(btCucina, "images/cucinaBN.png",50,50);
            		insertPrivateFacts();                
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
    	}
    	
    	if (event.getSource()==btCameraLetto)
	    {
            try {//if i choose bedroom then disable the other position
            		Soggiorno=false;
            		Cucina=false;
            		CameraLetto=true;
            		position.setCodice("Camera da letto");
            		position.setLocation(3);
            		this.setImageButton(btSoggiorno, "images/soggiornoBN.png",50,50);
            		this.setImageButton(btCameraLetto, "images/cameraletto.png",50,50);
            		this.setImageButton(btCucina, "images/cucinaBN.png",50,50);
            		insertPrivateFacts();
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
    	}
    	
    	if (event.getSource()==btCucina)
	    {
            try {//if i choose chicken then disable the other position
            		Soggiorno=false;
            		Cucina=true;
            		CameraLetto=false;
            		position.setCodice("Cucina");
            		position.setLocation(2);
            		this.setImageButton(btSoggiorno, "images/soggiornoBN.png",50,50);
            		this.setImageButton(btCameraLetto, "images/cameralettoBN.png",50,50);
            		this.setImageButton(btCucina, "images/cucina.png",50,50);
            		insertPrivateFacts();
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
    	}
    	
    	if (event.getSource()==btAggiornaBatteria)
	    {
            try {
            	int level=Integer.parseInt(txtBatteria.getText());
                if (level>=0 && level<=100)
                {
                	battery.setLevel(level);
                	insertPrivateFacts();
                	lbBatteryError.setText("Aggiornamento effettuato!");
                }else{
                	lbBatteryError.setText("Il livello della batteria deve essere compreso tra 0 e 100");
                }
            } catch (Exception e) {
                lbBatteryError.setText("Valore non numerico!");
                e.printStackTrace();
            }
    	}
    	if (event.getSource()==btRegolaPrivata)
	    {
    		try {
            	IsNewRule IsNR = new IsNewRule(privateFacts, true);
            	IsNR.setTitle("New Rule");
            	IsNR.setSize(700, 500);
            	IsNR.setVisible(true);
            	IsNR.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
    	if (event.getSource()==btRegolaCondivisa)
	    {
            try {
            	if (runner.wois!=null){
            		//insert the public fact inside the private fact
            		Vector<Fact> generalFacts = runner.wois.getSharedFacts();
            		for(int i=0;i<privateFacts.size();i++)
            		{
            			generalFacts.add(privateFacts.get(i));
            		}
            		
            		IsNewRule IsNR = new IsNewRule(generalFacts, false);
                	IsNR.setTitle("New Rule");
                	IsNR.setSize(700, 500);
                	IsNR.setVisible(true);
                	IsNR.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            	}
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
    	if (event.getSource()==btCancellaRegolaPrivata)
	    {
            try {
            	deleteRuleFromFile("resources/local_rules.txt",tbPrivateRules.getValueAt(tbPrivateRules.getSelectedRow(), 0).toString());
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
    	
    }
    
    
    public void updatePrivateFact(Fact fact)
    {
    	try {
    		Vector <String> tempAttr = fact.getAttributes();
    		Vector <String> tempVal = fact.getValues();
    		String tempFactType = fact.getFactType();
    		Class cls;
    		for (int i=0;i<tempAttr.size();i++){//update all the attribute, even if not modified
    			switch(tempFactType){
    			case "Position" :	cls = Class.forName("ami_drools." + tempFactType);
    								Position p = (Position) cls.cast(mDevices.get(fact.getId()));
    								p.updateField(tempAttr.get(i), tempVal.get(i));
    								break;
    			case "Battery" :	cls = Class.forName("ami_drools." + tempFactType);
									Battery b = (Battery) cls.cast(mDevices.get(fact.getId()));
									b.updateField(tempAttr.get(i), tempVal.get(i));
									break;
    			}
    		}
		} catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
		}
    	
    }
    
    private void extractRuleFromFile(String fileName) throws FileNotFoundException
	{
    	Vector<String> rows=new Vector<String>();
		BufferedReader br = new BufferedReader(new FileReader(ClassLoader.getSystemResource(fileName).getFile()));
        try {
            String line = br.readLine();
            while (line != null) {
                if(line.contains("rule ")) {
                    //extract the rule name
                	rows.add(line.substring(line.indexOf("\""),line.length()));
                } else if (line.contains("String 2")) {
                    // ...
                }               
                line = br.readLine();
            }
            br.close();
            if (fileName.contains("shared"))
            {
            	setSharedRuleTable(rows);
            }else{
            	setPrivateRuleTable(rows);
            }
            
        } catch(IOException e) {
            
        }
	}
    
    private void setPrivateRuleTable(Vector<String> rows)
    {
    	DefaultTableModel model = new DefaultTableModel();  
    	// Create a column 
    	model.addColumn("Rule"); 
    	for(int i=0;i<rows.size();i++)
    	{	// Append a row 
        	model.addRow(new Object[]{rows.get(i)});
        }
    	tbPrivateRules = new JTable(model);
    	
    }
    
    private void setSharedRuleTable(Vector<String> rows)
    {
    	DefaultTableModel model = new DefaultTableModel();  
    	// Create a column 
    	model.addColumn("Rule"); 
    	for(int i=0;i<rows.size();i++)
    	{	// Append a row 
        	model.addRow(new Object[]{rows.get(i)});
        }
    	tbSharedRules = new JTable(model);
    	
    }
    
    private void deleteRuleFromFile(String fileName, String ruleName) throws FileNotFoundException
	{
    	Vector<String> rows=new Vector<String>();
		BufferedReader br = new BufferedReader(new FileReader(ClassLoader.getSystemResource(fileName).getFile()));
		String newFile=new String("");
		boolean delete=false;
        try {
            String line = br.readLine();
            while (line != null) {//delete until found end
                if(line.contains(ruleName)) {
                    delete=true;
                }
                if(!delete){
                	newFile+=line+"\n";
                }
                if(line.contains("end")) {
                	newFile+="end\n";
                    delete=false;
                }
                line = br.readLine();
            }
            br.close();
            try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ClassLoader.getSystemResource(fileName).getFile(), false)))) {
			    out.println(newFile);
			}catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
            setPrivateRuleTable(rows);
        } catch(IOException e) {
            
        }
	}
    private void updateInternalState(){
    	txtBatteria.setText(Integer.valueOf(battery.getLevel()).toString());
    	if (position.getCodice().toLowerCase().equals("soggiorno"))
    	{
    		Soggiorno=true;
    		Cucina=false;
    		CameraLetto=false;
    		this.setImageButton(btSoggiorno, "images/soggiorno.png",50,50);
    		this.setImageButton(btCameraLetto, "images/cameralettoBN.png",50,50);
    		this.setImageButton(btCucina, "images/cucinaBN.png",50,50);
    	}else if (position.getCodice().toLowerCase().equals("cucina")){
    		Soggiorno=true;
    		Cucina=false;
    		CameraLetto=false;
    		this.setImageButton(btSoggiorno, "images/soggiornoBN.png",50,50);
    		this.setImageButton(btCameraLetto, "images/cameralettoBN.png",50,50);
    		this.setImageButton(btCucina, "images/cucina.png",50,50);
    	}else{
    		Soggiorno=true;
    		Cucina=false;
    		CameraLetto=false;
    		this.setImageButton(btSoggiorno, "images/soggiornoBN.png",50,50);
    		this.setImageButton(btCameraLetto, "images/cameraletto.png",50,50);
    		this.setImageButton(btCucina, "images/cucinaBN.png",50,50);
    	}
    }
}
