package ami_drools;

public class NotRegisteredException extends Exception
{
    /**
     * Version ID used by deserialization in J2SE >= 1.5.0.
     */
    private static final long serialVersionUID = 1;
    
    /**
     *  @see Exception#Exception()
     */
    public NotRegisteredException()
    {
        super();
    }

    /**
     * @see Exception#Exception(java.lang.String)
     */
    public NotRegisteredException( String message )
    {
        super( message );
    }
}