package ami_drools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Clock {

	private String id;
	private Date dateTime;
	private boolean _privateVisibility=true;
	
	public Clock(String id, Date dateTime) {
		this.dateTime=dateTime;
		this.id=id;
	}
	
	public Clock(String id) {
		this.dateTime=new Date();
		this.id=id;
	}
	
	public Date getDate()
	{
		return this.dateTime;
	}
	public Date getDateUpdated()
	{
		return new Date();
	}
	
	public void setDate(Date dateTime)
	{
		this.dateTime=dateTime;
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return "Clock, Id: " + id + "; Date and Time: " + sdf.format(dateTime); 
	}
	public void updateField(String field, String value){
   	 	System.err.println(field);
		switch (field){
   	 		case "datTime" :DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ITALIAN);
							Date date;
							try {
								date = format.parse(value);
								setDate(date);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;

   	 /*case "spenta" : if (value=="true")
				setSpenta(true);
			else
				setSpenta(false);
			break;*/
   	 }
    }
}
