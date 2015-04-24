package com.ucheuxing.push.bean;

import java.io.Serializable;

public class InitConnect implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String type;
	public String msg;
	public int code;
	public String client_id;
	
	@Override
	public String toString() {
		return "NotifyConnect [type=" + type + ", msg=" + msg + ", code="
				+ code + ", client_id=" + client_id + "]";
	}
	
	

}
