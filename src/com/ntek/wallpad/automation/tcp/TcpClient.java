package com.ntek.wallpad.automation.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TcpClient implements Runnable {
	private static final int TCP_PORT = 6666;
	private Context context;
	private String ipAddress;
	private String id;
	
	public TcpClient(Context context, String ipAddress, String id) {
		super();
		this.context = context;
		this.ipAddress = ipAddress;
		this.id = id;
	}

	@Override
	public void run() {
		String response = "failed";
		try {
			Socket socket = new Socket();
			InetAddress inetAddress = InetAddress.getByName(ipAddress);
			SocketAddress socketAddr = new InetSocketAddress(inetAddress, TCP_PORT);
			Log.d("TCP", "C : Connecting.");
			socket.connect(socketAddr, 7000);

			Log.d("TCP", "C : Sending - " + id);
			PrintWriter out =
					new PrintWriter(new BufferedWriter(new OutputStreamWriter(
							socket.getOutputStream())), true);
			out.println(id);
			

			socket.setSoTimeout(7000);
			try {
				BufferedReader in =
						new BufferedReader(new InputStreamReader(socket
								.getInputStream()));
				String str = in.readLine();
				Log.d("TCP", "str : " + str);
				JSONObject json = new JSONObject(str);

				response = json.getString("response");
				Log.d("TCP", "C : Received - " + response);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				socket.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.d("TCP", "C : Done.");
		
		final Intent intent =
				new Intent("com.smartbean.dtcontrol.connect.action");
		intent.putExtra("response", response);
		context.sendBroadcast(intent);
	}

}
