package com.creativeAI.jisun.hanja;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class PWChangeActivity extends AppCompatActivity {
    ImageView back_Arrow, change_btn;
    TextView menu_title, _email, _nickname;
    EditText now_pw, new_pw, new_pw2;
    String tag, email, pw, result_text, nickname;

    private static MediaPlayer buttonmusic;
    private SharedPreferences backgroundData;
    boolean _effect_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag = "registration";
        // 이전 엑티비티에서 회원 id받아오기
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        pw = intent.getExtras().getString("pw");
        nickname = intent.getExtras().getString("nickname");

        ((MainActivity) MainActivity.musicContext).next = false;
        // 설정값 불러오기
        backgroundData = getSharedPreferences("backgroundData", MODE_PRIVATE);
        _effect_sound = backgroundData.getBoolean("EFFECT_SOUND", true);

        setContentView(R.layout.activity_pwchange);
        now_pw = (EditText) findViewById(R.id.now_pw);
        new_pw = (EditText) findViewById(R.id.new_pw);
        new_pw2 = (EditText) findViewById(R.id.new_pw2);
        _email = (TextView) findViewById(R.id.email);
        _email.setText(email);
        _nickname = (TextView) findViewById(R.id.nickname);
        _nickname.setText(nickname);

        back_Arrow = (ImageView) findViewById(R.id.backArrow);
        back_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

//                Intent intent = new Intent(PWChangeActivity.this, MyInformationActivity.class);
//                startActivity(intent);
//                finish();
                onBackPressed();
            }
        });

        menu_title = (TextView) findViewById(R.id.profileName);
        menu_title.setText("비밀번호 변경");

        // 아이디 생성 가능여부 확인 후 로그인 페이지로 이동
        change_btn = (ImageView) findViewById(R.id.change_btn);
        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                if (TextUtils.isEmpty(now_pw.getText()) || TextUtils.isEmpty(new_pw.getText()) || TextUtils.isEmpty(new_pw2.getText())) {
                    // 다이얼로그 바디
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(PWChangeActivity.this);
                    // 다이얼로그 메세지
                    alertdialog.setMessage("빈칸을 모두 작성해주세요.");

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
                    alert.setTitle("입력하지 않은 정보가 존재합니다.");
                    alert.show();
                } else {
                    if (now_pw.getText().toString().compareTo(pw) != 0) {
                        // 다이얼로그 바디
                        AlertDialog.Builder alertdialog = new AlertDialog.Builder(PWChangeActivity.this);
                        // 다이얼로그 메세지
                        alertdialog.setMessage("현재 비밀번호를 다시 확인해주세요.");

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
                        alert.setTitle("정확하지 않은 비밀번호입니다.");
                        alert.show();
                    } else if (new_pw.getText().toString().compareTo(new_pw2.getText().toString()) != 0) {
                        // 다이얼로그 바디
                        AlertDialog.Builder alertdialog = new AlertDialog.Builder(PWChangeActivity.this);
                        // 다이얼로그 메세지
                        alertdialog.setMessage("새 비밀번호를 다시 확인해주세요.");

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
                        alert.setTitle("비밀번호 확인이 일치하지 않습니다.");
                        alert.show();
                    } else {
                        String reqUser = email;
                        String reqPswd = new_pw.getText().toString();

                        JSONObject jsonObj = new JSONObject();
                        JSONObject userinfo = new JSONObject();

                        try {
                            // 여기에 name, value 추가하면 된다!!
                            userinfo.put("cmd", "updateuserinfo");
                            userinfo.put("user", reqUser);
                            userinfo.put("pswd", reqPswd);
                            jsonObj.put("userinfo", userinfo);

                            NetworkTask networkTask = new NetworkTask(jsonObj);
                            networkTask.execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
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
            // add user info success면 회원가입성공 / non-added memeber면 아이디를 다시 확인 해 주세요.
            result_text = s;
            if (result_text.compareTo("update user info success") == 0) {
                // 다이얼로그 바디
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(PWChangeActivity.this);
                // 다이얼로그 메세지
                alertdialog.setMessage("다시 로그인해주세요.");

                // 확인버튼
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(PWChangeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                // 메인 다이얼로그 생성
                AlertDialog alert = alertdialog.create();
                // 타이틀
                alert.setTitle("비밀번호 변경 성공");
                alert.show();
            } else {
                // 다이얼로그 바디
                AlertDialog.Builder alertdialog2 = new AlertDialog.Builder(PWChangeActivity.this);
                // 다이얼로그 메세지
                alertdialog2.setMessage("다시 시도해주세요.");

                // 확인버튼
                alertdialog2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // 메인 다이얼로그 생성
                AlertDialog alert2 = alertdialog2.create();
                // 타이틀
                alert2.setTitle("비밀번호 변경 실패");
                alert2.show();
            }

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
//            result.setText(s);
            if (s != null) {
            //    Toast.makeText(PWChangeActivity.this, "" + result_text, Toast.LENGTH_SHORT).show();
            } else {
            //    Toast.makeText(PWChangeActivity.this, "값이 없습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ((MainActivity) MainActivity.musicContext).next = false;
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
