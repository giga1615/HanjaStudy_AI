package com.creativeAI.jisun.hanja;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class QuizStartActivity extends AppCompatActivity {
    ImageView back_Arrow;
    ImageView quiz_start;
    TextView menu_title;

    String email, pw, coin;

    Intent quizIntent;
    public static Context musicContext;
    // 다음 엑티비티에서 음악 안 꺼지게 하기 위해 꼭 필요!!
    public Boolean next;
    private static MediaPlayer buttonmusic;

    private SharedPreferences backgroundData;
    boolean _background_sound, _effect_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_start);

        // 이전 엑티비티에서 회원 id받아오기
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        pw = intent.getExtras().getString("pw");
        coin = intent.getExtras().getString("coin");

        quizIntent = new Intent(getApplicationContext(), StartService.class);
        quizIntent.putExtra("where", "퀴즈");
        musicContext = this;
        next = false;
        // 설정값 불러오기
        backgroundData = getSharedPreferences("backgroundData", MODE_PRIVATE);
        _background_sound = backgroundData.getBoolean("BACKGROUND_SOUND", true);
        _effect_sound = backgroundData.getBoolean("EFFECT_SOUND", true);

        if (_background_sound)
            try {
                startMusic();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "어플을 다시 실행 해 주세요.", Toast.LENGTH_SHORT).show();
            }

        back_Arrow = (ImageView) findViewById(R.id.backArrow);
        back_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

//                Intent intent = new Intent(QuizStartActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
                //onBackPressed();
                ((MainActivity) MainActivity.musicContext).finish();
                Intent intent = new Intent(QuizStartActivity.this, MainActivity.class);
                intent.putExtra("pw", pw);
                intent.putExtra("email", email);
                next = false;
                startActivity(intent);
                finish();
            }
        });

        quiz_start = (ImageView) findViewById(R.id.quiz_start);
        quiz_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                Intent intent = new Intent(QuizStartActivity.this, QuizActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("pw", pw);
                intent.putExtra("coin", coin);
                next = true;
                startActivity(intent);
                finish();
            }
        });

        menu_title = (TextView) findViewById(R.id.profileName);
        menu_title.setText("퀴즈");
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        ((MainActivity) MainActivity.musicContext).finish();
        Intent intent = new Intent(QuizStartActivity.this, MainActivity.class);
        intent.putExtra("pw", pw);
        intent.putExtra("email", email);
        next = false;
        startActivity(intent);
        finish();
    }

    void startMusic() {
        startService(quizIntent);
    }

    void stopMusic() {
        stopService(quizIntent);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //KeyguardManager islock = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        if (islock.inKeyguardRestrictedInputMode() && !pm.isInteractive())
        if (next == false)
            stopService(quizIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (next == false)
            stopService(quizIntent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (next == false && _background_sound) {
            stopService(quizIntent);
            startService(quizIntent);
        }
    }
}
