package com.ntek.wallpad.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import org.doubango.ngn.NgnEngine;

import android.content.Intent;
import android.util.Log;

public class Client implements Runnable {
	//SHCHO
	private int UDPPORT = 9999;
	
	public Client(int port) {
		if(port > 0){
			this.UDPPORT = port;
		}
	}
	
	@Override
	public void run() {
		Log.d("UDP", "C: Thread Start!!!");
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
		
		final Intent intent = new Intent(ServerData.UPD_SEARCH_SERVER_END_CALLBACK);
		NgnEngine.getInstance().getMainActivity().sendBroadcast(intent);
		
		Log.d("UDP", "C: Thread End!!!");
	}
}

