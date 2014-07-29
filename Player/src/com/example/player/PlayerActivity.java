package com.example.player;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

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
					.replace(android.R.id.content, playerFragment,
							PlayerFragment.FRAGMENT_TAG).commit();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
			playerFragment.changeVolume(audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC));
			return true;

		case KeyEvent.KEYCODE_VOLUME_DOWN:
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);

			playerFragment.changeVolume(audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC));
			return true;
		default:
			return super.onKeyDown(keyCode, event);

		}

	}

}
