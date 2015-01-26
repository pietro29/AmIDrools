package ami_drools;

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
import java.io.InputStream;
import java.io.InputStreamReader;
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
	private RuleRunner runner;
	private Vector<Fact> privateFacts;
	private Vector<String> declareType;
	private Vector<String> attributeType;
	private JTextField txtValore;
	
	JComboBox<String> cbTipologia;
	JComboBox<String> cbAttributo;
	JComboBox<String> cbOperatore;
	private JTextPane textPane;
	private JPanel panelIFResult;
	private JPanel panelIFConstructor;
	
	public IsNewRule(Vector<Fact> privateFacts) {
		this.privateFacts=privateFacts;
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
		
		panelIFConstructor = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelIFConstructor.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelIF.add(panelIFConstructor);
		
		JLabel lblNewLabel = new JLabel("IF");
		panelIFConstructor.add(lblNewLabel);
		
		cbTipologia = new JComboBox<String>(declareType);
		panelIFConstructor.add(cbTipologia);
		cbTipologia.addActionListener((ActionListener) this);
		
		
		cbAttributo = new JComboBox<String>();
		panelIFConstructor.add(cbAttributo);
		
		cbOperatore = new JComboBox<String>();
		panelIFConstructor.add(cbOperatore);
		
		txtValore = new JTextField();
		panelIFConstructor.add(txtValore);
		txtValore.setColumns(10);
		
		JButton btAddCondition = new JButton("Aggiungi");
		panelIFConstructor.add(btAddCondition);
		cbAttributo.addActionListener((ActionListener) this);
		
		panelIFResult = new JPanel();
		panelIF.add(panelIFResult);
		panelIFResult.setLayout(new GridLayout(0, 1, 0, 0));
		
		textPane = new JTextPane();
		panelIFResult.add(textPane);
		cbTipologia.setSelectedIndex(0);
	}

	private void getDeclareFromFacts() {
		try {
			declareType=new Vector<String>();
			System.err.println(privateFacts.toString());
			for (int j=0;j<privateFacts.size();j++)
			{
				boolean inserito =false;
				Fact fact=privateFacts.get(j);
				String tempFactType = fact.getFactType();
	    		for(int i=0;i<declareType.size();i++)
	    		{//inserisco la tipologia se non l'ho già fatto
	    			if (declareType.get(i).equals(tempFactType)) inserito=true;
	    		}
	    		if (!inserito)declareType.add(tempFactType);
			}
			System.err.println( declareType.toString());
		} catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource()==cbTipologia)
	    {
			Vector<String> attribute;
            try {
            	attribute=new Vector<String>();
            	attributeType=new Vector<String>();
            	String tipologia = new String(cbTipologia.getSelectedItem().toString());
            	System.err.println(tipologia);
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
    		    				attributeType.addElement(tempAttrType.get(i));
    		    			}
    		    		}
    		    		inserito=false;
    				}
    			}
            	System.err.println(attribute.toString());
            	//populate combo box
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
		if (event.getSource()==cbAttributo)
	    {
			Vector<String> op = new Vector<String>();
            try {
            	String attributo = new String(cbAttributo.getSelectedItem().toString());
            	System.err.println(attributo);
            	System.err.println(cbAttributo.getItemCount());
            	for (int j=0;j<cbAttributo.getItemCount();j++)
    			{//set the operator
            		System.err.println(attributo);
    				if (cbAttributo.getItemAt(j).equals(attributo)){
    					if (attributeType.get(j).toLowerCase().equals("boolean") || attributeType.get(j).toLowerCase().equals("string"))
    					{
    						op.add(new String("="));
    						op.add(new String("!="));
    					}
    					if (attributeType.get(j).toLowerCase().equals("int") || attributeType.get(j).toLowerCase().equals("integer"))
    					{
    						op.add(new String("="));
    						op.add(new String("!="));
    						op.add(new String(">"));
    						op.add(new String("<"));
    					}
    					
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
	}
}
