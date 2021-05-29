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

public class WithdrawActivity extends AppCompatActivity {
    ImageView back_Arrow, withdrawal_btn;
    TextView menu_title, _email, _nickname;
    String tag, email, nickname, result_text;
    EditText pw;

    private static MediaPlayer buttonmusic;
    private SharedPreferences backgroundData;
    boolean _effect_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        tag = "withdrawal";
        // 이전 엑티비티에서 회원 id받아오기
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        nickname = intent.getExtras().getString("nickname");
        ((MainActivity) MainActivity.musicContext).next = false;
        // 설정값 불러오기
        backgroundData = getSharedPreferences("backgroundData", MODE_PRIVATE);
        _effect_sound = backgroundData.getBoolean("EFFECT_SOUND", true);

        back_Arrow = (ImageView) findViewById(R.id.backArrow);
        back_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

//                Intent intent = new Intent(MyInformationActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
                onBackPressed();
            }
        });

        menu_title = (TextView) findViewById(R.id.profileName);
        menu_title.setText("회원탈퇴");
        _email = (TextView) findViewById(R.id.email);
        _email.setText(email);
        _nickname = (TextView) findViewById(R.id.nickname);
        _nickname.setText(nickname);
        pw = (EditText) findViewById(R.id.now_pw2);

        withdrawal_btn = (ImageView) findViewById(R.id.withdrawal_button);
        withdrawal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                // 비밀번호를 입력하지 않은 경우
                if (TextUtils.isEmpty(pw.getText())) {
                    //다이얼로그 바디
                    final AlertDialog.Builder alertdialog = new AlertDialog.Builder(WithdrawActivity.this);
                    // 다이얼로그 메세지
                    alertdialog.setMessage("비밀번호를 입력해주세요.");

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
                    alert.setTitle("비밀번호를 입력하지 않았습니다.");
                    alert.show();
                } else {
                    //다이얼로그 바디
                    final AlertDialog.Builder alertdialog = new AlertDialog.Builder(WithdrawActivity.this);
                    // 다이얼로그 메세지
                    alertdialog.setMessage("정말 계정을 삭제하시겠습니까?");

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
                                userinfo.put("pswd", pw.getText().toString());
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
                    alert.setTitle("계정 삭제");
                    alert.show();
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
            result_text = s;

            if (result_text.compareTo("delete user info success") == 0) {
                // 다이얼로그 바디
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(WithdrawActivity.this);
                // 다이얼로그 메세지
                alertdialog.setMessage("시작화면으로 이동합니다.");

                // 확인버튼
                alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(WithdrawActivity.this, StartActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                // 메인 다이얼로그 생성
                AlertDialog alert = alertdialog.create();
                // 타이틀
                alert.setTitle("계정이 삭제되었습니다.");
                alert.show();
            } else if (result_text.compareTo("non-added member") == 0) {
                // 다이얼로그 바디
                AlertDialog.Builder alertdialog2 = new AlertDialog.Builder(WithdrawActivity.this);
                // 다이얼로그 메세지
                alertdialog2.setMessage("비밀번호를 다시 확인해주세요.");

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
                alert2.setTitle("비밀번호가 틀렸습니다.");
                alert2.show();
            }


            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
//            result.setText(s);
            if (s != null) {
            //    Toast.makeText(WithdrawActivity.this, result_text + "", Toast.LENGTH_SHORT).show();
            } else {
            //    Toast.makeText(WithdrawActivity.this, "값이 없습니다", Toast.LENGTH_SHORT).show();
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
        if (((MainActivity) MainActivity.musicContext).next == false&& ((MainActivity) MainActivity.musicContext)._background_sound) {
            ((MainActivity) MainActivity.musicContext).stopMusic();
            ((MainActivity) MainActivity.musicContext).startMusic();
        }
    }
}
