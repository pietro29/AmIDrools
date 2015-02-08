package utility;

public class ConditionActionItem implements Comparable<ConditionActionItem> {

	private int id_model;
    private String des_model;
    private int id_attribute;
    private String des_attribute;
    private String op;
    private String value;

    public ConditionActionItem(int id_model, String des_model,int id_attribute, String des_attribute,String op, String value)
    {
        this.id_model = id_model;
        this.des_model=des_model;
        this.id_attribute=id_attribute;
        this.des_attribute=des_attribute;
        this.op=op;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return des_model + ", " + des_attribute + " " + op + " " + value;
    }

    public int getId_model()
    {
        return id_model;
    }
    
    public int getId_attribute()
    {
        return id_attribute;
    }
    
    public String getDes_model()
    {
        return des_model;
    }
    
    public String getDes_attribute()
    {
        return des_attribute;
    }

    public String getOp()
    {
        return op;
    }
    
    public String getValue()
    {
        return value;
    }
    
    @Override
    public int compareTo(ConditionActionItem other){
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than 
        // other and 0 if they are supposed to be equal
        int last = this.des_model.compareTo(other.des_model);
        return last == 0 ? this.des_attribute.compareTo(other.des_attribute) : last;
    }

}
