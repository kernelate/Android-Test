package com.ntek.dpinterface;


public class dpjni 
{
	public native void led1control (boolean onOff);
	public native void led2control (boolean onOff);
	public native void speakerMode();
	public native void handsetMode();

	static
	{
//		System.load(String.format("data/data/com.ntek.wallpad/lib/a.so"));
		System.loadLibrary("dp_interface");
	}
	
}
