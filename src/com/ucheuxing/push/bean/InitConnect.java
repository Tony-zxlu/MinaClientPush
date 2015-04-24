package com.ucheuxing.push.bean;


public class InitConnect extends BaseBean {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String msg;
	public int code;
	public String client_id;

	@Override
	public String toString() {
		return "NotifyConnect [type=" + type + ", msg=" + msg + ", code="
				+ code + ", client_id=" + client_id + "]";
	}

}
