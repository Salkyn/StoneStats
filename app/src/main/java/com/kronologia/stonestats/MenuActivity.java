/*
Activité de liens vers les autres activités, rien de particulier ici

 */

package com.kronologia.stonestats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MenuActivity extends Activity {

	private Button newgame_button;
	private Button decks_button;
	private Button stats_button;
	private Button test_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		AdView adView = (AdView) this.findViewById(R.id.adMob);

		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for emulators
						//test mode on DEVICE (this example code must be replaced with your device uniquq ID)
				.addTestDevice("8DA713B1452BDF2B7391493632F9D827") // S4 Mini
				.build();
		adView.loadAd(adRequest);

		newgame_button = (Button) findViewById(R.id.newgame_button);
		decks_button = (Button) findViewById(R.id.deck_button);
		stats_button = (Button) findViewById(R.id.stats_button);
		test_button = (Button) findViewById(R.id.test_button);

		newgame_button.setOnClickListener(ng_listener);
		decks_button.setOnClickListener(deck_listener);
		stats_button.setOnClickListener(stats_listener);
		test_button.setOnClickListener(test_listener);
	}

    @Override
    public void onBackPressed() {
    }

	private OnClickListener ng_listener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(MenuActivity.this, MainActivity.class);
			startActivity(intent);
		}
	};

	private OnClickListener deck_listener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(MenuActivity.this, ListDecksActivity.class);
			startActivity(intent);
		}
	};

	private OnClickListener stats_listener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(MenuActivity.this, StatsActivity.class);
			startActivity(intent);
		}
	};

	private OnClickListener test_listener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(MenuActivity.this, ParametersActivity.class);
			startActivity(intent);
		}
	};
}
