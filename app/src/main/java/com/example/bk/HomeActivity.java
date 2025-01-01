// HomeActivity.java
package com.example.bk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ImageButton buttonAdd, buttonSearch, buttonMessages;
    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;

    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 初始化 Firestore
        db = FirebaseFirestore.getInstance();

        // 初始化视图
        buttonAdd = findViewById(R.id.button_add);
        buttonSearch = findViewById(R.id.button_search);
        buttonMessages = findViewById(R.id.button_messages);
        recyclerViewPosts = findViewById(R.id.recycler_view_posts);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置 RecyclerView
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        recyclerViewPosts.setAdapter(postAdapter);

        // 加载帖子
        loadPosts();

        // 设置添加按钮的点击事件
        buttonAdd.setOnClickListener(v -> {
            // 启动 AddPostActivity
            Intent intent = new Intent(HomeActivity.this, AddPostActivity.class);
            startActivity(intent);
        });

        // 设置搜索按钮的点击事件（功能暂未开发）
        buttonSearch.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "搜索功能尚未开发", Toast.LENGTH_SHORT).show();
        });



        // 设置消息按钮的点击事件（功能暂未开发）
        buttonMessages.setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "消息功能尚未开发", Toast.LENGTH_SHORT).show();
        });

        // 设置底部导航栏的选中项为“首页”
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        // 设置底部导航栏的点击事件
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.navigation_home) {
                // 当前界面，无需操作
                return true;
            } else if (itemId == R.id.navigation_game_library) {
                // 启动 GameLibraryActivity
                Intent intent = new Intent(HomeActivity.this, GameLibraryActivity.class);
                // 防止多次实例化
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_me) {
                // 启动 MeActivity
                Intent intent = new Intent(HomeActivity.this, MeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    private void loadPosts() {
        CollectionReference postsRef = db.collection("tiezi");
        postsRef.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = document.toObject(Post.class);
                            post.setId(document.getId()); // 确保帖子ID被设置
                            postList.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(HomeActivity.this, "加载帖子失败: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 确保底部导航栏的选中项正确
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
}
