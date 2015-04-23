package com.ucheuxing.push;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ucheuxing.push.util.LogUtil;
import com.ucheuxing.push.util.NetUtils;

public class PushActivity extends Activity implements OnClickListener {

	private static final String TAG = PushActivity.class.getSimpleName();
	private Button mConnectBtn, mDisconnectBtn, mSendBtn, mJsonBtn;
	private EditText mInputMsg, mIP, mPort;

	private String hostname;
	private int port;
	private IoSession session;

	private LogUtil log;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		log = new LogUtil(TAG);
		mConnectBtn = (Button) findViewById(R.id.btnConnect);
		mDisconnectBtn = (Button) findViewById(R.id.btnDisconnect);
		mSendBtn = (Button) findViewById(R.id.btnSendMsg);
		mJsonBtn = (Button) findViewById(R.id.btnJson);
		mInputMsg = (EditText) findViewById(R.id.textView1);
		mIP = (EditText) findViewById(R.id.etIP);
		mPort = (EditText) findViewById(R.id.etPort);
		// //////////////////////////////////
		mConnectBtn.setOnClickListener(this);
		mDisconnectBtn.setOnClickListener(this);
		mSendBtn.setOnClickListener(this);
		mJsonBtn.setOnClickListener(this);
		
		String testSign = NetUtils.testSign();
		Log.d("sign", testSign);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnConnect:
			new Thread(new Runnable() {

				@Override
				public void run() {
					connectServer();
				}
			}).start();
			break;
		case R.id.btnDisconnect:
			session.close(true);
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
		case R.id.btnJson:
			testJson();
			break;

		default:
			break;
		}
	}

	private void connectServer() {
		// TODO Auto-generated method stub
		NioSocketConnector connector = new NioSocketConnector();
		connector.setHandler(new MinaClientHandler(PushActivity.this));
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new TextLineCodecFactory()));

		hostname = mIP.getText().toString().trim();
		String portStr = mPort.getText().toString().trim();
		if (TextUtils.isEmpty(hostname) || TextUtils.isEmpty(portStr)) {
			Toast.makeText(getApplicationContext(), "先配置好IP和端口", 0).show();
			return;
		}
		port = Integer.parseInt(portStr);
		ConnectFuture future = connector.connect(new InetSocketAddress(
				hostname, port));
		future.awaitUninterruptibly();
		session = future.getSession();
		// 设置心跳
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				session.write("heart beat");
			}
		}, 0, 1000 * 5);
	}

	public void testJson() {
		String json = "{'name':'zxlu','val':100,'status':true,'f':1.0}";
		try {
			JSONObject jsonObject = new JSONObject(json);
			String name = jsonObject.getString("name");
			int val = jsonObject.getInt("val");
			boolean status = jsonObject.getBoolean("status");
			double f = jsonObject.getDouble("f");
			log.d("json", " name :　" + name + " status :　" + status + " val :　"
					+ val + " f :　" + f);
		} catch (JSONException e) {
			e.printStackTrace();
			log.d("json", e.toString());
		}
	}
}
