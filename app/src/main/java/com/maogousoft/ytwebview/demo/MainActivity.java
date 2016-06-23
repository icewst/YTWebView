package com.maogousoft.ytwebview.demo;

import android.app.Activity;
import android.os.Bundle;

import com.maogousoft.ytwebview.view.YTWebView;

public class MainActivity extends Activity {

    private YTWebView ytWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ytWebView = (YTWebView) findViewById(R.id.ytWebView);

        ytWebView.loadUrl("baidu.com");
    }
}
