package ami_drools;

public class Position {
	
	private String id;
	private int location;
	private boolean _privateVisibility=true;
	private String codice;
	
	public Position(String id, int location, String codice) {
		this.id=id;
		this.location=location;
		this.codice=codice;
	}
	
	public int getLocation()
	{
		return this.location;
	}
	
	public void setLocation(int location)
	{
		this.location=location;
	}
	
	public String getCodice()
	{
		return this.codice;
	}
	
	public void setCodice(String codice)
	{
		this.codice=codice;
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

}
