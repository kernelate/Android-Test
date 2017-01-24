package com.ntek.wallpad.Utils;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ntek.wallpad.R;

public class WifiArrayAdapter extends ArrayAdapter<ScanResult>
{

	private Context mContext;
	private int id;
	private List<ScanResult> items;

	public WifiArrayAdapter(Context context, int textViewResourceId, List<ScanResult> objects)
	{
		super(context, textViewResourceId, objects);
		this.mContext = context;
		id = textViewResourceId;
		items = objects;
	}

	public ScanResult getItem(int i)
	{
		return items.get(i);
	}

	static class ViewHolder
	{
		public ViewHolder(View v)
		{
		}

		TextView SSID;
		ImageView wifiStr;
		TextView capabilities;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		ViewHolder viewHolder;
		if (view == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = layoutInflater.inflate(id, null);
			viewHolder = new ViewHolder(view);
			viewHolder.SSID = (TextView) view.findViewById(R.id.wifiNetworkTextView);
			viewHolder.capabilities = (TextView) view.findViewById(R.id.wifiNetworkCapabilitiesTextView);
			viewHolder.wifiStr = (ImageView) view.findViewById(R.id.wifiLevelImageView);
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) view.getTag();
		}

		final ScanResult scanResult = items.get(position);
		String capabilities = "";
		if (scanResult != null)
		{
			if (viewHolder.SSID != null)
			{
				viewHolder.SSID.setText(scanResult.SSID);
			}

			if (viewHolder.capabilities != null)
			{
				if (scanResult.capabilities.contains("WPA-PSK") && scanResult.capabilities.contains("WPA2-PSK"))
					capabilities += "Secured with WPA/WPA2 ";
				else if (scanResult.capabilities.contains("WPA2-PSK"))
					capabilities += "Secured with WPA2 ";
				else if (scanResult.capabilities.contains("WPA-PSK"))
					capabilities += "Secured with WPA ";
				else if (scanResult.capabilities.contains("EAP"))
					capabilities += "Secured with 802.1x ";
				else if (scanResult.capabilities.contains("WEP"))
					capabilities += "Secured with WEP ";
				else  
					capabilities += "Open ";
				
				if (scanResult.capabilities.contains("WPS"))
					capabilities += "(WPS Available)";
				
				viewHolder.capabilities.setText(capabilities);
			}

			if (viewHolder.wifiStr != null)
			{
				if (capabilities.contains("Secured"))
				{
					if (scanResult.level <= -1 && scanResult.level >= -39)
						viewHolder.wifiStr.setImageResource(R.drawable.ic_wifi_lock_signal_4);
					else if (scanResult.level <= -40 && scanResult.level >= -69)
						viewHolder.wifiStr.setImageResource(R.drawable.ic_wifi_lock_signal_3);
					else if (scanResult.level <= -70 && scanResult.level >= -89)
						viewHolder.wifiStr.setImageResource(R.drawable.ic_wifi_lock_signal_2);
					else if (scanResult.level <= -90 && scanResult.level >= -100)
						viewHolder.wifiStr.setImageResource(R.drawable.ic_wifi_lock_signal_1);
				}
				else
				{
					if (scanResult.level <= -1 && scanResult.level >= -39)
						viewHolder.wifiStr.setImageResource(R.drawable.ic_wifi_signal_4);
					else if (scanResult.level <= -40 && scanResult.level >= -69)
						viewHolder.wifiStr.setImageResource(R.drawable.ic_wifi_signal_3);
					else if (scanResult.level <= -70 && scanResult.level >= -89)
						viewHolder.wifiStr.setImageResource(R.drawable.ic_wifi_signal_2);
					else if (scanResult.level <= -90 && scanResult.level >= -100)
						viewHolder.wifiStr.setImageResource(R.drawable.ic_wifi_signal_1);
				}
			}
		}
		return view;
	}
}
