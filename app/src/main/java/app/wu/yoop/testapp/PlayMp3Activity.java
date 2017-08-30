package app.wu.yoop.testapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zhy.http.okhttp.callback.StringCallback;

import app.wu.yoop.testapp.util.HttpUtil;
import app.wu.yoop.testapp.util.MsRequest;
import okhttp3.Call;


/**
 * Created by Silva on 2017/8/1.
 */

public class PlayMp3Activity extends AppCompatActivity implements View.OnClickListener {

    private SeekBar mSeekBar;
    private String path = "http://file.kuyinyun.com/group1/M00/90/B7/rBBGdFPXJNeAM-nhABeMElAM6bY151.mp3";
    private Player mPlayer;
    private EditText mEditText;
    private Button mTvPlay;

    View statusBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_mp3_player);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mEditText = (EditText) findViewById(R.id.editText);
        mTvPlay = (Button) findViewById(R.id.tv_play);
        mTvPlay.setOnClickListener(this);
        mEditText.setText(path);

        mPlayer = new Player(mSeekBar);
        getBanner();

        setFullScreen(this);
    }

    /**
     * 播放
     *
     * @param view
     */
    public void play(View view) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                mPlayer.playUrl(path);
            }
        }).start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_play:
                play(mTvPlay);
                getBanner();
                break;
            default:
                break;
        }
    }

    private void getBanner () {
        HttpUtil.get(MsRequest.APP_GET_BANNERS.getUrl(), null, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

            }
        });
    }

    /**
     * 设置全屏
     */
    public void setFullScreen(Activity activity) {
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideStatusBar();
    }

    protected void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        if(statusBarView != null){
            statusBarView.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
