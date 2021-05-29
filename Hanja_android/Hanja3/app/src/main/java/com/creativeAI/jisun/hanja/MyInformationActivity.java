package com.creativeAI.jisun.hanja;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MyInformationActivity extends AppCompatActivity {
    ImageView back_Arrow, logout_button, withdraw_button;
    TextView menu_title, _email, _nickname, _this_week_hanja, _last_week_hanja, _quiz_count, _answer_rate, _learning_hanja;
    RelativeLayout family_study_check, change_pw;
    String tag, email, pw, family_nickname;
    ProgressBar study_amount;

    private static MediaPlayer buttonmusic;
    private SharedPreferences backgroundData;
    boolean _effect_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);

        ((MainActivity) MainActivity.musicContext).next = false;

        tag = "memberinfo";
        // 이전 엑티비티에서 회원 id받아오기
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        pw = intent.getExtras().getString("pw");

        JSONObject jsonObj = new JSONObject();
        JSONObject userinfo = new JSONObject();
        try {
            // 여기에 name, value 추가하면 된다!!
            userinfo.put("user", email);
            jsonObj.put("userinfo", userinfo);

            NetworkTask networkTask = new NetworkTask(jsonObj);
            networkTask.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 설정값 불러오기
        backgroundData = getSharedPreferences("backgroundData", MODE_PRIVATE);
        _effect_sound = backgroundData.getBoolean("EFFECT_SOUND", true);

        _email = (TextView) findViewById(R.id.email);
        _nickname = (TextView) findViewById(R.id.nickname);
        _this_week_hanja = (TextView) findViewById(R.id.this_week_hanja);
        _last_week_hanja = (TextView) findViewById(R.id.last_week_hanja);
        _quiz_count = (TextView) findViewById(R.id.quiz_count);
        _answer_rate = (TextView) findViewById(R.id.answer_rate);
        _learning_hanja = (TextView) findViewById(R.id.learning_hanja);
        study_amount = (ProgressBar) findViewById(R.id.progressBar3);

        back_Arrow = (ImageView) findViewById(R.id.backArrow);
        back_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                Intent intent = new Intent(MyInformationActivity.this, MainActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("pw", pw);
                startActivity(intent);
                ((MainActivity) MainActivity.musicContext).finish();
                ((MainActivity) MainActivity.musicContext).next = false;
                finish();
                // ((MainActivity) MainActivity.musicContext).next = false;
                // onBackPressed();
            }
        });

        menu_title = (TextView) findViewById(R.id.profileName);
        menu_title.setText("내정보");

        family_study_check = (RelativeLayout) findViewById(R.id.family_study_check);
        family_study_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                if (family_nickname.compareTo("null") == 0) {
                    // 다이얼로그 바디
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(MyInformationActivity.this);
                    // 다이얼로그 메세지
                    alertdialog.setMessage("가족을 먼저 추가해주세요.");

                    // 확인버튼
                    alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    // 메인 다이얼로그 생성
                    AlertDialog alert = alertdialog.create();
                    // 타이틀
                    alert.setTitle("가족 없음");
                    alert.show();
                } else {
                    Intent intent = new Intent(MyInformationActivity.this, FamilyProgressActivity.class);
                    intent.putExtra("email", email);
                    ((MainActivity) MainActivity.musicContext).next = true;
                    startActivity(intent);
                }

            }
        });

        change_pw = (RelativeLayout) findViewById(R.id.change_pw);
        change_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                Intent intent = new Intent(MyInformationActivity.this, PWChangeActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("pw", pw);
                intent.putExtra("nickname", _nickname.getText().toString());
                ((MainActivity) MainActivity.musicContext).next = true;
                startActivity(intent);
            }
        });

        logout_button = (ImageView) findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                tag = "logout";

                // 다이얼로그 바디
                final AlertDialog.Builder alertdialog = new AlertDialog.Builder(MyInformationActivity.this);
                // 다이얼로그 메세지
                alertdialog.setMessage("정말 로그아웃 하시겠습니까?");

                // 취소버튼
                alertdialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // 확인버튼
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject jsonObj = new JSONObject();
                        JSONObject userinfo = new JSONObject();
                        try {
                            // 여기에 name, value 추가하면 된다!!
                            userinfo.put("user", email);
                            jsonObj.put("userinfo", userinfo);

                            NetworkTask networkTask = new NetworkTask(jsonObj);
                            networkTask.execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // 메인 다이얼로그 생성
                AlertDialog alert = alertdialog.create();
                // 타이틀
                alert.setTitle("로그아웃");
                alert.show();
            }
        });

        withdraw_button = (ImageView) findViewById(R.id.withdraw_button);
        withdraw_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                Intent intent = new Intent(MyInformationActivity.this, WithdrawActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("nickname", _nickname.getText().toString());
                ((MainActivity) MainActivity.musicContext).next = true;
                startActivity(intent);
            }
        });
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private JSONObject jsonObj;

        public NetworkTask(JSONObject jsonObj) {
            this.jsonObj = jsonObj;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection(tag);
            result = requestHttpURLConnection.request(jsonObj); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (tag) {
                case "memberinfo":
                    try {
                        String user, nickname, this_week_hanja, last_week_hanja, quiz_count, answer_rate, learning_hanja;
                        JSONObject jObject = new JSONObject(s);
                        JSONObject obj = jObject.getJSONObject("userinfo");
                        user = obj.getString("user");
                        nickname = obj.getString("nickname");
                        this_week_hanja = obj.getString("this_week_hanja");
                        last_week_hanja = obj.getString("last_week_hanja");
                        quiz_count = obj.getString("quiz_count");
                        answer_rate = obj.getString("answer_rate");
                        learning_hanja = obj.getString("learning_hanja");
                        family_nickname = obj.getString("family_nickname");

                        _email.setText(user);
                        _nickname.setText(nickname);
                        _this_week_hanja.setText(this_week_hanja + "개");
                        _last_week_hanja.setText(last_week_hanja + "개");
                        _quiz_count.setText(quiz_count + "회");
                        _answer_rate.setText(answer_rate + "%");
                        _learning_hanja.setText(learning_hanja + "/40");
                        study_amount.setProgress(Integer.parseInt(learning_hanja));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "logout":
                    switch (s) {
                        case "logout success":
                            Toast.makeText(getApplicationContext(), "로그아웃 성공~", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MyInformationActivity.this, StartActivity.class);
                            ((MainActivity) MainActivity.musicContext).next = false;
                            startActivity(intent);
                            finish();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "로그아웃 실패", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // ((MainActivity) MainActivity.musicContext).next = false;
        back_Arrow.performClick();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //KeyguardManager islock = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        if (islock.inKeyguardRestrictedInputMode() && !pm.isInteractive())
        if (((MainActivity) MainActivity.musicContext).next == false)
            ((MainActivity) MainActivity.musicContext).stopMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (((MainActivity) MainActivity.musicContext).next == false)
            ((MainActivity) MainActivity.musicContext).stopMusic();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (((MainActivity) MainActivity.musicContext).next == false && ((MainActivity) MainActivity.musicContext)._background_sound) {
            ((MainActivity) MainActivity.musicContext).stopMusic();
            ((MainActivity) MainActivity.musicContext).startMusic();
        }
    }
}
