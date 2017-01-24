package com.ntek.rockchip.jni;

public class RKJNI {
	
	public native void SEledlight (int onOff);
	public native void SEledlock (int onOff);
	public native void SEledunlock (int onOff);
	public native void SEledonline (int onOff);
	public native void SEledflash1 (int onOff);
	public native void SEledflash2 (int onOff);
	public native void SEledflashboth (int onOff);
	public native void DPledspeaker (int onOff);
	public native void DPled1(int onOff);
	public native void DPled2(int onOff);
	public native void DT2led(String pattern);
	
	public native void DPspeakerMode();
	public native void DPhandsetMode();
	public native void DPled1blink();
	public native void DPled2blink();
	
	public native void relay0 (int onOff);
	public native void relay1 (int onOff);
	
	public native void device(String name);
	
	static {
		System.loadLibrary("RKJNI");
	 }
}
