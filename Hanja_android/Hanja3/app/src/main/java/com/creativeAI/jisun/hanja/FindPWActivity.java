package com.creativeAI.jisun.hanja;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FindPWActivity extends AppCompatActivity {
    ImageView back_Arrow, ok_btn;
    TextView menu_title;

    private static MediaPlayer buttonmusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        back_Arrow = (ImageView) findViewById(R.id.backArrow);
        back_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                buttonmusic.start();

                onBackPressed();
//                Intent intent = new Intent(FindPWActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
            }
        });

        menu_title = (TextView)findViewById(R.id.profileName);
        menu_title.setText("비밀번호 찾기");

        // 아이디 생성 가능여부 확인 후 로그인 페이지로 이동
        ok_btn = (ImageView)findViewById(R.id.ok);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                buttonmusic.start();

                // 다이얼로그 바디
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(FindPWActivity.this);
                // 다이얼로그 메세지
                alertdialog.setMessage("로그인 후 반드시 비밀번호를 변경해주세요.");

                // 확인버튼
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(FindPWActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                // 메인 다이얼로그 생성
                AlertDialog alert = alertdialog.create();
                // 타이틀
                alert.setTitle("가입한 이메일로 임시번호가 발송되었습니다.");
                alert.show();
            }
        });
    }
}
