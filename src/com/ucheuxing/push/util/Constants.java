package com.ucheuxing.push.util;

public class Constants {

	public static final String SALT_FIGURE = "ucheu13@xing";

	public static final String NOTIFICATION_ICON = "notification_icon";
	public static final String SOCKET_HOST_NAME = "socket_host_name";
	public static final String SOCKET_PORT = "socket_port";

	public static final int HEART_BEAT_INTERVAL = 10;// unit : second
	public static final int KEEP_ALIVE_RESPONSE_TIMEOUT = 5;
	 /**
     * 心跳包 ping message
     */
    public static final String PING_MESSAGE="{'type':'ping'}";

}
