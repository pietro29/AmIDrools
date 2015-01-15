package ami_drools;

import java.util.Date;

public class Lock {

	private String factId;
	private Date dateLocked;
	private boolean locked;
	
	public Lock(String factId) {
		this.factId=factId;
		this.dateLocked=new Date();
		this.locked=false;
	}
	/**
	 * set attribute locked=true and set current datetime to dateLocked
	 */
	public void setLock(){
		locked=true;
		dateLocked = new Date();
	}
	/**
	 * set attribute locked=false
	 */
	public void unLock(){
		locked=false;
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
}
