package jswebproduction.searchbaractivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Jsweb Stage 2 on 10/04/2017.
 */

public class Browser extends SearchWidget {
    String url;

    private class BrowserHolder extends Holder {
        WebView     browser;

        public void setViewElements(View convertView) {
            this.browser = (WebView) convertView.findViewById(R.id.browser_webView);
            this.browser.setTag("Webview Tag");
            this.browser.setWebViewClient(new MyBrowser(Browser.super.context));
            this.browser.getSettings().setLoadsImagesAutomatically(true);
            this.browser.getSettings().setJavaScriptEnabled(true);
            this.browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            this.browser.setVerticalScrollBarEnabled(false);
            this.browser.setHorizontalScrollBarEnabled(false);
            this.browser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        v.clearFocus();
                }
            });
            this.browser.loadUrl(url);
        }

        public void updateViewElements(Object data) {

        }
    }

    public Browser () {
        super.layoutId = R.layout.browser_fragment_layout;
        super.context = getActivity();
        this.url = "http://www.google.com";
    }

    public void setUrl(String query) {
        this.url = query;
    }

    public Holder getHolder() {
        return new BrowserHolder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("BROWSER_FRAGMENT", "onCreateView called");

        View view = inflater.inflate(R.layout.browser_fragment_layout, container, false);
        return view;
    }

    private class MyBrowser extends WebViewClient {
        private static final String         filename = "test.js";
        private Context                     context = null;
        private String                      jsFileContent = null;

        public MyBrowser(Context context) {
            this.context = context;
            if(jsFileContent == null) {
                jsFileContent = "";
                try {
                    if (this.context == null)
                        throw new RuntimeException("context is not initialized");
                    AssetManager am = context.getAssets();
                    InputStream is = am.open(filename);
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while (( line = br.readLine()) != null) {
                        jsFileContent += line;
                    }
                    is.close();
                }
                catch(Exception e) {
                    Log.d("BROWSER_INNER_CLASS", String.valueOf(e));
                }
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url){
            super.onPageFinished(view, url);
            view.loadUrl("javascript:(" + jsFileContent + ")()");
            view.requestLayout();
            Log.d("ON_PAGE_FINISH", "Callback On page finish");
        }
    }
}
