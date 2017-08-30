package app.wu.yoop.testapp.widget;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Silva on 2017/8/1.
 */

public class Player implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener {

    private SeekBar seekBar;
    private MediaPlayer mMediaPlayer;
    private Timer mTimer = new Timer();

    public Player(SeekBar seekBar) {
        super();
        this.seekBar = seekBar;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mTimer.schedule(timeTask, 0, 1000);
    }

    TimerTask timeTask = new TimerTask() {
        @Override
        public void run() {
            if (mMediaPlayer == null)
                return;
            if (mMediaPlayer.isPlaying() && seekBar.isPressed() == false) {
                mHandler.sendEmptyMessage(0);
            }

        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int position = mMediaPlayer.getCurrentPosition();
            int duration = mMediaPlayer.getDuration();
            if (duration > 0) {
                long pos = seekBar.getMax() * position / duration;
                seekBar.setProgress((int)pos);
            }
        }
    };

    public  void play () {
        mMediaPlayer.start();
    }

    public void setUrl (String url) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause () {
        mMediaPlayer.pause();
    }

    public void stop () {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
        int currentProgress = seekBar.getMax()
                * mMediaPlayer.getCurrentPosition() / mMediaPlayer.getDuration();
        Log.e(currentProgress + "% play", percent + " buffer");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
