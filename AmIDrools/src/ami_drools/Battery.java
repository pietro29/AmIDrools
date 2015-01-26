package ami_drools;

public class Battery {
	
	private String id;
	private int level;
	private boolean _privateVisibility=true;
	
	public Battery(String id, int level) {
		this.id=id;
		this.level=level;
	}
	
	public int getLevel()
	{
		return this.level;
	}
	
	public void setLevel(int level)
	{
		this.level=level;
	}
	
	public boolean getPublicVisibility()
	{
		return this._privateVisibility;
	}
	
	public void setId(String id)
	{
		this.id=id;
	}
	
	public String getId()
	{
		return this.id;
	}
	@Override
	public String toString()
	{
		return "Battery, Id: " + id + "; Livello: " + level + "%"; 
	}
	public void updateField(String field, String value){
   	 	System.err.println(field);
		switch (field){
   	 		case "level" : setLevel(Integer.parseInt(value));
   	 				break;
   	 /*case "spenta" : if (value=="true")
				setSpenta(true);
			else
				setSpenta(false);
			break;*/
   	 }
    }

}
