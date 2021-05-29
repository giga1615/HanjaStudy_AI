package com.creativeAI.jisun.hanja;

import android.app.Activity;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WindowAdapter extends RecyclerView.Adapter<WindowAdapter.ItemViewHolder> {
    String _email, family_nickname;
    String tag = "add_family";
    Activity activity;

    private static MediaPlayer buttonmusic;

    // adapter에 들어갈 list 입니다.
    private ArrayList<WindowData> listData = new ArrayList<>();

    public WindowAdapter(String email, Activity activity, String family_nickname) {
        this._email = email;
        this.activity = activity;
        this.family_nickname = family_nickname;
    }

    public void set_nickname(String nickname) {
        this.family_nickname=nickname;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(WindowData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    void clearItem(){
        listData.clear();
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView friend_nickname;
        private ImageView imageView;
        private ImageView make_family;
        private WindowData data;

        ItemViewHolder(View itemView) {
            super(itemView);

            friend_nickname = itemView.findViewById(R.id.friend_nickname);
            imageView = itemView.findViewById(R.id.imageView);
            make_family = itemView.findViewById(R.id.make_family);
        }

        void onBind(WindowData data) {
            this.data = data;
            friend_nickname.setText(data.getTitle());
            imageView.setImageResource(data.getResId());
            //make_family.setOnClickListener(this);

            if (family_nickname.compareTo("null") == 0) {
                make_family.setVisibility(View.VISIBLE);
                make_family.setOnClickListener(this);
            } else {
                if (family_nickname.compareTo(data.getTitle())==0)
                    make_family.setImageResource(R.drawable.w15);
                else {
                    make_family.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public void onClick(View v) {
            buttonmusic = MediaPlayer.create(activity, R.raw.buttonmusic);
            buttonmusic.setLooping(false);
            buttonmusic.start();

            //Toast.makeText(v.getContext(), data.getTitle(), Toast.LENGTH_SHORT).show();
            // 다이얼로그 바디
            AlertDialog.Builder alertdialog3 = new AlertDialog.Builder(activity);
            // 다이얼로그 메세지
            alertdialog3.setMessage("가족이 된 후, 가족을 끊을 수 없습니다. \n신중하게 결정해주세요.");

            // 확인버튼
            alertdialog3.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    JSONObject jsonObj = new JSONObject();
                    JSONObject userinfo = new JSONObject();

                    try {
                        // 여기에 name, value 추가하면 된다!!
                        userinfo.put("user", _email);
                        userinfo.put("friend_nickname", data.getTitle());
                        jsonObj.put("userinfo", userinfo);

                        NetworkTask networkTask = new NetworkTask(jsonObj);
                        networkTask.execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            // 취소버튼
            alertdialog3.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // 메인 다이얼로그 생성
            AlertDialog alert3 = alertdialog3.create();
            // 타이틀
            alert3.setTitle("가족으로 등록하시겠습니까?");
            alert3.show();
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
            switch (s) {
                case "already have family : user":
                    // 다이얼로그 바디
                    AlertDialog.Builder alertdialog2 = new AlertDialog.Builder(activity);
                    // 다이얼로그 메세지
                    alertdialog2.setMessage("가족은 1명만 가질 수 있습니다.");

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
                    alert2.setTitle("본인이 이미 가족이 존재합니다.");
                    alert2.show();
                    break;
                case "add family member success":
                    // 다이얼로그 바디
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(activity);
                    // 다이얼로그 메세지
                    alertdialog.setMessage("가족이 추가되었습니다. \n가족정보에서 확인하세요.");

                    // 확인버튼
                    alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity)MainActivity.musicContext).window_close.performClick();
                            dialog.cancel();
                        }
                    });

                    // 메인 다이얼로그 생성
                    AlertDialog alert = alertdialog.create();
                    // 타이틀
                    alert.setTitle("가족맺기 성공");
                    alert.show();
                    break;
                case "already have family : friend user":
                    // 다이얼로그 바디
                    AlertDialog.Builder alertdialog3 = new AlertDialog.Builder(activity);
                    // 다이얼로그 메세지
                    alertdialog3.setMessage("가족은 1명만 가질 수 있습니다.");

                    // 확인버튼
                    alertdialog3.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    // 메인 다이얼로그 생성
                    AlertDialog alert3 = alertdialog3.create();
                    // 타이틀
                    alert3.setTitle("친구가 이미 가족이 존재합니다.");
                    alert3.show();
                    break;
            }
        }
    }
}

