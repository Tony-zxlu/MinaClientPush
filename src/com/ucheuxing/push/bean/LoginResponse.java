package com.ucheuxing.push.bean;

import java.io.Serializable;

public class LoginResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String type;
	public String msg;
	public int code;
	public String client_id;
	public String user_id;


	public LoginResponse(String type, String msg, int code, String client_id,
			String user_id) {
		super();
		this.type = type;
		this.msg = msg;
		this.code = code;
		this.client_id = client_id;
		this.user_id = user_id;
	}


	@Override
	public String toString() {
		return "LoginResponse [type=" + type + ", msg=" + msg + ", code="
				+ code + ", client_id=" + client_id + ", user_id=" + user_id
				+ "]";
	}


}
