// PostDetailActivity.java
package com.example.bk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class PostDetailActivity extends AppCompatActivity {

    private TextView textViewDetailAuthorEmail, textViewDetailTitle, textViewDetailContent, textViewDetailTimestamp;
    private ImageButton buttonBack;

    private FirebaseFirestore db;

    private String postId;
    private Post currentPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 初始化视图
        textViewDetailAuthorEmail = findViewById(R.id.text_view_detail_author_email); // 修改 ID
        textViewDetailTitle = findViewById(R.id.text_view_detail_title);
        textViewDetailContent = findViewById(R.id.text_view_detail_content);
        textViewDetailTimestamp = findViewById(R.id.text_view_detail_timestamp);
        buttonBack = findViewById(R.id.button_back);

        // 初始化 Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // 获取传递的帖子 ID
        postId = getIntent().getStringExtra("post_id");
        if (postId == null) {
            Toast.makeText(this, "帖子 ID 不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 加载帖子详情
        loadPostDetail(postId);

        // 设置返回按钮的点击事件
        buttonBack.setOnClickListener(v -> {
            // 返回首页
            Intent intent = new Intent(PostDetailActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    /**
     * 加载帖子详情
     */
    private void loadPostDetail(String postId) {
        DocumentReference postRef = db.collection("tiezi").document(postId);
        postRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    currentPost = doc.toObject(Post.class);
                    if (currentPost != null) {
                        // 设置帖子详情
                        textViewDetailAuthorEmail.setText("作者邮箱: " + (currentPost.getEmail() != null ? currentPost.getEmail() : "未知邮箱"));
                        textViewDetailTitle.setText("标题: " + (currentPost.getTitle() != null ? currentPost.getTitle() : "无标题"));
                        textViewDetailContent.setText("内容: " + (currentPost.getContent() != null ? currentPost.getContent() : "无内容"));
                        textViewDetailTimestamp.setText("发布时间: " + formatTimestamp(currentPost.getTimestamp()));
                    }
                } else {
                    Toast.makeText(PostDetailActivity.this, "帖子不存在", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(PostDetailActivity.this, "加载帖子失败: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * 格式化时间戳为可读格式
     */
    private String formatTimestamp(long timestamp) {
        // 使用您喜欢的时间格式化方式
        // 例如，使用 java.text.SimpleDateFormat
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        java.util.Date date = new java.util.Date(timestamp);
        return sdf.format(date);
    }
}
