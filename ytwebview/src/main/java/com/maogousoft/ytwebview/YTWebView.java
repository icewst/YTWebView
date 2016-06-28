package com.maogousoft.ytwebview;

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

import com.maogousoft.ytwebview.interf.IOnRefreshWebViewListener;
import com.maogousoft.ytwebview.view.PullToRefreshLayout;

/**
 * 牙疼WebView
 *
 * @author Toby
 */
public class YTWebView extends RelativeLayout {

    private static final String TAG = "YTWebView";
    private Context ctx;
    private WebView webView;
    private PullToRefreshLayout pullToRefreshLayout;

    public YTWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;

        LayoutInflater.from(context).inflate(R.layout.yt_webview_layout, this);
        pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        webView = (WebView) findViewById(R.id.sWebView);

        // SDK11，开启硬件加速，会导致白屏。 这里取消硬件加速
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        //设置支持JS
        webView.getSettings().setJavaScriptEnabled(true);
        //设置支持本地存储
        webView.getSettings().setDomStorageEnabled(true);
        // 将图片调整到适合webview的大小
        webView.getSettings().setUseWideViewPort(true);
        //让WebView中文件下载，到系统浏览器去下
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

    /**
     * 设置刷新成功
     */
    public void setRefreshSuccess() {

    }

    /**
     * 设置刷新失败
     */
    public void setRefreshFail() {

    }

    /**
     * 设置刷新是否启用
     *
     * @param isEnable
     */
    public void setRefreshEnable(boolean isEnable) {

    }

    /**
     * 设置刷新中回调
     *
     * @param listener
     */
    public void setOnRefreshWebViewListener(IOnRefreshWebViewListener listener) {
        pullToRefreshLayout.setOnRefreshListener(listener);
    }

    /**
     * 获取WebView
     *
     * @return
     */
    public WebView getWebView() {
        return webView;
    }
}
