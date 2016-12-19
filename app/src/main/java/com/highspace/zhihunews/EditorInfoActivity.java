package com.highspace.zhihunews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import constants.ApiConstant;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class EditorInfoActivity extends SwipeBackActivity {

    public static final String EDITOR_ID = "editor_id";
    private ImageButton mBackIB;
    private WebView mWebView;
    private String mEditorHomeUrl;
    private WebSettings mWebSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_info);
        // Bmob.initialize(this, "03487e0e22a3e6e1aef9506e4f1c32b5");
        Intent intent = getIntent();
        if (intent != null) {
            mEditorHomeUrl = ApiConstant.EDITOR_HOME_PAGE + intent.getIntExtra(EDITOR_ID, 0) + "/profile-page/android";
        }
        initView();
        initEvent();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
    }

    private void initView() {
        mBackIB = (ImageButton) findViewById(R.id.editor_info_toolbar_back);
        mWebView = (WebView) findViewById(R.id.editor_info_wb);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setAppCachePath(getApplicationContext().getDir("cache", 0).getPath());
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setDomStorageEnabled(true);

        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setDisplayZoomControls(false);
        mWebSettings.setLoadsImagesAutomatically(true);

        mWebView.loadUrl(mEditorHomeUrl);


    }

    private void initEvent() {
        mBackIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
