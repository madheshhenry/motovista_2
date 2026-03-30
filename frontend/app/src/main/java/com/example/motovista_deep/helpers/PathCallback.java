package com.example.motovista_deep.helpers;

public interface PathCallback {
    void onPathReceived(String path);
    void onError(String error);
}
