package com.example.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MediaPlayerService extends Service implements OnCompletionListener {
	MediaPlayer player;
	float volume;
	private final String LOG_TAG = MediaPlayerService.class.getSimpleName()
			.toString();
	PlayerBinder mediaPlayerBinder;
	BroadcastReceiver playerReciever;
	IntentFilter playerIntentFilter;
	public final static String ACTION_PLAYER_CHANGE = "com.example.player.PLAYER_CHANGE";
	public final static String EXTRA_VOLUME = "com.example.VOLUME";

	public static enum ACTION {
		PLAY, PAUSE, VOLUME_CHANGE
	};

	@Override
	public void onCreate() {
		super.onCreate();
		Intent intent = new Intent(this, PlayerActivity.class);
		Notification notification = new Notification(R.drawable.ic_launcher,
				"Player", System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent, 0);
		notification.setLatestEventInfo(getApplicationContext(), "Now playing",
				(Uri.parse(getString(R.raw.gorillaz)).getLastPathSegment()),
				pendingIntent);
		startForeground(17, notification);
		player = new MediaPlayer();
		player = MediaPlayer.create(this, R.raw.gorillaz);
		player.setOnCompletionListener(this);
		playerIntentFilter = new IntentFilter(ACTION_PLAYER_CHANGE);
		playerReciever = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getBooleanExtra(ACTION.PLAY.toString(), false)) {
					player.start();
					Log.d(LOG_TAG, "start player");
				}
				if (intent.getBooleanExtra(ACTION.PAUSE.toString(), false)) {
					player.pause();
					Log.d(LOG_TAG, "pause player");
				}
				if (intent.getBooleanExtra(ACTION.VOLUME_CHANGE.toString(),
						false)) {
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
