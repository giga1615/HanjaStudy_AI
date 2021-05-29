package com.creativeAI.jisun.hanja;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    TextView goto_join;
    TextView find_pw;
    ImageView login_btn;
    EditText email, pw;
    private static MediaPlayer buttonmusic;

    // 자동로그인
    CheckBox autoLogin;
    private SharedPreferences appData;
    private boolean saveLoginData;
    String auto_email, auto_pw;

    String result_text, _email;
    String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tag = "login";
        email = (EditText) findViewById(R.id.email);
        pw = (EditText) findViewById(R.id.pw);
        //자동로그인
        autoLogin = (CheckBox) findViewById(R.id.auto_login);

        ((StartActivity) StartActivity.musicContext).next = false;

        goto_join = (TextView) findViewById(R.id.goto_join);
        SpannableStringBuilder builder = new SpannableStringBuilder(" 회원가입하기");
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FED81A")), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        goto_join.append(builder);
        goto_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                ((StartActivity) StartActivity.musicContext).next = true;
                startActivity(intent);
                finish();
            }
        });

        find_pw = (TextView) findViewById(R.id.find_pw);
        find_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindPWActivity.class);
                ((StartActivity) StartActivity.musicContext).next = true;
                startActivity(intent);
            }
        });

        login_btn = (ImageView) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "전송 시작!", Toast.LENGTH_SHORT).show();
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                buttonmusic.start();

                String reqUser = email.getText().toString(); // 보내는 메시지를 받아옴
                String reqPswd = pw.getText().toString();

                JSONObject jsonObj = new JSONObject();
                JSONObject userinfo = new JSONObject();

                try {
                    // 여기에 name, value 추가하면 된다!!
                    userinfo.put("user", reqUser);
                    userinfo.put("pswd", reqPswd);
                    jsonObj.put("userinfo", userinfo);

                    NetworkTask networkTask = new NetworkTask(jsonObj);
                    networkTask.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // 설정값 불러오기
        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        // 이전에 로그인 정보를 저장시킨 기록이 있다면
        if(saveLoginData) {
            email.setText(auto_email);
            pw.setText(auto_pw);
            autoLogin.setChecked(saveLoginData);
        }
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
            // login success면 로그인 / non-added memeber면 아이디를 다시 확인 해 주세요.
            //result_text = s;
            switch (s) {
                case "non-added member":
                    // 다이얼로그 바디
                    final AlertDialog.Builder alertdialog = new AlertDialog.Builder(LoginActivity.this);
                    // 다이얼로그 메세지
                    alertdialog.setMessage("아이디 혹은 비밀번호를 다시 한 번 확인 해 주세요.");

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
                    alert.setTitle("로그인 정보가 일치하지 않습니다.");
                    alert.show();
                default:
                    try {
                        JSONObject jObject = new JSONObject(s);
                        JSONObject obj = jObject.getJSONObject("userinfo");
                        _email = obj.getString("user");
                        Intent intent = new Intent(LoginActivity.this, StoryActivity.class);
                        intent.putExtra("email", _email);
                        intent.putExtra("pw", pw.getText().toString());
                        startActivity(intent);
                        ((StartActivity) StartActivity.musicContext).next = false;
                        ((StartActivity) StartActivity.musicContext).stopMusic();
                        save();
                        finish();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "로그인 실패, 다시시도해주세요.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    break;
            }

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
//            result.setText(s);
//            if (s != null) {
//                Toast.makeText(LoginActivity.this, "" + _email, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(LoginActivity.this, "값이 없습니다", Toast.LENGTH_SHORT).show();
//            }
        }
    }

    // 설정값을 저장하는 함수
    private  void save() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor.putBoolean("SAVE_LOGIN_DATA", autoLogin.isChecked());
        editor.putString("ID", email.getText().toString().trim());
        editor.putString("PWD", pw.getText().toString().trim());

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }

    // 설정값을 불러오는 함수
    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        auto_email = appData.getString("ID", "");
        auto_pw = appData.getString("PWD", "");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ((StartActivity) StartActivity.musicContext).next = false;
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //KeyguardManager islock = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        if (islock.inKeyguardRestrictedInputMode() && !pm.isInteractive())
        if (((StartActivity) StartActivity.musicContext).next == false)
            ((StartActivity) StartActivity.musicContext).stopMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (((StartActivity) StartActivity.musicContext).next == false)
            ((StartActivity) StartActivity.musicContext).stopMusic();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (((StartActivity) StartActivity.musicContext).next == false) {
            ((StartActivity) StartActivity.musicContext).stopMusic();
            ((StartActivity) StartActivity.musicContext).startMusic();
        }
    }
}
