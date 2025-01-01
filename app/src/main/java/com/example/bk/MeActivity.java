package com.example.bk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationViewMe;
    private TextView textViewUserId;
    private Button buttonLogout;
    private Button buttonMyPosts;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        // 初始化 Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 初始化视图
        bottomNavigationViewMe = findViewById(R.id.bottom_navigation_me);
        textViewUserId = findViewById(R.id.text_view_user_id);
        buttonLogout = findViewById(R.id.button_logout);
        buttonMyPosts = findViewById(R.id.button_my_posts); // 新增的按钮“我的帖子”

        // 设置底部导航栏的默认选中项为“我”
        bottomNavigationViewMe.setSelectedItemId(R.id.navigation_me);

        // 设置底部导航栏的点击事件
        bottomNavigationViewMe.setOnNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(MeActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_game_library) {
                Intent intent = new Intent(MeActivity.this, GameLibraryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_me) {
                // 当前页面，无需操作
                return true;
            }

            return false;
        });

        // 显示当前用户的 ID (邮箱)
        displayUserId();

        // 设置注销按钮的点击事件
        buttonLogout.setOnClickListener(v -> logout());

        // “我的帖子”按钮事件：跳转到 MyPostsActivity
        buttonMyPosts.setOnClickListener(v -> {
            Intent intent = new Intent(MeActivity.this, MyPostsActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 显示当前用户的 ID (邮箱)
     */
    private void displayUserId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            textViewUserId.setText("邮箱: " + (email != null ? email : "未知用户"));
        } else {
            textViewUserId.setText("用户未登录");
        }
    }

    /**
     * 用户注销功能
     */
    private void logout() {
        mAuth.signOut();
        Toast.makeText(MeActivity.this, "已注销", Toast.LENGTH_SHORT).show();
        // 跳转到登录页面（假设你有一个 LoginActivity）
        Intent intent = new Intent(MeActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
