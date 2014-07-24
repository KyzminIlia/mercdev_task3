package com.example.player;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PlayerFragment extends Fragment implements OnCompletionListener {
	public static final String FRAGMENT_TAG = PlayerFragment.class
			.getSimpleName();
	private final String LOG_TAG = getClass().getSimpleName();

	Button playerButton;
	TextView statusLabel;
	MediaPlayer player;
	TextView musicLabel;
	String status;
	String buttonStatus;

	@Override
	public void onStop() {
		status = statusLabel.getText().toString();
		buttonStatus = playerButton.getText().toString();
		super.onStop();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		playerButton = (Button) view.findViewById(R.id.player_button);
		statusLabel = (TextView) view.findViewById(R.id.status_label);
		playerButton.setOnClickListener(new PlayClick());
		musicLabel = (TextView) view.findViewById(R.id.music_label);
		musicLabel.setText(Uri.parse(getString(R.raw.explosion))
				.getLastPathSegment());
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
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		player = new MediaPlayer();
		player = MediaPlayer.create(getActivity(), R.raw.explosion);
		player.setOnCompletionListener(this);
		status = getString(R.string.status_idle);
		buttonStatus = getString(R.string.button_play);

	}

	private class PlayClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "start music on " + player.getCurrentPosition()
					+ "/" + player.getDuration());
			player.start();
			statusLabel.setText(getString(R.string.status_playing));
			playerButton.setText(getString(R.string.button_pause));
			playerButton.setOnClickListener(new PauseClick());
			status = statusLabel.getText().toString();
			buttonStatus = playerButton.getText().toString();

		}

	}

	private class PauseClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "pause music on " + player.getCurrentPosition()
					+ "/" + player.getDuration());
			player.pause();
			statusLabel.setText(getString(R.string.status_paused));
			playerButton.setText(getString(R.string.button_play));
			playerButton.setOnClickListener(new PlayClick());
			status = statusLabel.getText().toString();
			buttonStatus = playerButton.getText().toString();

		}

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		player.reset();
		player = new MediaPlayer();
		player = MediaPlayer.create(getActivity(), R.raw.explosion);
		player.setOnCompletionListener(this);
		playerButton.setText(getString(R.string.button_play));
		playerButton.setOnClickListener(new PlayClick());
		statusLabel.setText(getString(R.string.status_idle));
		status = statusLabel.getText().toString();
		buttonStatus = playerButton.getText().toString();
		Log.d(LOG_TAG, "reset player " + player.getCurrentPosition() + "/"
				+ player.getDuration());

	}
}
