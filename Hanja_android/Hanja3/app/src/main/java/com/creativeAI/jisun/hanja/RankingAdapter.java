package com.creativeAI.jisun.hanja;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ItemViewHolder> {
    String nickname;

    public RankingAdapter(String nickname) {
        this.nickname = nickname;
    }

    // adapter에 들어갈 list 입니다.
    private ArrayList<RankingData> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_item, parent, false);
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

    void addItem(RankingData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView rank_num;
        private TextView ranking_nickname;
        private TextView learning_num;
        private ImageView imageView;

        ItemViewHolder(View itemView) {
            super(itemView);

            ranking_nickname = itemView.findViewById(R.id.ranking_nickname);
            learning_num = itemView.findViewById(R.id.learning_num);
            imageView = itemView.findViewById(R.id.imageView);
            rank_num = itemView.findViewById(R.id.rank_num);
        }

        void onBind(RankingData data) {
            ranking_nickname.setText(data.getTitle());
            learning_num.setText(data.getContent());
            imageView.setImageResource(data.getResId());
            rank_num.setText(data.getRank_num());

            if(ranking_nickname.getText().toString().compareTo(nickname)==0) {
                ranking_nickname.setTextColor(Color.parseColor("#9454DC"));
            }
        }
    }
}

