package com.example.bk;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends HomeActivity { // 将 MyPostsActivity 继承自 HomeActivity
    private static final String TAG = "MyPostsActivity";

    private RecyclerView recyclerViewMyPosts;
    private ProgressBar progressBarMyPosts;

    private PostAdapter postAdapter;
    private List<Post> postList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference tieziRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        MaterialToolbar toolbar = findViewById(R.id.top_menu_bar_my_posts);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tieziRef = db.collection("tiezi");

        recyclerViewMyPosts = findViewById(R.id.recycler_view_my_posts);
        progressBarMyPosts = findViewById(R.id.progress_bar_my_posts);

        recyclerViewMyPosts.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();

        // 此处传入this，因为MyPostsActivity已继承自HomeActivity类型不报错
        postAdapter = new PostAdapter(this, postList);
        recyclerViewMyPosts.setAdapter(postAdapter);

        loadMyPosts();
    }

    private void loadMyPosts() {
        progressBarMyPosts.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(MyPostsActivity.this, "用户未登录，无法查看帖子", Toast.LENGTH_SHORT).show();
            progressBarMyPosts.setVisibility(View.GONE);
            return;
        }

        String userEmail = currentUser.getEmail();
        if (userEmail == null) {
            Toast.makeText(MyPostsActivity.this, "无法获取用户邮箱信息", Toast.LENGTH_SHORT).show();
            progressBarMyPosts.setVisibility(View.GONE);
            return;
        }

        tieziRef.whereEqualTo("email", userEmail) // 帖子使用"email"作为作者邮箱字段
                .get()
                .addOnCompleteListener(task -> {
                    progressBarMyPosts.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        postList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Post post = doc.toObject(Post.class);
                            postList.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                        if (postList.isEmpty()) {
                            Toast.makeText(MyPostsActivity.this, "您还没有创建过帖子", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "加载帖子失败", task.getException());
                        Toast.makeText(MyPostsActivity.this, "加载帖子失败：" + (task.getException() != null ? task.getException().getMessage() : "未知错误"), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
