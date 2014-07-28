package com.example.player;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

public class PlayerActivity extends FragmentActivity {
	MediaPlayerService mediaPlayerService;
	PlayerFragment playerFragment;


	@Override
	protected void onDestroy() {		
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		playerFragment = (PlayerFragment) getSupportFragmentManager()
				.findFragmentByTag(PlayerFragment.FRAGMENT_TAG);
		if (playerFragment == null) {
			playerFragment = new PlayerFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, playerFragment,
							PlayerFragment.FRAGMENT_TAG).commit();
		}
		

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(
				outState,
				PlayerFragment.FRAGMENT_TAG,
				getSupportFragmentManager().findFragmentByTag(
						PlayerFragment.FRAGMENT_TAG));
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(
						android.R.id.content,
						getSupportFragmentManager()
								.getFragment(savedInstanceState,
										PlayerFragment.FRAGMENT_TAG),
						PlayerFragment.FRAGMENT_TAG).commit();
	}
}
