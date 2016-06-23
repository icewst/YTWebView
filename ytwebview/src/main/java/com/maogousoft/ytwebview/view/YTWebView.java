package com.maogousoft.ytwebview.view;

import android.app.Activity;
import android.content.Context;

import android.util.AttributeSet;

import android.view.View;
import android.webkit.WebView;

import com.maogousoft.ytwebview.R;


/**
 * 自定义WebView
 *
 * @author Toby
 */
public class YTWebView extends WebView {

    /**
     * 公共
     */
    public static final int REQUESTCODE_COMMON = 90000;


    /**
     * 添加到JS里的对象
     */
    public static final String JS_OBJ_NAME = "jsObj";

    private static final String TAG = "YTWebView";

    /**
     * 本地WEB目录
     */
    public static final String WEB_DIR = "file:///android_asset/";

    /**
     * 是否锁定后退键
     */
    private boolean isLockBackKey = false;

    /**
     * 是否获取标题，默认为true
     */
    private boolean isGetTitle = true;

    /**
     * 屏蔽返回，执行js
     */
    private String backJS;

    /**
     * 最后访问的url。 用来做reload用
     */
    private String lastUrl = "";

    /**
     * 最后访问的baseUrl。 用来做reload用
     */
    private String lastBaseUrl = "";

    /**
     * 最后访问的url是否带sign。 用来做reload用
     */
    private boolean lastIsSign;

    /**
     * 手动传过来的BaseUrl
     */
    private String manuallyBaseUrl;

    private Context ctx;


    private Activity act;


    private WebView webView;


    public YTWebView(Context context, AttributeSet attrs) {
        super(context, attrs);


    }


}
