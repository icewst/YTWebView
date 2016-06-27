package com.maogousoft.ytwebview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.maogousoft.ytwebview.PullToRefreshLayout;
import com.maogousoft.ytwebview.Pullable;
import com.maogousoft.ytwebview.R;

/**
 * 牙疼WebView
 *
 * @author Toby
 */
public class YTWebView extends RelativeLayout {

    static final String TAG = "YTWebView";
    private Context ctx;
    private WebView webView;
    private PullToRefreshLayout pullToRefreshLayout;

    public YTWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        //绑定Layout
        LayoutInflater.from(context).inflate(R.layout.activity_webview, this);
        pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        webView = (WebView) findViewById(R.id.sWebView);
        //初始化WebView
        initWebView();
    }


    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    private void initWebView() {


        // SDK11，开启硬件加速，会导致白屏。 这里取消硬件加速
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // 将图片调整到适合webview的大小
        webView.getSettings().setUseWideViewPort(true);

        webView.setDownloadListener(new DownloadListener() {

            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {
                // 实现下载的代码
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                ctx.startActivity(intent);
            }
        });
    }

    public WebView getWebView() {
        return webView;
    }

 
}
