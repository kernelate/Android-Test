package com.ntek.wallpad.automation.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TcpLoginClient implements Runnable {
	private static final String TAG = TcpLoginClient.class.getCanonicalName();
	private Context context;
	private String ipAddress;
	private String loginID;
	private String loginPassword;
	
	public TcpLoginClient(Context context, String ipAddress, String loginID, String loginPassword) {
		super();
		this.context = context;
		this.ipAddress = ipAddress;
		this.loginID = loginID;
		this.loginPassword = loginPassword;
	}
	
	@Override
	public void run() {
		String response = "failed";
		try {
			InetAddress serverAddr = InetAddress.getByName(ipAddress);
			SocketAddress socketAddr =
					new InetSocketAddress(serverAddr,
							5555);

			Socket socket = new Socket();
			Log.d(TAG, "C : Connecting...");
			try {
				socket.connect(socketAddr, 7000);
				JSONObject message = new JSONObject();

				message.put("type", "LOGIN");
				message.put("loginID", loginID);
				message.put("loginPassword", loginPassword);

				Log.d(TAG, "C : Sending : '" + message + "'");
				PrintWriter out =
						new PrintWriter(new BufferedWriter(
								new OutputStreamWriter(socket
										.getOutputStream())), true);

				out.println(message);
				Log.d(TAG, "C : Sent.");
				Log.d(TAG, "C : Done.");

				socket.setSoTimeout(7000);

				BufferedReader in =
						new BufferedReader(new InputStreamReader(socket
								.getInputStream()));
				String str = in.readLine();
				Log.d(TAG, "str : " + str);

				JSONObject json = new JSONObject(str);
				response = json.getString("login");


			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			finally {
				socket.close();
			}
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		final Intent intent =
				new Intent(
						"com.smartbean.dtcontrol.TCP_LOGIN_CALLBACK");
		intent.putExtra("login", response);
		context.sendBroadcast(intent);
	}
}
