package com.hamzah.notifvolume;

import android.content.Context;
import android.media.AudioManager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class Main implements IXposedHookInitPackageResources {
	
	public static final int ID_RINGER = 1;
	public static final int ID_MEDIA = 2;
	public static final int ID_ALARM = 3;
	
	XSharedPreferences pref;
	
	
	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam)
			throws Throwable {
		if(!resparam.packageName.equals("com.android.systemui"))
			return;
		
		resparam.res.hookLayout("com.android.systemui", "layout", "status_bar_expanded", new XC_LayoutInflated() {
	        @Override
	        public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
	        	pref = new XSharedPreferences("com.hamzah.notifvolume", "pref");
	        	int width = Integer.parseInt(pref.getString("seekbar_width", "400"));
	        	int padding = Integer.parseInt(pref.getString("padding", "100"));
	        	
	        	ViewGroup panel = (ViewGroup) liparam.view.findViewById(
	                    liparam.res.getIdentifier("notification_panel", "id", "com.android.systemui"));
	        	Context c = panel.getContext();
	        	
	        	TableLayout layout = new TableLayout(c);
	        	
	        	//seek bars
	        	SeekBar ringer = new SeekBar(c);
	        	SeekBar media = new SeekBar(c);
	        	SeekBar alarm = new SeekBar(c);
	        	//labels
	        	TextView ringerLabel = new TextView(c);
	        	ringerLabel.setText("Ringer");

	        	TextView mediaLabel = new TextView(c);
	        	mediaLabel.setText("Media");
	        	
	        	TextView alarmLabel = new TextView(c);
	        	alarmLabel.setText("Alarm");
	        	
	        	//rows
	        	TableRow ringerRow = new TableRow(c);
	        	ringerRow.addView(ringerLabel);
	        	ringerRow.addView(ringer);
	        	
	        	TableRow mediaRow = new TableRow(c);
	        	mediaRow.addView(mediaLabel);
	        	mediaRow.addView(media);
	        	
	        	TableRow alarmRow = new TableRow(c);
	        	alarmRow.addView(alarmLabel);
	        	alarmRow.addView(alarm);
	        	
	        	layout.addView(ringerRow);
	        	layout.addView(mediaRow);
	        	layout.addView(alarmRow);	        	
	        	
	        	android.widget.TableRow.LayoutParams params = new android.widget.TableRow.LayoutParams(width, LayoutParams.WRAP_CONTENT);
	    		ringer.setLayoutParams(params);
	    		
	    		ringer.setProgress(ringer.getMax());
	        	
	        	//ringer.setBackgroundColor(Color.BLUE);
	        	layout.setGravity(Gravity.BOTTOM);
	        	layout.setPadding(5, 0, 5, padding);
	        	panel.addView(layout);
	        	
	        	AudioManager am = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
	        	
	        	int ringerMax = am.getStreamMaxVolume(AudioManager.STREAM_RING);
	        	int mediaMax = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	        	int alarmMax = am.getStreamMaxVolume(AudioManager.STREAM_ALARM);
	        	
	        	ringer.setMax(ringerMax);
	        	media.setMax(mediaMax);
	        	alarm.setMax(alarmMax);
	        	
	        	ringer.setProgress(am.getStreamVolume(AudioManager.STREAM_RING));
	        	media.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
	        	alarm.setProgress(am.getStreamVolume(AudioManager.STREAM_ALARM));
	        	
				ringer.setOnSeekBarChangeListener(new sbListener(ID_RINGER, am));
				media.setOnSeekBarChangeListener(new sbListener(ID_MEDIA, am));
				alarm.setOnSeekBarChangeListener(new sbListener(ID_ALARM, am));
	        }
		});
	}
	
	private class sbListener implements OnSeekBarChangeListener{
		
		private int id;
		private AudioManager am;
		
		public sbListener(int id, AudioManager am){
			this.id = id;
			this.am = am;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			XposedBridge.log("" + progress + " " + id);
			if(id == ID_RINGER)
				am.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
			if(id == ID_MEDIA)
				am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
			if(id == ID_ALARM)
				am.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}
		
	}

}