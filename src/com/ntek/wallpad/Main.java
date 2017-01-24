/* Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, Doubango Telecom.
*
* Contact: Mamadou Diop <diopmamadou(at)doubango(dot)org>
*	
* This file is part of imsdroid Project (http://code.google.com/p/imsdroid)
*
* imsdroid is free software: you can redistribute it and/or modify it under the terms of 
* the GNU General Public License as published by the Free Software Foundation, either version 3 
* of the License, or (at your option) any later version.
*	
* imsdroid is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
* See the GNU General Public License for more details.
*	
* You should have received a copy of the GNU General Public License along 
* with this program; if not, write to the Free Software Foundation, Inc., 
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package com.ntek.wallpad;

import org.doubango.ngn.events.NgnRegistrationEventTypes;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnSipSession.ConnectionState;
import org.doubango.ngn.utils.NgnPredicate;
import org.doubango.ngn.utils.NgnStringUtils;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.BaseScreen;
import com.ntek.wallpad.Screens.IBaseScreen;
import com.ntek.wallpad.Screens.ScreenAV;
import com.ntek.wallpad.Screens.ScreenAVQueue;
import com.ntek.wallpad.Screens.ScreenChatQueue;
import com.ntek.wallpad.Screens.ScreenFileTransferQueue;
import com.ntek.wallpad.Screens.ScreenHome;
import com.ntek.wallpad.Screens.ScreenSplash;
import com.ntek.wallpad.Screens.ScreenTabMessages;
import com.ntek.wallpad.Screens.BaseScreen.SCREEN_TYPE;
import com.ntek.wallpad.Services.IScreenService;
import com.ntek.wallpad.lockscreen.LockscreenPatternService;

public class Main extends ActivityGroup {
	private static String TAG = Main.class.getCanonicalName();
	
	public static final int ACTION_NONE = 0;
	public static final int ACTION_RESTORE_LAST_STATE = 1;
	public static final int ACTION_SHOW_AVSCREEN = 2;
	public static final int ACTION_SHOW_CONTSHARE_SCREEN = 3;
	public static final int ACTION_SHOW_SMS = 4;
	public static final int ACTION_SHOW_CHAT_SCREEN = 5;
	public static final int ACTION_SHOW_FRAGMENT_PHONE = 6;
	public static final int ACTION_SHOW_FRAGMENT_SETTINGS = 7;
	public static final int ACTION_SHOW_FRAGMENT_AUTOMATION = 8;
	public static final int ACTION_SHOW_FRAGMENT_GALLERY = 9;
	private static final int RC_SPLASH = 0;
	
	public static int action_setting;
	
	private Handler mHanler;
	private Engine mEngine;
	private IScreenService mScreenService;
	
	public Main(){
		super();
		instance();
		// Sets main activity (should be done before starting services)
//		mEngine = (Engine)Engine.getInstance();
//		mEngine.setMainActivity(this);
//    	mScreenService = ((Engine)Engine.getInstance()).getScreenService();
	}
	
	public void instance(){
		try{
			Log.d(TAG, " instance ");
		mEngine = (Engine)Engine.getInstance();
		mEngine.setMainActivity(this);
    	mScreenService = ((Engine)Engine.getInstance()).getScreenService();    	       
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	    }
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        Log.d(TAG, " main main main ");
     // TODO: need to uncomment to run auto update of apk
        AutoUpdateApk  autoUpdater = new AutoUpdateApk(getApplicationContext());
      
       // TODO: this is for testing only, remove for official APK
       //autoUpdater.checkUpdatesManually();
        
       
        try{
        mHanler = new Handler();
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        
        if(!Engine.getInstance().isStarted()){
        	Engine.getInstance().start();
//        	mScreenService.show(FragmentPhone.class);
//        	return;
        }
        
        final boolean trying = (mEngine.getSipService().getRegistrationState() == ConnectionState.CONNECTING || mEngine.getSipService().getRegistrationState() == ConnectionState.TERMINATING);
        if(mEngine.getSipService().isRegistered()){
			mEngine.showAppNotif(trying ?R.drawable.bullet_ball_glass_grey_16 : R.drawable.bullet_ball_glass_green_16, null);
			WallPad.acquirePowerLock();
		}
		else{
			mEngine.showAppNotif(trying ?R.drawable.bullet_ball_glass_grey_16 : R.drawable.bullet_ball_glass_red_16, null);
			WallPad.releasePowerLock();
		}
        
        Bundle bundle = savedInstanceState;
        if(bundle == null){
	        Intent intent = getIntent();
	        bundle = intent == null ? null : intent.getExtras();
        }
        if(bundle != null && bundle.getInt("action", Main.ACTION_NONE) != Main.ACTION_NONE)
        {
        	handleAction(bundle);
        	action_setting = bundle.getInt("action", Main.ACTION_NONE);
        }
        else if(mScreenService != null)
        {
        	Log.d(TAG, "111111111111111111 FragmentPhone");
        	mScreenService.show(FragmentPhone.class);
        }
        
        }
        catch (Exception e) 
        {
	        e.printStackTrace();
	        System.exit(0);
	    }
        startService(new Intent(this, LockscreenPatternService.class));
    }
    
    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		Bundle bundle = intent.getExtras();
		if(bundle != null){
			handleAction(bundle);
		}
	}
	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(mScreenService.getCurrentScreen().hasMenu()){
			return mScreenService.getCurrentScreen().createOptionsMenu(menu);
		}
		
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		if(mScreenService.getCurrentScreen().hasMenu()){
			menu.clear();
			return mScreenService.getCurrentScreen().createOptionsMenu(menu);
		}
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		IBaseScreen baseScreen = mScreenService.getCurrentScreen();
		if(baseScreen instanceof Activity){
			return ((Activity)baseScreen).onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(mScreenService == null){
			super.onSaveInstanceState(outState);
			return;
		}
		
		IBaseScreen screen = mScreenService.getCurrentScreen();
		if(screen != null){
			outState.putInt("action", Main.ACTION_RESTORE_LAST_STATE);
			outState.putString("screen-id", screen.getId());
			outState.putString("screen-type", screen.getType().toString());
		}
		
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		this.handleAction(savedInstanceState);
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.d(TAG, "onActivityResult("+requestCode+","+resultCode+")");
		if(resultCode == RESULT_OK){
			if(requestCode == Main.RC_SPLASH){
				Log.d(TAG, "Result from splash screen");
			}
		}
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(!BaseScreen.processKeyDown(keyCode, event)){
    		return super.onKeyDown(keyCode, event);
    	}
    	return true;
	}
    
    public void exit(){
    	mHanler.post(new Runnable() {
			public void run() {
				if (!Engine.getInstance().stop()) {
					Log.e(TAG, "Failed to stop engine");
				}				
				finish();
			}
		});
	}
    
    private void handleAction(Bundle bundle){
    	Log.d(TAG, " handle action ");
		final String id;
		switch(bundle.getInt("action", Main.ACTION_NONE)){
			// Default or ACTION_RESTORE_LAST_STATE
			default:
			case ACTION_RESTORE_LAST_STATE:
				id = bundle.getString("screen-id");
				final String screenTypeStr = bundle.getString("screen-type");
				final SCREEN_TYPE screenType = NgnStringUtils.isNullOrEmpty(screenTypeStr) ? SCREEN_TYPE.PHONE_FRAG_T :
						SCREEN_TYPE.valueOf(screenTypeStr);
				switch(screenType){
					case AV_T:
						mScreenService.show(ScreenAV.class, id);
						break;
					default:
						if(!mScreenService.show(id)){
							Log.d(TAG, "22222222222222 FragmentPhone");
							mScreenService.show(FragmentPhone.class);
						}
						break;
				}
				break;
				
			// Notify for new SMSs
			case ACTION_SHOW_SMS:
                mScreenService.show(ScreenTabMessages.class);
                break;
               
			// Show Audio/Video Calls
			case ACTION_SHOW_AVSCREEN:
				Log.d(TAG, "Main.ACTION_SHOW_AVSCREEN");
				
				final int activeSessionsCount = NgnAVSession.getSize(new NgnPredicate<NgnAVSession>() {
					@Override
					public boolean apply(NgnAVSession session) {
						return session != null && session.isActive();
					}
				});
				if(activeSessionsCount > 1){
					mScreenService.show(ScreenAVQueue.class);
				}
				else{
					NgnAVSession avSession = NgnAVSession.getSession(new NgnPredicate<NgnAVSession>() {
						@Override
						public boolean apply(NgnAVSession session) {
							return session != null && session.isActive() && !session.isLocalHeld() && !session.isRemoteHeld();
						}
					});
					if(avSession == null){
						avSession = NgnAVSession.getSession(new NgnPredicate<NgnAVSession>() {
							@Override
							public boolean apply(NgnAVSession session) {
								return session != null && session.isActive();
							}
						});
					}
					if(avSession != null){
						if(!mScreenService.show(ScreenAV.class, Long.toString(avSession.getId()))){
							Log.d(TAG, "333333333333 FragmentPhone");
							mScreenService.show(FragmentPhone.class);
						}
					}
					else{
						Log.e(TAG,"Failed to find associated audio/video session");
						mScreenService.show(FragmentPhone.class);
						mEngine.refreshAVCallNotif(R.drawable.phone_call_25);
					}
				}
				break;
				
			// Show Content Share Queue
			case ACTION_SHOW_CONTSHARE_SCREEN:
				mScreenService.show(ScreenFileTransferQueue.class);
				break;
				
			// Show Chat Queue
			case ACTION_SHOW_CHAT_SCREEN:
				mScreenService.show(ScreenChatQueue.class);
				break;
				
			// Show Phone Function
			case ACTION_SHOW_FRAGMENT_PHONE:
				Log.d(TAG, "ACTION_SHOW_FRAGMENT_PHONE");
                mScreenService.show(FragmentPhone.class);
                break;
                
            // Show Setting
			case ACTION_SHOW_FRAGMENT_SETTINGS:
				Log.d(TAG, "ACTION_SHOW_FRAGMENT_SETTINGS");
                mScreenService.show(FragmentBaseScreenSettings.class);
                break;  
            
            // Show Automation    
			case ACTION_SHOW_FRAGMENT_AUTOMATION:
                mScreenService.show(AutomationDoorlock.class);
                break;  
            
            // Show Gallery
			case ACTION_SHOW_FRAGMENT_GALLERY:
                mScreenService.show(FragmentGallery.class);
                break;  
		}
	}
}