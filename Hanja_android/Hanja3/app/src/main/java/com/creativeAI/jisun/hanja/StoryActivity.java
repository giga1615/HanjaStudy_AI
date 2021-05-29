package com.creativeAI.jisun.hanja;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class StoryActivity extends AppCompatActivity {
    ImageView story_title, story1, story2, story3, story4, story44, speech;
    ImageView story_next, story_back, skip_btn;
    ConstraintLayout story_background;
    //+++
    RelativeLayout go_to_main_layoout;
    //---
    int page_num;
    String email, pw;
    Intent storyIntent;

    private static MediaPlayer pageflip;
    private static MediaPlayer buttonmusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        // 이전 엑티비티에서 회원 id받아오기
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        pw = intent.getExtras().getString("pw");

        storyIntent = new Intent(getApplicationContext(), StartService.class);
        storyIntent.putExtra("where", "스토리");
        startMusic();

        page_num = 1;
        story_title = (ImageView) findViewById(R.id.story_title);
        story1 = (ImageView) findViewById(R.id.story1);
        story2 = (ImageView) findViewById(R.id.story2);
        story3 = (ImageView) findViewById(R.id.story3);
        story4 = (ImageView) findViewById(R.id.story4);
        story44 = (ImageView) findViewById(R.id.story44);
        story_background = (ConstraintLayout) findViewById(R.id.story_background);
        //+++
        go_to_main_layoout = (RelativeLayout) findViewById(R.id.go_to_main_layout);
        //---
        speech = (ImageView) findViewById(R.id.speech);


        skip_btn = (ImageView) findViewById(R.id.skip_btn);
        skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                buttonmusic.start();

                Intent intent = new Intent(StoryActivity.this, MainActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("pw", pw);
                stopMusic();
                startActivity(intent);
                finish();
            }
        });

        story_next = (ImageView) findViewById(R.id.story_next);
        story_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page_num++;
                pageflip = MediaPlayer.create(getApplicationContext(), R.raw.pageflip);
                pageflip.setLooping(false);
                pageflip.start();

                invisible_image();
                switch (page_num) {
                    case 1:
                        story_title.setImageResource(R.drawable.story2);
                        story1.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        story_title.setImageResource(R.drawable.story3);
                        story2.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        story_title.setImageResource(R.drawable.story6);
                        story3.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        story_background.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.story7));
                        story_title.setVisibility(View.INVISIBLE);
                        story4.setVisibility(View.VISIBLE);
                        story44.setVisibility(View.VISIBLE);
                        speech.setImageResource(R.drawable.story10);
                        break;
                    case 5:
                        speech.setImageResource(R.drawable.story12);
                        story4.setVisibility(View.VISIBLE);
                        story44.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        speech.setImageResource(R.drawable.story13);
                        story4.setVisibility(View.VISIBLE);
                        story44.setVisibility(View.VISIBLE);
                        break;
                    case 7:
                        speech.setImageResource(R.drawable.story14);
                        story4.setVisibility(View.VISIBLE);
                        story44.setVisibility(View.VISIBLE);
                        break;
                    default:
                        story_title.setImageResource(R.drawable.story2);
                        story1.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        story_back = (ImageView) findViewById(R.id.story_back);
        story_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page_num--;
                pageflip = MediaPlayer.create(getApplicationContext(), R.raw.pageflip);
                pageflip.setLooping(false);
                pageflip.start();

                invisible_image();
                switch (page_num) {
                    case 1:
                        story_title.setImageResource(R.drawable.story2);
                        story1.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        story_title.setImageResource(R.drawable.story3);
                        story2.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        story_title.setImageResource(R.drawable.story6);
                        story3.setVisibility(View.VISIBLE);
                        story_background.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.story1));
                        story_title.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        speech.setImageResource(R.drawable.story10);
                        story4.setVisibility(View.VISIBLE);
                        story44.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        speech.setImageResource(R.drawable.story12);
                        story4.setVisibility(View.VISIBLE);
                        story44.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        speech.setImageResource(R.drawable.story13);
                        story4.setVisibility(View.VISIBLE);
                        story44.setVisibility(View.VISIBLE);
                        break;
                    case 7:
                        speech.setImageResource(R.drawable.story14);
                        story4.setVisibility(View.VISIBLE);
                        story44.setVisibility(View.VISIBLE);
                        break;
                    default:
                        story_title.setImageResource(R.drawable.story2);
                        story1.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        invisible_image();
    }

    void invisible_image() {
        story1.setVisibility(View.INVISIBLE);
        story2.setVisibility(View.INVISIBLE);
        story3.setVisibility(View.INVISIBLE);
        story4.setVisibility(View.INVISIBLE);
        story44.setVisibility(View.INVISIBLE);
        speech.setVisibility(View.INVISIBLE);

        if (page_num == 1) {
            story_back.setVisibility(View.INVISIBLE);
            story1.setVisibility(View.VISIBLE);
        } else {
            story_back.setVisibility(View.VISIBLE);
            skip_btn.setVisibility(View.VISIBLE);
        }

        if (page_num == 7) {
            story_next.setVisibility(View.INVISIBLE);
            //+++
            skip_btn.setVisibility(View.INVISIBLE);

            story_background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    go_to_main_layoout.setVisibility(View.VISIBLE);
                    go_to_main_layoout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pageflip = MediaPlayer.create(getApplicationContext(), R.raw.finishmusic);
                            pageflip.setLooping(false);
                            pageflip.start();

                            Intent intent = new Intent(StoryActivity.this, MainActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("pw", pw);
                            stopMusic();
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            });
//            Handler timer = new Handler();
//            timer.postDelayed(new Runnable() { //2초후 쓰레드를 생성하는 postDelayed 메소드
//                public void run() {
//
//                    // 다이얼로그 바디
//                    final AlertDialog.Builder alertdialog = new AlertDialog.Builder(StoryActivity.this);
//                    // 다이얼로그 메세지
//                    alertdialog.setMessage("지금부터 자유롭게 플레이해 보세요!");
//
//                    // 확인버튼
//                    alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                            Intent intent = new Intent(StoryActivity.this, MainActivity.class);
//                            intent.putExtra("email", email);
//                            intent.putExtra("pw", pw);
//                            startActivity(intent);
//                            finish();
//                        }
//                    });
//
//                    // 메인 다이얼로그 생성
//                    AlertDialog alert = alertdialog.create();
//                    // 타이틀
//                    alert.setTitle("제목~~");
//                    alert.show();
//
//
//                }
//            }, 2000); //2000은 2초를 의미한다.
            //---
        } else
            story_next.setVisibility(View.VISIBLE);

        if (page_num >= 4)
            speech.setVisibility(View.VISIBLE);

    }

    void startMusic() {
        startService(storyIntent);
    }

    void stopMusic() {
        stopService(storyIntent);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //KeyguardManager islock = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        if (islock.inKeyguardRestrictedInputMode() && !pm.isInteractive())
        //if (next == false)
        stopService(storyIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if (next == false)
        stopService(storyIntent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //if (next == false) {
        stopService(storyIntent);
        startService(storyIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopMusic();
    }
}