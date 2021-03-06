package com.creativeAI.jisun.hanja;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ThemeAdapter adapter;
    private WindowAdapter friend_adapter;
    private RankingAdapter ranking_adapter;
    RelativeLayout window, search, window2, window_setting, tutorial;

    String tag, tag2, email, pw, family_nickname, rank, nickname;

    ImageView my_button, friend_button, window_close, make_button, invite_button, search_button, window_close2, window_close3;
    ImageView quiz, window_title2, search_back, yes_button, no_button, ok_button, ranking_button, hanja_num_button, theme_num_button;
    ImageView setting_button, window_close5, tutorial_button, tutorial_close, opensource_button;
    String coin = null;
    String learning_hanja = null;
    String learning_theme = null;
    String t1, t2, t3, t4;
    int tt1 = 0, tt2 = 0, tt3 = 0, tt4 = 0;
    TextView theme_text, coin_text, hanja_text, explain;

    RecyclerView recyclerView;

    EditText nick_text;

    Intent mainIntent;
    public static Context musicContext;
    // ?????? ?????????????????? ?????? ??? ????????? ?????? ?????? ??? ??????!!
    public Boolean next;

    private static MediaPlayer buttonmusic;
    // ?????????
    Switch background_sound, effect_sound, isSearch;
    private SharedPreferences backgroundData;
    private boolean saveSettingData;
    boolean _background_sound, _effect_sound;
    String _isSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tag = "themeinfo";
        // ?????? ?????????????????? ?????? id????????????
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        pw = intent.getExtras().getString("pw");

        mainIntent = new Intent(getApplicationContext(), StartService.class);
        mainIntent.putExtra("where", "??????");
        musicContext = this;
        next = false;

        recyclerView = findViewById(R.id.recyclerView);
        coin_text = (TextView) findViewById(R.id.coin_text);
        theme_text = (TextView) findViewById(R.id.theme_text);
        hanja_text = (TextView) findViewById(R.id.hanja_text);
        window = (RelativeLayout) findViewById(R.id.window);
        window2 = (RelativeLayout) findViewById(R.id.window2);
        search = (RelativeLayout) findViewById(R.id.search);
        window_setting = (RelativeLayout) findViewById(R.id.window_setting);
        tutorial = (RelativeLayout) findViewById(R.id.tutorial);
        nick_text = (EditText) findViewById(R.id.nick);
        window_title2 = (ImageView) findViewById(R.id.window_title2);
        search_back = (ImageView) findViewById(R.id.search_back);
        explain = (TextView) findViewById(R.id.explain);
        yes_button = (ImageView) findViewById(R.id.yes_button);
        no_button = (ImageView) findViewById(R.id.no_button);
        ok_button = (ImageView) findViewById(R.id.ok_button);
        background_sound = (Switch) findViewById(R.id.background_sound);
        effect_sound = (Switch) findViewById(R.id.effect_sound);
        isSearch = (Switch) findViewById(R.id.isSearch);

        // ??? ?????? ??????????????? ?????? ?????????
        window.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        window2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        window_setting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        tutorial.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        friend_init();

        // ?????? ???????????? ??????
        String reqUser = email;

        JSONObject jsonObj = new JSONObject();
        JSONObject userinfo = new JSONObject();

        try {
            // ????????? name, value ???????????? ??????!!
            userinfo.put("user", reqUser);
            jsonObj.put("userinfo", userinfo);

            NetworkTask networkTask = new NetworkTask(jsonObj);
            networkTask.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ?????? ?????? ?????? ????????????
        tag2 = "memberinfo";
        JSONObject jsonObj2 = new JSONObject();
        JSONObject userinfo2 = new JSONObject();

        try {
            // ????????? name, value ???????????? ??????!!
            userinfo2.put("user", email);
            jsonObj2.put("userinfo", userinfo2);

            NetworkTask2 networkTask2 = new NetworkTask2(jsonObj2);
            networkTask2.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // ????????? ????????????
        backgroundData = getSharedPreferences("backgroundData", MODE_PRIVATE);
        load();

        // ????????? ?????? ????????? ???????????? ????????? ?????????
        if (saveSettingData) {
//                    email.setText(auto_email);
//                    pw.setText(auto_pw);
            background_sound.setChecked(_background_sound);
            effect_sound.setChecked(_effect_sound);
            if (background_sound.isChecked())
                startMusic();
        }

        tutorial_button = (ImageView) findViewById(R.id.tutorial_button);
        tutorial_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                tutorial.setVisibility(View.VISIBLE);
            }
        });

        tutorial_close = (ImageView) findViewById(R.id.tutorial_close);
        tutorial_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();
                tutorial.setVisibility(View.INVISIBLE);
            }
        });

        my_button = (ImageView) findViewById(R.id.my_button);
        my_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                Intent intent = new Intent(MainActivity.this, MyInformationActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("pw", pw);
                next = true;
                startActivity(intent);
            }
        });

        quiz = (ImageView) findViewById(R.id.quiz_button);
        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                Intent intent = new Intent(MainActivity.this, QuizStartActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("pw", pw);
                intent.putExtra("coin", coin);
                next = false;
                startActivity(intent);
                finish();
            }
        });

        friend_button = (ImageView) findViewById(R.id.friend_button);
        friend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                window.setVisibility(View.VISIBLE);
                //friend_init();

                tag = "friends_list";
                // ?????? ???????????? ??????
                String reqUser = email;

                JSONObject jsonObj = new JSONObject();
                JSONObject userinfo = new JSONObject();

                try {
                    // ????????? name, value ???????????? ??????!!
                    userinfo.put("user", reqUser);
                    jsonObj.put("userinfo", userinfo);

                    NetworkTask networkTask = new NetworkTask(jsonObj);
                    networkTask.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ranking_button = (ImageView) findViewById(R.id.ranking_button);
        ranking_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                window2.setVisibility(View.VISIBLE);
                rank = "hanja";
                tag2 = "rank_list";
                // ?????? ???????????? ??????
                String reqUser = email;

                JSONObject jsonObj = new JSONObject();
                JSONObject userinfo = new JSONObject();

                try {
                    // ????????? name, value ???????????? ??????!!
                    userinfo.put("user", reqUser);
                    userinfo.put("order", "hanja");
                    jsonObj.put("userinfo", userinfo);

                    NetworkTask2 networkTask3 = new NetworkTask2(jsonObj);
                    networkTask3.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ranking_init();
            }
        });

        window_close3 = (ImageView) findViewById(R.id.window_close3);
        window_close3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                window2.setVisibility(View.INVISIBLE);
                tag = "themeinfo";
                // ?????? ???????????? ??????
                String reqUser = email;

                JSONObject jsonObj = new JSONObject();
                JSONObject userinfo = new JSONObject();

                try {
                    // ????????? name, value ???????????? ??????!!
                    userinfo.put("user", reqUser);
                    jsonObj.put("userinfo", userinfo);

                    NetworkTask networkTask = new NetworkTask(jsonObj);
                    networkTask.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        window_close = (ImageView) findViewById(R.id.window_close);
        window_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                window.setVisibility(View.INVISIBLE);
                // ?????? ?????? ?????? ????????????
                tag2 = "memberinfo";
                JSONObject jsonObj2 = new JSONObject();
                JSONObject userinfo2 = new JSONObject();

                try {
                    // ????????? name, value ???????????? ??????!!
                    userinfo2.put("user", email);
                    jsonObj2.put("userinfo", userinfo2);

                    NetworkTask2 networkTask2 = new NetworkTask2(jsonObj2);
                    networkTask2.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        make_button = (ImageView) findViewById(R.id.make_button);
        make_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                window.setVisibility(View.INVISIBLE);
                search.setVisibility(View.VISIBLE);
                search_back.setVisibility(View.VISIBLE);
                search_button.setVisibility(View.VISIBLE);
                nick_text.setText("");
                nick_text.setVisibility(View.VISIBLE);
            }
        });

        window_close2 = (ImageView) findViewById(R.id.window_close2);
        window_close2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nick_text.getWindowToken(), 0);
                search.setVisibility(View.INVISIBLE);
                yes_button.setVisibility(View.INVISIBLE);
                no_button.setVisibility(View.INVISIBLE);
                ok_button.setVisibility(View.INVISIBLE);
                tag = "themeinfo";
                // ?????? ???????????? ??????
                String reqUser = email;

                JSONObject jsonObj = new JSONObject();
                JSONObject userinfo = new JSONObject();

                try {
                    // ????????? name, value ???????????? ??????!!
                    userinfo.put("user", reqUser);
                    jsonObj.put("userinfo", userinfo);

                    NetworkTask networkTask = new NetworkTask(jsonObj);
                    networkTask.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                explain.setVisibility(View.INVISIBLE);
            }
        });

        search_button = (ImageView) findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                tag = "search_friend";
                // ?????? ???????????? ??????
                String search_nick = nick_text.getText().toString();

                JSONObject jsonObj = new JSONObject();
                JSONObject friendinfo = new JSONObject();

                try {
                    // ????????? name, value ???????????? ??????!!
                    friendinfo.put("nickname", search_nick);
                    jsonObj.put("friendinfo", friendinfo);

                    NetworkTask networkTask = new NetworkTask(jsonObj);
                    networkTask.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
//                buttonmusic.setLooping(false);
//                buttonmusic.start();

                tag = "add_friend";
                // ?????? ???????????? ??????
                String search_nick = nick_text.getText().toString();

                JSONObject jsonObj = new JSONObject();
                JSONObject userinfo = new JSONObject();

                try {
                    // ????????? name, value ???????????? ??????!!
                    userinfo.put("user", email);
                    userinfo.put("friend_nickname", search_nick);
                    jsonObj.put("userinfo", userinfo);

                    NetworkTask networkTask = new NetworkTask(jsonObj);
                    networkTask.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                yes_button.setVisibility(View.INVISIBLE);
                no_button.setVisibility(View.INVISIBLE);
            }
        });

        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
//                buttonmusic.setLooping(false);
//                buttonmusic.start();

                window_close2.performClick();
                window.setVisibility(View.VISIBLE);
                yes_button.setVisibility(View.INVISIBLE);
                no_button.setVisibility(View.INVISIBLE);
            }
        });

        ok_button = (ImageView) findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
//                buttonmusic.setLooping(false);
//                buttonmusic.start();

                window_close2.performClick();
                window.setVisibility(View.VISIBLE);
            }
        });

        invite_button = (ImageView) findViewById(R.id.invite_button);
        invite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                FeedTemplate params = FeedTemplate
                        .newBuilder(ContentObject.newBuilder("[??????????????????]",
                                "http://175.123.138.125:8070/hanja.jpg",
                                LinkObject.newBuilder().setWebUrl("play.google.com/apps/testing/com.creativeAI.jisun.hanja")
                                        .setMobileWebUrl("http://play.google.com/apps/testing/com.creativeAI.jisun.hanja").build())
                                .setDescrption("????????? ?????? ?????????????????? ?????????????")
                                .build())
                        //.setSocial(SocialObject.newBuilder().setLikeCount(10).setCommentCount(20).setSharedCount(30).setViewCount(40).build())
                        //.addButton(new ButtonObject("????????? ??????", LinkObject.newBuilder().setWebUrl("'https://developers.kakao.com").setMobileWebUrl("'https://developers.kakao.com").build()))
                        .addButton(new ButtonObject("???????????? ??????", LinkObject.newBuilder()
                                .setWebUrl("play.google.com/apps/testing/com.creativeAI.jisun.hanja")
                                .setMobileWebUrl("'http://play.google.com/apps/testing/com.creativeAI.jisun.hanja")
                                //.setAndroidExecutionParams("key1=value1")
                                //.setIosExecutionParams("key1=value1")
                                .build()))
                        .build();

                Map<String, String> serverCallbackArgs = new HashMap<String, String>();
                serverCallbackArgs.put("user_id", "${current_user_id}");
                serverCallbackArgs.put("product_id", "${shared_product_id}");

                KakaoLinkService.getInstance().sendDefault(getApplicationContext(), params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Logger.e(errorResult.toString());
                    }

                    @Override
                    public void onSuccess(KakaoLinkResponse result) {
                        // ????????? ?????????????????? ?????? ????????? ??????????????? ??????. ????????? ??????????????? ??????????????? ????????? ??? ??? ??????. ?????? ?????? ????????? ???????????? ????????? ??????????????? ??????.
                    }
                });
            }
        });

        setting_button = (ImageView) findViewById(R.id.setting_button);
        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                if(_isSearch.compareTo("Y")==0)
                    isSearch.setChecked(true);
                else
                    isSearch.setChecked(false);

                window_setting.setVisibility(View.VISIBLE);
            }
        });

        window_close5 = (ImageView) findViewById(R.id.window_close5);
        window_close5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                tag2 = "set_nickname";
                JSONObject jsonObj = new JSONObject();
                JSONObject userinfo = new JSONObject();

                try {
                    // ????????? name, value ???????????? ??????!!
                    userinfo.put("user", email);
                    if (isSearch.isChecked())
                        userinfo.put("search_nickname", "Y");
                    else
                        userinfo.put("search_nickname", "N");
                    jsonObj.put("userinfo", userinfo);

                    NetworkTask2 networkTask3 = new NetworkTask2(jsonObj);
                    networkTask3.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                window_setting.setVisibility(View.INVISIBLE);
                save();
            }
        });

        hanja_num_button = (ImageView) findViewById(R.id.hanja_num_button);
        theme_num_button = (ImageView) findViewById(R.id.theme_num_button);
        hanja_num_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                hanja_num_button.setImageResource(R.drawable.w22);
                theme_num_button.setImageResource(R.drawable.w19);
                rank = "hanja";
                tag2 = "rank_list";
                // ?????? ???????????? ??????
                String reqUser = email;

                JSONObject jsonObj = new JSONObject();
                JSONObject userinfo = new JSONObject();

                try {
                    // ????????? name, value ???????????? ??????!!
                    userinfo.put("user", reqUser);
                    userinfo.put("order", "hanja");
                    jsonObj.put("userinfo", userinfo);

                    NetworkTask2 networkTask3 = new NetworkTask2(jsonObj);
                    networkTask3.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ranking_init();
            }
        });

        theme_num_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                hanja_num_button.setImageResource(R.drawable.w18);
                theme_num_button.setImageResource(R.drawable.w21);
                rank = "theme";
                tag2 = "rank_list";
                // ?????? ???????????? ??????
                String reqUser = email;

                JSONObject jsonObj = new JSONObject();
                JSONObject userinfo = new JSONObject();

                try {
                    // ????????? name, value ???????????? ??????!!
                    userinfo.put("user", reqUser);
                    userinfo.put("order", "theme");
                    jsonObj.put("userinfo", userinfo);

                    NetworkTask2 networkTask3 = new NetworkTask2(jsonObj);
                    networkTask3.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ranking_init();
            }
        });

        opensource_button = (ImageView) findViewById(R.id.opensource_button);
        opensource_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                // ??????????????? ??????
                final AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this);
                // ??????????????? ?????????
                alertdialog.setMessage("[?????? ??????]\n??? ??????~????????? ?????????\n" +
                        "Song : Purple Planet Music - Magical Charms\n" +
                        "Music: https://www.purple-planet.com\n" +
                        "Music promoted by DayDreamSound : https://youtu.be/fOoWGqhO6oA\n" +
                        "\n" +
                        "??? ????????? ?????????\n" +
                        "https://www.youtube.com/watch?v=bdM7e_CRNvE\n" +
                        "\n" +
                        "??? ?????? ?????????\n" +
                        "Song : Purple Planet Music - Bunny Garden\n" +
                        "Music: https://www.purple-planet.com\n" +
                        "Music promoted by DayDreamSound : https://youtu.be/jj_1JuK1504\n" +
                        "\n" +
                        "??? ?????? ?????????\n" +
                        "Song : ????????? - Tongtong\n" +
                        "???????????? : https://gongu.copyright.or.kr\n" +
                        "Music promoted by DayDreamSound : https://youtu.be/1EjTsqD5w0s\n" +
                        "CC-BY 3.0  https://goo.gl/Y8U8b9\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "[?????? ?????????]\n" +
                        "???pngtree\n\n" +
                        "??? ?????? ??????\n" +
                        "https://kor.pngtree.com/freebackground/caribou-deer-design-moon-background_899826.html\n" +
                        "\n" +
                        "??? ????????????\n" +
                        "https://kor.pngtree.com/freebackground/mid-autumn-fantasy-posters_753497.html\n" +
                        "\n" +
                        "??? ????????????\n" +
                        "https://kor.pngtree.com/freebackground/chang-e-mid-autumn-moon-star_269992.html\n" +
                        "\n" +
                        "??? ?????? ??????\n" +
                        "https://kor.pngtree.com/freebackground/gorgeous--simple--purple-moon--mid-autumn-promotions--posters--background--psd_904449.html" +
                        "\n\n\n\n" +
                        "[?????????]\n"+
                        "??? ??????\n"+
                        "https://kor.pngtree.com/freepng/cartoon-blackboard_2627257.html\n"+
                        "\n"+
                        "??? ?????????\n"+
                        "https://kor.pngtree.com/freepng/pencil-eraser-japanese-lovely_3854825.html\n"+
                        "\n"+
                        "??? ?????????\n"+
                        "https://pngtree.com/freepng/vector-camera-icon_4015139.html\n"+
                        "\n"+
                        "??? ?????????\n"+
                        "https://kor.pngtree.com/freepng/flat-cartoon-camera-elements_3695478.html\n"+
                        "\n"+
                        "??? ?????????\n"+
                        "https://kor.pngtree.com/freepng/lock-vector-icon_3767398.html\n"+
                        "\n"+
                        "??? ????????????\n"+
                        "https://kor.pngtree.com/freepng/_4122981.html\n"+
                        "\n"+
                        "??? ????????????\n"+
                        "https://kor.pngtree.com/freepng/pink-blue-kitchen-table-table--vector-illustration_3708396.html\n"+
                        "\n"+
                        "??? ??????\n"+
                        "https://kor.pngtree.com/freepng/halloween_2434011.html"+
                        "\n\n\n\n"+
                        "[??????]\n" +
                        "??? ????????? ??????\n" +
                        "http://yanolja.in/ko/yafont/\n\n" +
                        "??? ?????? ?????????\n" +
                        "http://seoulite.kr/FfaxpNMe\n");

                // ????????????
                alertdialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                    }
                });

                // ?????? ??????????????? ??????
                AlertDialog alert = alertdialog.create();
                // ?????????
                alert.setTitle("???????????? ????????????");
                alert.show();
            }
        });

        background_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettingData = true;

                if (background_sound.isChecked() == false) {
                    _background_sound = false;
                    stopService(mainIntent);
                } else {
                    _background_sound = true;
                    startService(mainIntent);
                }
            }
        });

        effect_sound.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                saveSettingData = true;

                if (effect_sound.isChecked() == false) {
                    _effect_sound = false;
                } else {
                    _effect_sound = true;
                }
            }
        });

        isSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettingData = true;
//
//                if (isSearch.isChecked() == false) {
//                    _isSearch = false;
//                } else {
//                    _isSearch = true;
//                }
            }
        });
    }

    // ???????????? ???????????? ??????
    private void save() {
        // SharedPreferences ??????????????? ?????? ????????? Editor ??????
        SharedPreferences.Editor editor = backgroundData.edit();

        // ???????????????.put??????( ???????????? ??????, ???????????? ??? )
        // ???????????? ????????? ?????? ???????????? ????????????
        editor.putBoolean("SAVE_SETTING_DATA", saveSettingData);
        editor.putBoolean("BACKGROUND_SOUND", background_sound.isChecked());
        editor.putBoolean("EFFECT_SOUND", effect_sound.isChecked());
        editor.putBoolean("ISSEARCH", isSearch.isChecked());

        // apply, commit ??? ????????? ????????? ????????? ???????????? ??????
        editor.apply();
    }

    // ???????????? ???????????? ??????
    private void load() {
        // SharedPreferences ??????.get??????( ????????? ??????, ????????? )
        // ????????? ????????? ???????????? ?????? ??? ?????????
        saveSettingData = backgroundData.getBoolean("SAVE_SETTING_DATA", false);
        _background_sound = backgroundData.getBoolean("BACKGROUND_SOUND", true);
        _effect_sound = backgroundData.getBoolean("EFFECT_SOUND", true);
        //_isSearch = backgroundData.getBoolean("ISSEARCH", true);
    }

    private void init() {
        //RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ThemeAdapter(email, learning_theme, this);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() + 1);
    }

    private void friend_init() {
        RecyclerView friend_recyclerView = findViewById(R.id.friend_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        friend_recyclerView.setLayoutManager(linearLayoutManager);

        friend_adapter = new WindowAdapter(email, MainActivity.this, family_nickname);
        friend_recyclerView.setAdapter(friend_adapter);
    }

    private void ranking_init() {
        RecyclerView ranking_recyclerView = findViewById(R.id.ranking_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        ranking_recyclerView.setLayoutManager(linearLayoutManager);

        ranking_adapter = new RankingAdapter(nickname);
        ranking_recyclerView.setAdapter(null);
        ranking_recyclerView.setAdapter(ranking_adapter);
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private JSONObject jsonObj;

        public NetworkTask(JSONObject jsonObj) {
            this.jsonObj = jsonObj;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // ?????? ????????? ????????? ??????.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection(tag);
            result = requestHttpURLConnection.request(jsonObj); // ?????? URL??? ?????? ???????????? ????????????.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (tag) {
                case "themeinfo":
                    try {
                        JSONObject jObject = new JSONObject(s);
                        JSONObject obj = jObject.getJSONObject("userinfo");
                        coin = obj.getString("coin");
                        learning_hanja = obj.getString("learning_hanja");
                        learning_theme = obj.getString("learning_theme");

                        //if (Integer.parseInt(learning_hanja) != 0) {
                            JSONObject obj2 = jObject.getJSONObject("themeinfo");
                            t1 = obj2.getString("t1");
                            t2 = obj2.getString("t2");
                            t3 = obj2.getString("t3");
                            t4 = obj2.getString("t4");
                       // }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    coin_text.setText(coin);
                    hanja_text.setText(learning_hanja);
                    theme_text.setText(learning_theme);

                    if (Integer.parseInt(learning_hanja) != 0) {
                        //?????????????????? ?????? ??? ??????
                        tt1 = Integer.parseInt(t1);
                        tt2 = Integer.parseInt(t2);
                        tt3 = Integer.parseInt(t3);
                        tt4 = Integer.parseInt(t4);

                        ArrayList listStudyNum = new ArrayList<>();
                        listStudyNum.add(tt4);
                        listStudyNum.add(tt3);
                        listStudyNum.add(tt2);
                        listStudyNum.add(tt1);

                        //???????????? ??????????????????
                        init();
                        ArrayList listResld = new ArrayList<>();
                        // ?????? ??????
                        if (tt4 < 5)
                            listResld.add(R.drawable.m14);
                        else if (tt4 >= 5 && tt4 < 10)
                            listResld.add(R.drawable.m18);
                        else if (tt4 == 10)
                            listResld.add(R.drawable.m22);

                        // ?????? ??????
                        if (tt3 < 5)
                            listResld.add(R.drawable.m13);
                        else if (tt3 >= 5 && tt3 < 10)
                            listResld.add(R.drawable.m17);
                        else if (tt3 == 10)
                            listResld.add(R.drawable.m21);

                        // ????????? ??????
                        if (tt2 < 5)
                            listResld.add(R.drawable.m12);
                        else if (tt2 >= 5 && tt2 < 10)
                            listResld.add(R.drawable.m16);
                        else if (tt2 == 10)
                            listResld.add(R.drawable.m20);

                        // ????????????
                        if (tt1 < 5)
                            listResld.add(R.drawable.m11);
                        else if (tt1 >= 5 && tt1 < 10)
                            listResld.add(R.drawable.m15);
                        else if (tt1 == 10)
                            listResld.add(R.drawable.m19);

                        for (int i = 0; i < listResld.size(); i++) {
                            //??? List??? ????????? data ????????? set?????????
                            Data data = new Data();
                            data.setResId((Integer) listResld.get(i));
                            data.setStudyNum((Integer) listStudyNum.get(i));

                            //??? ?????? ????????? data??? adapter??? ???????????????.
                            adapter.addItem(data);
                        }

                        // adapter??? ?????? ?????????????????? ?????? ???????????????.
                        adapter.notifyDataSetChanged();
                    } else {
                        //?????????????????? ?????? ??? ??????
                        tt1 = Integer.parseInt(t1);
                        tt2 = Integer.parseInt(t2);
                        tt3 = Integer.parseInt(t3);
                        tt4 = Integer.parseInt(t4);

                        ArrayList listStudyNum = new ArrayList<>();
                        listStudyNum.add(tt4);
                        listStudyNum.add(tt3);
                        listStudyNum.add(tt2);
                        listStudyNum.add(tt1);

                        //???????????? ??????????????????
                        init();
                        ArrayList listResld = new ArrayList<>();
                        listResld.add(R.drawable.m14);
                        listResld.add(R.drawable.m13);
                        listResld.add(R.drawable.m12);
                        listResld.add(R.drawable.m11);
                        for (int i = 0; i < listResld.size(); i++) {
                            //??? List??? ????????? data ????????? set?????????
                            Data data = new Data();
                            data.setResId((Integer) listResld.get(i));
                            data.setStudyNum((Integer) listStudyNum.get(i));

                            //??? ?????? ????????? data??? adapter??? ???????????????.
                            adapter.addItem(data);
                        }

                        // adapter??? ?????? ?????????????????? ?????? ???????????????.
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case "friends_list":
                    //Toast.makeText(getApplication(), s + "", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jObject = new JSONObject(s);
                        JSONObject obj = jObject.getJSONObject("friends_list");
                        int length = obj.length();

                      //  Toast.makeText(getApplication(), length + "", Toast.LENGTH_SHORT).show();

                        ArrayList listNickName = new ArrayList<>();
                        for (int i = 1; i <= length; i++) {
                            listNickName.add(obj.getString("f" + i));
                        }
                        ArrayList listResId = new ArrayList<>();
                        for (int i = 1; i <= length; i++) {
                            listResId.add(R.drawable.w3);
                        }

                        //friend_init();
                        friend_adapter.clearItem();
                        for (int i = 0; i < length; i++) {
                            // ??? List??? ????????? data ????????? set ????????????.
                            WindowData data = new WindowData();
                            data.setTitle((String) listNickName.get(i));
                            data.setResId((Integer) listResId.get(i));

                            // ??? ?????? ????????? data??? adapter??? ???????????????.
                            friend_adapter.addItem(data);
                        }

                        // adapter??? ?????? ?????????????????? ?????? ???????????????.
                        friend_adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "search_friend":
                    search_back.setVisibility(View.INVISIBLE);
                    search_button.setVisibility(View.INVISIBLE);
                    nick_text.setVisibility(View.INVISIBLE);
                    explain.setVisibility(View.VISIBLE);

                    if (nick_text.getText().toString().compareTo(nickname) == 0) {
                        explain.setText("????????? ????????? ??? ????????????.");
                        ok_button.setVisibility(View.VISIBLE);
                    } else {
                        switch (s) {
                            case "nickname exist":
                                window_title2.setImageResource(R.drawable.w12);
                                explain.setText(nick_text.getText().toString() + "?????? ????????? ?????????????????????????");
                                yes_button.setVisibility(View.VISIBLE);
                                no_button.setVisibility(View.VISIBLE);
                                //Toast.makeText(getApplication(), "??????", Toast.LENGTH_SHORT).show();
                                break;
                            case "non-added member":
                                explain.setText("?????? ???????????? ???????????? ???????????? ????????????.");
                                ok_button.setVisibility(View.VISIBLE);
                                //Toast.makeText(getApplication(), s+"", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                    break;
                case "add_friend":
                    switch (s) {
                        case "add friend list success":
                            Toast.makeText(getApplication(), "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                            window_close2.performClick();
                            break;
                        case "already added friend":
                            explain.setText("?????? ????????? ????????? ????????? ?????????.");
                            ok_button.setVisibility(View.VISIBLE);
                            //Toast.makeText(getApplication(), s+"", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
            }
        }
    }

    public class NetworkTask2 extends AsyncTask<Void, Void, String> {

        private JSONObject jsonObj;

        public NetworkTask2(JSONObject jsonObj) {
            this.jsonObj = jsonObj;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // ?????? ????????? ????????? ??????.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection(tag2);
            result = requestHttpURLConnection.request(jsonObj); // ?????? URL??? ?????? ???????????? ????????????.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (tag2) {
                case "memberinfo":
                    try {
                        JSONObject jObject = new JSONObject(s);
                        JSONObject obj = jObject.getJSONObject("userinfo");
                        nickname = obj.getString("nickname");
                        family_nickname = obj.getString("family_nickname");
                        friend_adapter.set_nickname(family_nickname);
                        _isSearch = obj.getString("search_nickname");
                        if(_isSearch.compareTo("Y")==0)
                            isSearch.setChecked(true);
                        else
                            isSearch.setChecked(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "rank_list":
                    int rank_num = 1;
                    int previous_learning_num = 0;
                    switch (s) {
                        case "no friend info":
                            Toast.makeText(getApplication(), "????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                            break;
                        default:
//                            Toast.makeText(getApplication(), s + "", Toast.LENGTH_SHORT).show();
                            try {
                                JSONObject jObject = new JSONObject(s);
                                JSONObject obj1 = jObject.getJSONObject("rank_list");
                                JSONObject obj = obj1.getJSONObject("nickname");
                                int length = obj.length();

                                ArrayList listNickName2 = new ArrayList<>();
                                for (int i = 1; i <= length; i++) {
                                    listNickName2.add(obj.getString("" + i));
                                }
                                ArrayList listResId2 = new ArrayList<>();
                                for (int i = 1; i <= length; i++) {
                                    listResId2.add(R.drawable.w3);
                                }

                                ArrayList listRankNum = new ArrayList<>();
                                ArrayList listContent = new ArrayList<>();
                                if (rank.compareTo("hanja") == 0) {
                                    JSONObject obj2 = obj1.getJSONObject("learning_hanja");
                                    for (int i = 1; i <= length; i++) {
                                        if (previous_learning_num == Integer.parseInt(obj2.getString("" + i)))
                                            rank_num--;
                                        if (rank_num < 1)
                                            rank_num = 1;
                                        listRankNum.add("" + rank_num);
                                        previous_learning_num = Integer.parseInt(obj2.getString("" + i));
                                        rank_num++;
                                        listContent.add(obj2.getString("" + i));
                                    }
                                } else {
                                    JSONObject obj2 = obj1.getJSONObject("learning_theme");
                                    for (int i = 1; i <= length; i++) {
                                        if (previous_learning_num == Integer.parseInt(obj2.getString("" + i)))
                                            rank_num--;
                                        if (rank_num < 1)
                                            rank_num = 1;
                                        listRankNum.add("" + rank_num);
                                        previous_learning_num = Integer.parseInt(obj2.getString("" + i));
                                        rank_num++;
                                        listContent.add(obj2.getString("" + i));
                                    }
                                }

                                for (int i = 0; i < length; i++) {
                                    // ??? List??? ????????? data ????????? set ????????????.
                                    RankingData data = new RankingData();
                                    data.setTitle((String) listNickName2.get(i));
                                    data.setContent((String) listContent.get(i));
                                    data.setResId((Integer) listResId2.get(i));
                                    data.setRank_num((String) listRankNum.get(i));

                                    // ??? ?????? ????????? data??? adapter??? ???????????????.
                                    ranking_adapter.addItem(data);
                                }

                                // adapter??? ?????? ?????????????????? ?????? ???????????????.
                                ranking_adapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    break;
                case "set_nickname":
                //    Toast.makeText(getApplication(), "" + s, Toast.LENGTH_SHORT).show();
                    if(isSearch.isChecked())
                        _isSearch = "Y";
                    else
                        _isSearch = "N";
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // ??????????????? ??????
        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this);
        // ??????????????? ?????????
        alertdialog.setMessage("????????????????????? ?????????????????????????");

        // ????????????
        alertdialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save();
                next = false;
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
        });
        alertdialog.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        // ?????? ??????????????? ??????
        AlertDialog alert = alertdialog.create();
        // ?????????
        alert.setTitle("?????? ??????");
        alert.show();
    }

    void startMusic() {
        startService(mainIntent);
    }

    void stopMusic() {
        stopService(mainIntent);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //KeyguardManager islock = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        if (islock.inKeyguardRestrictedInputMode() && !pm.isInteractive())
        if (next == false)
            stopService(mainIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (next == false)
            stopService(mainIntent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (next == false && _background_sound) {
            stopService(mainIntent);
            startService(mainIntent);
        }
    }
}