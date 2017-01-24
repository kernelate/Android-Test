package com.ntek.wallpad.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.ntek.wallpad.Utils.RingProgressDialogManager;

public class AsyncClient extends AsyncTask<Void, Void, String> {
	
	private static final String TAG = AsyncClient.class.getCanonicalName(); 
	
	private Context m_context = null;
	private int UDPPORT = 9999;
	
	public AsyncClient(int port, Context context)
	{
		Log.i(TAG, TAG);
		this.m_context = context;
    	if(port > 0) {
    		this.UDPPORT = port;
    	}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Log.i(TAG, "onPreExecute()");
		RingProgressDialogManager.show(m_context, "Please wait...", "Searching for Devices");
	}
	

	@Override
	protected String doInBackground(Void... params) {
		Log.d("UDP", "C: Thread Start!!!");
		String result = "failed";
		try {
			Log.d("UDP", "C: Connecting");
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			
			byte SendBuf[] = new byte[17];
			SendBuf = ("doortalk").getBytes();
			InetAddress Addr = InetAddress.getByName("255.255.255.255");
			DatagramPacket sPacket = new DatagramPacket(SendBuf, SendBuf.length, Addr, UDPPORT);
		
			Log.d("UDP", "C: Sending");
			socket.send(sPacket);
			socket.setSoTimeout(7000);
			
			// Neil 2014-07-31
			/**
			 * Add infinite loop to receive many packets from many server 
			 * allow SocketTimeoutException to end the loop at Socket.receive(rPacket); 
			 */
			try {
				ServerData.getList().clear();
				while(true)
				{
					byte RecBuf[] = new byte[100];
					DatagramPacket rPacket = new DatagramPacket(RecBuf, RecBuf.length);
					
					Log.d("UDP", "C: Receiving");
					socket.receive(rPacket);
					
					InetAddress serverAddr = rPacket.getAddress();
					String serverAddrStr = serverAddr.toString().substring(1);
					String serverDisplayName = new String(rPacket.getData(),rPacket.getOffset(),rPacket.getLength());
					
					Log.d("UDP", "C: Packet received : " + serverDisplayName);	
					
					if ((serverDisplayName.length() > 8 && serverDisplayName.substring(0, 9).equals("IDENTITY_")))
					{
						serverDisplayName = serverDisplayName.split("_")[1];
					}
					
					ServerData.getList().add(new ServerData(serverDisplayName, serverAddrStr));
				}
			} 
			catch (SocketTimeoutException e) { 
				Log.e("UDP","C: Search end here"); 
			}
			
			/** Close socket to prevent leaks **/
			socket.close(); 
			Log.d("UDP", "C: Socket close");
		} catch (IOException e) {
			Log.e("UDP", "C: Error", e);
		}
		
		Log.d("UDP", "C: Thread End!!!");
		
		if (ServerData.getList().size() > 0)
		{
			result = "success";
		}
		return result;
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
		Log.i(TAG, "onCancelled()");
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Log.i(TAG, "onPostExecute(String result)");
		Log.d(TAG, result);
		//you can return the serverdata.getlist instead
		if (result.equals("success") || result.equals("failed"))
		{
			RingProgressDialogManager.hide();
		}
		
		final Intent intent = new Intent(ServerData.UPD_SEARCH_SERVER_END_CALLBACK);
		m_context.sendBroadcast(intent);
	}
	
}
