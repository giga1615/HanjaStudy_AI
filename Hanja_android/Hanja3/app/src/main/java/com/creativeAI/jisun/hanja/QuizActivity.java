package com.creativeAI.jisun.hanja;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class QuizActivity extends AppCompatActivity {
    Context mcontext;
    // 힌트를 위해 필요
    String hanja[] = new String[4];
    String hint_click;
    // 맞은 수, 틀린 수
    int correct_num = 0, incorrect_num = 0;
    ImageView back_Arrow, result_button, hint_button;
    TextView menu_title, quiz_text1, quiz_text2, quiz_text3, quiz_text4, answer_text1, answer_text2, answer_text3, quiz_num;
    String email, pw;
    int question_num = 1;
    String img_url[] = new String[10];
    String answer[] = new String[10];
    String ex1[] = new String[10];
    String ex2[] = new String[10];
    String ex3[] = new String[10];
    ImageView next_button;
    ImageButton quiz_op1, quiz_op2, quiz_op3, quiz_op4;
    // 퀴즈 푼 문제 카운트
    //int num = 1;
    // 답 textview번호
    int aa = 0;
    // 사용자가 고른 답 번호
    int a = 0;
    RelativeLayout correct_layout, incorrect_layout;

    ImageView imView;
    String tag, tag2, coin;
    Bitmap bmlmg;
    private static MediaPlayer buttonmusic, selectmusic, resultmusic;
//    back task;

    private SharedPreferences backgroundData;
    boolean _effect_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        mcontext = this;
        hint_click = "N";
        ((QuizStartActivity) QuizStartActivity.musicContext).next = false;

        // 이전 엑티비티에서 회원 id받아오기
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        pw = intent.getExtras().getString("pw");
        coin = intent.getExtras().getString("coin");

        tag = "quiz_list";
        tag2 = "quiz_end";
        imView = (ImageView) findViewById(R.id.imView);
        //task.execute(imgUrl);

        quiz_num = (TextView) findViewById(R.id.quiz_num);
        quiz_text1 = (TextView) findViewById(R.id.quiz_text1);
        quiz_text2 = (TextView) findViewById(R.id.quiz_text2);
        quiz_text3 = (TextView) findViewById(R.id.quiz_text3);
        quiz_text4 = (TextView) findViewById(R.id.quiz_text4);
        answer_text1 = (TextView) findViewById(R.id.answer_text);
        answer_text2 = (TextView) findViewById(R.id.answer_text2);
        answer_text3 = (TextView) findViewById(R.id.answer_text3);
        result_button = (ImageView) findViewById(R.id.result_button);
        hint_button = (ImageView) findViewById(R.id.hint_button);
        correct_layout = (RelativeLayout) findViewById(R.id.correct_layout);
        incorrect_layout = (RelativeLayout) findViewById(R.id.incorrect_layout);

        // 최 상단 레이아웃만 클릭 되도록
        correct_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        incorrect_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        if (hint_click.compareTo("N") == 0)
            hint_button.setVisibility(View.VISIBLE);

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

        next_button = (ImageView) findViewById(R.id.next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question_num != 0) {
                    buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                    buttonmusic.setLooping(false);
                    if (_effect_sound)
                        buttonmusic.start();

                    correct_layout.setVisibility(View.INVISIBLE);
                    incorrect_layout.setVisibility(View.INVISIBLE);
                    quiz_op1.setSelected(false);
                    quiz_op2.setSelected(false);
                    quiz_op3.setSelected(false);
                    quiz_op4.setSelected(false);
                    a = 0;
                    aa = 0;
                }
                if (question_num != 10) {
                    random_quiz(ex1[question_num], ex2[question_num], ex3[question_num], answer[question_num], img_url[question_num]);
                    question_num++;
                    quiz_num.setText(question_num + "/10");
                } else {
                    selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.finishmusic);
                    selectmusic.setLooping(false);
                    if (_effect_sound)
                        selectmusic.start();

                    try {
                        JSONObject jsonObj = new JSONObject();
                        JSONObject userinfo = new JSONObject();
                        // 여기에 name, value 추가하면 된다!!
                        userinfo.put("user", email);
                        userinfo.put("solved_problem", correct_num + "");
                        userinfo.put("use_hint", hint_click);
                        jsonObj.put("userinfo", userinfo);

                        NetworkTask2 networkTask3 = new NetworkTask2(jsonObj);
                        networkTask3.execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // 다이얼로그 바디
                    AlertDialog.Builder alertdialog2 = new AlertDialog.Builder(QuizActivity.this);
                    // 다이얼로그 메세지
                    alertdialog2.setMessage("정답 수 : " + correct_num + ", 오답 수 : " + incorrect_num + "\n확인을 누르면 메인화면으로 이동합니다.");

                    // 확인버튼
                    alertdialog2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //onBackPressed();
                            // intent로 넘기면 화면 전환 너무 느려..
                            Intent intent = new Intent(QuizActivity.this, MainActivity.class);
                            intent.putExtra("pw", pw);
                            intent.putExtra("email", email);
                            ((QuizStartActivity) QuizStartActivity.musicContext).next = false;
                            startActivity(intent);
                            finish();
                        }
                    });

                    // 메인 다이얼로그 생성
                    AlertDialog alert2 = alertdialog2.create();
                    // 타이틀
                    alert2.setTitle("퀴즈를 모두 풀으셨습니다.");
                    alert2.show();
                }
                next_button.setVisibility(View.INVISIBLE);
                answer_text3.setVisibility(View.INVISIBLE);
            }
        });

        hint_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                if (Integer.parseInt(coin) == 0) {
                    // 다이얼로그 바디
                    final AlertDialog.Builder alertdialog = new AlertDialog.Builder(QuizActivity.this);
                    // 다이얼로그 메세지
                    alertdialog.setMessage("코인이 없어 힌트를 사용할 수 없습니다.");

                    // 확인버튼
                    alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    // 메인 다이얼로그 생성
                    AlertDialog alert = alertdialog.create();
                    // 타이틀
                    alert.setTitle("힌트 쓸 수 없음");
                    alert.show();
                } else {
                    // 다이얼로그 바디
                    final AlertDialog.Builder alertdialog = new AlertDialog.Builder(QuizActivity.this);
                    // 다이얼로그 메세지
                    alertdialog.setMessage("3코인을 이용하여, 보기 한자의 뜻을 보시겠습니까? \n힌트는 한 번만 사용가능합니다.");

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
                            hint_click = "Y";
                            // 코인소모 후
                            quiz_text1.setText(hanja[0]);
                            quiz_text2.setText(hanja[1]);
                            quiz_text3.setText(hanja[2]);
                            quiz_text4.setText(hanja[3]);
                            hint_button.setVisibility(View.INVISIBLE);
                        }
                    });

                    // 메인 다이얼로그 생성
                    AlertDialog alert = alertdialog.create();
                    // 타이틀
                    alert.setTitle("힌트 보기");
                    alert.show();
                }
            }
        });

        back_Arrow = (ImageView) findViewById(R.id.backArrow);
        back_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                // 다이얼로그 바디
                final AlertDialog.Builder alertdialog = new AlertDialog.Builder(QuizActivity.this);
                // 다이얼로그 메세지
                alertdialog.setMessage("지금 나가시면, " + "\n지금까지의 퀴즈기록은 저장되지 않습니다.");

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
                        //onBackPressed();
                        Intent intent = new Intent(QuizActivity.this, QuizStartActivity.class);
                        intent.putExtra("pw", pw);
                        intent.putExtra("email", email);
                        ((QuizStartActivity) QuizStartActivity.musicContext).next = false;
                        startActivity(intent);
                        finish();
                    }
                });

                // 메인 다이얼로그 생성
                AlertDialog alert = alertdialog.create();
                // 타이틀
                alert.setTitle("정말 나가시겠습니까?");
                alert.show();
            }
        });

        menu_title = (TextView) findViewById(R.id.profileName);
        menu_title.setText("퀴즈");

        quiz_op1 = (ImageButton) findViewById(R.id.quiz_op1);
        quiz_op2 = (ImageButton) findViewById(R.id.quiz_op2);
        quiz_op3 = (ImageButton) findViewById(R.id.quiz_op3);
        quiz_op4 = (ImageButton) findViewById(R.id.quiz_op4);

        quiz_op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.selectmusic);
                selectmusic.setLooping(false);
                if (_effect_sound)
                    selectmusic.start();

                a = 1;
                quiz_op1.setSelected(true);
                quiz_op2.setSelected(false);
                quiz_op3.setSelected(false);
                quiz_op4.setSelected(false);
                result_button.setVisibility(View.VISIBLE);
            }
        });

        quiz_op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.selectmusic);
                selectmusic.setLooping(false);
                if (_effect_sound)
                    selectmusic.start();

                a = 2;
                quiz_op1.setSelected(false);
                quiz_op2.setSelected(true);
                quiz_op3.setSelected(false);
                quiz_op4.setSelected(false);
                result_button.setVisibility(View.VISIBLE);
            }
        });

        quiz_op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.selectmusic);
                selectmusic.setLooping(false);
                if (_effect_sound)
                    selectmusic.start();

                a = 3;
                quiz_op1.setSelected(false);
                quiz_op2.setSelected(false);
                quiz_op3.setSelected(true);
                quiz_op4.setSelected(false);
                result_button.setVisibility(View.VISIBLE);
            }
        });

        quiz_op4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.selectmusic);
                selectmusic.setLooping(false);
                if (_effect_sound)
                    selectmusic.start();

                a = 4;
                quiz_op1.setSelected(false);
                quiz_op2.setSelected(false);
                quiz_op3.setSelected(false);
                quiz_op4.setSelected(true);
                result_button.setVisibility(View.VISIBLE);
            }
        });

        result_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                answer_text3.setVisibility(View.VISIBLE);
                if (aa == a) {
                    //Toast.makeText(getApplicationContext(), "정답입니다.", Toast.LENGTH_SHORT).show();
                    correct_num++;
                    correct_layout.setVisibility(View.VISIBLE);
                    resultmusic = MediaPlayer.create(getApplicationContext(), R.raw.correctmusic);
                    resultmusic.setLooping(false);
                    if (_effect_sound)
                        resultmusic.start();
                } else {
                    //Toast.makeText(getApplicationContext(), "틀렸습니다. 정답은" + aa + "번 입니다.", Toast.LENGTH_SHORT).show();
                    incorrect_num++;
                    incorrect_layout.setVisibility(View.VISIBLE);
                    resultmusic = MediaPlayer.create(getApplicationContext(), R.raw.incorrectmusic);
                    resultmusic.setLooping(false);
                    if (_effect_sound)
                        resultmusic.start();
                }
                result_button.setVisibility(View.INVISIBLE);
                next_button.setVisibility(View.VISIBLE);
            }
        });
        quiz_num.setText(question_num + "/10");
    }

//    private class back extends AsyncTask<String, Integer, Bitmap> {
//
//        @Override
//        protected Bitmap doInBackground(String... urls) {
//            // TODO Auto-generated method stub
//            try {
//                URL myFileUrl = new URL(urls[0]);
//                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
//                conn.setDoInput(true);
//                conn.connect();
//
//                InputStream is = conn.getInputStream();
//                bmlmg = BitmapFactory.decodeStream(is);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return bmlmg;
//        }
//        protected void onPostExecute(Bitmap img) {
//            imView.setImageBitmap(bmlmg);
//        }
//    }

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
            switch (s) {
                case "not enough learning info":
                    // 다이얼로그 바디
                    final AlertDialog.Builder alertdialog = new AlertDialog.Builder(QuizActivity.this);
                    // 다이얼로그 메세지
                    alertdialog.setMessage("최소 10문제 이상 한자를 학습하셔야 \n퀴즈를 풀 수 있습니다.");

                    // 확인버튼
                    alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(QuizActivity.this, QuizStartActivity.class);
//                        startActivity(intent);
//                        finish();
                            //onBackPressed();
                            Intent intent = new Intent(QuizActivity.this, MainActivity.class);
                            intent.putExtra("pw", pw);
                            intent.putExtra("email", email);
                            ((QuizStartActivity) QuizStartActivity.musicContext).next = false;
                            startActivity(intent);
                            finish();
                        }
                    });

                    // 메인 다이얼로그 생성
                    AlertDialog alert = alertdialog.create();
                    // 타이틀
                    alert.setTitle("퀴즈를 풀 수 없습니다.");
                    alert.show();
                    break;
                default:
                    try {
                        JSONObject jObject = new JSONObject(s);
                        JSONObject obj = jObject.getJSONObject("quiz_list");
                        int length = obj.length();

                        //Toast.makeText(getApplication(), s + "", Toast.LENGTH_LONG).show();

                        JSONObject question = obj.getJSONObject("q1");
                        img_url[0] = question.getString("img_url");
                        answer[0] = question.getString("answer");
                        ex1[0] = question.getString("ex1");
                        ex2[0] = question.getString("ex2");
                        ex3[0] = question.getString("ex3");

                        JSONObject question2 = obj.getJSONObject("q2");
                        img_url[1] = question2.getString("img_url");
                        answer[1] = question2.getString("answer");
                        ex1[1] = question2.getString("ex1");
                        ex2[1] = question2.getString("ex2");
                        ex3[1] = question2.getString("ex3");

                        JSONObject question3 = obj.getJSONObject("q3");
                        img_url[2] = question3.getString("img_url");
                        answer[2] = question3.getString("answer");
                        ex1[2] = question3.getString("ex1");
                        ex2[2] = question3.getString("ex2");
                        ex3[2] = question3.getString("ex3");

                        JSONObject question4 = obj.getJSONObject("q4");
                        img_url[3] = question4.getString("img_url");
                        answer[3] = question4.getString("answer");
                        ex1[3] = question4.getString("ex1");
                        ex2[3] = question4.getString("ex2");
                        ex3[3] = question4.getString("ex3");

                        JSONObject question5 = obj.getJSONObject("q5");
                        img_url[4] = question5.getString("img_url");
                        answer[4] = question5.getString("answer");
                        ex1[4] = question5.getString("ex1");
                        ex2[4] = question5.getString("ex2");
                        ex3[4] = question5.getString("ex3");

                        JSONObject question6 = obj.getJSONObject("q6");
                        img_url[5] = question6.getString("img_url");
                        answer[5] = question6.getString("answer");
                        ex1[5] = question6.getString("ex1");
                        ex2[5] = question6.getString("ex2");
                        ex3[5] = question6.getString("ex3");

                        JSONObject question7 = obj.getJSONObject("q7");
                        img_url[6] = question7.getString("img_url");
                        answer[6] = question7.getString("answer");
                        ex1[6] = question7.getString("ex1");
                        ex2[6] = question7.getString("ex2");
                        ex3[6] = question7.getString("ex3");

                        JSONObject question8 = obj.getJSONObject("q8");
                        img_url[7] = question8.getString("img_url");
                        answer[7] = question8.getString("answer");
                        ex1[7] = question8.getString("ex1");
                        ex2[7] = question8.getString("ex2");
                        ex3[7] = question8.getString("ex3");

                        JSONObject question9 = obj.getJSONObject("q9");
                        img_url[8] = question9.getString("img_url");
                        answer[8] = question9.getString("answer");
                        ex1[8] = question9.getString("ex1");
                        ex2[8] = question9.getString("ex2");
                        ex3[8] = question9.getString("ex3");

                        JSONObject question10 = obj.getJSONObject("q10");
                        img_url[9] = question10.getString("img_url");
                        answer[9] = question10.getString("answer");
                        ex1[9] = question10.getString("ex1");
                        ex2[9] = question10.getString("ex2");
                        ex3[9] = question10.getString("ex3");

                        random_quiz(ex1[0], ex2[0], ex3[0], answer[0], img_url[0]);

//                        for (int num = 0; num < 10; num++) {
//                            JSONObject question = obj.getJSONObject("q" + num);
//                            img_url[num] = question.getString("img_url");
//                            answer[num] = question.getString("answer");
//                            ex1[num] = question.getString("ex1");
//                            ex2[num] = question.getString("ex2");
//                            ex3[num] = question.getString("ex3");
//                        }

//                        task = new back();
//                        task.execute(img_url);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    void random_quiz(String ex1, String ex2, String ex3, String answer, String img_url) {
        Picasso.with(mcontext)
                .load(img_url)                                 // 이미지 URL
                //.placeholder(R.drawable.sadmonkey)          // 이미지 로드 실패하면 띄울 이미지
                //.transform(PicassoTransformations.resizeTransformation)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into((ImageView) findViewById(R.id.imView));

        String question[] = new String[4];
        question[0] = ex1;
        question[1] = ex2;
        question[2] = ex3;
        question[3] = answer;

        int is_input[] = {0, 0, 0, 0};
        aa = 0;

        String tt[] = new String[4];
        int cnt = 0;
        while (cnt < 4) {
            int i = (int) (Math.random() * 4);
            if (is_input[i] == 0) {
                if (i == 3)
                    aa = cnt + 1;
                tt[cnt] = question[i];
                hanja[cnt] = question[i];
                is_input[i] = 1;
                cnt++;
            }
        }
        quiz_text1.setText(tt[0].charAt(0) + "");
        quiz_text2.setText(tt[1].charAt(0) + "");
        quiz_text3.setText(tt[2].charAt(0) + "");
        quiz_text4.setText(tt[3].charAt(0) + "");
        // Toast.makeText(getApplicationContext(), aa + "번이 정답", Toast.LENGTH_SHORT).show();
        answer_text1.setText(answer);
        answer_text2.setText(answer);
        answer_text3.setText(ex1 + "\n" + ex2 + "\n" + ex3);
    }

    public class NetworkTask2 extends AsyncTask<Void, Void, String> {

        private JSONObject jsonObj;

        public NetworkTask2(JSONObject jsonObj) {
            this.jsonObj = jsonObj;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection(tag2);
            result = requestHttpURLConnection.request(jsonObj); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplication(), "퀴즈결과 반영 OK", Toast.LENGTH_SHORT).show();
//            switch (tag2) {
//                case "quiz_end":
//                    if(s.compareTo("update answer_rate info success")==0)
//                    try {
//                        JSONObject jObject = new JSONObject(s);
//                        JSONObject obj = jObject.getJSONObject("userinfo");
//                        family_nickname = obj.getString("family_nickname");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        ((QuizStartActivity) QuizStartActivity.musicContext).next = false;
        back_Arrow.performClick();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //KeyguardManager islock = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        if (islock.inKeyguardRestrictedInputMode() && !pm.isInteractive())
        if (((QuizStartActivity) QuizStartActivity.musicContext).next == false)
            ((QuizStartActivity) QuizStartActivity.musicContext).stopMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (((QuizStartActivity) QuizStartActivity.musicContext).next == false)
            ((QuizStartActivity) QuizStartActivity.musicContext).stopMusic();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (((QuizStartActivity) QuizStartActivity.musicContext).next == false && ((MainActivity) MainActivity.musicContext)._background_sound) {
            ((QuizStartActivity) QuizStartActivity.musicContext).stopMusic();
            ((QuizStartActivity) QuizStartActivity.musicContext).startMusic();
        }
    }
}
