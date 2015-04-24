package com.ucheuxing.push.bean;

import java.io.Serializable;

public abstract class BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * code ok
	 */
	public static final int CODE_OK = 0;
	/**
	 * 支付ok
	 */
	public static final int PAY_OK = 1;
	public String type;
}
