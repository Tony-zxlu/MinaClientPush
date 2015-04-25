package com.ucheuxing.push.bean;


/**
 * 付款详情的bean
 * 
 * @author Tony DateTime 2015-4-21 下午3:02:55
 * @version 1.0
 */
public class PayCodeNotify extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public String msg;
	public int code;
	public String uid;
	public int status;// 付款或者扫码成功

	public PayCodeNotify(String type, String msg, int code, String uid, int status) {
		super();
		this.type = type;
		this.msg = msg;
		this.code = code;
		this.uid = uid;
		this.status = status;
	}

	@Override
	public String toString() {
		return "PaymentNotify [type=" + type + ", msg=" + msg + ", code="
				+ code + ", uid=" + uid + ", status=" + status + "]";
	}

}
