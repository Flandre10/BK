package com.example.bk;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private List<Game> gameList;
    private Context context;

    public GameAdapter(Context context, List<Game> games) {
        this.context = context;
        this.gameList = games;
    }

    public void setGameList(List<Game> games) {
        this.gameList = games;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.game_item, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = gameList.get(position);
        holder.textViewGameName.setText(game.getName());

        // 不再显示游戏ID，也不使用 textViewGameId
        // 如果需要显示其他信息，可在此添加

        String coverImageUrl = game.getCoverImageUrl();
        if (coverImageUrl == null || coverImageUrl.isEmpty()) {
            coverImageUrl = "";
        }

        // 使用 Glide 加载封面图片
        Glide.with(context)
                .load(coverImageUrl)
                .placeholder(R.drawable.ic_game_placeholder)
                .error(R.drawable.ic_game_placeholder)
                .fallback(R.drawable.ic_game_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageViewGameCover);

        // 点击事件：进入游戏详情界面
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GameDetailActivity.class);
            intent.putExtra("game_name", game.getName());
            intent.putExtra("store_url", game.getStoreUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return gameList != null ? gameList.size() : 0;
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewGameCover;
        TextView textViewGameName;
        // 移除 textViewGameId 的定义和使用

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewGameCover = itemView.findViewById(R.id.image_view_game_cover);
            textViewGameName = itemView.findViewById(R.id.text_view_game_name);
            // 不再查找 textViewGameId，因为布局中已删除
        }
    }
}
