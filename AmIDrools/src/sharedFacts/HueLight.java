package sharedFacts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class HueLight implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	/**
	 * Hue Device Number
	 */
	private String deviceNumber;
    private Boolean isOn;
    private int brightness = 0; // possible values are 0 - 255
	private int colorTemperature = 154; // possible values are 154 - 500
    
    public HueLight(String id, String deviceNumber, Boolean isOn){
    	this.id=id;
    	this.deviceNumber=deviceNumber;
    	this.isOn=isOn;
    }
    public HueLight(String id, String deviceNumber){
    	this.id=id;
    	this.deviceNumber=deviceNumber;
    }
    /**
     * 
     * @param id get from db
     */
    public HueLight(String id){
    	this.id=id;
    }
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
     public String getDeviceNumber() {
         return this.deviceNumber;
     }

     public void setDeviceNumber(String deviceNumber) {
         this.deviceNumber = deviceNumber;
     }
     
     public Boolean getisOn() {
    	 getStatus();
         return this.isOn;
     }

     public void setBrightness(int brightness){
    	 this.brightness=brightness;
    	 this.brightness = this.brightness < 0 ? 0 : this.brightness;
 		this.brightness = this.brightness > 255 ? 255 : this.brightness;

 		if (this.brightness > 0) {
 			this.isOn = true;
 			executeMessage("{\"bri\":" + this.brightness + ",\"on\":true}");
 		} else {
 			this.isOn = false;
 			executeMessage("{\"on\":false}");
 		}
     }
     public int getBrightness(){
    	 return this.brightness;
     }
     public void setColorTemperature(int colorTemperature){
    	 this.colorTemperature=colorTemperature;
    	 this.colorTemperature = this.colorTemperature < 154 ? 154
 				: this.colorTemperature;
 		this.colorTemperature = this.colorTemperature > 500 ? 500
 				: this.colorTemperature;

 		executeMessage("{\"ct\":" + this.colorTemperature + "}");
     }
     public int getColorTemperature(){
    	 return this.colorTemperature;
     }
     /**
      * set isOn through REST API service
      * @param isOn
      */
     public void setisOn(Boolean isOn) {
         this.isOn = isOn;
         executeMessage("{\"on\":" + String.valueOf(isOn) + "}");
     }
     /**
      * Send Hue Bridge message command
      * @param message Json format
      */
     private void executeMessage(String message) {
    	 try {
    	 URL url = new URL("http://localhost:8000/api/newdeveloper/lights/"+ this.deviceNumber +"/state");
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestMethod("PUT");
         connection.setDoOutput(true);
         connection.setRequestProperty("Content-Type", "application/json");
         connection.setRequestProperty("Accept", "application/json");
         OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
         osw.write(message);
         osw.flush();
         osw.close();
         System.err.println(connection.getResponseCode());
    	 } catch (Exception e) {
 			System.out.println("HUE web service error set");
 		}
     }
     public void getStatus(){
    	 try {
    		
    		 String url = "http://localhost:8000/api/newdeveloper/lights/"+ this.deviceNumber;
    		 
 			URL obj = new URL(url);
 			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 	 
 			// optional default is GET
 			con.setRequestMethod("GET");
 	 
 			//add request header
 			//con.setRequestProperty("User-Agent", USER_AGENT);
 	 
 			int responseCode = con.getResponseCode();
 			System.out.println("\nSending 'GET' request to URL : " + url);
 			System.out.println("Response Code : " + responseCode);
 	 
 			BufferedReader in = new BufferedReader(
 			        new InputStreamReader(con.getInputStream()));
 			String inputLine;
 			StringBuffer response = new StringBuffer();
 	 
 			while ((inputLine = in.readLine()) != null) {
 				response.append(inputLine);
 			}
 			in.close();
 	 
 			JSONParser jsonParser = new JSONParser();
       	 
 			JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());
	 		
 			// handle a structure into the json object
 			JSONObject structure = (JSONObject) jsonObject.get("state");
 			
 			
 			this.isOn = (Boolean) structure.get("on");
 			
 			long brightness = (long) structure.get("bri");
 			
	       	this.brightness=(int) brightness;
	       	
	       	long colorTemperature= (long) structure.get("ct");
	       	
	       	this.colorTemperature=(int) colorTemperature;
	       	
 			//print result
 			//System.out.println(response.toString());
    	 } catch (Exception e) {
			System.out.println("HUE web service error get");
		}
     }
     /**
      * Update attribute value from Fact class vector
      * @param field
      * @param value
      */
     public void updateField(String field, String value){
    	 switch (field){
    	 case "deviceNumber" : setDeviceNumber(value);
    	 				break;
    	 case "isOn" : if (value.equals("true"))
    		 				setisOn(true);
    	 				else
    	 					setisOn(false);
    	 				break;
    	 case "brightness" : setBrightness(Integer.parseInt(value));
					break;
    	 case "colorTemperature" : setColorTemperature(Integer.parseInt(value));
					break;
    	 }
     }
     /**
      * Method to update shared Facts vector
      * @param field name
      * @return updated value
      */
     public String getUpdatedField(String field){
    	 String s="";
    	 switch (field){
    	 case "deviceNumber" : s=getDeviceNumber();
    	 				break;
    	 case "accesa" : if (getisOn())
    		 				s="true";
    	 				else
    	 					s="false";
    	 				break;
    	 case "brightness" : s=String.valueOf(getBrightness());
			break;
    	 case "colorTemperature" : s=String.valueOf(getColorTemperature());
			break;
    	 }
    	 return s;
     }
}
