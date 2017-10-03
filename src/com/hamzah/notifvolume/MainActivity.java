package com.hamzah.notifvolume;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null)
			getFragmentManager().beginTransaction()
					.add(android.R.id.content, new PrefFragment()).commit();
	}

	public static class PrefFragment extends PreferenceFragment{
		@SuppressWarnings("deprecation")
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			this.getPreferenceManager().setSharedPreferencesName("pref");
			this.getPreferenceManager().setSharedPreferencesMode(
					Context.MODE_WORLD_READABLE);
			addPreferencesFromResource(R.xml.pref);
		}
	}
	
}