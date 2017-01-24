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

public class TcpConnectClient implements Runnable {
	private static final String TAG = TcpConnectClient.class.getCanonicalName();
	private Context context;
	private String ipAddress;
	private int port = 5555;

	public TcpConnectClient(Context context, String ipAddress) {
		super();
		this.context = context;
		this.ipAddress = ipAddress;
	}

	public TcpConnectClient(Context context, String ipAddress, int port) {
		super();
		this.context = context;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	@Override
	public void run() {
		String response = "failed";
		try {
			InetAddress serverAddr = InetAddress.getByName(ipAddress);
			SocketAddress socketAddr = new InetSocketAddress(serverAddr, port);

			Socket socket = new Socket();
			Log.d(TAG, "C : Connecting...");
			try {
				socket.connect(socketAddr, 7000);
				PrintWriter out =
						new PrintWriter(new BufferedWriter(new OutputStreamWriter(
								socket.getOutputStream())), true);

				out.println("connect");
				Log.d("TCP", "C : Sent.");
				Log.d("TCP", "C : Done.");

				socket.setSoTimeout(7000);

				BufferedReader in =
						new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String str = in.readLine();
				Log.d("TCP", "str : " + str);
				JSONObject json = new JSONObject(str);
				response = json.getString("connect");

				if (response.equals(""))
					response = "UNNAMED_DOORTALK";
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

		final Intent intent = new Intent("com.smartbean.dtcontrol.TCP_CONNECT_CALLBACK");
		intent.putExtra("connect", response);
		context.sendBroadcast(intent);
	}

}
