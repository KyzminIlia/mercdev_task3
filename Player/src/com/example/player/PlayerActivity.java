package com.example.player;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class PlayerActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PlayerFragment playerFragment = (PlayerFragment) getSupportFragmentManager()
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
