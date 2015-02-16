package ami_drools;

import utility.ConditionActionItem;
import utility.ResultSetSerializable;
import utility.rulesSQLIS;
import utility.ComboItem;
import utility.rulesSQLManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JComboBox;

import java.awt.FlowLayout;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JLabel;


/**
 * @author Carè-Pavoni
 *
 */
public class IsNewRule extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Wois wois;
	private String ISName;
	//private Vector<String> factsType;
	private Boolean privFact;
	//private Vector<Fact> privateFacts;
	private Vector<ComboItem> ifModels;
	private Vector<ComboItem> thenModels;
	//private Vector<String> attributeType;
	//private Vector<String> attributeTypeTHEN;
	//set vector for the storage of the condition
	private Vector<ConditionActionItem> conditions;
	//-----------------------------------
	//set vector for the storage of the action
	private Vector<ConditionActionItem> actions;
	//-----------------------------------
	private JTextField txtValore;
	//private Integer counter=0;
	JComboBox<ComboItem> cbTipologia;
	JComboBox<ComboItem> cbAttributo;
	JComboBox<ComboItem> cbOperatore;
	private JTextPane txtResult;
	private JPanel panelIFResult;
	private JPanel panelIFConstructor;
	private JButton btAddCondition;
	private JPanel panelTHENContructor;
	private JLabel lblThen;
	private JComboBox<ComboItem> cbTipologiaTHEN;
	private JComboBox<ComboItem> cbAttributoTHEN;
	private JComboBox<ComboItem> cbOperatoreTHEN;
	private JTextField txtValoreTHEN;
	private JButton btAddAction;
	private JPanel panelTHENResult;
	private JTextPane txtResultTHEN;
	private JPanel panelSaveRule;
	private JPanel panel_1;
	private JTextField txtRuleName;
	private JLabel lbnewRule;
	private JButton btSaveRule;
	private JButton btRefresh;
	private JButton btUndoAction;
	private JButton btUndoCondition;
	/**Constructor method
	 * @param privFact set to true if want to use only private fact
	 */
	public IsNewRule(Boolean privFact, Wois wois, String ISName) {
		this.wois=wois;
		this.privFact=privFact;
		this.ISName=ISName;
		conditions=new Vector<ConditionActionItem>();
		actions=new Vector<ConditionActionItem>();
		getContentPane().setLayout(new GridLayout(2, 0, 0, 0));
		getIFModels();
		getTHENModels();
		JPanel panelIF = new JPanel();
		panelIF.setBorder(new LineBorder(Color.DARK_GRAY, 2, true));
		getContentPane().add(panelIF);
		panelIF.setLayout(new GridLayout(2, 1, 0, 0));
		this.setIconImage(new ImageIcon(ClassLoader.getSystemResource("images/drools.png")).getImage());
		JPanel panelTHEN = new JPanel();
		panelTHEN.setBorder(new LineBorder(Color.DARK_GRAY, 2, true));
		getContentPane().add(panelTHEN);
		panelTHEN.setLayout(new GridLayout(2, 1, 0, 0));
		
		panelTHENContructor = new JPanel();
		FlowLayout fl_panelTHENContructor = (FlowLayout) panelTHENContructor.getLayout();
		fl_panelTHENContructor.setAlignment(FlowLayout.LEFT);
		panelTHEN.add(panelTHENContructor);
		
		lblThen = new JLabel("THEN");
		panelTHENContructor.add(lblThen);
		
		cbTipologiaTHEN = new JComboBox<ComboItem>(thenModels);
		cbTipologiaTHEN.addActionListener((ActionListener) this);
		panelTHENContructor.add(cbTipologiaTHEN);
		
		cbAttributoTHEN = new JComboBox<ComboItem>();
		panelTHENContructor.add(cbAttributoTHEN);
		cbAttributoTHEN.addActionListener((ActionListener) this);
		
		cbOperatoreTHEN = new JComboBox<ComboItem>();
		panelTHENContructor.add(cbOperatoreTHEN);
		cbOperatoreTHEN.addActionListener((ActionListener) this);
		txtValoreTHEN = new JTextField();
		txtValoreTHEN.setColumns(10);
		panelTHENContructor.add(txtValoreTHEN);
		
		btAddAction = new JButton("Aggiungi");
		panelTHENContructor.add(btAddAction);
		btAddAction.addActionListener((ActionListener) this);
		
		panelTHENResult = new JPanel();
		panelTHEN.add(panelTHENResult);
		panelTHENResult.setLayout(new GridLayout(0, 1, 0, 0));
		
		txtResultTHEN = new JTextPane();
		panelTHENResult.add(txtResultTHEN);
		
		panelIFConstructor = new JPanel();
		panelIF.add(panelIFConstructor);
		
		panelIFResult = new JPanel();
		panelIF.add(panelIFResult);
		panelIFResult.setLayout(new GridLayout(0, 1, 0, 0));
		
		txtResult = new JTextPane();
		panelIFResult.add(txtResult);
		panelIFConstructor.setLayout(new GridLayout(2, 1, 0, 0));
		
		panelSaveRule = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panelSaveRule.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panelIFConstructor.add(panelSaveRule);
		
		lbnewRule = new JLabel("Nome Regola");
		panelSaveRule.add(lbnewRule);
		
		txtRuleName = new JTextField();
		panelSaveRule.add(txtRuleName);
		txtRuleName.setColumns(30);
		
		btSaveRule = new JButton("Salva");
		panelSaveRule.add(btSaveRule);
		btSaveRule.addActionListener((ActionListener) this);
		
		panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelIFConstructor.add(panel_1);
		
		JLabel lblNewLabel = new JLabel("IF");
		panel_1.add(lblNewLabel);
		
		cbTipologia = new JComboBox<ComboItem>(ifModels);
		panel_1.add(cbTipologia);
		cbTipologia.addActionListener((ActionListener) this);
		
		
		
		cbAttributo = new JComboBox<ComboItem>();
		panel_1.add(cbAttributo);
		
		cbOperatore = new JComboBox<ComboItem>();
		panel_1.add(cbOperatore);
		
		txtValore = new JTextField();
		panel_1.add(txtValore);
		txtValore.setColumns(20);
		
		btAddCondition = new JButton("Aggiungi");
		panel_1.add(btAddCondition);
		btAddCondition.addActionListener((ActionListener) this);
		cbAttributo.addActionListener((ActionListener) this);
		cbTipologia.setSelectedIndex(0);
		
		btUndoCondition = new JButton("Undo");
		panel_1.add(btUndoCondition);
		btUndoCondition.addActionListener((ActionListener) this);
		
		cbTipologiaTHEN.setSelectedIndex(0);
		
		btUndoAction = new JButton("Undo");
		panelTHENContructor.add(btUndoAction);
		btUndoAction.addActionListener((ActionListener) this);
		
		txtRuleName.setText("New Rule 1");
		
		btRefresh = new JButton("Ricarica");
		panelSaveRule.add(btRefresh);
		btRefresh.addActionListener((ActionListener) this);
	}

	/**
	 * Get the models for the creation of the condition
	 */
	private void getIFModels() {
		try {
			ifModels=new Vector<ComboItem>();
			ResultSet rs;
			rs=rulesSQLIS.getModelsIF();
			if (rs==null){
	    		System.out.println("Table of models is empty");
	    	} else {
	    		try {
					while (rs.next()) {
						ifModels.add(new ComboItem(rs.getInt("id_model"), rs.getString("des_model")));
					}
				} catch (SQLException e) {
					System.out.println("Database connection error");
				}
	    	}
			if(!privFact && wois!=null)
			{
				ResultSetSerializable rsPublic;
				System.out.println("x1---------");
				rsPublic=wois.getPublicModelsIf();
				if (rsPublic==null){
		    		System.out.println("Table of models is empty");
		    	} else {
		    		System.out.println("x2---------");
		    		try {
						while (rsPublic.next()) {
							System.out.println("x2---------");
							ifModels.add(new ComboItem(Integer.parseInt(rsPublic.getString("id_model")), rsPublic.getString("des_model"),true));
						}
					} catch (Exception e) {
						System.out.println("Database connection error");
					}
		    	}
			}
		} catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
		}
	}
	
	/**
	 * Get the models for the creation of the action 
	 */
	private void getTHENModels() {
		try {
			thenModels=new Vector<ComboItem>();
			ResultSet rs;
			rs=rulesSQLIS.getModelsTHEN();
			if (rs==null){
	    		System.out.println("Table of models is empty");
	    	} else {
	    		try {
					while (rs.next()) {
						thenModels.add(new ComboItem(rs.getInt("id_model"), rs.getString("des_model")));
					}
				} catch (SQLException e) {
					System.out.println("Database connection error");
				}
	    	}
			if(!privFact && wois!=null)
			{
				ResultSetSerializable rsPublic;
				rsPublic=wois.getPublicModelsThen();
				if (rsPublic==null){
		    		System.out.println("Table of models is empty");
		    	} else {
		    		try {
						while (rsPublic.next()) {
							thenModels.add(new ComboItem(Integer.parseInt(rsPublic.getString("id_model")), rsPublic.getString("des_model"),true));
						}
					} catch (Exception e) {
						System.out.println("Database connection error");
					}
		    	}
			}
		} catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource()==cbTipologia)
	    {
			Vector<ComboItem> attribute=new Vector<ComboItem>();
            try {
            	int id=((ComboItem)cbTipologia.getSelectedItem()).getKey();         	
            	//populate condition attribute combo box
            	if(!((ComboItem)cbTipologia.getSelectedItem()).getShared()){
            		ResultSet rs;
        			rs=rulesSQLIS.getAttributeFromModels(id);
        			if (rs==null){
        	    		System.out.println("Table of template is empty");
        	    	} else {
        	    		try {
        					while (rs.next()) {
        						attribute.add(new ComboItem(rs.getInt("id_attribute"), rs.getString("des_attribute")));
        					}
        				} catch (SQLException e) {
        					System.out.println("Database connection error");
        				}
        	    	}rs.close();
            	}else{
            		ResultSetSerializable rs;
        			rs=wois.getAttributesFromModel(id);
        			if (rs==null){
        	    		System.out.println("Table of template is empty");
        	    	} else {
        	    		try {
        					while (rs.next()) {
        						attribute.add(new ComboItem(Integer.parseInt(rs.getString("id_attribute")), rs.getString("des_attribute")));
        					}
        				} catch (Exception e) {
        					System.out.println("Database connection error");
        				}
        	    	}
            	}
            	
    			if(attribute.size()>0)
    			{
    				if (cbAttributo!=null)
	            	{
	            		cbAttributo.removeAllItems();
	            	}
    				for(int i=0;i<attribute.size();i++)
    				{
    					cbAttributo.addItem(attribute.get(i));
    				}
	            	if (cbAttributo!=null)
	            	{
	            		cbAttributo.setSelectedIndex(0);
	            	}
    			}
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
		if (event.getSource()==cbTipologiaTHEN)
	    {
			Vector<ComboItem> attribute=new Vector<ComboItem>();
            try {
            	int id=((ComboItem)cbTipologiaTHEN.getSelectedItem()).getKey();         	
            	//populate action attribute combo box
            	if(!((ComboItem)cbTipologiaTHEN.getSelectedItem()).getShared()){
	            	ResultSet rs;
	    			rs=rulesSQLIS.getAttributeFromModels(id);
	    			if (rs==null){
	    	    		System.out.println("Table of template is empty");
	    	    	} else {
	    	    		try {
	    					while (rs.next()) {
	    						attribute.add(new ComboItem(rs.getInt("id_attribute"), rs.getString("des_attribute")));
	    					}
	    				} catch (SQLException e) {
	    					System.out.println("Database connection error");
	    				}
	    	    	}rs.close();
            	}else{
            		ResultSetSerializable rs;
        			rs=wois.getAttributesFromModel(id);
        			if (rs==null){
        	    		System.out.println("Table of template is empty");
        	    	} else {
        	    		try {
        					while (rs.next()) {
        						attribute.add(new ComboItem(Integer.parseInt(rs.getString("id_attribute")), rs.getString("des_attribute")));
        					}
        				} catch (Exception e) {
        					System.out.println("Database connection error");
        				}
        	    	}
            	}
    			if(attribute.size()>0)
    			{
    				if (cbAttributoTHEN!=null)
	            	{
    					cbAttributoTHEN.removeAllItems();
	            	}
    				for(int i=0;i<attribute.size();i++)
    				{
    					cbAttributoTHEN.addItem(attribute.get(i));
    				}
	            	if (cbAttributoTHEN!=null)
	            	{
	            		cbAttributoTHEN.setSelectedIndex(0);
	            	}
    			}
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
		if (event.getSource()==cbAttributo)
	    {
			Vector<ComboItem> op = new Vector<ComboItem>();
			try {
				if(cbAttributo.getSelectedItem()!=null){
	            	int id=((ComboItem)cbAttributo.getSelectedItem()).getKey();         	
	            	//populate condition operation combo box, differs from type to type
	            	if(!((ComboItem)cbTipologia.getSelectedItem()).getShared()){
		            	ResultSet rs;
		    			rs=rulesSQLIS.getTypeOfAttributes(id);
		    			if (rs==null){
		    	    		System.out.println("Type of attribute not found");
		    	    	} else {
		    	    		try {
		    					while (rs.next()) {
		    						if(rs.getString("type_attribute").equals("Boolean") || rs.getString("type_attribute").equals("String"))
		    						{
		    							op.add(new ComboItem(0, "="));
		    							op.add(new ComboItem(0, "!="));
		    						}else{
		    							op.add(new ComboItem(0, "="));
		    							op.add(new ComboItem(0, "!="));
		    							op.add(new ComboItem(0, "<"));
		    							op.add(new ComboItem(0, "<="));
		    							op.add(new ComboItem(0, ">"));
		    							op.add(new ComboItem(0, ">="));
		    						}
		    					}
		    				} catch (SQLException e) {
		    					System.out.println("Database connection error");
		    				}
		    	    	}rs.close();
	            	}else{
	            		ResultSetSerializable rs;
		    			rs=wois.getTypeOfAttributes(id);
		    			if (rs==null){
		    	    		System.out.println("Type of attribute not found");
		    	    	} else {
		    	    		try {
		    					while (rs.next()) {
		    						if(rs.getString("type_attribute").equals("Boolean") || rs.getString("type_attribute").equals("String"))
		    						{
		    							op.add(new ComboItem(0, "="));
		    							op.add(new ComboItem(0, "!="));
		    						}else{
		    							op.add(new ComboItem(0, "="));
		    							op.add(new ComboItem(0, "!="));
		    							op.add(new ComboItem(0, "<"));
		    							op.add(new ComboItem(0, "<="));
		    							op.add(new ComboItem(0, ">"));
		    							op.add(new ComboItem(0, ">="));
		    						}
		    					}
		    				} catch (Exception e) {
		    					System.out.println("Database connection error");
		    				}
	            	}
	            	}
	    			if(op.size()>0)
	    			{
	    				if (cbOperatore!=null)
		            	{
	    					cbOperatore.removeAllItems();
		            	}
	    				for(int i=0;i<op.size();i++)
	    				{
	    					cbOperatore.addItem(op.get(i));
	    				}
		            	if (cbOperatore!=null)
		            	{
		            		cbOperatore.setSelectedIndex(0);
		            	}
	    			}
				}
				
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
		if (event.getSource()==cbAttributoTHEN)
	    {
			Vector<ComboItem> op = new Vector<ComboItem>();
            try {
            	if(cbAttributoTHEN.getSelectedItem()!=null){
	            	int id=((ComboItem)cbAttributoTHEN.getSelectedItem()).getKey();         	
	            	//populate action operation combo box, is an assignment or a notification
	            	ResultSet rs;
	    			rs=rulesSQLIS.getTypeOfAttributes(id);
	    			if (rs==null){
	    	    		System.out.println("Type of attribute not found");
	    	    	} else {
	    	    		op.add(new ComboItem(0, "notify"));
						op.add(new ComboItem(0, "="));
	    	    	}rs.close();
	    			if(op.size()>0)
	    			{
	    				if (cbOperatoreTHEN!=null)
		            	{
	    					cbOperatoreTHEN.removeAllItems();
		            	}
	    				for(int i=0;i<op.size();i++)
	    				{
	    					cbOperatoreTHEN.addItem(op.get(i));
	    				}
		            	if (cbOperatoreTHEN!=null)
		            	{
		            		cbOperatoreTHEN.setSelectedIndex(0);
		            	}
	    			}
            	}
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
		if (event.getSource()==btAddCondition)
	    {//confirm a condition
			boolean inserire=true;
			try {
            	ComboItem model = (ComboItem)cbTipologia.getSelectedItem();
            	ComboItem attribute = (ComboItem)cbAttributo.getSelectedItem();
            	String operator = new String(cbOperatore.getSelectedItem().toString());
            	if(operator.equals("=")) operator="==";
            	try {
					ResultSet rs;
					rs=rulesSQLIS.getTypeOfAttributes(attribute.getKey());
					if (rs==null){
						System.out.println("Type of attribute not found");
					} else {
						while (rs.next()) {
							String typeAttr=new String(rs.getString("type_attribute").toLowerCase());
	    	    			inserire=checkAttributeType(typeAttr,txtValore);
	    	    		}
					}rs.close();
				} catch (Exception e) {
				}
            	String value = new String(txtValore.getText());
            	//store the condition
            	if (inserire){
            		conditions.add(new ConditionActionItem(model.getKey(), model.getValue(), attribute.getKey(), attribute.getValue(), operator, value.replace("\"", ""),model.getShared()));
            		RefreshConditionPanel();
            	}
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
		if (event.getSource()==btAddAction)
	    {
			boolean inserire=true;
			try {//confirm an action
				ComboItem model = (ComboItem)cbTipologiaTHEN.getSelectedItem();
            	ComboItem attribute = (ComboItem)cbAttributoTHEN.getSelectedItem();
            	String operator = new String(cbOperatoreTHEN.getSelectedItem().toString());
            	if(!model.getShared()){
	            	try {
						ResultSet rs;
						rs=rulesSQLIS.getTypeOfAttributes(attribute.getKey());
						if (rs==null){
							System.out.println("Type of attribute not found");
						} else {
							while (rs.next()) {
								String typeAttr=new String(rs.getString("type_attribute").toLowerCase());
		    	    			inserire=checkAttributeType(typeAttr,txtValoreTHEN);
		    	    		}
						}rs.close();
					} catch (Exception e) {
					}
            	}else{
            		try {
						ResultSetSerializable rs;
						rs=wois.getTypeOfAttributes(attribute.getKey());
						if (rs==null){
							System.out.println("Type of attribute not found");
						} else {
							while (rs.next()) {
								String typeAttr=new String(rs.getString("type_attribute").toLowerCase());
		    	    			inserire=checkAttributeType(typeAttr,txtValoreTHEN);
		    	    		}
						}
					} catch (Exception e) {
					}
            	}
            		
            	String value = new String(txtValoreTHEN.getText());
            	//store the condition
            	if (inserire){
	            	//store the action
	            	actions.add(new ConditionActionItem(model.getKey(), model.getValue(), attribute.getKey(), attribute.getValue(), operator, value.replace("\"", ""),model.getShared()));
	            	insertExistsCondition(model.getKey(), model.getValue(),0 ,"" ,"","",model.getShared());
	            	RefreshActionPanel();
            	}
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
		if (event.getSource()==btRefresh)
	    {//refresh the text area
			RefreshConditionPanel();
			RefreshActionPanel();
	    }
		if (event.getSource()==btUndoCondition)
	    {
			try {//remove the last condition
				conditions.remove(conditions.size()-1);
			} catch (Exception e) {
			}
			RefreshConditionPanel();
	    }
		if (event.getSource()==btUndoAction)
	    {
			try {//remove the last action
				actions.remove(actions.size()-1);
			} catch (Exception e) {
			}
			RefreshActionPanel();
	    }
		if (event.getSource()==btSaveRule)
	    {
			/*try {//write into the file txt
				String fileName = new String();
				if(privFact)
				{
					fileName="resources/local_rules.txt";
				}else
				{
					fileName="resources/shared_rules.txt";
				}
				if(!txtResult.equals("") && !txtResultTHEN.equals(""))
				{
					try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ClassLoader.getSystemResource(fileName).getFile(), true)))) {
					    out.println("\n"+txtResult.getText()+"\n"+txtResultTHEN.getText()+"\n");
					}catch (IOException e) {
					    //exception handling left as an exercise for the reader
					}
				}
            } catch (Exception e) {
            	e.printStackTrace();
            }
			*/
			//insert rule header
			if(privFact){
				insertPrivateRules();
	    }else{
	    	try {
				insertSharedRules();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotRegisteredException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    }
	}
	
	private void RefreshConditionPanel(){
		//create the rule
    	String newRule = new String("rule \""+txtRuleName.getText()+"\" \nno-loop\n");
    	if (conditions.size()>0){
    		newRule+="when \n";
        	if (!privFact)newRule+="\t $wi: Wois() \n";

        	String oldMod=new String("_");
        	
        	Collections.sort(conditions);
        	for(int i=0;i<conditions.size();i++)
        	{
        		String mod= conditions.get(i).getDes_model();
        		String att=conditions.get(i).getDes_attribute();
        		String op=conditions.get(i).getOp();
        		String value=conditions.get(i).getValue();
        		if(!oldMod.equals(mod))
        		{
        			if(i>0){
        				newRule+=") \n";
        			}
        			newRule+="\t $"+mod.toLowerCase()+":" + mod+ "("+ att + op + value;
        			oldMod=new String(mod);
        		}else
        		{
        			if(!op.toString().equals(""))newRule+=", "+att + op + value; 
        		}
        		
        	}
        	newRule+=") \n";
    	}else{
    		newRule="--Nessuna condizione inserita!";
    	}
    	txtResult.setText(newRule);
	}
	
	private void RefreshActionPanel(){
		String newRule = new String("then \n");
		if (actions.size()>0){
			String oldMod=new String("_");
	    	Collections.sort(conditions);
	    	boolean atLeatOneSet=false;
	    	//prima imposto tutti i setLock se non è una regola che usa solo fatti privati
	    	if (!privFact){
	    		String ifString=new String("\tif(");
	    		for(int i=0;i<actions.size();i++){
	    			
	    			String mod= actions.get(i).getDes_model();
	        		String att=actions.get(i).getDes_attribute();
	        		String op=actions.get(i).getOp();
	        		String value=actions.get(i).getValue();
	        		if(op.equals("=") && !oldMod.equals(mod)){
	        			if(!actions.get(i).getShared()){
		        			/*try {
								ResultSet rs;
								rs=rulesSQLIS.getModel(actions.get(i).getId_model());
								if (rs==null){
									System.out.println("Model not found");
								} else {//set the lock only for public fact
									if(rs.getInt("id_user")==0)
									{
										String var = new String("$"+mod.toLowerCase());
										ifString+="setLock("+var+".getId(),$wi,ISName) && ";
										oldMod=new String(mod);
										atLeatOneSet=true;
									}	
								}rs.close();
							} catch (SQLException e) {
							}*/
	        			}else{
	        				try{
		        				ResultSetSerializable rs;
								rs=wois.getPublicModel(actions.get(i).getId_model());
								if (rs==null){
									System.out.println("Model not found");
								} else {//set the lock only for public fact
									rs.next();
									if(Integer.parseInt(rs.getString("id_user"))==0)
									{
										String var = new String("$"+mod.toLowerCase());
										ifString+="setLock("+var+".getId(),$wi,ISName) && ";
										oldMod=new String(mod);
										atLeatOneSet=true;
									}	
								}
						} catch (Exception e) {
						}
	        			}
	        				            			
	        		}
	    		}
	    		if(atLeatOneSet)
	    		{
	    			ifString=ifString.substring(0, ifString.length()-4);
		    		ifString+=(")\n\t{ \n");
		    		newRule=newRule.concat(ifString);
	    		}
	    		
	    	} 
	    	
	    	for(int i=0;i<actions.size();i++){
	    		String mod= actions.get(i).getDes_model();
	    		String att=actions.get(i).getDes_attribute();
	    		String op=actions.get(i).getOp();
	    		String value=actions.get(i).getValue();
	    		String var = new String("$"+mod.toLowerCase());
	    		if(op.equals("notify"))
	        	{ //print some text
	        		newRule+="\t txtArea.append(\""+ value +"\"+"+var+".get"+att.substring(0,1).toUpperCase()+att.substring(1,att.length())+"()+\"\\n\");\n";
	        	}else{//modify an attribute
	        		/*
	        		 * if(setLock($f.getId(),$wi,ISName))
						{
							modify($f) {setAccesa(false)};
							$f.getModificati().add(new String("accesa"));
						}*/
	        		if(!privFact) newRule+="\t";
	        		newRule+="\t modify("+var+") {set"+att.substring(0,1).toUpperCase()+att.substring(1,att.length())+"("+value+")};\n";
	        		if(!privFact) newRule+="\t";
	        		newRule+="\t "+var+".getModificati().add(new String(\""+att+"\"));\n";
	        	}
	    	}
	    	if (!privFact && atLeatOneSet)newRule+="\t}\n";
	    	newRule+=" end ";
		}else{
			newRule="--Nessuna azione inserita!";
		}
    	txtResultTHEN.setText(newRule);
	}
	
	/**check if the value in the text box is the same type as the value of the attribute
	 * @param typeAttr define the attribute type
	 * @param txtvalue define the text box where is the value is stored
	 * @return true if the type match
	 */
	private boolean checkAttributeType(String typeAttr, JTextField txtvalue){
		boolean inserire=true;
		String value=txtvalue.getText();
		if(typeAttr.equals("boolean")){//boolean case
			if(cbOperatoreTHEN.getSelectedItem().toString().equals("=") && !(value.toLowerCase().equals("true") || value.toLowerCase().equals("false"))){
				int dialogButton = JOptionPane.YES_NO_OPTION;
				int dialogResult = JOptionPane.showConfirmDialog (null, "Il valore deve essere booleano, "
						+ "vuoi confermare un valore True?","Warning",dialogButton);
				if(dialogResult == JOptionPane.YES_OPTION){
					value="True";
					txtvalue.setText(value);
				}else{
					inserire=false;
					//JOptionPane.showMessageDialog(null, "Error, invalid input type! " + value + "is not a boolean type");
				}
				
			}
		}else if(typeAttr.equals("string") && cbOperatoreTHEN.getSelectedItem().toString().equals("=")){//string case
			txtvalue.setText("\""+value+"\"");
		}else if(cbOperatoreTHEN.getSelectedItem().toString().equals("=") && (typeAttr.equals("int") || typeAttr.equals("integer") || typeAttr.equals("double") || typeAttr.equals("decimal") || typeAttr.equals("numeric") || typeAttr.equals("real"))){
			//numeric case
			if(!value.matches("-?\\d+(\\.\\d+)?")){//check if the value is a numeric type looking the pattern
				inserire=false;
				JOptionPane.showMessageDialog(null, "Error, invalid input type! " + value + "is not a number");
			}
		}
		return inserire;
	}
	
	private void insertPrivateRules(){
		String SQL=new String("");
		try {
			SQL+=rulesSQLIS.RulesInsert(txtRuleName.getText(), 1, true, 50, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//insert conditions
		String oldMod=new String("_");
		Collections.sort(conditions);
		for(int i=0;i<conditions.size();i++)
		{
			String mod= conditions.get(i).getDes_model();
    		if(!oldMod.equals(mod))
			{
				try {
					SQL+=rulesSQLIS.RulesIfFactsInsert( conditions.get(i).getId_model(),  conditions.get(i).getDes_model().toLowerCase());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					SQL+=rulesSQLIS.RulesIfFactsDetailsInsert(conditions.get(i).getId_attribute(), conditions.get(i).getOp(), conditions.get(i).getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				oldMod=new String(mod);
			}else
			{
				SQL+=rulesSQLIS.RulesIfFactsDetailsInsert(conditions.get(i).getId_attribute(), conditions.get(i).getOp(), conditions.get(i).getValue());
				//newRule+=", "+attribute + operator + value; 
			}
		}
		
		//insert action
		oldMod=new String("_");
		Collections.sort(actions);
		for(int i=0;i<actions.size();i++)
		{
			String mod= actions.get(i).getDes_model();
			if(!oldMod.equals(mod))
			{
				//newRule+="\t $"+template.toLowerCase()+":" + template + "("+ attribute + operator + value;
				try {
					SQL+=rulesSQLIS.RulesThenFactsInsert( actions.get(i).getId_model(),  actions.get(i).getDes_model().toLowerCase());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					SQL+=rulesSQLIS.RulesThenFactsDetailsInsert(actions.get(i).getId_attribute(), actions.get(i).getOp(), actions.get(i).getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				oldMod=new String(mod);
			}else
			{
				SQL+=rulesSQLIS.RulesThenFactsDetailsInsert(actions.get(i).getId_attribute(), actions.get(i).getOp(), actions.get(i).getValue());
				//newRule+=", "+attribute + operator + value; 
			}
		}
		try {
			rulesSQLIS.fireSQLInsertPrivateRule(SQL);
			conditions=new Vector<ConditionActionItem>();
			actions=new Vector<ConditionActionItem>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void insertSharedRules() throws RemoteException, NotRegisteredException
	{
		if(wois!=null){
			//insert private model if not exists
			Integer id_user=1;
			Vector<ConditionActionItem> condAct=new Vector<ConditionActionItem>();
			condAct.addAll(conditions);
			condAct.addAll(actions);
			for(int i=0;i<condAct.size();i++){
				try {
				ResultSetSerializable rs;
				rs=wois.getUsersModels(condAct.get(i).getDes_model(), id_user);
				if (rs==null){
					System.out.println("Model not found");
				} else {
					if(!rs.next() && !condAct.get(i).getShared())//no rows and no shared fact, insert the model
					{
						ResultSet rsLocalModel;
						rsLocalModel=rulesSQLIS.getModel(condAct.get(i).getId_model());
						if(rsLocalModel==null)
						{System.out.println("Model not found");}else{
							if(rsLocalModel.next())
							{
								ResultSet rsLocalModelAttributes;
								rsLocalModelAttributes=rulesSQLIS.getAttributeFromModels(condAct.get(i).getId_model());
								Vector<String> des_attribute=new Vector<String>();
								Vector<String> type_attribute=new Vector<String>();
								if(rsLocalModelAttributes==null)
								{System.out.println("Model not found");}else{
									while(rsLocalModelAttributes.next())
									{
										des_attribute.add(rsLocalModelAttributes.getString("des_attribute"));
										type_attribute.add(rsLocalModelAttributes.getString("type_attribute"));
									}
								}
								try {
									boolean inserito=true;
									boolean if_model=rsLocalModel.getBoolean("if_model");
									boolean then_model=rsLocalModel.getBoolean("then_model");
									inserito=wois.newModelWithAttribute(rsLocalModel.getString("des_model"), id_user,if_model ,
											then_model, des_attribute, type_attribute);
									if (!inserito)
									{
										System.out.println("Error, model not insert");
									}
								} catch (RemoteException e) {
									e.printStackTrace();
								}
							}
							
						}rsLocalModel.close();
					}	
				}
				} catch (SQLException e) {
				}
			}
			//insert rule, the id is not the same, so pass the description
			String SQL=new String("");
			try {
				SQL+=wois.RulesInsert_manager(txtRuleName.getText(),ISName, true, 50, false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//insert conditions
			String oldMod=new String("_");
			Collections.sort(conditions);
			for(int i=0;i<conditions.size();i++)
			{
				String mod= conditions.get(i).getDes_model();
	    		if(!oldMod.equals(mod))
				{
					try {
						//SQL+=rulesSQLIS.RulesIfFactsInsert( conditions.get(i).getId_model(),  conditions.get(i).getDes_model().toLowerCase());
						SQL+=wois.RulesIfFactsInsert_manager( conditions.get(i).getDes_model(),  conditions.get(i).getDes_model().toLowerCase());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						//SQL+=rulesSQLIS.RulesIfFactsDetailsInsert(conditions.get(i).getId_attribute(), conditions.get(i).getOp(), conditions.get(i).getValue());
						SQL+=wois.RulesIfFactsDetailsInsert_manager(conditions.get(i).getDes_attribute(),mod, conditions.get(i).getOp(), conditions.get(i).getValue());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					oldMod=new String(mod);
				}else
				{
					SQL+=wois.RulesIfFactsDetailsInsert_manager(conditions.get(i).getDes_attribute(),mod, conditions.get(i).getOp(), conditions.get(i).getValue()); 
				}
			}
			
			//insert action
			oldMod=new String("_");
			Collections.sort(actions);
			for(int i=0;i<actions.size();i++)
			{
				String mod= actions.get(i).getDes_model();
				if(!oldMod.equals(mod))
				{
					//newRule+="\t $"+template.toLowerCase()+":" + template + "("+ attribute + operator + value;
					try {
						//SQL+=rulesSQLIS.RulesThenFactsInsert( actions.get(i).getId_model(),  actions.get(i).getDes_model().toLowerCase());
						SQL+=wois.RulesThenFactsInsert_manager( actions.get(i).getDes_model(),  actions.get(i).getDes_model().toLowerCase());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						//SQL+=rulesSQLIS.RulesThenFactsDetailsInsert(actions.get(i).getId_attribute(), actions.get(i).getOp(), actions.get(i).getValue());
						SQL+=wois.RulesThenFactsDetailsInsert_manager(actions.get(i).getDes_attribute(),mod, actions.get(i).getOp(), actions.get(i).getValue());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					oldMod=new String(mod);
				}else
				{
					//SQL+=rulesSQLIS.RulesThenFactsDetailsInsert(actions.get(i).getId_attribute(), actions.get(i).getOp(), actions.get(i).getValue());
					SQL+=wois.RulesThenFactsDetailsInsert_manager(actions.get(i).getDes_attribute(),mod, actions.get(i).getOp(), actions.get(i).getValue());
				}
			}
			try {
				wois.newRule(SQL);
				conditions=new Vector<ConditionActionItem>();
				actions=new Vector<ConditionActionItem>();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
	}
	private void insertExistsCondition(Integer id_model,String des_model, Integer id_attribute,String des_attribute, String operator, String value, boolean shared){
		//if not exists the variable in the condition insert it
		boolean trovato=false;
		int i=0;
		for(i=0;i<conditions.size() && !trovato;i++)
		{
			if(des_model.toLowerCase().equals(conditions.get(i)))
			{
				trovato=true;
			}
		}
		if(!trovato){
			conditions.add(new ConditionActionItem(id_model, des_model, id_attribute, des_attribute, operator, value.replace("\"", ""),shared));
    		RefreshConditionPanel();
		}
	}
}
