// AddGameActivity.java
package com.example.bk;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddGameActivity extends AppCompatActivity {

    private static final String TAG = "AddGameActivity";

    private EditText editTextStoreUrl;
    private Button buttonAddGameSubmit;
    private ProgressBar progressBarAddGame;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        // 初始化视图
        editTextStoreUrl = findViewById(R.id.edit_text_store_url);
        buttonAddGameSubmit = findViewById(R.id.button_add_game_submit);
        progressBarAddGame = findViewById(R.id.progress_bar_add_game);

        // 初始化 Firestore
        db = FirebaseFirestore.getInstance();

        // 设置添加游戏按钮点击事件
        buttonAddGameSubmit.setOnClickListener(v -> {
            String storeUrl = editTextStoreUrl.getText().toString().trim();
            if (TextUtils.isEmpty(storeUrl)) {
                editTextStoreUrl.setError("商店链接URL不能为空");
                return;
            }

            if (!isValidUrl(storeUrl)) {
                editTextStoreUrl.setError("无效的商店链接URL");
                return;
            }

            // 开始添加游戏
            new FetchGameDetailsTask().execute(storeUrl);
        });
    }

    /**
     * 验证URL格式
     */
    private boolean isValidUrl(String url) {
        String regex = "^(http|https)://[^\\s]+$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    /**
     * 从 Steam store URL 中提取游戏ID
     */
    private String extractGameId(String storeUrl) {
        try {
            Uri uri = Uri.parse(storeUrl);
            String path = uri.getPath(); // "/app/359550/Tom_Clancys_Rainbow_Six_Siege/"
            String[] segments = path.split("/");
            for (int i = 0; i < segments.length; i++) {
                if (segments[i].equals("app") && i + 1 < segments.length) {
                    return segments[i + 1];
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "提取游戏ID失败", e);
        }
        return null;
    }

    /**
     * 异步任务，用于从商店链接中获取游戏名称和封面图片URL
     */
    private class FetchGameDetailsTask extends AsyncTask<String, Void, Game> {

        private String storeUrl;
        private String errorMessage = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarAddGame.setVisibility(View.VISIBLE);
            buttonAddGameSubmit.setEnabled(false);
        }

        @Override
        protected Game doInBackground(String... strings) {
            storeUrl = strings[0];
            String gameId = extractGameId(storeUrl);
            if (gameId == null) {
                errorMessage = "无法从链接中提取游戏ID，请检查URL格式。";
                return null;
            }

            try {
                // 使用 Jsoup 连接并解析HTML
                Document doc = Jsoup.connect(storeUrl).get();

                // 获取游戏名称
                Elements titleElement = doc.select("div.apphub_AppName");
                if (titleElement.isEmpty()) {
                    errorMessage = "无法解析游戏名称。";
                    return null;
                }
                String gameName = titleElement.first().text();

                // 获取封面图片URL
                Elements coverElement = doc.select("img.game_header_image_full");
                if (coverElement.isEmpty()) {
                    // 尝试另一种选择器
                    coverElement = doc.select("img.game_header_image");
                }
                if (coverElement.isEmpty()) {
                    errorMessage = "无法解析封面图片URL。";
                    return null;
                }
                String coverImageUrl = coverElement.first().absUrl("src");

                // 创建 Game 对象
                return new Game(gameId, gameName, coverImageUrl, storeUrl);

            } catch (IOException e) {
                Log.e(TAG, "网络错误", e);
                errorMessage = "网络错误: " + e.getMessage();
            } catch (Exception e) {
                Log.e(TAG, "解析错误", e);
                errorMessage = "解析错误: " + e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Game game) {
            super.onPostExecute(game);
            progressBarAddGame.setVisibility(View.GONE);
            buttonAddGameSubmit.setEnabled(true);

            if (game != null) {
                // 添加到 Firestore
                db.collection("Game")
                        .add(game)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(AddGameActivity.this, "游戏添加成功", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "游戏添加成功: " + documentReference.getId());
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddGameActivity.this, "添加游戏失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "添加游戏失败", e);
                        });
            } else {
                // 显示错误信息
                Toast.makeText(AddGameActivity.this, "添加游戏失败: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}
