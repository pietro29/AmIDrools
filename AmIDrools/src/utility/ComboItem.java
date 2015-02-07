package utility;
//use only for the creation of the rules
public class ComboItem implements Comparable<ComboItem> 
{
    private int key;
    private String value;

    public ComboItem(int key, String value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return value;
    }

    public int getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
    
    @Override
    public int compareTo(ComboItem other){
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than 
        // other and 0 if they are supposed to be equal
        int last = this.value.compareTo(other.value);
        return last == 0 ? this.value.compareTo(other.value) : last;
    }
}