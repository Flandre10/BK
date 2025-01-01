// GameDetailActivity.java
package com.example.bk;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameDetailActivity extends AppCompatActivity {

    private ImageButton buttonBack;
    private TextView textViewTitle;
    private WebView webViewStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        // 初始化视图
        buttonBack = findViewById(R.id.button_back_game_detail);
        textViewTitle = findViewById(R.id.text_view_game_detail_title);
        webViewStore = findViewById(R.id.web_view_game_store);

        // 获取传递的数据
        String gameName = getIntent().getStringExtra("game_name");
        String storeUrl = getIntent().getStringExtra("store_url");

        // 设置界面标题
        textViewTitle.setText(gameName != null ? gameName : "游戏详情");

        // 设置返回按钮点击事件
        buttonBack.setOnClickListener(v -> finish());

        // 配置 WebView
        WebSettings webSettings = webViewStore.getSettings();
        webSettings.setJavaScriptEnabled(true); // 启用JavaScript
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webViewStore.setWebViewClient(new WebViewClient());

        if (storeUrl != null && !storeUrl.isEmpty()) {
            webViewStore.loadUrl(storeUrl);
        } else {
            webViewStore.loadData("<h2>无效的商店链接</h2>", "text/html", "UTF-8");
        }
    }

    // 确保 WebView 能正确处理返回键
    @Override
    public void onBackPressed() {
        if (webViewStore.canGoBack()) {
            webViewStore.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
