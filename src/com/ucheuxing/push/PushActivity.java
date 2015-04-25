package com.ucheuxing.push;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

import android.app.Activity;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.ucheuxing.push.util.Constants;
import com.ucheuxing.push.util.LogUtil;
import com.ucheuxing.push.util.NotifyManager;
import com.ucheuxing.push.util.SharedPreferUtils;
import com.ucheuxing.push.util.ToastUtils;

public class PushActivity extends Activity implements OnClickListener {

	private static final String TAG = PushActivity.class.getSimpleName();
	private Button mConnectBtn, mDisconnectBtn, mSendBtn;
	private EditText mInputMsg, mIP, mPort;

	private String hostname;
	private int port;
	private IoSession session;

	private LogUtil log;

	ServiceManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		log = new LogUtil(TAG);
		mConnectBtn = (Button) findViewById(R.id.btnConnect);
		mDisconnectBtn = (Button) findViewById(R.id.btnDisconnect);
		mSendBtn = (Button) findViewById(R.id.btnSendMsg);
		mInputMsg = (EditText) findViewById(R.id.textView1);
		mIP = (EditText) findViewById(R.id.etIP);
		mPort = (EditText) findViewById(R.id.etPort);
		// //////////////////////////////////
		mConnectBtn.setOnClickListener(this);
		mDisconnectBtn.setOnClickListener(this);
		mSendBtn.setOnClickListener(this);

		manager = new ServiceManager(this);
		manager.setNotificationIcon(R.drawable.ic_launcher);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnConnect:
			hostname = mIP.getText().toString().trim();
			String portStr = mPort.getText().toString().trim();
			if (TextUtils.isEmpty(hostname) || TextUtils.isEmpty(portStr)) {
				ToastUtils.showShort(this, "先配置好IP和端口");
				return;
			}
			port = Integer.parseInt(portStr);
			SharedPreferUtils.setString(this, Constants.SOCKET_HOST_NAME,
					hostname);
			SharedPreferUtils.setInt(this, Constants.SOCKET_PORT, port);
			manager.startService();
			break;
		case R.id.btnDisconnect:
			manager.stopService();
			break;
		case R.id.btnSendMsg:
			WriteFuture future = session.write(mInputMsg.getText().toString()
					.trim()
					+ "\n");
			future.addListener(new IoFutureListener<IoFuture>() {

				@Override
				public void operationComplete(IoFuture ioFuture) {
				}
			});
			break;
		default:
			break;
		}
	}

	// private void connectServer() {
	// // TODO Auto-generated method stub
	//
	// // 建立connect对象
	// NioSocketConnector connector = new NioSocketConnector();
	// // 为connector设置handler
	// connector.setHandler(new ReceiveDataHandler(PushActivity.this));
	//
	// connector.getFilterChain().addLast("codec",
	// new ProtocolCodecFilter(new TextLineCodecFactory()));
	//
	// hostname = mIP.getText().toString().trim();
	// String portStr = mPort.getText().toString().trim();
	// if (TextUtils.isEmpty(hostname) || TextUtils.isEmpty(portStr)) {
	// Toast.makeText(getApplicationContext(), "先配置好IP和端口", 0).show();
	// return;
	// }
	// port = Integer.parseInt(portStr);
	// ConnectFuture future = connector.connect(new InetSocketAddress(
	// hostname, port));
	// future.awaitUninterruptibly();
	// session = future.getSession();
	// // 设置心跳
	// // Timer timer = new Timer();
	// // timer.schedule(new TimerTask() {
	// //
	// // @Override
	// // public void run() {
	// // session.write("heart beat");
	// // }
	// // }, 0, 1000 * 5);
	// }


	public void testNotification(View view) {
		Uri uri = RingtoneManager.getActualDefaultRingtoneUri(this,
				RingtoneManager.TYPE_NOTIFICATION);
		RingtoneManager.getRingtone(this, uri).play();
	}
}
