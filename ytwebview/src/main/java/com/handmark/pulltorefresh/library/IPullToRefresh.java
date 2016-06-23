/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library;

import android.view.View;
import android.view.animation.Interpolator;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;

public interface IPullToRefresh<T extends View> {


    /**
     * Get the mode that this view is currently in. This is only really useful
     * when using <code>Mode.BOTH</code>.
     *
     * @return Mode that the view is currently in
     */
    Mode getCurrentMode();


    /**
     * Returns a proxy object which allows you to call methods on all of the
     * LoadingLayouts (the Views which show when Pulling/Refreshing).
     * <p/>
     * You should not keep the result of this method any longer than you need
     * it.
     *
     * @return Object which will proxy any calls you make on it, to all of the
     * LoadingLayouts.
     */
    ILoadingLayout getLoadingLayoutProxy();

    /**
     * Returns a proxy object which allows you to call methods on the
     * LoadingLayouts (the Views which show when Pulling/Refreshing). The actual
     * LoadingLayout(s) which will be affected, are chosen by the parameters you
     * give.
     * <p/>
     * You should not keep the result of this method any longer than you need
     * it.
     *
     * @param includeStart - Whether to include the Start/Header Views
     * @param includeEnd   - Whether to include the End/Footer Views
     * @return Object which will proxy any calls you make on it, to the
     * LoadingLayouts included.
     */
    ILoadingLayout getLoadingLayoutProxy(boolean includeStart, boolean includeEnd);

    /**
     * Get the mode that this view has been set to. If this returns
     * <code>Mode.BOTH</code>, you can use <code>getCurrentMode()</code> to
     * check which mode the view is currently in
     *
     * @return Mode that the view has been set to
     */
    Mode getMode();

    /**
     * Get the Wrapped Refreshable View. Anything returned here has already been
     * added to the content view.
     *
     * @return The View which is currently wrapped
     */
    T getRefreshableView();


    /**
     * @return - The state that the View is currently in.
     */
    State getState();

    /**
     * Whether Pull-to-Refresh is enabled
     *
     * @return enabled
     */
    boolean isPullToRefreshEnabled();

    /**
     * Gets whether Overscroll support is enabled. This is different to
     * Android's standard Overscroll support (the edge-glow) which is available
     * from GINGERBREAD onwards
     *
     * @return true - if both PullToRefresh-OverScroll and Android's inbuilt
     * OverScroll are enabled
     */
    boolean isPullToRefreshOverScrollEnabled();

    /**
     * Returns whether the Widget is currently in the Refreshing mState
     *
     * @return true if the Widget is currently refreshing
     */
    boolean isRefreshing();

    /**
     * Returns whether the widget has enabled scrolling on the Refreshable View
     * while refreshing.
     *
     * @return true if the widget has enabled scrolling while refreshing
     */
    boolean isScrollingWhileRefreshingEnabled();

    /**
     * Mark the current Refresh as complete. Will Reset the UI and hide the
     * Refreshing View
     */
    void onRefreshComplete();


    /**
     * Set the mode of Pull-to-Refresh that this view will use.
     *
     * @param mode - Mode to set the View to
     */
    void setMode(Mode mode);

    /**
     * Set OnPullEventListener for the Widget
     *
     * @param listener - Listener to be used when the Widget has a pull event to
     *                 propogate.
     */
    void setOnPullEventListener(OnPullEventListener<T> listener);

    /**
     * Set OnRefreshListener for the Widget
     *
     * @param listener - Listener to be used when the Widget is set to Refresh
     */
    void setOnRefreshListener(OnRefreshListener<T> listener);


    /**
     * Sets the Widget to be in the refresh state. The UI will be updated to
     * show the 'Refreshing' view, and be scrolled to show such.
     */
    void setRefreshing();

    /**
     * Sets the Widget to be in the refresh state. The UI will be updated to
     * show the 'Refreshing' view.
     *
     * @param doScroll - true if you want to force a scroll to the Refreshing
     *                 view.
     */
    void setRefreshing(boolean doScroll);


    /**
     * By default the Widget disables scrolling on the Refreshable View while
     * refreshing. This method can change this behaviour.
     *
     * @param scrollingWhileRefreshingEnabled - true if you want to enable
     *                                        scrolling while refreshing
     */
    void setScrollingWhileRefreshingEnabled(boolean scrollingWhileRefreshingEnabled);


}