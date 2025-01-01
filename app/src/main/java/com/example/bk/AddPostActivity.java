// AddPostActivity.java
package com.example.bk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private ImageButton buttonBack;
    private EditText editTextTitle, editTextContent;
    private Button buttonPublish;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

                                                                    // 初始化 Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 初始化视图
        buttonBack = findViewById(R.id.button_back);
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextContent = findViewById(R.id.edit_text_content);
        buttonPublish = findViewById(R.id.button_publish);

        // 初始化 ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在发表...");
        progressDialog.setCancelable(false);

        // 返回按钮
        buttonBack.setOnClickListener(v -> finish());

        // 发表按钮
        buttonPublish.setOnClickListener(v -> publishPost());
    }

    private void publishPost() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        // 输入验证
        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("请输入标题");
            editTextTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(content)) {
            editTextContent.setError("请输入正文内容");
            editTextContent.requestFocus();
            return;
        }

        progressDialog.show();

        // 获取当前用户
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "用户未登录", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        String uid = currentUser.getUid();
        String email = currentUser.getEmail(); // 确保获取用户的email
        if (email == null) {
            email = "unknown@example.com"; // 如果无法获取邮箱，给一个默认值
        }

        // 创建帖子数据
        Map<String, Object> postData = new HashMap<>();
        postData.put("uid", uid);
        postData.put("email", email); // 存储邮箱信息
        postData.put("title", title);
        postData.put("content", content);
        postData.put("timestamp", System.currentTimeMillis());
        postData.put("like_count", 0);
        postData.put("liked_users", new ArrayList<String>());

        // 将帖子数据存入 Firestore 的 'tiezi' 集合
        db.collection("tiezi")
                .add(postData)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddPostActivity.this, "发表成功", Toast.LENGTH_LONG).show();
                    // 返回首页并刷新
                    Intent intent = new Intent(AddPostActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddPostActivity.this, "发表失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
