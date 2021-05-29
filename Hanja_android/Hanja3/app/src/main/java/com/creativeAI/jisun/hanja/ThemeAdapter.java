package com.creativeAI.jisun.hanja;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ThemeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOTER = 2;
    String email, learning_theme;
    private static MediaPlayer selectmusic;
    Activity activity;

    ThemeAdapter(String email, String learning_theme, Activity activity) {
        this.email = email;
        this.learning_theme = learning_theme;
        this.activity = activity;
    }

    // adapter에 들어갈 list 입니다.
    private ArrayList<Data> listData = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Layoutinflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        final Context context = parent.getContext();
        RecyclerView.ViewHolder holder;
        View view;

        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
            holder = new HeaderViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
            holder = new FooterViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            holder = new ItemViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
        } else {
            // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.onBind(listData.get(position - 1), position);
        }
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size() + 2;
    }

    void addItem(Data data) {
        //외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private Data data;
        int itemPosition;
        RelativeLayout lock, even_layout, odd_layout;
        private TextView odd_num, even_num;

        public ItemViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            lock = itemView.findViewById(R.id.lock);
            odd_layout = itemView.findViewById(R.id.odd_layout);
            even_layout = itemView.findViewById(R.id.even_layout);
            odd_num = itemView.findViewById(R.id.odd_num);
            even_num = itemView.findViewById(R.id.even_num);
        }

        void onBind(Data data, int position) {
            this.data = data;

            imageView.setImageResource(data.getResId());
            odd_num.setText(data.getStudyNum()+"/10");
            even_num.setText(data.getStudyNum()+"/10");

            itemView.setOnClickListener(this);
            imageView.setOnClickListener(this);
            // 클릭한 리사이클 뷰 번호를 저장
            itemPosition = position;

            if (Integer.parseInt(learning_theme) < 3)
                if (position == 1)
                    lock.setVisibility(View.VISIBLE);           // 주방테마에 자물쇠채우기
            else
                lock.setVisibility(View.INVISIBLE);

            if(position%2==0) {
                even_layout.setVisibility(View.VISIBLE);
                odd_layout.setVisibility(View.INVISIBLE);
            } else {
                even_layout.setVisibility(View.INVISIBLE);
                odd_layout.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onClick(View v) {
            selectmusic = MediaPlayer.create(v.getContext(), R.raw.selectmusic);
            selectmusic.setLooping(false);
            if (((MainActivity) MainActivity.musicContext)._effect_sound)
                selectmusic.start();

            if(itemPosition==1 && Integer.parseInt(learning_theme)<3){
                // 다이얼로그 바디
                final AlertDialog.Builder alertdialog = new AlertDialog.Builder(activity);
                // 다이얼로그 메세지
                alertdialog.setMessage("오픈되어있는 다른 테마들을 먼저 모두 학습해주세요.");

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
                alert.setTitle("학습 불가");
                alert.show();
            } else {
                // itemPosition을 전달하여 그에 맞는 단어를 띄움
                Intent intent = new Intent(v.getContext(), VocabularyActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("pw", ((MainActivity) MainActivity.musicContext).pw);
                intent.putExtra("theme_number", itemPosition);
                v.getContext().startActivity(intent);
            }
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        HeaderViewHolder(View headerView) {
            super(headerView);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View footerView) {
            super(footerView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else if (position == listData.size() + 1)
            return TYPE_FOOTER;
        else
            return TYPE_ITEM;
    }
}