package com.ucheuxing.push.bean;

import java.io.Serializable;

public class LoginRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String type;
	public String sign;
	public String ct;
	public String v;
	public String client_id;
	public String uid;

	public LoginRequest(String type, String sign, String client_type,
			String version, String client_id, String uid) {
		super();
		this.type = type;
		this.sign = sign;
		this.ct = client_type;
		this.v = version;
		this.client_id = client_id;
		this.uid = uid;
	}

	@Override
	public String toString() {
		return "LoginRequest [type=" + type + ", sign=" + sign
				+ ", client_type=" + ct + ", version=" + v
				+ ", client_id=" + client_id + ", user_id=" + uid + "]";
	}

}
