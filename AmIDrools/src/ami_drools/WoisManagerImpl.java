package ami_drools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import sharedFacts.Lampadina;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.UIManager.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.TabExpander;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.LineBorder;

public class WoisManagerImpl extends UnicastRemoteObject implements WoisManager {

	/**
     * Version ID used by deserialization in J2SE >= 1.5.0.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Table containing all the members of this WoIS. It maps names ({@link String}s) to engines ({@link IsIntf}s).
     */
    private Hashtable members = new Hashtable();
    
    /**
     * Table containing all the lock of the facts in this WoIS
     */
    private Hashtable<String, Lock> locks = new Hashtable<String, Lock>();
    
    /**
     * Map from engines ({@link IsIntf}s) to their names ({@link String}s). For data consistency,
     * access should be synchronized on {@link #members}.
     */
    private Map mNames = new HashMap(); 
    /**
     * Map of the fact and their ID
     */
    private Map mFacts = new HashMap();
    /**
     * Map of the device and their ID
     */
    private Map mDevices = new HashMap();
    /**
     * Map of the registered users
     */
    private Map mUsers = new HashMap();
    /**
     * Map of the priorities
     */
    private Map mPriorities = new HashMap();
    /**
     * Name of the controlled WoIS
     */
    private final String woisName;
    
    /**
     * URL used for binding to the RMI registry.
     */
    private final String bindingUrl;
    
    /**
     * Shared facts vector
     */
    private Vector<Fact> sharedFacts;
    
    /**
     * Assertion object vector
     */
    private Vector<Assertion> assertions;
    
    private Thread checklockdate;
    
    private Thread checkIs;
    
    private boolean uIActivation=true;
    
    //Interfaccia grafica
    /*
    JTabbedPane tabbedPane;
	JPanel panel1;
	JPanel panel2;
    */
    
	JTextArea textArea;
	DefaultListModel<String> model;
	ButtonGroup groupStereo;
	ButtonGroup groupPersona;
	JRadioButton rbLampadinaAccesa;
	JRadioButton rbLampadinaSpenta;
	JRadioButton rbStereoAcceso;
	JRadioButton rbStereoSpento;
	JRadioButton rbPersonaDentro;
	JRadioButton rbPersonaFuori;
	JTextArea textAreaLog;
    
	private final static String newline = "\n";
	private JTable tableUser;
	//Device
	Lampadina lampadina;
	Lampadina lampadina2;
	//
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
        sharedFacts = new Vector<Fact>();
        assertions = new Vector<Assertion>();
        
        System.out.println(System.getProperty("java.rmi.server.hostname"));
        
        
        boolean done = false;
        try {
            Naming.rebind( name, this); 
            done = true;
        } finally {
            // If bind fails, unexport this object
            if (! done)
                try {
                    UnicastRemoteObject.unexportObject( this, true );
                } catch (NoSuchObjectException ee) {
                }
        }
        //DEVICE 1
        lampadina = new Lampadina("1","lampadina1",true,true);
        mDevices.put(lampadina.getId(), lampadina);
        
        Fact fatto = new Fact("1","Lampadina");
        fatto.insertAttributeValue("codice", "String", "lampadina1");
        fatto.insertAttributeValue("accesa", "Boolean", "true");
        fatto.insertAttributeValue("spenta", "Boolean", "true");
        
      //Aggiungo gli oggetti al vettore dei fatti condivisi
        sharedFacts.add(fatto);
        mFacts.put(fatto.getId(), fatto);
        
        locks.put(fatto.getId(),new Lock(fatto.getId()) );
        
        //DEVICE 2
        lampadina2 = new Lampadina("2","lampadina2",true,true);
        mDevices.put(lampadina2.getId(), lampadina2);
        
        Fact fatto2 = new Fact("2","Lampadina");
        fatto2.insertAttributeValue("codice", "String", "lampadina2");
        fatto2.insertAttributeValue("accesa", "Boolean", "true");
        fatto2.insertAttributeValue("spenta", "Boolean", "true");
        
        sharedFacts.add(fatto2);
        mFacts.put(fatto2.getId(), fatto2);
        
        locks.put(fatto2.getId(),new Lock(fatto2.getId()) );
        
        //Read priority config. file
        getPrioritiesTable();
        
        
        //Start lock thread
        checklockdate=new Thread("Check lock"){
        	public void run(){
        		try {
					checkLockProcess();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        };
        checklockdate.start();
        
        //Start IS thread
        checkIs = new Thread("Check IS"){
        	public void run(){
        		try {
					checkIsProcess();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotRegisteredException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        };
        checkIs.start();
        
        startUserInterface(name);
        
        loadInterfaceData();
        
        writeTextAreaLog("Creazione della rete " + name);
        writeTextAreaLog("Hostname: " + System.getProperty("java.rmi.server.hostname"));
    }
    
   public void startUserInterface(String name){
	  
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
	   
	 //GUI
       JFrame frame = new JFrame("WoIS manager" + name);
		// Add a window listner for close button
		frame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		//Create top panel
		JPanel topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );	
		frame.getContentPane().add(topPanel);
		
		
		
	JPanel panel0 = new JPanel();
	
	//Devices panel
	JPanel panel1=new JPanel();
   	
   	//end device panel
   	
   	//Users panel
   	JPanel panel2=new JPanel();
   	//
   	
   	//Tebbed pane creation
   	JTabbedPane tabbedPane = new JTabbedPane();
   	
   	ImageIcon iconPanel0 = new ImageIcon(ClassLoader.getSystemResource("images/lightbulb32.png"), "home");
	
	tabbedPane.addTab("Home", iconPanel0, panel0, "Home");
	panel0.setLayout(new GridLayout(2, 0, 0, 0));
	
	JPanel panel01 = new JPanel();
	panel0.add(panel01);
	
	JLabel lbltitle0 = new JLabel("AmIDrools Manager");
	lbltitle0.setForeground(new Color(0, 0, 0));
	lbltitle0.setBackground(SystemColor.window);
	lbltitle0.setFont(new Font("Ubuntu Light", Font.BOLD, 28));
	panel01.add(lbltitle0);
	
	JLabel lbLogo = new JLabel("");
	panel01.add(lbLogo);
	lbLogo.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/AmIDrools.PNG"), "AmIDrools"));
	
	JPanel panel02 = new JPanel();
	panel02.setBorder(new MatteBorder(2, 2, 2, 2, (Color) new Color(0, 0, 0)));
	panel0.add(panel02);
	panel02.setLayout(new GridLayout(0, 1, 0, 0));
	
	textAreaLog = new JTextArea();
	textAreaLog.setEditable(false);
	panel02.add(textAreaLog);
   	
   	ImageIcon iconPanel1 = new ImageIcon(ClassLoader.getSystemResource("images/wand32.png"), "devices");
	
	tabbedPane.addTab("Devices", iconPanel1, panel1, "Devices");
	panel1.setLayout(new GridLayout(3, 1, 0, 0));
	
	JPanel panel11 = new JPanel();
	panel11.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
	panel1.add(panel11);
	panel11.setLayout(new GridLayout(2, 1, 0, 0));
	
	JLabel lbLight0 = new JLabel(lampadina.getCodice().toUpperCase());
	lbLight0.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 20));
	lbLight0.setHorizontalAlignment(lbLight0.CENTER);
	panel11.add(lbLight0);
	
	JPanel panel111 = new JPanel();
	panel11.add(panel111);
	panel111.setLayout(new GridLayout(0, 1, 0, 0));
	
	final JButton btnLight0 = new JButton("Switch on/off");
	panel111.add(btnLight0);
	
	btnLight0.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
        	if (lampadina.getAccesa()){
        		lampadina.setAccesa(false);
        		setImageButton(btnLight0, "images/light-off-icon.png");
        	} else {
        		lampadina.setAccesa(true);
        		setImageButton(btnLight0, "images/light-on-icon.png");
        	}
        }
	});
	if (lampadina.getAccesa()){
		setImageButton(btnLight0, "images/light-on-icon.png");
	} else {
		setImageButton(btnLight0, "images/light-off-icon.png");
	}
	
	JPanel panel12 = new JPanel();
	panel12.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
	panel1.add(panel12);
	panel12.setLayout(new GridLayout(2, 1, 0, 0));
	
	JLabel lblLight1 = new JLabel(lampadina2.getCodice().toUpperCase());
	lblLight1.setFont(new Font("Droid Sans Mono", Font.PLAIN, 20));
	lblLight1.setHorizontalAlignment(SwingConstants.CENTER);
	panel12.add(lblLight1);
	
	JPanel panel121 = new JPanel();
	panel12.add(panel121);
	panel121.setLayout(new GridLayout(0, 1, 0, 0));
	
	final JButton btnLight1 = new JButton("Switch on/off");
	panel121.add(btnLight1);
	
	btnLight1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
        	if (lampadina.getAccesa()){
        		lampadina.setAccesa(false);
        		setImageButton(btnLight1, "images/light-off-icon.png");
        	} else {
        		lampadina.setAccesa(true);
        		setImageButton(btnLight1, "images/light-on-icon.png");
        	}
        }
	});
	if (lampadina.getAccesa()){
		setImageButton(btnLight1, "images/light-on-icon.png");
	} else {
		setImageButton(btnLight1, "images/light-off-icon.png");
	}
	
	ImageIcon iconPanel2 = new ImageIcon(ClassLoader.getSystemResource("images/users32.png"), "users");
	//tabbedPane.addTab( "Users", panel2 );
	tabbedPane.addTab("Users", iconPanel2, panel2, "Users");
	
    model = new DefaultListModel<String>();
	panel2.setLayout(new GridLayout(2, 1, 0, 0));
	topPanel.add( tabbedPane, BorderLayout.CENTER );
		
		frame.pack();
		//frame.setIconImage(new ImageIcon(ClassLoader.getSystemResource("images/drools.png")).getImage());
		frame.setIconImage(new ImageIcon(ClassLoader.getSystemResource("images/AmIDrools.PNG")).getImage());
		frame.setMinimumSize(new Dimension(600, 500));
		frame.setVisible(true);
		
		JPanel panel21 = new JPanel();
		panel2.add(panel21);
		panel21.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		final JTextArea textArea_1 = new JTextArea();
		textArea_1.setColumns(20);
		textArea_1.setLineWrap(true);
		panel21.add(textArea_1);
		textArea_1.setTabSize(4);
		textArea_1.setRows(6);
		textArea_1.setEditable(false);
		
		//Icon updateIcon = new ImageIcon("images/Update.png");
		JButton btUpdateMembersList = new JButton();
		btUpdateMembersList.setToolTipText("Update List");
		btUpdateMembersList.setFont(new Font("Ubuntu", Font.BOLD | Font.ITALIC, 12));
		btUpdateMembersList.setText("Update List");
		panel21.add(btUpdateMembersList);
		
   	btUpdateMembersList.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
        	textArea_1.setText("");
        	
        	if (members.size()==0){
        		textArea_1.setText("Member list is empty");
        	}
        	else {
        	
		        	Iterator<Map.Entry<String, IsIntf>> it = members.entrySet().iterator();
		
		        	while (it.hasNext()) {
		        		Map.Entry<String, IsIntf> entry = it.next();
		        		
		        		textArea_1.append(entry.getKey() + newline);
		        	}
        	}
        }
    });
   	
   	setImageButton(btUpdateMembersList, "images/exchange32.png");
   	
   	JPanel panel22 = new JPanel();
   	panel2.add(panel22);
   	GridBagLayout gbl_panel22 = new GridBagLayout();
   	gbl_panel22.columnWidths = new int[]{0, 0};
   	gbl_panel22.rowHeights = new int[]{0, 0, 0};
   	gbl_panel22.columnWeights = new double[]{1.0, Double.MIN_VALUE};
   	gbl_panel22.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
   	panel22.setLayout(gbl_panel22);
   	
   	JScrollPane scrollPane = new JScrollPane();
   	GridBagConstraints gbc_scrollPane = new GridBagConstraints();
   	gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
   	gbc_scrollPane.fill = GridBagConstraints.BOTH;
   	gbc_scrollPane.gridx = 0;
   	gbc_scrollPane.gridy = 0;
   	gbc_scrollPane.gridwidth=4;
   	gbc_scrollPane.weighty=2;
   	panel22.add(scrollPane, gbc_scrollPane);
   	
   	String [] columnsName={"User","Priority"};
   	final DefaultTableModel modelTable = new DefaultTableModel();
   	modelTable.addColumn("User");
   	modelTable.addColumn("Priority");
   	
   	int i=0;
   	Iterator<Map.Entry<String, Integer>> it = mPriorities.entrySet().iterator();
   	while (it.hasNext()) {
   		Map.Entry<String, Integer> entry = it.next(); 		
   		modelTable.addRow(new Object[] { entry.getKey().toString(), entry.getValue().toString() });
   	i+=1;	
   	}
   	
   	tableUser = new JTable(modelTable);
   	tableUser.setShowVerticalLines(true);
   	tableUser.setShowHorizontalLines(true);
   	JTableHeader header = tableUser.getTableHeader();
   	header.setBackground(Color.DARK_GRAY);
    header.setForeground(Color.white);
   	
   	
   	scrollPane.setViewportView(tableUser);
   	
   	JLabel lbInsertUser = new JLabel("User");
   	lbInsertUser.setFont(new Font("Ubuntu Light", Font.BOLD, 12));
   	GridBagConstraints gbc_lbInsertUser = new GridBagConstraints();
   	gbc_lbInsertUser.insets = new Insets(0, 0, 5, 0);
   	gbc_lbInsertUser.fill = GridBagConstraints.HORIZONTAL;
   	gbc_lbInsertUser.gridx = 0;
   	gbc_lbInsertUser.gridy = 1;
   	gbc_lbInsertUser.weightx=1;
   	gbc_lbInsertUser.weighty=1;
   	panel22.add(lbInsertUser, gbc_lbInsertUser);
   	
   	JLabel lbInsertPriority = new JLabel("Priority");
   	lbInsertPriority.setFont(new Font("Ubuntu Light", Font.BOLD, 12));
   	GridBagConstraints gbc_lbInsertPriority = new GridBagConstraints();
   	gbc_lbInsertPriority.insets = new Insets(0, 0, 5, 0);
   	gbc_lbInsertPriority.fill = GridBagConstraints.HORIZONTAL;
   	gbc_lbInsertPriority.gridx = 0;
   	gbc_lbInsertPriority.gridy = 2;
   	gbc_lbInsertPriority.weightx=1;
   	gbc_lbInsertPriority.weighty=1;
   	panel22.add(lbInsertPriority, gbc_lbInsertPriority);
   	
   	final JTextField txtInsertUser = new JTextField();
   	GridBagConstraints gbc_txtInsertUser = new GridBagConstraints();
   	gbc_txtInsertUser.insets = new Insets(0, 0, 5, 0);
   	gbc_txtInsertUser.fill = GridBagConstraints.HORIZONTAL;
   	gbc_txtInsertUser.gridx = 1;
   	gbc_txtInsertUser.gridy = 1;
   	gbc_txtInsertUser.weightx=1;
   	gbc_txtInsertUser.weighty=1;
   	panel22.add(txtInsertUser, gbc_txtInsertUser);
   	
   	final JTextField txtInsertPriority = new JTextField();
   	GridBagConstraints gbc_txtInsertPriority = new GridBagConstraints();
   	gbc_txtInsertPriority.insets = new Insets(0, 0, 5, 0);
   	gbc_txtInsertPriority.fill = GridBagConstraints.HORIZONTAL;
   	gbc_txtInsertPriority.gridx = 1;
   	gbc_txtInsertPriority.gridy = 2;
   	gbc_txtInsertPriority.weightx=1;
   	gbc_txtInsertPriority.weighty=1;
   	panel22.add(txtInsertPriority, gbc_txtInsertPriority);
   	
   	JButton btInsertUser = new JButton();
   	btInsertUser.setFont(new Font("Ubuntu", Font.BOLD | Font.ITALIC, 12));
   	btInsertUser.setToolTipText("Add User");
   	btInsertUser.setText("Add User");
   	GridBagConstraints gbc_btInsertUser = new GridBagConstraints();
   	gbc_btInsertUser.insets = new Insets(0, 0, 5, 0);
   	gbc_btInsertUser.fill = GridBagConstraints.NONE;
   	gbc_btInsertUser.gridx = 2;
   	gbc_btInsertUser.gridy = 1;
   	gbc_btInsertUser.weightx=1;
   	gbc_btInsertUser.weighty=1;
   	gbc_btInsertUser.gridheight=2;
   	panel22.add(btInsertUser, gbc_btInsertUser);
   	btInsertUser.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
        	if(!(txtInsertUser.getText().toString().equals("") && txtInsertPriority.getText().toString().equals(""))){
        		modelTable.addRow(new Object[] { txtInsertUser.getText().toString(), txtInsertPriority.getText().toString() });
        		mPriorities.put(txtInsertUser.getText().toString(), Integer.parseInt(txtInsertPriority.getText().toString()));
        		try {
					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ClassLoader.getSystemResource("resources/wois_priorities.txt").getFile(), true)));
					out.println(txtInsertUser.getText().toString() + "-" + txtInsertPriority.getText().toString() );
				} catch (IOException e1) {
					System.out.println("Non ho scritto");
					e1.getStackTrace();
				}
        		txtInsertPriority.setText("");
        		txtInsertUser.setText("");
        	}
        	
        }
   	});
   	setImageButton(btInsertUser, "images/plus32.png");
   	
	JButton btDeleteUser = new JButton();
	btDeleteUser.setFont(new Font("Ubuntu", Font.BOLD | Font.ITALIC, 12));
	btDeleteUser.setToolTipText("Delete User");
	btDeleteUser.setText("Delete User");
   	GridBagConstraints gbc_btDeleteUser = new GridBagConstraints();
   	gbc_btDeleteUser.insets = new Insets(0, 0, 5, 0);
   	gbc_btDeleteUser.fill = GridBagConstraints.NONE;
   	gbc_btDeleteUser.gridx = 3;
   	gbc_btDeleteUser.gridy = 1;
   	gbc_btDeleteUser.weightx=1;
   	gbc_btDeleteUser.weighty=1;
   	gbc_btDeleteUser.gridheight=2;
   	panel22.add(btDeleteUser, gbc_btDeleteUser);
   	btDeleteUser.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
        	if (tableUser.getSelectedRow()>-1){
        		int row = tableUser.convertRowIndexToModel(tableUser.getSelectedRow());
            	mPriorities.remove(modelTable.getValueAt(row, 0));
            	modelTable.removeRow(row);
        	}
        	
        }
   	});
   	setImageButton(btDeleteUser, "images/stop32.png");
   }
   
   private void writeTextAreaLog(String message){
	   textAreaLog.append(message + newline);
   }
   
   private void loadInterfaceData(){
	   /*
	    Iterator<Map.Entry<String, Integer>> it = mPriorities.entrySet().iterator();

   	while (it.hasNext()) {
   		Map.Entry<String, Integer> entry = it.next();
   		model.addElement(entry.getKey().toString() + " - " + entry.getValue().toString());
   	}*/
   }
   
   public void setImageButton(JButton bt, String pathImage){
		bt.setIcon(new ImageIcon(ClassLoader.getSystemResource(pathImage)));
       Image img = new ImageIcon(ClassLoader.getSystemResource(pathImage)).getImage();
       int minDimension=bt.getWidth();
       if(minDimension>bt.getHeight())
       	minDimension=bt.getHeight();
       Image newimg = img.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH);  
       bt.setIcon(new ImageIcon(newimg));  
       bt.setBorderPainted(false);
       bt.setFocusPainted(false);
       bt.setContentAreaFilled(false);
	}
   
    private void checkLockProcess() throws InterruptedException{
    	while(true){
    		checkLockStatus();
        	checklockdate.sleep(5000);
    	}
    	
    }
    private void checkLockStatus(){
    	Iterator<Map.Entry<String, Lock>> it = locks.entrySet().iterator();
    	Date dateToCompare=new Date();
    	//System.out.println(dateToCompare.getTime());
    	while (it.hasNext()) {
    		Map.Entry<String, Lock> entry = it.next();
    		if(entry.getValue().getLock()){
    			//System.out.println(entry.getValue().getDateLocked().getTime());
    			if(dateToCompare.getTime()-entry.getValue().getDateLocked().getTime()>5000){
    				entry.getValue().unLock();
    			}
    		}
    	}
    }
    private void checkIsProcess() throws InterruptedException, NotRegisteredException{
    	while(true){
    		checkISStatus();
    		checkIs.sleep(600000);
    	}
    	
    }
    private void checkISStatus() {
    	Iterator<Map.Entry<String, IsIntf>> it = members.entrySet().iterator();

    	while (it.hasNext()) {
    		Map.Entry<String, IsIntf> entry = it.next();
    		try {
				if(! entry.getValue().getIsName().equals(entry.getKey().toString())){
					removeMember(entry.getValue());
				} else {
					System.out.println("Il dispositivo è connesso");
				}
			} catch (RemoteException e) {
				//e.printStackTrace();
				System.out.println("Il dispositivo non risponde");
				try {
					removeMember(entry.getValue());
				} catch (RemoteException | NotRegisteredException e1) {
					e1.printStackTrace();
				}
			} catch (NotRegisteredException e) {
				System.out.println("Il dispositivo non è registrato");
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
     * @param inf the engine.
     * @param name the name of the engine in this WoIS.
     * @throws AlreadyRegisteredException if the name is already in use by another IS or if
     *             <code>dr</code> was already registered with another name.
     */
    protected void insertMember( IsIntf inf, String name ) throws AlreadyRegisteredException
    {
        synchronized (members) {
            Object v1 = members.get( name );
            if (v1 != null && ! v1.equals( inf ) ){
            	throw new AlreadyRegisteredException( name + " is already in use" );
            }
            Object v2 = mNames.get( inf ); 
            if (v2 != null && ! v2.equals( name )){
                throw new AlreadyRegisteredException( inf + " is already subscriped with another name" );
            }
            Object old = members.put( name, inf );
            if (old != null) {
                // assert v1 == dr;
            }
            mNames.put( inf, name );
            User user = new User(name,name, getUserPriority(name) );
            mUsers.put(name, user);
        }
        if (uIActivation){
        	writeTextAreaLog("Login " + name);
        }
    }
    public void addMember( IsIntf inf, String name ) throws RemoteException
    {
        // IMPORTANT: update members before getting templates and facts */
        try {
			insertMember( inf, name );
		} catch (AlreadyRegisteredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public int getUserPriority(String infName){
    	return (int) mPriorities.get(infName);
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
    
    public void removeMember( IsIntf inf ) throws RemoteException, NotRegisteredException
    {
        synchronized (members) {
            Object name = mNames.remove( inf );
            if (name == null)
                throw new NotRegisteredException( "IS not found: " + inf );
            Object old = members.remove( name );
            if (old == null) {
                throw new RuntimeException( "Internal error: incosistent maps while removing " + name + " " + inf );
            }
            removeIsData(name.toString());
            if (uIActivation){
            	writeTextAreaLog("Log out " + name);
            }
        }
        
    }
    private void removeIsData(String isName){
    	//Remove all IS assertions
    	for(int i=0; i<assertions.size();i++){
    		if(assertions.get(i).getUser().getName().equals(isName)){
    			assertions.remove(i);
    		}
    	}
    	//Remove from the registered user
    	mUsers.remove(isName);
    	//Remove all user lock
    	Iterator<Map.Entry<String, Lock>> it = locks.entrySet().iterator();
    	while (it.hasNext()) {
    		Map.Entry<String, Lock> entry = it.next();
    		if (entry.getValue().getIsId().equals(isName)){
    			entry.getValue().unLock();
    		}
    	}
    }
    /**
     * return the vector of the shared facts of the WoIS
     */
    public synchronized Vector <Fact> getSharedFacts(){
    	for (Fact fact: sharedFacts){
    		fact.removeAllModifiedAttributed();
    	}
    	return sharedFacts;
    }
    /**
     * @throws ClassNotFoundException 
     * 
     */
    public synchronized void setSharedFacts(Vector <Fact> sharedFactUpdate, String isName) throws ClassNotFoundException{
    	Vector <String> tempAttr;
    	Vector <String> tempVal;
    	List tempModified;
    	String tempId;
    	String tempFactType;
    	Fact factToUpdate;
    	Boolean update = true;
    	User tempUsr;
    	
    	
    		
	    	for (Fact fact : sharedFactUpdate){
	    		
	    		tempAttr = fact.getAttributes();
	    		tempVal = fact.getValues();
	    		tempModified = fact.getModified();
	    		tempFactType = fact.getFactType();
	    		factToUpdate = (Fact) mFacts.get(fact.getId());
	    		tempUsr =(User) mUsers.get(isName);
	    		for (int i=0;i<tempAttr.size();i++){
	    			//If the array of the modified attribute, check the priority table and then (if check returns true) update the object (and the priority table)
	    			if (tempModified.contains(tempAttr.get(i))){
	    				
	    				//se trovo un asserzione precedente devo controllare la priorità dell'utente che tenta di modificare il valore dell'attributo
	    				update = checkAssertionPriority( fact.getId(), tempAttr.get(i),tempUsr.getPriority());
	    				
	    				if (update){
	    					
	    					//aggiorno il valore dell'attributo
		    				factToUpdate.updateAttributeValue(tempAttr.get(i), tempVal.get(i));
		    				//gestisco la tabella delle asserzioni
		    				updateAssertionTable(tempAttr.get(i),tempUsr,fact);
		    				//aggiorno i device specifici
			    			switch(tempFactType){
			    			case "Lampadina" : Class cls = Class.forName("sharedFacts." + tempFactType);
			    								Lampadina l = (Lampadina) cls.cast(mDevices.get(fact.getId()));
			    								l.updateField(tempAttr.get(i), tempVal.get(i));
			    								break;
			    			}
			    			//Set lock to false
			    			locks.get(fact.getId()).unLock();
			    			
			    			if (uIActivation){
			    	        	writeTextAreaLog(tempFactType + " " + fact.getId() + "update: " + tempAttr.get(i) + " :" + tempVal.get(i));
			    	        }
	    				}	
	    			}
	    		}
	    	}
    }
    /**
     * Check if the Is can update an attribute value
     * @param idDevice 
     * @param attribute
     * @param priority
     * @return
     */
    public boolean checkAssertionPriority(String idDevice, String attribute, int priority){
    	boolean check = true;
    	for (int i=0;i<assertions.size();i++){
    		Assertion asrt = assertions.get(i);
    		Fact asserctionFact = asrt.getFact();
    		User asserctionUser = asrt.getUser();
    		String asserctionAttribute = asrt.getAttribute();
    		if (asserctionFact.getId().equals(idDevice)  && asserctionAttribute.equals(attribute) && asserctionUser.getPriority()>priority){
    			check=false;
    		}
    	}
    	return check;
    }
    /**
     * Manage the assertions vector. Delete lower priority assertion, insert new assertion.
     * @param attribute
     * @param user
     * @param fact
     */
    public void updateAssertionTable( String attribute, User user, Fact fact){
    	boolean insert = true;
    	
    	for (int i=0;i<assertions.size();i++){
    		Assertion asrt = assertions.get(i);
    		Fact asserctionFact = asrt.getFact();
    		User asserctionUser = asrt.getUser();
    		String asserctionAttribute = asrt.getAttribute();
    		
    		if (asserctionFact.getId().equals(fact.getId())  && asserctionAttribute.equals(attribute)){
    			if (asserctionUser.getPriority()<user.getPriority()){
    				assertions.remove(i);
    			}
    			if (asserctionUser.getPriority()==user.getPriority()){
    				if(asserctionUser.getId().equals(user.getId())){
    					insert=false;
    				}
    			}
    		}
    		
    	}
    	if(insert){
    		Assertion newAsrt = new Assertion(user, fact, attribute);
    		assertions.add(newAsrt);
    	}
    }
    
    public boolean getLock(String idFact, String isId){
    	synchronized (locks) {
    		if (!locks.get(idFact).getLock())
    			return false;
    		else{
    			if (locks.get(idFact).getIsId().equals(isId))
    				return false;
    			else
    				return true;
    		}
    	}
    }
    public boolean setLock(String idFact, String isId){
    	synchronized (locks) {
    		if (!getLock(idFact, isId)){
        		locks.get(idFact).setLock(isId);
        		return true;
        	} else 
        		return false;
		}
    	
    }
    public String getSharedTemplates(){
    	return getStringFromFile("shared_declare.txt");
    }
    public String getSharedFunctions(){
    	return getStringFromFile("shared_function.txt");
    }
    public String getSharedRules(){
    	return getStringFromFile("shared_rules.txt");
    }
    private String getStringFromFile(String fileName) {
		String s = "" ;
		try {
			InputStream in=ClassLoader.getSystemResourceAsStream("resources/" + fileName);
    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    while (line != null) {
		         sb.append(line);
		         sb.append(System.lineSeparator());
		            line = br.readLine();
		    }
		    s = sb.toString();
		    br.close();
		    } catch (Throwable t) {
		    	//System.err.println(t.toString());
		    	s="";
		    }
		//System.out.println(s);
		return s;
	}
    private void getPrioritiesTable(){
    	String s="";
    	try {
			
    		InputStream in=ClassLoader.getSystemResourceAsStream("resources/wois_priorities.txt");
    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		
			StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    String[] parts;
		    while (line != null) {
		         sb.append(line);
		         sb.append(System.lineSeparator());
		         
		         parts = line.split(":");
		         mPriorities.put(parts[0], Integer.parseInt(parts[1]));
		            line = br.readLine();
		    }
		    s = sb.toString();
		    br.close();
		    } catch (Throwable t) {
		    	s="";
		    }
    }
    public String say() throws RemoteException {
        return "Ciao Client";
      }
    /**
     * Main function
     * @param args  <code>args[0]</code> is the name of the new WoIS
     */
    public static void main( String[] args ) throws Exception
    {
    	LocateRegistry.createRegistry(1099);
    	System.setProperty("java.rmi.server.hostname", "192.168.153.130");
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
