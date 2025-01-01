// GameLibraryActivity.java
package com.example.bk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;

public class GameLibraryActivity extends AppCompatActivity {

    private static final String TAG = "GameLibraryActivity";

    private TextView textViewGameLibrary;
    private RecyclerView recyclerViewGames;
    private GameAdapter gameAdapter;
    private List<Game> gameList;

    private BottomNavigationView bottomNavigationView;
    private ImageButton buttonAddGame;

    private FirebaseFirestore db;
    private CollectionReference gameRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_library);

                                                                                // 初始化 Firestore
        db = FirebaseFirestore.getInstance();
        gameRef = db.collection("Game");

        // 初始化视图
        textViewGameLibrary = findViewById(R.id.text_view_game_library_title);
        recyclerViewGames = findViewById(R.id.recycler_view_games);
        bottomNavigationView = findViewById(R.id.bottom_navigation_game_library);
        buttonAddGame = findViewById(R.id.button_add_game);

        // 设置 RecyclerView
        // 使用 GridLayoutManager 每行显示2列
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewGames.setLayoutManager(gridLayoutManager);

        gameList = new ArrayList<>();
        gameAdapter = new GameAdapter(this, gameList);
        recyclerViewGames.setAdapter(gameAdapter);

        // 添加游戏按钮
        buttonAddGame.setOnClickListener(v -> {
            Intent intent = new Intent(GameLibraryActivity.this, AddGameActivity.class);
            startActivity(intent);
        });

        // 设置底部导航栏的默认选中项
        bottomNavigationView.setSelectedItemId(R.id.navigation_game_library);

        // 设置底部导航栏的点击事件
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.navigation_home) {
                // 启动 HomeActivity
                Intent intent = new Intent(GameLibraryActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_game_library) {
                // 当前页面，无需操作
                return true;
            } else if (itemId == R.id.navigation_me) {
                // 启动 MeActivity
                Intent intent = new Intent(GameLibraryActivity.this, MeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }

            return false;
        });

        // 设置监听 Firestore 数据变化，实现实时更新游戏列表
        gameRef.orderBy("gameId")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "加载游戏失败", e);
                        Toast.makeText(GameLibraryActivity.this, "加载游戏失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        gameList.clear();
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            Game game = document.toObject(Game.class);
                            if (game != null) {
                                Log.d(TAG, "加载游戏: " + game.getName());
                                gameList.add(game);
                            } else {
                                Log.w(TAG, "无法解析游戏数据");
                            }
                        }
                        gameAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "快照为空");
                        Toast.makeText(GameLibraryActivity.this, "没有游戏数据", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.navigation_game_library);
    }
}
