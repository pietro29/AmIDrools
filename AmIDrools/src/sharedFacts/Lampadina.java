package sharedFacts;

public class Lampadina implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String codice;
    private Boolean accesa;
    private Boolean spenta;
    
    public Lampadina(String id, String codice, Boolean accesa,Boolean spenta){
    	this.id=id;
    	this.codice=codice;
    	this.accesa=accesa;
    	this.spenta=spenta;
    }
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
     public String getCodice() {
         return this.codice;
     }

     public void setCodice(String codice) {
         this.codice = codice;
     }
     
     public Boolean getAccesa() {
         return this.accesa;
     }

     public void setAccesa(Boolean accesa) {
         this.accesa = accesa;
     }
     
     public Boolean getSpenta() {
         return this.spenta;
     }

     public void setSpenta(Boolean spenta) {
         this.spenta = spenta;
     }
     public void updateField(String field, String value){
    	 switch (field){
    	 case "codice" : setCodice(value);
    	 				break;
    	 case "accesa" : if (value.equals("true"))
    		 				setAccesa(true);
    	 				else
    	 					setAccesa(false);
    	 				break;
    	 case "spenta" : if (value.equals("true"))
				setSpenta(true);
			else
				setSpenta(false);
			break;
    	 }
     }
}
