package com.creativeAI.jisun.hanja;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

public class VocabularyActivity extends AppCompatActivity {
    Context mcontext;
    ImageView back_Arrow, vt1, vt2, vt3, vt4, vt5, vt6, vt7, vt8, vt9, vt10;
    TextView menu_title;
    int theme_num;
    String theme, email, tag, pw;
    ImageView v1, v2, v3, v4, v5, v6, v7, v8, v9, v10;
    String img1 = "null", img2 = "null", img3 = "null", img4 = "null", img5 = "null", img6 = "null", img7 = "null", img8 = "null", img9 = "null", img10 = "null";
    //    String imgUrl = "http://175.123.138.125:8070/learning/aaaaa@email.com/t1_v1.jpg";
//    Bitmap bmlmg;
//    back task;
    private SharedPreferences backgroundData;
    boolean _background_sound, _effect_sound;

    // 학습한 적 있는지 알려주는 변수
    Boolean isLearning;

    Handler handler = new Handler();
    Intent studyIntent;
    public static Context musicContext;
    // 다음 엑티비티에서 음악 안 꺼지게 하기 위해 꼭 필요!!
    public Boolean next;

    private static MediaPlayer buttonmusic, selectmusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        isLearning = false;

        mcontext = this;
        tag = "learning_list";
        //task = new back();
        //task.execute(imgUrl);
        // 이전 엑티비티에서 클릭한 테마 번호 받아와서 title설정
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        pw = intent.getExtras().getString("pw");
        theme_num = intent.getExtras().getInt("theme_number");  //*int형(https://coding-factory.tistory.com/203)
        theme = "t" + (5 - theme_num);
       // Toast.makeText(getApplicationContext(), theme_num + "", Toast.LENGTH_SHORT).show();   // 테마 id확인하려고 임시로 써놓음

        studyIntent = new Intent(getApplicationContext(), StartService.class);
        studyIntent.putExtra("where", "학습");
        musicContext = this;
        next = false;

        // 설정값 불러오기
        backgroundData = getSharedPreferences("backgroundData", MODE_PRIVATE);
        _background_sound = backgroundData.getBoolean("BACKGROUND_SOUND", true);
        _effect_sound = backgroundData.getBoolean("EFFECT_SOUND", true);

        if (_background_sound)
            try {
                startMusic();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "어플을 다시 실행 해 주세요.", Toast.LENGTH_SHORT).show();
            }

        //task = new back();
        JSONObject jsonObj = new JSONObject();
        JSONObject userinfo = new JSONObject();

        try {
            // 여기에 name, value 추가하면 된다!!
            userinfo.put("user", email);
            userinfo.put("theme", theme);
            jsonObj.put("userinfo", userinfo);

            NetworkTask networkTask = new NetworkTask(jsonObj);
            networkTask.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        back_Arrow = (ImageView) findViewById(R.id.backArrow);
        back_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                next = false;
                Intent intent = new Intent(VocabularyActivity.this, MainActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("pw", pw);
                ((MainActivity) MainActivity.musicContext).finish();
                startActivity(intent);
                finish();
                //onBackPressed();
            }
        });

        menu_title = (TextView) findViewById(R.id.profileName);
        vt1 = (ImageView) findViewById(R.id.vt1);
        vt2 = (ImageView) findViewById(R.id.vt2);
        vt3 = (ImageView) findViewById(R.id.vt3);
        vt4 = (ImageView) findViewById(R.id.vt4);
        vt5 = (ImageView) findViewById(R.id.vt5);
        vt6 = (ImageView) findViewById(R.id.vt6);
        vt7 = (ImageView) findViewById(R.id.vt7);
        vt8 = (ImageView) findViewById(R.id.vt8);
        vt9 = (ImageView) findViewById(R.id.vt9);
        vt10 = (ImageView) findViewById(R.id.vt10);


        switch (theme_num) {
            case 4:
                menu_title.setText("단어장 : 신체");
                vt1.setImageResource(R.drawable.v1);
                vt2.setImageResource(R.drawable.v2);
                vt3.setImageResource(R.drawable.v3);
                vt4.setImageResource(R.drawable.v4);
                vt5.setImageResource(R.drawable.v5);
                vt6.setImageResource(R.drawable.v6);
                vt7.setImageResource(R.drawable.v7);
                vt8.setImageResource(R.drawable.v8);
                vt9.setImageResource(R.drawable.v9);
                vt10.setImageResource(R.drawable.v10);
                break;
            case 3:
                menu_title.setText("단어장 : 동물원");
                vt1.setImageResource(R.drawable.v11);
                vt2.setImageResource(R.drawable.v12);
                vt3.setImageResource(R.drawable.v13);
                vt4.setImageResource(R.drawable.v14);
                vt5.setImageResource(R.drawable.v15);
                vt6.setImageResource(R.drawable.v16);
                vt7.setImageResource(R.drawable.v17);
                vt8.setImageResource(R.drawable.v18);
                vt9.setImageResource(R.drawable.v19);
                vt10.setImageResource(R.drawable.v20);
                break;
            case 2:
                menu_title.setText("단어장 : 자연");
                vt1.setImageResource(R.drawable.v21);
                vt2.setImageResource(R.drawable.v22);
                vt3.setImageResource(R.drawable.v23);
                vt4.setImageResource(R.drawable.v24);
                vt5.setImageResource(R.drawable.v25);
                vt6.setImageResource(R.drawable.v26);
                vt7.setImageResource(R.drawable.v27);
                vt8.setImageResource(R.drawable.v28);
                vt9.setImageResource(R.drawable.v29);
                vt10.setImageResource(R.drawable.v30);
                break;
            case 1:
                menu_title.setText("단어장 : 주방");
                vt1.setImageResource(R.drawable.v31);
                vt2.setImageResource(R.drawable.v32);
                vt3.setImageResource(R.drawable.v33);
                vt4.setImageResource(R.drawable.v34);
                vt5.setImageResource(R.drawable.v35);
                vt6.setImageResource(R.drawable.v36);
                vt7.setImageResource(R.drawable.v37);
                vt8.setImageResource(R.drawable.v38);
                vt9.setImageResource(R.drawable.v39);
                vt10.setImageResource(R.drawable.v40);
                break;
        }

        v1 = (ImageView) findViewById(R.id.v1);
        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.selectmusic);
                selectmusic.setLooping(false);
                if (_effect_sound)
                    selectmusic.start();

                if (img1.compareTo("null") == 0) {
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v1");
                    next = true;
                    startActivity(intent);
                } else {
                    isLearning = true;
                    Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v1");
                    intent.putExtra("url", img1);
                    next = true;
                    startActivity(intent);
                }
            }
        });
        v2 = (ImageView) findViewById(R.id.v2);
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.selectmusic);
                selectmusic.setLooping(false);
                if (_effect_sound)
                    selectmusic.start();

                if (img2.compareTo("null") == 0) {
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v2");
                    next = true;
                    startActivity(intent);
                } else {
                    isLearning = true;
                    Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v2");
                    intent.putExtra("url", img2);
                    next = true;
                    startActivity(intent);
                }
            }
        });
        v3 = (ImageView) findViewById(R.id.v3);
        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.selectmusic);
                selectmusic.setLooping(false);
                if (_effect_sound)
                    selectmusic.start();
                if (img3.compareTo("null") == 0) {
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v3");
                    next = true;
                    startActivity(intent);
                } else {
                    isLearning = true;
                    Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v3");
                    intent.putExtra("url", img3);
                    next = true;
                    startActivity(intent);
                }
            }
        });
        v4 = (ImageView) findViewById(R.id.v4);
        v4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.selectmusic);
                selectmusic.setLooping(false);
                if (_effect_sound)
                    selectmusic.start();
                if (img4.compareTo("null") == 0) {
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v4");
                    next = true;
                    startActivity(intent);
                } else {
                    isLearning = true;
                    Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v4");
                    intent.putExtra("url", img4);
                    next = true;
                    startActivity(intent);
                }
            }
        });
        v5 = (ImageView) findViewById(R.id.v5);
        v5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.selectmusic);
                selectmusic.setLooping(false);
                if (_effect_sound)
                    selectmusic.start();
                if (img5.compareTo("null") == 0) {
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v5");
                    next = true;
                    startActivity(intent);
                } else {
                    isLearning = true;
                    Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v5");
                    intent.putExtra("url", img5);
                    next = true;
                    startActivity(intent);
                }
            }
        });
        v6 = (ImageView) findViewById(R.id.v6);
        v6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.selectmusic);
                selectmusic.setLooping(false);
                if (_effect_sound)
                    selectmusic.start();
                if (img6.compareTo("null") == 0) {
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v6");
                    next = true;
                    startActivity(intent);
                } else {
                    isLearning = true;
                    Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v6");
                    intent.putExtra("url", img6);
                    next = true;
                    startActivity(intent);
                }
            }
        });
        v7 = (ImageView) findViewById(R.id.v7);
        v7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.selectmusic);
                selectmusic.setLooping(false);
                if (_effect_sound)
                    selectmusic.start();
                if (img7.compareTo("null") == 0) {
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v7");
                    next = true;
                    startActivity(intent);
                } else {
                    isLearning = true;
                    Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v7");
                    intent.putExtra("url", img7);
                    next = true;
                    startActivity(intent);
                }
            }
        });
        v8 = (ImageView) findViewById(R.id.v8);
        v8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.selectmusic);
                selectmusic.setLooping(false);
                if (_effect_sound)
                    selectmusic.start();
                if (img8.compareTo("null") == 0) {
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v8");
                    next = true;
                    startActivity(intent);
                } else {
                    isLearning = true;
                    Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v8");
                    intent.putExtra("url", img8);
                    next = true;
                    startActivity(intent);
                }
            }
        });
        v9 = (ImageView) findViewById(R.id.v9);
        v9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.selectmusic);
                selectmusic.setLooping(false);
                if (_effect_sound)
                    selectmusic.start();
                if (img9.compareTo("null") == 0) {
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v9");
                    next = true;
                    startActivity(intent);
                } else {
                    isLearning = true;
                    Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v9");
                    intent.putExtra("url", img9);
                    next = true;
                    startActivity(intent);
                }
            }
        });
        v10 = (ImageView) findViewById(R.id.v10);
        v10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectmusic = MediaPlayer.create(getApplicationContext(), R.raw.selectmusic);
                selectmusic.setLooping(false);
                if (_effect_sound)
                    selectmusic.start();
                if (img10.compareTo("null") == 0) {
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v10");
                    next = true;
                    startActivity(intent);
                } else {
                    isLearning = true;
                    Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", "v10");
                    intent.putExtra("url", img10);
                    next = true;
                    startActivity(intent);
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

            try {
                JSONObject jObject = new JSONObject(s);
                JSONObject obj = jObject.getJSONObject("learninginfo");
                img1 = obj.getString("img1");
                img2 = obj.getString("img2");
                img3 = obj.getString("img3");
                img4 = obj.getString("img4");
                img5 = obj.getString("img5");
                img6 = obj.getString("img6");
                img7 = obj.getString("img7");
                img8 = obj.getString("img8");
                img9 = obj.getString("img9");
                img10 = obj.getString("img10");

                PicassoTransformations.targetWidth = 2000;
                if (img1.compareTo("null") != 0) {
                    Picasso.with(mcontext)
                            .load(img1)                                 // 이미지 URL
                            .placeholder(R.drawable.loading_monkey)          // 이미지 로드 실패하면 띄울 이미지
                            //.transform(PicassoTransformations.resizeTransformation)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                            .into((ImageView) findViewById(R.id.v1));
                    v1.setImageAlpha(160);
                }
                if (img2.compareTo("null") != 0) {
                    Picasso.with(mcontext)
                            .load(img2)                                 // 이미지 URL
                            .placeholder(R.drawable.loading_monkey)          // 이미지 로드 실패하면 띄울 이미지
                            //.transform(PicassoTransformations.resizeTransformation)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                            .into((ImageView) findViewById(R.id.v2));
                    v2.setImageAlpha(160);
                }
                if (img3.compareTo("null") != 0) {
                    Picasso.with(mcontext)
                            .load(img3)                                 // 이미지 URL
                            .placeholder(R.drawable.loading_monkey)          // 이미지 로드 실패하면 띄울 이미지
                            //.transform(PicassoTransformations.resizeTransformation)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                            .into((ImageView) findViewById(R.id.v3));
                    v3.setImageAlpha(160);
                }
                if (img4.compareTo("null") != 0) {
                    Picasso.with(mcontext)
                            .load(img4)                                 // 이미지 URL
                            .placeholder(R.drawable.loading_monkey)          // 이미지 로드 실패하면 띄울 이미지
                            //.transform(PicassoTransformations.resizeTransformation)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                            .into((ImageView) findViewById(R.id.v4));
                    v4.setImageAlpha(160);
                }
                if (img5.compareTo("null") != 0) {
                    Picasso.with(mcontext)
                            .load(img5)                                 // 이미지 URL
                            .placeholder(R.drawable.loading_monkey)          // 이미지 로드 실패하면 띄울 이미지
                            //.transform(PicassoTransformations.resizeTransformation)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                            .into((ImageView) findViewById(R.id.v5));
                    v5.setImageAlpha(160);
                }
                if (img6.compareTo("null") != 0) {
                    Picasso.with(mcontext)
                            .load(img6)                                 // 이미지 URL
                            .placeholder(R.drawable.loading_monkey)          // 이미지 로드 실패하면 띄울 이미지
                            //.transform(PicassoTransformations.resizeTransformation)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                            .into((ImageView) findViewById(R.id.v6));
                    v6.setImageAlpha(160);
                }
                if (img7.compareTo("null") != 0) {
                    Picasso.with(mcontext)
                            .load(img7)                                 // 이미지 URL
                            .placeholder(R.drawable.loading_monkey)          // 이미지 로드 실패하면 띄울 이미지
                            // .transform(PicassoTransformations.resizeTransformation)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                            .into((ImageView) findViewById(R.id.v7));
                    v7.setImageAlpha(160);
                }
                if (img8.compareTo("null") != 0) {
                    Picasso.with(mcontext)
                            .load(img8)                                 // 이미지 URL
                            .placeholder(R.drawable.loading_monkey)          // 이미지 로드 실패하면 띄울 이미지
                            //.transform(PicassoTransformations.resizeTransformation)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                            .into((ImageView) findViewById(R.id.v8));
                    v8.setImageAlpha(160);
                }
                if (img9.compareTo("null") != 0) {
                    Picasso.with(mcontext)
                            .load(img9)                                 // 이미지 URL
                            .placeholder(R.drawable.loading_monkey)          // 이미지 로드 실패하면 띄울 이미지
                            // .transform(PicassoTransformations.resizeTransformation)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                            .into((ImageView) findViewById(R.id.v9));
                    v9.setImageAlpha(160);
                }
                if (img10.compareTo("null") != 0) {
                    Picasso.with(mcontext)
                            .load(img10)                                 // 이미지 URL
                            .placeholder(R.drawable.loading_monkey)          // 이미지 로드 실패하면 띄울 이미지
                            //.transform(PicassoTransformations.resizeTransformation)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                            .into((ImageView) findViewById(R.id.v10));
                    v10.setImageAlpha(160);
                }
//                    v10.setImageAlpha(160);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
//            result.setText(s);
            if (s != null) {
           //     Toast.makeText(VocabularyActivity.this, "" + email, Toast.LENGTH_SHORT).show();
            } else {
           //     Toast.makeText(VocabularyActivity.this, "값이 없습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class PicassoTransformations {

        public static int targetWidth = 1600;

        public static Transformation resizeTransformation = new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    source.recycle();
                }
                return result;
            }

            @Override
            public String key() {
                return "resizeTransformation#" + System.currentTimeMillis();
            }
        };
    }

    void startMusic() {
        startService(studyIntent);
    }

    void stopMusic() {
        stopService(studyIntent);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //KeyguardManager islock = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        if (islock.inKeyguardRestrictedInputMode() && !pm.isInteractive())
        if (next == false)
            stopService(studyIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (next == false)
            stopService(studyIntent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (next == false && _background_sound) {
            stopService(studyIntent);
            startService(studyIntent);
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        back_Arrow.performClick();
    }
}