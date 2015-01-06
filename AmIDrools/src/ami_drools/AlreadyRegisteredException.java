package ami_drools;

public class AlreadyRegisteredException extends Exception
{
    /** Version ID used by deserialization in J2SE >= 1.5.0. */
    private static final long serialVersionUID = 1;
    
    /**
     * 
     */
    public AlreadyRegisteredException()
    {
        super();
    }
    /**
     * @param message
     */
    public AlreadyRegisteredException( String message )
    {
        super( message );
    }
}
