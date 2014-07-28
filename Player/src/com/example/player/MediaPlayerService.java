package com.example.player;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class MediaPlayerService extends Service implements OnCompletionListener {
	MediaPlayer player;
	float volume;
	PlayerBinder mediaPlayerBinder;
	BroadcastReceiver playerReciever;
	IntentFilter playerIntentFilter;
	public final String ACTION_PLAYER_CHANGE = "com.example.player.PLAYER_CHANGE";
	public final String EXTRA_PLAY = "com.example.player.PLAY";
	public final String EXTRA_PAUSE = "com.example.player.PAUSE";
	public final String EXTRA_VOLUME_CHANGE = "com.example.VOLUME_CHANGE";
	public final String EXTRA_VOLUME = "com.example.VOLUME";

	@Override
	public void onCreate() {
		super.onCreate();
		player = new MediaPlayer();
		player = MediaPlayer.create(this, R.raw.gorillaz);
		player.setOnCompletionListener(this);
		playerIntentFilter = new IntentFilter(ACTION_PLAYER_CHANGE);

		playerReciever = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getBooleanExtra(EXTRA_PLAY, false)) {
					player.start();
				}
				if (intent.getBooleanExtra(EXTRA_PAUSE, false)) {
					player.pause();
				}
				if (intent.getBooleanExtra(EXTRA_VOLUME_CHANGE, false)) {
					volume = intent.getIntExtra(EXTRA_VOLUME, 0);
					player.setVolume((float) volume / 100, (float) volume / 100);
				}

			}
		};
		LocalBroadcastManager.getInstance(getApplicationContext())
				.registerReceiver(playerReciever, playerIntentFilter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		player.reset();
		player = new MediaPlayer();
		player = MediaPlayer.create(this, R.raw.gorillaz);
		player.setOnCompletionListener(this);
		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(new Intent(PlayerFragment.ACTION_MUSIC_END));
	}

	@Override
	public void onDestroy() {
		LocalBroadcastManager.getInstance(getApplicationContext())
				.unregisterReceiver(playerReciever);
		super.onDestroy();
	}

	class PlayerBinder extends Binder {
		MediaPlayerService getService() {
			return MediaPlayerService.this;
		}
	}

}
