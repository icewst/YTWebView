package com.maogousoft.ytwebview.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;


import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 自定义WebView
 *
 * @author Toby
 */
public class YTWebView extends PullToRefreshWebView {


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

    private MyJSObj jsObj;

    public YTWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
        initPullView();
        initWebView();

    }

    private void init(Context context) {

        ctx = context;
        act = (Activity) context;
    }

    private void initPullView() {

        getLoadingLayoutProxy().setTextColor(ColorStateList.valueOf(0xff636363));
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xfff8f8f8);
        getLoadingLayoutProxy().setHeaderBackground(gd);
        getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
        getLoadingLayoutProxy().setPullLabel("下拉刷新");
        getLoadingLayoutProxy().setReleaseLabel("释放开始刷新");
        getLoadingLayoutProxy().setLastUpdatedLabel("最后更新时间:" + formatTime2(System.currentTimeMillis()));

        // 下拉刷新事件
        setOnRefreshListener(new OnRefreshListener<WebView>() {

            @Override
            public void onRefresh(PullToRefreshBase<WebView> refreshView) {
                reload(false);
            }
        });

    }

    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    private void initWebView() {
        webView = getRefreshableView();

        // SDK11，开启硬件加速，会导致白屏。 这里取消硬件加速
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // 将图片调整到适合webview的大小
        webView.getSettings().setUseWideViewPort(true);

        // 设置一些，JS可以调用的本地函数
        jsObj = new MyJSObj();
        webView.addJavascriptInterface(jsObj, JS_OBJ_NAME);

        // 设置加载提示条在加载完成前显示，完成后不显示
        webView.setWebViewClient(new WebViewClient() {

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if (mPageStartedListener != null) {
                    // 从Web里面反调用过来的，他是在非UI线程
                    act.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mPageStartedListener.started();
                        }
                    });
                }
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (mPageFinishedListener != null) {
                    // 从Web里面反调用过来的，他是在非UI线程
                    act.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mPageFinishedListener.finished();
                        }
                    });
                }
            }
        });

        webView.setDownloadListener(new DownloadListener() {

            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {
                // 实现下载的代码
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                act.startActivity(intent);
            }
        });
    }


    /**
     * 不用WebView的reload，用这个替代
     */
    public void reload() {
        Log.i(TAG, "调用MyWebView-reload");
        loadUrl(lastBaseUrl, lastUrl, true, lastIsSign);
    }

    /**
     * 不用WebView的reload，用这个替代
     */
    public void reload(boolean isShowLoadDialog) {
        Log.i(TAG, "调用MyWebView-reload");
        loadUrl(lastBaseUrl, lastUrl, isShowLoadDialog, lastIsSign);
    }

    /**
     * 不用WebView的loadUrl，用这个替代
     */
    public void loadUrl(String url) {
        loadUrl(null, url, true, false);
    }

    /**
     * 不用WebView的loadUrl，用这个替代
     */
    public void loadUrl(String baseUrl, String url) {
        loadUrl(baseUrl, url, true, false);
    }

    /**
     * 不用WebView的loadUrl，用这个替代
     */
    public void loadUrl(String baseUrl, String url, boolean isSign) {
        loadUrl(baseUrl, url, true, isSign);
    }

    private void loadUrl(String baseUrl, String url, boolean isShowLoadDialog, boolean isSign) {
        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "loadUrl参数为空");
            return;
        }

        Log.i(TAG, "loadUrl一次:\n   baseUrl:" + baseUrl + ", \n   url:" + url);

        if (url.startsWith("javascript")) {
            // 调用js，还是用原来的
            webView.loadUrl(url);
        } else if (url.startsWith("file")) {
            // 调用assets的
            lastUrl = url;
            lastBaseUrl = baseUrl;
            lastIsSign = false;// 本地URL，肯定不做加密传输
            // 读取出来
            String content = getFromAssets(ctx, url.replace(WEB_DIR, ""));
            if (content == null) {
                Log.e(TAG, "loadUrl，读取assets数据为空，url为：" + url);
                return;
            }
            // 第一个参数：相对路径，在本例中 HTML 文本内用到的所有资源文件，不论是图片还是其它的 JavaScript 文件或者 CSS
            // 文件，其路径都是相对于这个参数的
            webView.loadDataWithBaseURL(WEB_DIR, content, "text/html", "UTF-8", null);
        } else {
            lastUrl = url;
            lastBaseUrl = baseUrl;
            lastIsSign = isSign;
            webView.loadUrl(url);
        }
    }

    /**
     * 显示Toast
     *
     * @param text 文本内容
     */
    protected void MyToast(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示Toast
     *
     * @param resId string资源id
     */
    protected void MyToast(int resId) {
        Toast.makeText(ctx, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * webview 加载完成
     */
    public OnPageFinishedListener mPageFinishedListener;

    public interface OnPageFinishedListener {

        void finished();
    }

    /**
     * 设置监听，当webview加载完成后 （因为WebView自己的onPageFinish不靠谱，在4.4以上经常不调用，或者无故调用。所以内部实现
     * 替换为 onProgressChanged 100的时候，就认为加载完成）
     *
     * @param listener
     */
    public void setOnPageFinishedListener(OnPageFinishedListener listener) {
        mPageFinishedListener = listener;
    }

    /**
     * webview 开始加载
     */
    public OnPageStartedListener mPageStartedListener;

    public interface OnPageStartedListener {

        void started();
    }

    public void setOnPageStartedListener(OnPageStartedListener listener) {
        mPageStartedListener = listener;
    }

    /**
     * webview 获取到title
     */
    public OnGettedTitleListener mGettedTitleListener;

    public interface OnGettedTitleListener {

        void getted(String title);
    }

    public void setOnGettedTitleListener(OnGettedTitleListener listener) {
        mGettedTitleListener = listener;
    }

    public class MyJSObj {


        @JavascriptInterface
        public void goBackBT() {
            act.finish();
        }

        /**
         * webview通知app在当前界面显示一个native的信息
         */
        @JavascriptInterface
        public void showMsg(final String msg) {
            Log.i(TAG, "调用showMsg，参数:" + msg);

            if (!TextUtils.isEmpty(msg)) {
                Toast.makeText(ctx, urlDecode(msg), Toast.LENGTH_SHORT).show();
            }
        }


        /**
         * webview发起一个请求调用设备的日期选择，然后将选择到的日期传回webview中指定的js方法（
         * callback的值为指定的方法名）
         */
        @JavascriptInterface
        public void dayPicker(String dateset, final String callback) {
            Calendar c = Calendar.getInstance();

            try {
                String[] dateInit = dateset.split("/");
                // 传入月份，需要先减1。显示的时候才是正确的。
                c.set(Integer.parseInt(dateInit[0]), Integer.parseInt(dateInit[1]) - 1, Integer.parseInt(dateInit[2]));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "dayPicker函数，传入的初始化日期转换出错:" + dateset);
            }

            DatePickerDialog dialog = new DatePickerDialog(act, new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker dp, int year, int monthOfYear, int dayOfMonth) {

                    // 选择的月，会比真实的少一月，所以要加1
                    String month = (monthOfYear + 1) + "";
                    if ((monthOfYear + 1) < 10) {
                        // 选择的月，如果是10月以内，需要在前面加个0
                        month = "0" + month;
                    }

                    // 选择的日，如果是10号以内，需要在前面加个0
                    String day = dayOfMonth + "";
                    if (dayOfMonth < 10) {
                        day = "0" + day;
                    }

                    String result = year + "-" + month + "-" + day;
                    Log.i(TAG, "dayPicker，选择结果result:" + result);
                    final String url = "javascript:" + callback + "(\"" + result + "\")";
                    act.runOnUiThread(new Runnable() {

                        public void run() {
                            loadUrl(url);
                        }
                    });

                }

            }, c.get(Calendar.YEAR), // 传入年份
                    c.get(Calendar.MONTH), // 传入月份
                    c.get(Calendar.DAY_OF_MONTH) // 传入天数
            );

            dialog.show();
        }

        /**
         * webview发起一个请求调用设备的时间（以半小时为单位）选择，然后将选择到的时间传回webview中指定的js方法（
         * callback的值为指定的方法名）
         */
        @JavascriptInterface
        public void hourPicker(String hourset, final String callback) {
            Calendar c = Calendar.getInstance();

            try {
                String[] hourInit = hourset.split("/");

                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourInit[0]));
                c.set(Calendar.MINUTE, Integer.parseInt(hourInit[1]));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "hourPicker函数，传入的初始化时间转换出错:" + hourset);
            }

            TimePickerDialog dialog = new TimePickerDialog(act, new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker arg0, int hourOfDay, int minute) {
                    String hourOfDayStr = hourOfDay + "";
                    if (hourOfDay < 10) {
                        hourOfDayStr = "0" + hourOfDayStr;
                    }

                    String minuteStr = minute + "";
                    if (minute < 10) {
                        minuteStr = "0" + minuteStr;
                    }

                    String result = hourOfDayStr + ":" + minuteStr;
                    Log.i(TAG, "hourPicker，选择结果result:" + result);
                    final String url = "javascript:" + callback + "(\"" + result + "\")";
                    act.runOnUiThread(new Runnable() {

                        public void run() {
                            loadUrl(url);
                        }
                    });

                }
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);

            dialog.show();
        }

        /**
         * webview发起一个请求调用设备的日期选择，然后将选择到的日期传回webview中指定的js方法（
         * callback的值为指定的方法名）
         */
        @JavascriptInterface
        public void monPicker(String monset, final String callback) {
            Calendar c = Calendar.getInstance();

            try {
                String[] dateInit = monset.split("/");
                // 传入月份，需要先减1。显示的时候才是正确的。
                c.set(Integer.parseInt(dateInit[0]), Integer.parseInt(dateInit[1]) - 1, 1);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "monPicker函数，传入的初始化日期转换出错:" + monset);
            }

            DatePickerDialog dialog = new DatePickerDialog(act, new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker dp, int year, int monthOfYear, int dayOfMonth) {

                    // 选择的月，会比真实的少一月，所以要加1
                    String month = (monthOfYear + 1) + "";
                    if ((monthOfYear + 1) < 10) {
                        // 选择的月，如果是10月以内，需要在前面加个0
                        month = "0" + month;
                    }

                    String result = year + "-" + month;
                    Log.i(TAG, "monPicker，选择结果result:" + result);
                    final String url = "javascript:" + callback + "(\"" + result + "\")";
                    act.runOnUiThread(new Runnable() {

                        public void run() {
                            loadUrl(url);
                        }
                    });

                }

            }, c.get(Calendar.YEAR), // 传入年份
                    c.get(Calendar.MONTH), // 传入月份
                    c.get(Calendar.DAY_OF_MONTH) // 传入天数
            );

            dialog.show();
        }


        @JavascriptInterface
        public void setTitle(final String value) {
            Log.i(TAG, "调用setTitle，参数value:" + value);
            // webview发起一个setTitle(value)的方法让设备变更当前窗口顶部的title

            if (mGettedTitleListener != null) {
                // 从Web里面反调用过来的，他是在非UI线程
                act.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mGettedTitleListener.getted(value);
                    }
                });
            }
        }


        @JavascriptInterface
        public void backWithJS(String jsname, String value) {
            Log.i(TAG, "调用backWithJS，参数jsname：" + jsname + "，参数value：" + value);
            Intent intent = new Intent();
            intent.putExtra("jsname", jsname);
            intent.putExtra("value", value);
            act.setResult(Activity.RESULT_OK, intent);
            act.finish();
        }


        /**
         * 插入js函数，获取HTML的title
         *
         * @param title
         */
        @JavascriptInterface
        public void getTitleByHtml(final String title) {
            Log.i(TAG, "调用getTitleByHtml，参数:" + title);

            if (mGettedTitleListener != null) {
                // 从Web里面反调用过来的，他是在非UI线程
                act.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mGettedTitleListener.getted(title);
                    }
                });
            }
        }

        /**
         * 通知webview改变当前的baseURL来进行webview内部的页面切换
         *
         * @param domain
         */
        @JavascriptInterface
        public void setBaseURL(String domain) {
            Log.i(TAG, "调用setBaseURL，参数:" + domain);
            manuallyBaseUrl = domain;
        }


        /**
         * 发起一个runPull()的方法，此时设备将对当前webview的下拉刷新机制解除屏蔽
         */
        @JavascriptInterface
        public void runPull() {
            Log.i(TAG, "调用runPull");

            act.runOnUiThread(new Runnable() {

                public void run() {
                    setMode(Mode.PULL_FROM_START);
                }
            });
        }

        /**
         * 发起一个stopPull()的方法，此时设备将对当前webview的下拉刷新机制屏蔽
         */
        @JavascriptInterface
        public void stopPull() {
            Log.i(TAG, "调用stopPull");

            act.runOnUiThread(new Runnable() {

                public void run() {
                    setMode(Mode.DISABLED);
                }
            });
        }

        /**
         * 将webview的滚动条隐藏
         */
        @JavascriptInterface
        public void hideScroll() {
            Log.i(TAG, "调用hideScroll");
            webView.setVerticalScrollBarEnabled(false); // 垂直不显示
        }

        /**
         * 将webview的滚动条取消隐藏
         */
        @JavascriptInterface
        public void unhideScroll() {
            Log.i(TAG, "调用unhideScroll");
            webView.setVerticalScrollBarEnabled(true);
        }


        /**
         * 刷新页面
         */
        @JavascriptInterface
        public void refresh() {
            reload();
        }


    }


    /**
     * 格式化时间戳为11:13
     *
     * @param time
     * @return
     */
    public static String formatTime2(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime(time);
        return format.format(date);
    }


    // 从assets 文件夹中获取文件并读取数据
    public static String getFromAssets(Context ctx, String fileName) {
        String result = null;
        try {
            InputStream in = ctx.getResources().getAssets().open(fileName);
            // 创建byte数组
            byte[] buffer = new byte[in.available()];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
//            result = EncodingUtils.getString(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * url解码
     *
     * @param str 例如：%E4%BB%BB%E5%8A%A1%E4%BA%8B%E4%BB%B6%E7%85%A7%E7%89%87
     * @return 例如：任务事件照片
     */
    public static String urlDecode(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }

        try {
            return java.net.URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str;
    }

}
