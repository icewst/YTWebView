package com.maogousoft.ytwebview.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.maogousoft.ytwebview.YTWebView;
import com.maogousoft.ytwebview.view.PullToRefreshLayout;

public class MainActivity extends Activity {

    private YTWebView ytWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ytWebView = (YTWebView) findViewById(R.id.ytWebView);

//        ytWebView.getPullToRefreshLayout().setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
//
//                // 下拉刷新操作
//                new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        // 千万别忘了告诉控件刷新完毕了哦！
//                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
//                        //  pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
//                    }
//                }.sendEmptyMessageDelayed(0, 3000);
//            }
//        });
//
//        ytWebView.getWebView().loadUrl("https://www.baidu.com");
    }
}
