// PostAdapter.java
package com.example.bk;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// 如果使用 Kotlin，请相应调整

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(HomeActivity searchActivity, List<Post> posts) {
        this.postList = posts;
    }

    public void setPostList(List<Post> posts) {
        this.postList = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.textViewAuthorEmail.setText("作者邮箱: " + (post.getEmail() != null ? post.getEmail() : "未知邮箱"));
        holder.textViewTitle.setText("标题: " + (post.getTitle() != null ? post.getTitle() : "无标题"));

        // 截取正文内容的前20个字符
        String content = post.getContent() != null ? post.getContent() : "";
        if (content.length() > 20) {
            content = content.substring(0, 20) + "...";
        }
        holder.textViewContent.setText("内容: " + content);

        // 设置点击事件，打开帖子详情
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("post_id", post.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAuthorEmail, textViewTitle, textViewContent;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAuthorEmail = itemView.findViewById(R.id.text_view_email);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewContent = itemView.findViewById(R.id.text_view_content);
        }
    }
}
