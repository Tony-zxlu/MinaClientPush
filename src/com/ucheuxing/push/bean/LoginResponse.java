package com.ucheuxing.push.bean;


public class LoginResponse extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String msg;
	public int code;
	public String client_id;
	public String uid;


	public LoginResponse(String type, String msg, int code, String client_id,
			String uid) {
		super();
		this.type = type;
		this.msg = msg;
		this.code = code;
		this.client_id = client_id;
		this.uid = uid;
	}


	@Override
	public String toString() {
		return "LoginResponse [type=" + type + ", msg=" + msg + ", code="
				+ code + ", client_id=" + client_id + ", uid=" + uid
				+ "]";
	}


}
