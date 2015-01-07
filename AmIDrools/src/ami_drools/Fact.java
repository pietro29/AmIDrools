package ami_drools;

import java.util.Vector;

public class Fact {
	
	private String id;
	
	private String factType;
	
	private Vector <String> attributes;

	private Vector <String> values;
	
	private Vector <String> attributesType;
	
	public Fact (String id, String factType){
		this.id= id;
		this.factType=factType;
		attributes = new Vector <String>();
		values = new Vector <String>();
		attributesType = new Vector <String>();
	}
	public Vector <String> getAttributes () {
		return attributes;
	}
	public Vector <String> getValues () {
		return values;
	}
	public void insertAttributeValue(String attribute, String attributeType, String value){
		attributes.addElement(attribute);
		attributesType.addElement(attributeType);
		values.addElement(value);
	}
	public void updateAttributeValue(String attribute,String value){
		for (int i = 0 ; i<attributes.size(); i++)
		{
			if (attributes.get(i)==attribute)
			{
				values.set(i, value);
			}
		}
	}
	public String printFact(){
		String print = "";
		print += id + " - " + factType + " - " + values.get(0); 
		return print;
	}
}
