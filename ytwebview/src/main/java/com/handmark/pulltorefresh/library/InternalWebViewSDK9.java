package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.webkit.WebView;

@TargetApi(9)
public final class InternalWebViewSDK9 extends WebView {

	// WebView doesn't always scroll back to it's edge so we add some
	// fuzziness
	static final int OVERSCROLL_FUZZY_THRESHOLD = 2;

	// WebView seems quite reluctant to overscroll so we use the scale
	// factor to scale it's value
	static final float OVERSCROLL_SCALE_FACTOR = 1.5f;

	private PullToRefreshWebView pullToRefreshWebView;

	public InternalWebViewSDK9(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setPullView(PullToRefreshWebView pullToRefreshWebView) {
		this.pullToRefreshWebView = pullToRefreshWebView;
	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

		final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY,
				maxOverScrollX, maxOverScrollY, isTouchEvent);

		// Does all of the hard work...
		OverscrollHelper.overScrollBy(pullToRefreshWebView, deltaX, scrollX, deltaY, scrollY, getScrollRange(),
				OVERSCROLL_FUZZY_THRESHOLD, OVERSCROLL_SCALE_FACTOR, isTouchEvent);

		return returnValue;
	}

	private int getScrollRange() {
		return (int) Math.max(
				0,
				Math.floor(pullToRefreshWebView.mRefreshableView.getContentHeight()
						* pullToRefreshWebView.mRefreshableView.getScale())
						- (getHeight() - getPaddingBottom() - getPaddingTop()));
	}
}
