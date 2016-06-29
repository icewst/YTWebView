package com.maogousoft.ytwebview.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.maogousoft.ytwebview.YTWebView;
import com.maogousoft.ytwebview.interf.OnRefreshWebViewListener;

public class MainActivity extends Activity {

    private YTWebView ytWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ytWebView = (YTWebView) findViewById(R.id.ytWebView);

        ytWebView.setOnRefreshWebViewListener(new OnRefreshWebViewListener() {
            @Override
            public void onRefresh() {
                // 模拟接口调用3秒
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //调用接口结束
                        ytWebView.setRefreshSuccess();
//                        ytWebView.setRefreshFail();
                    }
                }.sendEmptyMessageDelayed(0, 3000);
            }
        });

        ytWebView.getWebView().loadUrl("http://www.baidu.com");
    }
}
