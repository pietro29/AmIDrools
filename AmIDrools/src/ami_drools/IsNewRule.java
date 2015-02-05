package ami_drools;

import utility.rulesSQL;
import utility.ComboItem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import java.awt.GridLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JComboBox;

import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BoxLayout;

public class IsNewRule extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<String> factsType;
	private Boolean privFact;
	private Vector<Fact> privateFacts;
	private Vector<ComboItem> declareType;
	//private Vector<String> attributeType;
	private Vector<String> attributeTypeTHEN;
	//set 4 vector for the storage of the condition
	private Vector<String> typeSelected;
	private Vector<String> attributeSelected;
	private Vector<String> attributeTypeSelected;
	private Vector<String> opSelected;
	private Vector<String> valueSelected;
	//-----------------------------------
	private JTextField txtValore;
	private Integer counter=0;
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
	private JButton btAggiungiTHEN;
	private JPanel panelTHENResult;
	private JTextPane txtResultTHEN;
	private JPanel panelSaveRule;
	private JPanel panel_1;
	private JTextField txtRuleName;
	private JLabel lbnewRule;
	private JButton btSaveRule;
	public IsNewRule(Vector<Fact> privateFacts,Boolean privFact ) {
		this.privateFacts=privateFacts;
		this.privFact=privFact;
		
		typeSelected=new Vector<String>();
		attributeSelected=new Vector<String>();
		attributeTypeSelected=new Vector<String>();
		opSelected=new Vector<String>();
		valueSelected=new Vector<String>();
		
		getContentPane().setLayout(new GridLayout(2, 0, 0, 0));
		getDeclareFromFacts();
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
		
		cbTipologiaTHEN = new JComboBox<ComboItem>(declareType);
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
		
		btAggiungiTHEN = new JButton("Aggiungi");
		panelTHENContructor.add(btAggiungiTHEN);
		btAggiungiTHEN.addActionListener((ActionListener) this);
		
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
		
		cbTipologia = new JComboBox<ComboItem>(declareType);
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
		cbTipologiaTHEN.setSelectedIndex(0);
		txtRuleName.setText("New Rule 1");
	}

	private void getDeclareFromFacts() {
		try {
			declareType=new Vector<ComboItem>();
			ResultSet rs;
			rs=rulesSQL.getModels();
			if (rs==null){
	    		System.out.println("Table of models is empty");
	    	} else {
	    		try {
					while (rs.next()) {
						declareType.add(new ComboItem(rs.getInt("id_model"), rs.getString("des_model")));
					}
				} catch (SQLException e) {
					System.out.println("Database connection error");
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
            	//populate combo box
            	ResultSet rs;
    			rs=rulesSQL.getAttributeFromModels(id);
    			if (rs==null){
    	    		System.out.println("Table of template is empty");
    	    	} else {
    	    		try {
    					while (rs.next()) {
    						attribute.add(new ComboItem(rs.getInt("id_template"), rs.getString("des_template")));
    					}
    				} catch (SQLException e) {
    					System.out.println("Database connection error");
    				}
    	    	}
            	DefaultComboBoxModel model = new DefaultComboBoxModel(attribute.toArray());
            	if (cbAttributo!=null)
            	{
            		cbAttributo.removeAllItems();
            		cbAttributo.setModel(model);
            		cbAttributo.setSelectedIndex(0);
            	}
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
		if (event.getSource()==cbTipologiaTHEN)
	    {
			Vector<String> attribute;
            try {
            	attribute=new Vector<String>();
            	attributeTypeTHEN=new Vector<String>();
            	String tipologia = new String(cbTipologiaTHEN.getSelectedItem().toString());
            	//System.err.println(tipologia);
            	boolean inserito = false;
            	for (int j=0;j<privateFacts.size() && !inserito;j++)
    			{//if i found an object of that type extract the attributes
    				if (privateFacts.get(j).getFactType().equals(tipologia)){
    					Fact fact=privateFacts.get(j);
    					Vector <String> tempAttr = fact.getAttributes();
    		    		Vector <String> tempAttrType = fact.getAttributesType();
    		    		for (int i=0;i<tempAttr.size();i++){
    		    			if(!tempAttr.get(i).equals("_privateVisibility"))
    		    			{
    		    				attribute.addElement(tempAttr.get(i));
    		    				attributeTypeTHEN.addElement(tempAttrType.get(i));
    		    			}
    		    		}
    		    		inserito=false;
    				}
    			}
            	//System.err.println(attribute.toString());
            	//populate combo box
            	DefaultComboBoxModel model = new DefaultComboBoxModel(attribute.toArray());
            	if (cbAttributoTHEN!=null)
            	{
            		cbAttributoTHEN.removeAllItems();
            		cbAttributoTHEN.setModel(model);
            		cbAttributoTHEN.setSelectedIndex(0);
            	}
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
		if (event.getSource()==cbAttributo)
	    {
			Vector<ComboItem> op = new Vector<ComboItem>();
            try {
            	int id=((ComboItem)cbAttributo.getSelectedItem()).getKey();         	
            	//populate combo box
            	ResultSet rs;
    			rs=rulesSQL.getTypeOfAttributes(id);
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
    	    	}
            	//populate combo box
            	DefaultComboBoxModel model = new DefaultComboBoxModel(op.toArray());
            	cbOperatore.removeAllItems();
            	cbOperatore.setModel(model);
            	cbOperatore.setSelectedIndex(0);
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
		if (event.getSource()==cbAttributoTHEN)
	    {
			Vector<String> op = new Vector<String>();
            try {
            	op.add(new String("="));
    			op.add(new String("scrivi"));
    			//populate combo box
            	DefaultComboBoxModel model = new DefaultComboBoxModel(op.toArray());
            	cbOperatoreTHEN.removeAllItems();
            	cbOperatoreTHEN.setModel(model);
            	cbOperatoreTHEN.setSelectedIndex(0);
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
		if (event.getSource()==btAddCondition)
	    {
			try {
            	String template = new String(cbTipologia.getSelectedItem().toString());
            	String attribute = new String(cbAttributo.getSelectedItem().toString());
            	String operator = new String(cbOperatore.getSelectedItem().toString());
            	if(operator.equals("=")) operator="==";
             	String value = new String(txtValore.getText());
            	//store the condition
            	typeSelected.add(template);
            	attributeSelected.add(attribute);
            	opSelected.add(operator);
            	valueSelected.add(value);
            	//create the rule
            	String newRule = new String("rule \""+txtRuleName.getText()+"\" \nno-loop\n");
            	newRule+="when \n";
            	newRule+="\t $wi: Wois() \n";
            	/*if (!txtResult.getText().equals(""))
            	{
            		newRule=txtResult.getText();
            	}*/
            	//newRule+="\t $"+template.toLowerCase()+":" + template + "("+ attribute + operator + value +") \n";
            	String oldTemplate=new String("_");
            	
            	Collections.sort(typeSelected);
            	for(int i=0;i<typeSelected.size();i++)
            	{
            		template= typeSelected.get(i);
            		attribute=attributeSelected.get(i);
            		operator=opSelected.get(i);
            		value=valueSelected.get(i);
            		if(!oldTemplate.equals(template))
            		{
            			if(i>0){
            				newRule+=") \n";
            			}
            			newRule+="\t $"+template.toLowerCase()+":" + template + "("+ attribute + operator + value;
            			oldTemplate=new String(template);
            		}else
            		{
            			newRule+=", "+attribute + operator + value; 
            		}
            		
            	}
            	newRule+=") \n";
            	txtResult.setText(newRule);
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
		if (event.getSource()==btAggiungiTHEN)
	    {
			try {
            	String template = new String(cbTipologiaTHEN.getSelectedItem().toString());
            	String attribute = new String(cbAttributoTHEN.getSelectedItem().toString());
            	String operator = new String(cbOperatoreTHEN.getSelectedItem().toString());
            	String value = new String(txtValoreTHEN.getText());
            	//if(operator.equals("scrivi")) value="";
            	//create the rule
            	String newRule = new String("then \n");
            	if (!txtResultTHEN.getText().equals(""))
            	{
            		newRule=txtResultTHEN.getText().substring(0, txtResultTHEN.getText().indexOf(" end "));
            	}
            	if(operator.equals("scrivi"))
            	{ //print some text
            		String var = new String("$"+template.toLowerCase());
            		newRule+="\t txtArea.append(\""+ value +"\"+"+var+".get"+attribute.substring(0,1).toUpperCase()+attribute.substring(1,attribute.length())+"()+\"\\n\");\n";
            	}else{//modify an attribute
            		/*
            		 * if(setLock($f.getId(),$wi,ISName))
						{
							modify($f) {setAccesa(false)};
							$f.getModificati().add(new String("accesa"));
						}*/
            		
            		String var = new String("$"+template.toLowerCase());
            		if (!privFact)
            		{
            			newRule+="\tif(setLock("+var+".getId(),$wi,ISName))\n";
            			newRule+="\t{\n";
            		}
            		newRule+="\t modify("+var+") {set"+attribute.substring(0,1).toUpperCase()+attribute.substring(1,attribute.length())+"("+value+")};\n";
            		newRule+="\t "+var+".getModificati().add(new String(\""+attribute+"\"));\n";
            		if (!privFact)
            		{
            			newRule+="\t}\n";
            		}
            	}
            	newRule+=" end ";
            	txtResultTHEN.setText(newRule);
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
		if (event.getSource()==btSaveRule)
	    {
			try {//write into the file txt
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
			
			Integer id=-1;
			//insert rule header
			System.err.println("0");
			try {
				rulesSQL.RulesInsert(txtRuleName.getText(), 1, 1, 50, 0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String oldTemplate=new String("_");
			String template;
			String attribute;
			String operator;
			String value;
			Collections.sort(typeSelected);
			for(int i=0;i<typeSelected.size();i++)
			{
				template= typeSelected.get(i);
				attribute=attributeSelected.get(i);
				operator=opSelected.get(i);
				value=valueSelected.get(i);
				if(!oldTemplate.equals(template))
				{
					//newRule+="\t $"+template.toLowerCase()+":" + template + "("+ attribute + operator + value;
					System.err.println("1");
					try {
						rulesSQL.RulesIfFactsInsert(1, template.toLowerCase());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.err.println("2");
					try {
						rulesSQL.RulesIfFactsDetailsInsert(1, operator.toLowerCase(), value);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					oldTemplate=new String(template);
				}else
				{
					rulesSQL.RulesIfFactsDetailsInsert(1, operator.toLowerCase(), value);
					//newRule+=", "+attribute + operator + value; 
				}
				
			}
			
			typeSelected=new Vector<String>();
			attributeSelected=new Vector<String>();
			attributeTypeSelected=new Vector<String>();
			opSelected=new Vector<String>();
			valueSelected=new Vector<String>();
			
    	}
	}
}
