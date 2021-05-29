package com.creativeAI.jisun.hanja;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class DrawActivity extends AppCompatActivity {
    //그리기 뷰 전역 변수.
    private DrawLine drawLine = null;
    ImageView clean_button;
    Uri imageURI, albumURI;
    String url;
    ImageView photo, hanja, recapture_button, save_button;
    String email, theme, voca_place;
    String mCurrentPhotoPath, imageFileName;
    String cmd = null;
    TextView menu_title;
    ImageView back_Arrow;
    Context mcontext;

    ImageView yes_button, no_button, ok_button;
    TextView explain, theme_voca;
    RelativeLayout save_image;
    private static MediaPlayer buttonmusic;
    private SharedPreferences backgroundData;
    boolean _effect_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mcontext = this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        setContentView(R.layout.activity_draw);
        menu_title = (TextView) findViewById(R.id.profileName);
        menu_title.setText("한자 따라쓰기");
        explain = (TextView) findViewById(R.id.explain);
        theme_voca = (TextView) findViewById(R.id.theme_voca);
        save_image = (RelativeLayout) findViewById(R.id.save_image);

        // 최 상단 레이아웃만 클릭 되도록
        save_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        ((VocabularyActivity) VocabularyActivity.musicContext).next = false;
        // 설정값 불러오기
        backgroundData = getSharedPreferences("backgroundData", MODE_PRIVATE);
        _effect_sound = backgroundData.getBoolean("EFFECT_SOUND", true);

        clean_button = (ImageView) findViewById(R.id.clean_button);
        clean_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();
                ((VocabularyActivity) VocabularyActivity.musicContext).next = true;
                //    LinearLayout llCanvas = (LinearLayout) findViewById(R.id.llCanvas);
                //    llCanvas.removeAllViews();
                //    onWindowFocusChanged(false);
                recreate();
            }
        });

      /*  yes_button = (ImageView) findViewById(R.id.yes_button);
        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrawActivity.this, VocabularyActivity.class);
                intent.putExtra("send_tag", "learning_list");
                intent.putExtra("email", email);
                intent.putExtra("theme_num", theme);
                intent.putExtra("voca_place", voca_place);
                startActivity(intent);
                finish();
            }
        });

        no_button = (ImageView) findViewById(R.id.no_button);
        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrawActivity.this, MainActivity.class);
                window_close2.performClick();
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            }
        }); */

        ok_button = (ImageView) findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();
                Intent intent = new Intent(DrawActivity.this, MainActivity.class);
                //  save_image.setVisibility(View.INVISIBLE);
                //   ok_button.setVisibility(View.INVISIBLE);
                //   explain.setVisibility(View.INVISIBLE);
                intent.putExtra("email", email);
                ((VocabularyActivity) VocabularyActivity.musicContext).next = false;
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        theme = intent.getExtras().getString("theme_num");
        voca_place = intent.getExtras().getString("voca_place");
        if (((VocabularyActivity) VocabularyActivity.musicContext).isLearning == false) {
            mCurrentPhotoPath = intent.getExtras().getString("mCurrentPhotoPath");
            imageFileName = intent.getExtras().getString("imageFileName");
            albumURI = intent.getParcelableExtra("albumURI");
            imageURI = intent.getParcelableExtra("imageURI");
        } else {
            url = intent.getExtras().getString("url");
        }
        photo = (ImageView) findViewById(R.id.photo);
        hanja = (ImageView) findViewById(R.id.hanja);

        back_Arrow = (ImageView) findViewById(R.id.backArrow);
        back_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();
//                Intent intent = new Intent(CameraActivity.this, VocabularyActivity.class);
//                startActivity(intent);
//                finish();
                onBackPressed();
            }
        });

//        if(((VocabularyActivity)VocabularyActivity.musicContext).isLearning==true) {
//            Picasso.with(mcontext)
//                    .load(url)                                 // 이미지 URL
//                    //.placeholder(R.drawable.loading_monkey)          // 이미지 로드 실패하면 띄울 이미지
//                    //.transform(PicassoTransformations.resizeTransformation)
//                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
//                    .into((ImageView) findViewById(R.id.hanja));
//        }

        recapture_button = (ImageView) findViewById(R.id.recapture_button);
        recapture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();
                ((VocabularyActivity) VocabularyActivity.musicContext).next = true;
                Intent intent = new Intent(DrawActivity.this, CameraActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("theme_num", theme);
                intent.putExtra("voca_place", voca_place);
                ((VocabularyActivity) VocabularyActivity.musicContext).isLearning = false;
                startActivity(intent);
                finish();
                //onBackPressed();
            }

        });
        save_button = (ImageView) findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();
                cmd = "save";
                int idx = mCurrentPhotoPath.indexOf("/JPEG");
                String Pathcut = mCurrentPhotoPath.substring(0, idx);

                String img_path = Pathcut;
                String img_name = imageFileName;
                NetworkTask networkTask = new NetworkTask(cmd, email, theme, voca_place, img_path, img_name);
                networkTask.execute();
            }
        });

        if (((VocabularyActivity) VocabularyActivity.musicContext).isLearning == true) {
            //recapture_button.setVisibility(View.INVISIBLE);
            save_button.setVisibility(View.INVISIBLE);
        }

        if (imageURI != null) {
            photo.setImageURI(imageURI);
        } else if (albumURI != null) {
            photo.setImageURI(albumURI);
        } else {
            Picasso.with(mcontext)
                    .load(url)                                 // 이미지 URL
                    //.placeholder(R.drawable.loading_monkey)          // 이미지 로드 실패하면 띄울 이미지
                    //.transform(PicassoTransformations.resizeTransformation)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into((ImageView) findViewById(R.id.photo));
            //Toast.makeText(getApplication(), "전송된 사진없음", Toast.LENGTH_LONG).show();
        }

        switch (theme) {
            case "t1":
                switch (voca_place) {
                    case "v1":
                        hanja.setImageResource(R.drawable.t1_v1);
                        break;
                    case "v2":
                        hanja.setImageResource(R.drawable.t1_v2);
                        break;
                    case "v3":
                        hanja.setImageResource(R.drawable.t1_v3);
                        break;
                    case "v4":
                        hanja.setImageResource(R.drawable.t1_v4);
                        break;
                    case "v5":
                        hanja.setImageResource(R.drawable.t1_v5);
                        break;
                    case "v6":
                        hanja.setImageResource(R.drawable.t1_v6);
                        break;
                    case "v7":
                        hanja.setImageResource(R.drawable.t1_v7);
                        break;
                    case "v8":
                        hanja.setImageResource(R.drawable.t1_v8);
                        break;
                    case "v9":
                        hanja.setImageResource(R.drawable.t1_v9);
                        break;
                    case "v10":
                        hanja.setImageResource(R.drawable.t1_v10);
                        break;
                }
                break;

            case "t2":
                switch (voca_place) {
                    case "v1":
                        hanja.setImageResource(R.drawable.t2_v1);
                        break;
                    case "v2":
                        hanja.setImageResource(R.drawable.t2_v2);
                        break;
                    case "v3":
                        hanja.setImageResource(R.drawable.t2_v3);
                        break;
                    case "v4":
                        hanja.setImageResource(R.drawable.t2_v4);
                        break;
                    case "v5":
                        hanja.setImageResource(R.drawable.t2_v5);
                        break;
                    case "v6":
                        hanja.setImageResource(R.drawable.t2_v6);
                        break;
                    case "v7":
                        hanja.setImageResource(R.drawable.t2_v7);
                        break;
                    case "v8":
                        hanja.setImageResource(R.drawable.t2_v8);
                        break;
                    case "v9":
                        hanja.setImageResource(R.drawable.t2_v9);
                        break;
                    case "v10":
                        hanja.setImageResource(R.drawable.t2_v10);
                        break;
                }
                break;

            case "t3":
                switch (voca_place) {
                    case "v1":
                        hanja.setImageResource(R.drawable.t3_v1);
                        break;
                    case "v2":
                        hanja.setImageResource(R.drawable.t3_v2);
                        break;
                    case "v3":
                        hanja.setImageResource(R.drawable.t3_v3);
                        break;
                    case "v4":
                        hanja.setImageResource(R.drawable.t3_v4);
                        break;
                    case "v5":
                        hanja.setImageResource(R.drawable.t3_v5);
                        break;
                    case "v6":
                        hanja.setImageResource(R.drawable.t3_v6);
                        break;
                    case "v7":
                        hanja.setImageResource(R.drawable.t3_v7);
                        break;
                    case "v8":
                        hanja.setImageResource(R.drawable.t3_v8);
                        break;
                    case "v9":
                        hanja.setImageResource(R.drawable.t3_v9);
                        break;
                    case "v10":
                        hanja.setImageResource(R.drawable.t3_v10);
                        break;
                }
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        //hasFocus : 앱이 화면에 보여졌을때 true로 설정되어 호출됨.
        //만약 그리기 뷰 전역변수에 값이 없을경우 전역변수를 초기화 시킴.
        if (hasFocus && drawLine == null) {
            //그리기 뷰가 보여질(나타날) 레이아웃 찾기..
            LinearLayout llCanvas = (LinearLayout) findViewById(R.id.llCanvas);
            if (llCanvas != null) //그리기 뷰가 보여질 레이아웃이 있으면...
            {
                //그리기 뷰 레이아웃의 넓이와 높이를 찾아서 Rect 변수 생성.
                Rect rect = new Rect(0, 0,
                        llCanvas.getMeasuredWidth(), llCanvas.getMeasuredHeight());

                //그리기 뷰 초기화..
                drawLine = new DrawLine(this, rect);

                //그리기 뷰를 그리기 뷰 레이아웃에 넣기 -- 이렇게 하면 그리기 뷰가 화면에 보여지게 됨.
                llCanvas.addView(drawLine);
            }

            //이건.. 상단 메뉴(RED, BLUE ~~~)버튼 설정...
            //일단 초기값은 0번(RED)으로.. ^^
            resetCurrentMode(0);
        }

        super.onWindowFocusChanged(hasFocus);
    }

    //코딩 하기 쉽게 하기 위해서.. 사용할 상단 메뉴 버튼들의 아이디를 배열에 넣는다..
    private int[] btns = {R.id.btnRED, R.id.btnBLUE, R.id.btnGREEN, R.id.btnWHITE};
    //코딩 하기 쉽게 하기 위해서.. 상단 메뉴 버튼의 배열과 똑같이 실제 색상값을 배열로 만든다.
    private int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.WHITE};

    //선택한 색상에 맞도록 버튼의 배경색과 글자색을 바꾸고, 그리기 뷰에도 알려 준다.
    private void resetCurrentMode(int curMode) {
        for (int i = 0; i < btns.length; i++) {
            //이건.. 배열 뒤지면서... 버튼이 있는지 체크..
            ImageView btn = (ImageView) findViewById(btns[i]);
            if (btn != null) {
                //버튼 있으면 배경색과 글자색 변경..
                //만약 선택한 버튼값과 찾은 버튼이 동일하면 회색배경에 흰색글자 버튼으로 변경.
                //동일하지 않으면 흰색배경에 회색글자 버튼으로 변경.
                 btn.setBackgroundColor(i == curMode ? 0xff555555 : 0xffffffff);
                //btn.setTextColor(i == curMode ? 0xffffffff : 0xff555555);
            }
        }

        //만약 그리기 뷰가 초기화 되었으면, 그리기 뷰에 글자색을 알려줌..
        if (drawLine != null) drawLine.setLineColor(colors[curMode]);
    }

    //버튼을 클릭했을때 호출 되는 함수.
    //이 함수가 호출될때 어떤 버튼(뷰)에서 호출했는지를 같이 알려준다.
    //버튼 클릭시 이 함수를 호출 하게 하기 위해서는...
    //main.xml에서
    //<Button ~~~~ android:onClick="btnClick" ~~~~ />
    //이렇게 btnClick이라는 함수명을 넣어 줘야함.
    public void btnClick(View view) {
        buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
        buttonmusic.setLooping(false);
        if (_effect_sound)
            buttonmusic.start();
        if (view == null) return;

        for (int i = 0; i < btns.length; i++) {
            //배열 뒤지면서 클릭한 버튼이 있는지 확인..
            if (btns[i] == view.getId()) {
                //만약 선택한 버튼이 있으면.. 버튼모양 및 그리기 뷰 설정을 하기 위해서 함수 호출..
                resetCurrentMode(i);

                //더이상 처리를 할 필요가 없으니까.. for문을 빠져 나옴..
                break;
            }
        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String cmd;
        private String user;
        private String theme;
        private String word;
        private String img_path;
        private String img_name;

        public NetworkTask(String cmd, String user, String theme, String word, String img_path, String img_name) {

            this.cmd = cmd;
            this.user = user;
            this.theme = theme;
            this.word = word;
            this.img_path = img_path;
            this.img_name = img_name;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnectionImg requestHttpURLConnection = new RequestHttpURLConnectionImg();
            result = requestHttpURLConnection.request(cmd, user, theme, word, img_path, img_name);

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s) {
                case "save image success":
                 //   Toast.makeText(getApplication(), "사진이 서버에 저장되었습니다.", Toast.LENGTH_LONG).show();
                    //explain.setText("이 사진이 '"+theme_voca.getText().toString() + "'이 맞습니까?");
                    explain.setText("단어 학습이 완료되었습니다!");
                    explain.setVisibility(View.VISIBLE);
                    save_image.setVisibility(View.VISIBLE);
                    // yes_button.setVisibility(View.VISIBLE);
                    // no_button.setVisibility(View.VISIBLE);
                    ok_button.setVisibility(View.VISIBLE);
                    break;
                case "image is null":
                 //   Toast.makeText(getApplication(), "사진이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                    break;
            }
         //   Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ((VocabularyActivity) VocabularyActivity.musicContext).next = false;

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //KeyguardManager islock = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        if (islock.inKeyguardRestrictedInputMode() && !pm.isInteractive())
        if (((VocabularyActivity) VocabularyActivity.musicContext).next == false)
            ((VocabularyActivity) VocabularyActivity.musicContext).stopMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (((VocabularyActivity) VocabularyActivity.musicContext).next == false)
            ((VocabularyActivity) VocabularyActivity.musicContext).stopMusic();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (((VocabularyActivity) VocabularyActivity.musicContext).next == false && ((MainActivity) MainActivity.musicContext)._background_sound) {
            ((VocabularyActivity) VocabularyActivity.musicContext).stopMusic();
            ((VocabularyActivity) VocabularyActivity.musicContext).startMusic();
        }
    }
}

