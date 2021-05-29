package com.creativeAI.jisun.hanja;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class StartService extends Service {
    public MediaPlayer mp;

    public StartService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    public void onStart(Intent intent, int startId) {
        Log.i("Example", "Service onStart()");
        super.onStart(intent, startId);
        String where = intent.getStringExtra("where");
        switch (where){
            case "시작":
                mp = MediaPlayer.create(this, R.raw.start_music);
                break;
            case "스토리":
                mp = MediaPlayer.create(this, R.raw.story_music);
                break;
            case "메인":
                mp = MediaPlayer.create(this, R.raw.main_music);
                break;
            case "퀴즈":
                mp = MediaPlayer.create(this, R.raw.quiz_music);
                break;
            case "학습":
                mp = MediaPlayer.create(this, R.raw.study_music);
                break;
        }
        mp.setLooping(true);

        // 리스너 등록
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // 재생 사운드 출력을 시작할 준비가 되었을 때
                mediaPlayer.start();
            }
        });
    }

    public void onDestroy() {
        Log.i("Example", "Service onDestroy()");
        super.onDestroy();
        mp.pause();
        mp.reset();

        // 해지 리스너 등록
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
    }
}
