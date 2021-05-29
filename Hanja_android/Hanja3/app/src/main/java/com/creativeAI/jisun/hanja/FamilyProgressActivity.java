package com.creativeAI.jisun.hanja;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class FamilyProgressActivity extends AppCompatActivity {
    ImageView back_Arrow;
    TextView menu_title;
    String tag, email;
    TextView _email, _nickname, _this_week_hanja, _last_week_hanja, _quiz_count, _answer_rate, _learning_hanja;
    ProgressBar study_amount;

    private static MediaPlayer buttonmusic;
    private SharedPreferences backgroundData;
    boolean _effect_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_progress);

        tag = "familyinfo";
        // 이전 엑티비티에서 회원 id받아오기
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");

        ((MainActivity) MainActivity.musicContext).next = false;
        // 설정값 불러오기
        backgroundData = getSharedPreferences("backgroundData", MODE_PRIVATE);
        _effect_sound = backgroundData.getBoolean("EFFECT_SOUND", true);

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

        _email = (TextView) findViewById(R.id.email);
        _nickname = (TextView) findViewById(R.id.nickname);
        _this_week_hanja = (TextView) findViewById(R.id.this_week_hanja);
        _last_week_hanja = (TextView) findViewById(R.id.last_week_hanja);
        _quiz_count = (TextView) findViewById(R.id.quiz_count);
        _answer_rate = (TextView) findViewById(R.id.answer_rate);
        _learning_hanja = (TextView) findViewById(R.id.learning_hanja2);
        study_amount = (ProgressBar) findViewById(R.id.progressBar4);

        back_Arrow = (ImageView) findViewById(R.id.backArrow);
        back_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

//                Intent intent = new Intent(FamilyProgressActivity.this, MyInformationActivity.class);
//                startActivity(intent);
//                finish();
                onBackPressed();
            }
        });

        menu_title = (TextView) findViewById(R.id.profileName);
        menu_title.setText("가족진도");
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
            try {
                String user, nickname, this_week_hanja, last_week_hanja, quiz_count, answer_rate, learning_hanja;
                JSONObject jObject = new JSONObject(s);
                JSONObject obj = jObject.getJSONObject("familyinfo");
                user = obj.getString("user");
                nickname = obj.getString("nickname");
                this_week_hanja = obj.getString("this_week_hanja");
                last_week_hanja = obj.getString("last_week_hanja");
                quiz_count = obj.getString("quiz_count");
                answer_rate = obj.getString("answer_rate");
                learning_hanja = obj.getString("learning_hanja");

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
