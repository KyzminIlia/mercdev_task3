package com.example.player;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PlayerFragment extends Fragment implements OnSeekBarChangeListener {
	public static final String FRAGMENT_TAG = PlayerFragment.class
			.getSimpleName();
	private final String LOG_TAG = getClass().getSimpleName();
	public static final String ACTION_MUSIC_END = "com.example.player.MUSIC_END";
	Intent mediaPlayerIntent;
	private final String ACTION_START_SERVICE = "com.example.player.MediaPlayerService";
	boolean serviceStarted = false;
	Button playerButton;
	TextView statusLabel;
	TextView musicLabel;
	String status;
	String buttonStatus;
	SeekBar volumeBar;
	BroadcastReceiver serviceReciever;
	MediaPlayerService mediaPlayerService;
	boolean isPaused = true;
	int progress;

	public void setService(MediaPlayerService mediaPlayerService) {
		this.mediaPlayerService = mediaPlayerService;
	}

	@Override
	public void onResume() {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) getActivity()
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : activityManager
				.getRunningServices(Integer.MAX_VALUE))
			if (MediaPlayerService.class.getName().equals(
					service.service.getClassName())) {
				isRunning = true;
			}
		if (!isPaused)
			if (isRunning) {
				statusLabel.setText(getString(R.string.status_playing));
				playerButton.setText(getString(R.string.button_pause));
				playerButton.setOnClickListener(new PauseClick());
				status = statusLabel.getText().toString();
				buttonStatus = playerButton.getText().toString();
			}

		super.onResume();
	}

	@Override
	public void onStop() {
		status = statusLabel.getText().toString();
		buttonStatus = playerButton.getText().toString();
		progress = volumeBar.getProgress();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		LocalBroadcastManager
				.getInstance(getActivity().getApplicationContext())
				.unregisterReceiver(serviceReciever);
		if (!isPaused)
			getActivity().stopService(mediaPlayerIntent);
		super.onDestroy();
	}

	public void changeVolume(int volume) {
		volumeBar.setProgress(volume);
		progress = volume;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		AudioManager audioManager = (AudioManager) getActivity()
				.getSystemService(getActivity().AUDIO_SERVICE);
		playerButton = (Button) view.findViewById(R.id.player_button);
		statusLabel = (TextView) view.findViewById(R.id.status_label);
		playerButton.setOnClickListener(new PlayClick());
		musicLabel = (TextView) view.findViewById(R.id.music_label);
		musicLabel.setText(Uri.parse(getString(R.raw.gorillaz))
				.getLastPathSegment());
		volumeBar = (SeekBar) view.findViewById(R.id.volume_bar);
		volumeBar.setMax(audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		volumeBar.setProgress(progress);
		volumeBar.setOnSeekBarChangeListener(this);
		playerButton.setText(buttonStatus);
		statusLabel.setText(status);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.f_player, null);
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		AudioManager audioManager = (AudioManager) getActivity()
				.getSystemService(getActivity().AUDIO_SERVICE);
		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mediaPlayerIntent = new Intent(ACTION_START_SERVICE);
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		status = getString(R.string.status_idle);
		buttonStatus = getString(R.string.button_play);
		progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mediaPlayerService = new MediaPlayerService();
		serviceReciever = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				playerButton.setText(getString(R.string.button_play));
				playerButton.setOnClickListener(new PlayClick());
				statusLabel.setText(getString(R.string.status_idle));
				status = statusLabel.getText().toString();
				buttonStatus = playerButton.getText().toString();

			}

		};
		LocalBroadcastManager
				.getInstance(getActivity().getApplicationContext())
				.registerReceiver(serviceReciever,
						new IntentFilter(ACTION_MUSIC_END));

	}

	@Override
	public void onStart() {
		super.onStart();

	}

	private class PlayClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (!serviceStarted) {
				getActivity().startService(mediaPlayerIntent);
				serviceStarted = true;
			}

			statusLabel.setText(getString(R.string.status_playing));
			playerButton.setText(getString(R.string.button_pause));
			playerButton.setOnClickListener(new PauseClick());
			status = statusLabel.getText().toString();
			buttonStatus = playerButton.getText().toString();
			isPaused = false;
			Intent playIntent = new Intent(
					mediaPlayerService.ACTION_PLAYER_CHANGE);
			playIntent
					.putExtra(MediaPlayerService.ACTION.PLAY.toString(), true);
			LocalBroadcastManager.getInstance(
					getActivity().getApplicationContext()).sendBroadcast(
					playIntent);

		}

	}

	private class PauseClick implements OnClickListener {

		@Override
		public void onClick(View v) {

			Intent pauseIntent = new Intent(
					mediaPlayerService.ACTION_PLAYER_CHANGE);
			pauseIntent.putExtra(MediaPlayerService.ACTION.PAUSE.toString(),
					true);
			LocalBroadcastManager.getInstance(
					getActivity().getApplicationContext()).sendBroadcast(
					pauseIntent);
			statusLabel.setText(getString(R.string.status_paused));
			playerButton.setText(getString(R.string.button_play));
			playerButton.setOnClickListener(new PlayClick());
			status = statusLabel.getText().toString();
			buttonStatus = playerButton.getText().toString();
			isPaused = true;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Intent changeVolumeIntent = new Intent(
				mediaPlayerService.ACTION_PLAYER_CHANGE);
		changeVolumeIntent.putExtra(
				MediaPlayerService.ACTION.VOLUME_CHANGE.toString(), true);
		changeVolumeIntent.putExtra(MediaPlayerService.EXTRA_VOLUME, progress);
		LocalBroadcastManager
				.getInstance(getActivity().getApplicationContext())
				.sendBroadcast(changeVolumeIntent);
		Log.d(LOG_TAG, "volume changed to " + progress);

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}
}
