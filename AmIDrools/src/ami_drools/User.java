package ami_drools;
/**
 * This class binds a user and its priority
 *
 */
public class User {

	private String id;
	private String name;
	private int priority;
	
	public User(String id, String name, int priority){
		this.id=id;
		this.name=name;
		this.priority=priority;
	}
	/**
	 * 
	 * @return attribute ID
	 */
	public String getId(){
		return id;
	}
	/**
	 * 
	 * @return name of the user
	 */
	public String getName(){
		return name;
	}
	/**
	 * 
	 * @return the priority of the user
	 */
	public int getPriority(){
		return priority;
	}
	/**
	 * 
	 * @param id
	 */
	public void setId(String id){
		this.id=id;
	}
	/**
	 * 
	 * @param name
	 */
	public void setName(String name){
		this.name=name;
	}
	/**
	 * 
	 * @param priority
	 */
	public void setPriority(int priority){
		this.priority=priority;
	}
}
