package ami_drools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Fact implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String factType;
	
	private Vector <String> attributes;

	private Vector <String> values;
	
	private Vector <String> attributesType;
	
	private List modified;
	
	public Fact (String id, String factType){
		this.id= id;
		this.factType=factType;
		attributes = new Vector <String>();
		values = new Vector <String>();
		attributesType = new Vector <String>();
		modified = new ArrayList<String>();
	}
	/**
	 * Get attributes array
	 * @return attributes array
	 */
	public Vector <String> getAttributes () {
		return attributes;
	}
	/**
	 * Get values array
	 * @return values array
	 */
	public Vector <String> getValues () {
		return values;
	}
	/**
	 * 
	 * @return id of a Fact
	 */
	public String getId(){
		return id;
	}
	/**
	 * 
	 * @return Fact type
	 */
	public String getFactType(){
		return factType;
	}
	/**
	 *Get attributes type array
	 * @return attributes type array
	 */
	public Vector <String> getAttributesType(){
		return attributesType;
	}
	/**
	 * 
	 * @return List of modified attributes of the fact
	 */
	public List getModified(){
		return modified;
	}
	/**
	 * Insert a new modified attribute in the list
	 * @param modifiedAttr
	 */
	public void insertModifiedAttribute(String modifiedAttr){
		modified.add(modifiedAttr);
	}
	public void insertModifiedAttribute(Vector<String> modifiedAttr){
		for (String str:modifiedAttr){
			modified.add(modifiedAttr);
		}
	}
	/**
	 * Remove all elements of the list
	 */
	public void removeAllModifiedAttributed(){
		modified.clear();
	}
	/**
	 * Insert a new element in the attribute array.
	 * @param attribute
	 * @param attributeType
	 * @param value
	 */
	public void insertAttributeValue(String attribute, String attributeType, String value){
		attributes.addElement(attribute);
		attributesType.addElement(attributeType);
		values.addElement(value);
	}
	/**
	 * Update the value of the attribute passed as parameter with a new value
	 * @param attribute
	 * @param value
	 */
	public void updateAttributeValue(String attribute,String value){
		for (int i = 0 ; i<attributes.size(); i++)
		{
			if (attributes.get(i).toString().equals(attribute))
			{
				values.set(i, value);
			}
		}
	}
	public String printFact(){
		String print = "";
		try {
			print += id + " - " + factType + " - " + values.get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return print;
	}
}
