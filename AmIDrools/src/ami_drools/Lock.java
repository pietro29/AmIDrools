package ami_drools;

import java.util.Date;

public class Lock {

	private String factId;
	private String isId;
	private Date dateLocked;
	private boolean locked;
	
	public Lock(String factId) {
		this.factId=factId;
		this.isId="";
		this.dateLocked=new Date();
		this.locked=false;
	}
	/**
	 * set attribute locked=true and set current datetime to dateLocked
	 */
	public void setLock(String isId){
		locked=true;
		dateLocked = new Date();
		setIsId(isId);
	}
	/**
	 * set attribute locked=false
	 */
	public void unLock(){
		locked=false;
		setIsId("");
	}
	public boolean getLock(){
		return locked;
	}
	/**
	 * 
	 * @return datetime of the last setLock() invocation
	 */
	public Date getDateLocked(){
		return dateLocked;
	}
	/**
	 * set current datetime to dateLocked
	 */
	public void setDateLocked(){
		this.dateLocked=new Date();
	}
	public String getIsId(){
		return isId;
	}
	public void setIsId(String isId){
		this.isId=isId;
	}
}
