package com.creativeAI.jisun.hanja;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Scene;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class StartActivity extends AppCompatActivity {
    ImageView login_btn, join_btn;
    String hash;
    Intent startIntent;
    public static Context musicContext;
    // 다음 엑티비티에서 음악 안 꺼지게 하기 위해 꼭 필요!!
    public Boolean next;
    private static MediaPlayer buttonmusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        startIntent = new Intent(getApplicationContext(), StartService.class);
        startIntent.putExtra("where", "시작");
        musicContext = this;
        next = false;
        try{
            startMusic();
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), "어플을 다시 실행 해 주세요.", Toast.LENGTH_SHORT).show();
        }

        hash = getKeyHash(this);
        //Toast.makeText(getApplicationContext(), hash, Toast.LENGTH_SHORT).show();

        // 로그인버튼
        login_btn = (ImageView) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                buttonmusic.start();

                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                next = true;
                startActivity(intent);
            }
        });

        // 회원가입버튼
        join_btn = (ImageView) findViewById(R.id.join_btn);
        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                buttonmusic.start();

                Intent intent = new Intent(StartActivity.this, JoinActivity.class);
                next = true;
                startActivity(intent);
            }
        });

    }

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.w("KeyHash : ", Base64.encodeToString(md.digest(), Base64.NO_WRAP));
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w("KeyHash : ", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        // mp.stop();
        //super.onBackPressed();
        // 다이얼로그 바디
        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(StartActivity.this);
        // 다이얼로그 메세지
        alertdialog.setMessage("한자공부한자를 종료하시겠습니까?");

        // 확인버튼
        alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
        });
        alertdialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        // 메인 다이얼로그 생성
        AlertDialog alert = alertdialog.create();
        // 타이틀
        alert.setTitle("어플 종료");
        alert.show();
    }

    void startMusic() {
        startService(startIntent);
    }

    void stopMusic() {
        stopService(startIntent);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //KeyguardManager islock = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        if (islock.inKeyguardRestrictedInputMode() && !pm.isInteractive())
        if (next == false)
            stopService(startIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (next == false)
            stopService(startIntent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (next == false) {
            stopService(startIntent);
            startService(startIntent);
        }
    }
}