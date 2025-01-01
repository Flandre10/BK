// Game.java
package com.example.bk;

import com.google.firebase.firestore.Exclude;

public class Game {
    private String gameId;
    private String name;
    private String coverImageUrl;
    private String storeUrl;

    // 无参构造函数（Firestore需要）
    public Game() {}

    // 参数化构造函数
    public Game(String gameId, String name, String coverImageUrl, String storeUrl) {
        this.gameId = gameId;
        this.name = name;
        this.coverImageUrl = coverImageUrl;
        this.storeUrl = storeUrl;
    }

    // Getters 和 Setters
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getStoreUrl() {
        return storeUrl;
    }

    public void setStoreUrl(String storeUrl) {
        this.storeUrl = storeUrl;
    }
}
