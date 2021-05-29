package com.creativeAI.jisun.hanja;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class JoinActivity extends AppCompatActivity {
    TextView goto_login;
    ImageView id_make_btn;

    EditText name, nickname, y, m, d, email, pw, pw2;
    String birth, tag, result_text;
    private static MediaPlayer buttonmusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        tag = "registration";

        ((StartActivity) StartActivity.musicContext).next = false;

        goto_login = (TextView) findViewById(R.id.goto_login);
        SpannableStringBuilder builder = new SpannableStringBuilder(" 로그인하기");
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#9454DC")), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        goto_login.append(builder);
        goto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                ((StartActivity) StartActivity.musicContext).next = true;
                startActivity(intent);
                finish();
            }
        });

        name = (EditText) findViewById(R.id.name);
        nickname = (EditText) findViewById(R.id.nickname);
        y = (EditText) findViewById(R.id.y);
        m = (EditText) findViewById(R.id.m);
        d = (EditText) findViewById(R.id.d);
        email = (EditText) findViewById(R.id.email);
        pw = (EditText) findViewById(R.id.pw);
        pw2 = (EditText) findViewById(R.id.pw2);

        // 아이디 생성 가능여부 확인 후 로그인 페이지로 이동
        id_make_btn = (ImageView) findViewById(R.id.id_make_btn);
        id_make_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                buttonmusic.start();

                if (TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(nickname.getText()) || TextUtils.isEmpty(y.getText()) || TextUtils.isEmpty(m.getText()) || TextUtils.isEmpty(d.getText()) ||
                        TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(pw.getText()) || TextUtils.isEmpty(pw2.getText())) {
                    // 다이얼로그 바디
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(JoinActivity.this);
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
                } else if (y.length() < 4 || m.length() < 2 || d.length() < 2) {
                    // 다이얼로그 바디
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(JoinActivity.this);
                    // 다이얼로그 메세지
                    alertdialog.setMessage("ex) '1997년 1월 1일'의 경우, '1997년 01월 01일'으로 입력하세요.");

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
                    alert.setTitle("생년월일을 제대로 입력해주세요.");
                    alert.show();
                } else {
                    if (pw.getText().toString().compareTo(pw2.getText().toString()) != 0) {
                        // 다이얼로그 바디
                        AlertDialog.Builder alertdialog = new AlertDialog.Builder(JoinActivity.this);
                        // 다이얼로그 메세지
                        alertdialog.setMessage("비밀번호를 다시 확인해주세요.");

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
                        birth = y.getText().toString() + "-" + m.getText().toString() + "-" + d.getText().toString();

                        String reqUser = email.getText().toString(); // 보내는 메시지를 받아옴
                        String reqPswd = pw.getText().toString();

                        JSONObject jsonObj = new JSONObject();
                        JSONObject userinfo = new JSONObject();

                        try {
                            // 여기에 name, value 추가하면 된다!!
                            userinfo.put("cmd", "adduserinfo");
                            userinfo.put("user", reqUser);
                            userinfo.put("pswd", reqPswd);
                            userinfo.put("name", name.getText().toString());
                            userinfo.put("nickname", nickname.getText().toString());
                            userinfo.put("birthday", birth);
                            jsonObj.put("userinfo", userinfo);

                            NetworkTask networkTask = new JoinActivity.NetworkTask(jsonObj);
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
            if (result_text.compareTo("add user info success") == 0) {
                // 다이얼로그 바디
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(JoinActivity.this);
                // 다이얼로그 메세지
                alertdialog.setMessage("로그인해주세요.");

                // 확인버튼
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                        ((StartActivity) StartActivity.musicContext).next = true;
                        startActivity(intent);
                        finish();
                    }
                });

                // 메인 다이얼로그 생성
                AlertDialog alert = alertdialog.create();
                // 타이틀
                alert.setTitle("계정이 생성되었습니다.");
                alert.show();
            } else if (result_text.compareTo("already added user") == 0) {
                // 다이얼로그 바디
                AlertDialog.Builder alertdialog2 = new AlertDialog.Builder(JoinActivity.this);
                // 다이얼로그 메세지
                alertdialog2.setMessage("계정을 다시 확인해주세요.");

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
                alert2.setTitle("이미 존재하는 계정입니다.");
                alert2.show();
            } else if (result_text.compareTo("already added nickname") == 0) {
                // 다이얼로그 바디
                AlertDialog.Builder alertdialog2 = new AlertDialog.Builder(JoinActivity.this);
                // 다이얼로그 메세지
                alertdialog2.setMessage("계정을 다시 확인해주세요.");

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
                alert2.setTitle("이미 존재하는 계정입니다.");
                alert2.show();
            } else {
                // 다이얼로그 바디
                AlertDialog.Builder alertdialog2 = new AlertDialog.Builder(JoinActivity.this);
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
                alert2.setTitle("계정을 생성할 수 없습니다.");
                alert2.show();
            }

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
//            result.setText(s);
            if (s != null) {
             //   Toast.makeText(JoinActivity.this, email.getText().toString() + " " + result_text, Toast.LENGTH_SHORT).show();
            } else {
           //     Toast.makeText(JoinActivity.this, "값이 없습니다", Toast.LENGTH_SHORT).show();
            }
        }
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
