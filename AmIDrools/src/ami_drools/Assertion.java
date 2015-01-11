package ami_drools;

import java.util.Date;

public class Assertion {

	private User user;
	private Fact fact;
	private String attribute;
	private Date date;
	
	public Assertion(User user, Fact fact, String attribute){
		this.user=user;
		this.fact=fact;
		this.attribute=attribute;
		this.date = new Date();
	}
	/**
	 * 
	 * @return asserting user
	 */
	public User getUser(){
		return user;
	}
	/**
	 * 
	 * @return return owner fact of the locked attribute
	 */
	public Fact getFact(){
		return fact;
	}
	/**
	 * 
	 * @return attribute locked by the assertion
	 */
	public String getAttribute(){
		return attribute;
	}
	
}
