/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.maogousoft.ytwebview.R;

public class PullToRefreshWebView extends PullToRefreshBase<WebView> {

    private static final OnRefreshListener<WebView> defaultOnRefreshListener = new OnRefreshListener<WebView>() {

        @Override
        public void onRefresh(PullToRefreshBase<WebView> refreshView) {
            refreshView.getRefreshableView().reload();
        }

    };

    private final WebChromeClient defaultWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                onRefreshComplete();
            }
        }

    };

    public PullToRefreshWebView(Context context) {
        super(context);

        /**
         * Added so that by default, Pull-to-Refresh refreshes the page
         */
        setOnRefreshListener(defaultOnRefreshListener);
        mRefreshableView.setWebChromeClient(defaultWebChromeClient);
    }

    public PullToRefreshWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        /**
         * Added so that by default, Pull-to-Refresh refreshes the page
         */
        setOnRefreshListener(defaultOnRefreshListener);
        mRefreshableView.setWebChromeClient(defaultWebChromeClient);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected WebView createRefreshableView(Context context, AttributeSet attrs) {
        WebView webView;
        if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
            InternalWebViewSDK9 w9 = new InternalWebViewSDK9(context, attrs);
            w9.setPullView(this);
            webView = w9;
        } else {
            webView = new WebView(context, attrs);
        }

        webView.setId(R.id.webview);
        return webView;
    }

    @Override
    protected boolean isReadyForPullStart() {
        return mRefreshableView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        double exactContentHeight = Math.floor(mRefreshableView.getContentHeight() * mRefreshableView.getScale());
        return mRefreshableView.getScrollY() >= (exactContentHeight - mRefreshableView.getHeight());
    }

    @Override
    protected void onPtrRestoreInstanceState(Bundle savedInstanceState) {
        super.onPtrRestoreInstanceState(savedInstanceState);
        mRefreshableView.restoreState(savedInstanceState);
    }

    @Override
    protected void onPtrSaveInstanceState(Bundle saveState) {
        super.onPtrSaveInstanceState(saveState);
        mRefreshableView.saveState(saveState);
    }
}
