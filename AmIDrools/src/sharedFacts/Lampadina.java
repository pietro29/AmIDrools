package sharedFacts;

public class Lampadina implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String codice;
    private Boolean accesa;
    private Boolean spenta;
    
    public Lampadina(String codice, Boolean accesa,Boolean spenta){
    	this.codice=codice;
    	this.accesa=accesa;
    	this.spenta=spenta;
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

}
