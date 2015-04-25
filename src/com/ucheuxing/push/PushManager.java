package com.ucheuxing.push;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import android.text.TextUtils;
import android.util.Log;

import com.ucheuxing.push.PushService.TaskSubmitter;
import com.ucheuxing.push.util.Constants;
import com.ucheuxing.push.util.SharedPreferUtils;

/**
 * 
 * @author Tony DateTime 2015-4-24 上午9:27:14
 * @version 1.0
 */
public class PushManager {

	private static final String TAG = PushManager.class.getSimpleName();

	private PushService pushService;

	private NioSocketConnector connector;
	private ReceiveDataHandler receiveDataHandler;

	private TaskSubmitter submitter;

	public PushManager(PushService pushService) {
		super();
		this.pushService = pushService;
		submitter = pushService.getTaskSubmitter();
		receiveDataHandler = new ReceiveDataHandler(pushService);
	}

	public void connect() {
		if (isActive()){
			Log.d(TAG, " try to connect , but it is active!");
			return;
		} 

		submitter.submit(new Runnable() {

			@Override
			public void run() {
				Log.d(TAG, " bulid socket connector ");
				// 1.建立connect对象
				connector = new NioSocketConnector();
				// 2.为connector设置handler
				connector.setHandler(receiveDataHandler);
				// 3.为connector设置filter
				connector.getFilterChain().addLast("codec",
						new ProtocolCodecFilter(new TextLineCodecFactory()));
				connector.setConnectTimeoutMillis(5000);
				String address = SharedPreferUtils.getString(pushService,
						Constants.SOCKET_HOST_NAME, "");
				int port = SharedPreferUtils.getInt(pushService,
						Constants.SOCKET_PORT, -1);
				Log.d(TAG, " address : " + address.toString() + " port :"
						+ port);
				if (!TextUtils.isEmpty(address) && port != -1) {
					// 4.连接socket
					ConnectFuture future = connector
							.connect(new InetSocketAddress(address, port));
					future.awaitUninterruptibly();
					future.addListener(new IoFutureListener<IoFuture>() {

						@Override
						public void operationComplete(IoFuture arg0) {
							Log.d(TAG, " operationComplete ");
						}
					});
				}
			}
		});
	}

	/**
	 * this connector is active or not
	 * 
	 * @author Tony DateTime 2015-4-25 下午1:04:02
	 * @return
	 */
	public boolean isActive() {
		return connector != null && connector.isActive();
	}

	public boolean isAuthenticated() {
		return isActive() && receiveDataHandler.isAuthenticated;
	}

	public void disConnect() {
		if (connector != null) {
			connector.dispose();
		}
	}
	

}
