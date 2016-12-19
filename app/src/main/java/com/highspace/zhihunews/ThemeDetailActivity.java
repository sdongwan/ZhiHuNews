package com.highspace.zhihunews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bean.NewsDetailBean;
import bean.NewsExtraBean;
import bean.StoriesBean;
import constants.ApiConstant;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.NetworkUtil;
import util.OkHttpUtil;

public class ThemeDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mBackIB;
    private ImageButton mShareIB;
    private ImageButton mCommentIB;
    private ImageButton mStarIB;
    private ImageButton mZanIB;
    private TextView mZanCount;
    private TextView mCommentCount;


    private StoriesBean mStoriesBean;
    private WebView mWebView;
    private WebSettings mWebSettings;
    private NewsDetailBean mNewsDetailBean;
    private NewsExtraBean mNewsExtraBean;

    private Handler mHandler;
    public static List<String> mImgUrlList;


    public static final String CONTENT_KIND = "kind";

    private boolean isCommonFlag = false;

    private static String mNewsExtraUrl;
    private int mLongComCount;
    private int mShortComCount;

    private LinearLayout mEnptyView;
    private String mNewsDetailUrl;
    private String mNewsShareUrl;

    private static final int TYPE_NEWS_EXTRA = 6;
    private static final int TYPE_NEWS_DETAIL = 5;

    public static final String STORIES_BEAN = "stories_bean";

    private Toolbar mToolbar;
    private RelativeLayout emptyView;
    private boolean isNetWorkOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_detail);
        //  Bmob.initialize(this, "03487e0e22a3e6e1aef9506e4f1c32b5");
        isNetWorkOk = NetworkUtil.isConnected(this);
        initView();
        if (isNetWorkOk) {
            Intent intent = getIntent();
            if (intent != null) {
                mStoriesBean = intent.getParcelableExtra(STORIES_BEAN);
                mNewsExtraUrl = ApiConstant.NEWS_EXTRA_INFO + mStoriesBean.getId();
                mNewsDetailUrl = ApiConstant.NEWS_DETAIL_URL + mStoriesBean.getId();
            }

            initEvent();
            setSupportActionBar(mToolbar);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }

    private void initEvent() {
        mBackIB.setOnClickListener(this);
        mCommentIB.setOnClickListener(this);
        mStarIB.setOnClickListener(this);
        mShareIB.setOnClickListener(this);
        mZanIB.setOnClickListener(this);


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == TYPE_NEWS_DETAIL) {

                    if (mNewsDetailBean.getBody() != null) {
                        String html = replaceImgInHtml(loadHtml(mNewsDetailBean));
                        mNewsShareUrl = mNewsDetailBean.getShare_url();
                        mWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
                        mEnptyView.setVisibility(View.GONE);
                    }

                } else if (msg.what == TYPE_NEWS_EXTRA) {

                    mZanCount.setText(mNewsExtraBean.getPopularity() + "");
                    mCommentCount.setText(mNewsExtraBean.getComments() + "");
                    mLongComCount = mNewsExtraBean.getLong_comments();
                    mShortComCount = mNewsExtraBean.getShort_comments();

                }

            }
        };

            /*
            获取新闻内容
             */
        OkHttpUtil.getInstance().getFromNet(mNewsDetailUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Toast.makeText(ThemeDetailActivity.this, "" + "失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body().toString() != null) {
                    Gson gson = new Gson();
                    mNewsDetailBean = gson.fromJson(response.body().string(), NewsDetailBean.class);
                    mHandler.sendEmptyMessage(TYPE_NEWS_DETAIL);
                }
            }
        });


        /*
        获取新闻额外信息
         */
        OkHttpUtil.getInstance().getFromNet(mNewsExtraUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(ThemeDetailActivity.this, "" + "失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body().toString() != null) {
                    Gson gson = new Gson();
                    mNewsExtraBean = gson.fromJson(response.body().string(), NewsExtraBean.class);
                    mHandler.sendEmptyMessage(TYPE_NEWS_EXTRA);
                }

            }
        });
    }

    private void initView() {
        emptyView = (RelativeLayout) findViewById(R.id.activity_theme_detail_empty_view);
        mToolbar = (Toolbar) findViewById(R.id.news_detail_toolbar);
        mCommentCount = (TextView) findViewById(R.id.toolbar_comment_count);
        mZanCount = (TextView) findViewById(R.id.toolbar_zan_count);
        mEnptyView = (LinearLayout) findViewById(R.id.loading_view);
        mEnptyView.setVisibility(View.VISIBLE);
        mBackIB = (ImageButton) findViewById(R.id.toolbar_news_back);
        mZanIB = (ImageButton) findViewById(R.id.toolbar_news_zan);
        mCommentIB = (ImageButton) findViewById(R.id.toolbar_news_comment);
        mShareIB = (ImageButton) findViewById(R.id.toolbar_news_share);
        mStarIB = (ImageButton) findViewById(R.id.toolbar_news_star);
        mWebView = (WebView) findViewById(R.id.news_detail_wb);
        mWebView.setWebViewClient(new WebViewClient());
        mWebSettings = mWebView.getSettings();
        mWebView.setWebChromeClient(new WebChromeClient());
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
        mWebView.addJavascriptInterface(this, "ZhihuNews");


    }

    /*
  <!doctype html>
  <html>
  <head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,user-scalable=no">

   */
    public static String loadHtml(NewsDetailBean newsDetailBean) {
        StringBuilder sb = new StringBuilder("<!doctype html>\n<html><head><meta charset=\"utf-8\">\n" +
                "\t<meta name=\"viewport\" content=\"width=device-width,user-scalable=no\">");
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">\n";
        String replaceImg = "<script src=\"file:///android_asset/img_replace.js\"></script>\n";
        sb.append(css).
                append(replaceImg)
                .append("</head><body  >")//onload=\"onLoadHtml()\"
                .append(newsDetailBean.getBody())
                .append("</body>\n</html>");

        String html = sb.toString();
        //html = html.replace("<div class=\"img-place-holder\">", "<img src=\"" + newsDetailBean.getImage() + "\"  " + "heght=\"280\" width=\"100%\" ");
        html = html.replace("<div class=\"img-place-holder\">", "");
        return html;
    }

    public String replaceImgInHtml(String html) {
        mImgUrlList = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("img");
        for (Element element : elements) {
            if (!"avatar".equals(element.attr("class"))) {
                String imgUrl = element.attr("src");
                mImgUrlList.add(imgUrl);
                element.attr("onclick", "onImageClick(this)");
            }
        }

        return document.html();
    }


    @JavascriptInterface
    public void openImage(String imgUrl) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra(ImageActivity.IMG_URL, imgUrl);
        intent.putExtra(ImageActivity.THEME_FLAG, true);
        ThemeDetailActivity.this.startActivity(intent);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_news_back:
                onBackPressed();
                break;
            case R.id.toolbar_news_comment:
                Intent commentIntent = new Intent(ThemeDetailActivity.this, CommentActivity.class);
                commentIntent.putExtra(CommentActivity.LONG, mLongComCount);
                commentIntent.putExtra(CommentActivity.SHORT, mShortComCount);
                commentIntent.putExtra(CommentActivity.ID, mStoriesBean.getId());
                startActivity(commentIntent);
                break;

            case R.id.toolbar_news_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, mNewsShareUrl);
                startActivity(Intent.createChooser(intent, "分享到...."));
                break;
            case R.id.toolbar_news_star:

                break;
            case R.id.toolbar_news_zan:

                break;

        }


    }

}
