package com.highspace.zhihunews;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import bean.NewsDetailBean;
import bean.NewsExtraBean;
import bean.StoriesBean;
import bean.TopStoriesBean;
import constants.ApiConstant;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.DensityUtil;
import util.MobleUtil;
import util.NetworkUtil;
import util.OkHttpUtil;

public class HomeDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private int mViewPagerPosition;
    private ImageButton mBackIB;
    private ImageButton mShareIB;
    private ImageButton mCommentIB;
    private ImageButton mStarIB;
    private ImageButton mZanIB;
    private ImageView mBigImg;
    private TextView mZanCount;
    private TextView mCommentCount;

    private TopStoriesBean mTopStoriesBean;
    private StoriesBean mStoriesBean;
    private WebView mWebView;
    private WebSettings mWebSettings;
    private NewsDetailBean mNewsDetailBean;
    private NewsExtraBean mNewsExtraBean;

    private Handler mHandler;
    public static List<String> mImgUrlList;
    public static final String VIEW_PAGER_POSITION = "positon";
    public static final String VIEW_PAGER_CONTENT = "content";
    public static final String CONTENT_KIND = "kind";
    public static final boolean isCommon = true;
    private boolean isCommonFlag = false;

    private static String mNewsExtraUrl;
    private int mLongComCount;
    private int mShortComCount;

    private LinearLayout mEnptyView;
    private String mShareUrl;

    private static final int TYPE_NEWS_EXTRA = 6;
    private static final int TYPE_NEWS_DETAIL = 5;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;

    private boolean isNetWorkOk;
    private RelativeLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        //Bmob.initialize(this, "03487e0e22a3e6e1aef9506e4f1c32b5");
        initView();
        setSupportActionBar(mToolbar);
        // getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
        mCollapsingToolbarLayout.setTitle("新闻详情");
        //通过CollapsingToolbarLayout修改字体颜色
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.BLACK);//设置还没收缩时状态下字体颜色
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);//设置收缩后Toolbar上字体的颜色

        isNetWorkOk = NetworkUtil.isConnected(this);
        if (isNetWorkOk) {
            Intent intent = getIntent();
            mViewPagerPosition = intent.getIntExtra(VIEW_PAGER_POSITION, 0);
            isCommonFlag = intent.getBooleanExtra(CONTENT_KIND, false);
            if (isCommonFlag) {
                mStoriesBean = intent.getParcelableExtra(VIEW_PAGER_CONTENT);
                mNewsExtraUrl = ApiConstant.NEWS_EXTRA_INFO + mStoriesBean.getId();
            } else {
                mTopStoriesBean = intent.getParcelableExtra(VIEW_PAGER_CONTENT);
                mShareUrl = ApiConstant.NEWS_DETAIL_URL + mTopStoriesBean.getId();
                mNewsExtraUrl = ApiConstant.NEWS_EXTRA_INFO + mTopStoriesBean.getId();

            }
            initEvent();
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
                    mEnptyView.setVisibility(View.GONE);
                    String html = replaceImgInHtml(loadHtml(mNewsDetailBean));
                    mShareUrl = mNewsDetailBean.getShare_url();
                    Picasso.with(HomeDetailActivity.this).load(mNewsDetailBean.getImage()).resize(MobleUtil.getScreenWidth(HomeDetailActivity.this), DensityUtil.dp2px(HomeDetailActivity.this, 320)).into(mBigImg);
                    mWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
                    // mWebView.loadUrl("javascript:onLoadHtml()");
                    //mWebView.loadDataWithBaseURL("x-data://base", loadHtml(mNewsDetailBean), "text/html", "UTF-8", null);
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
        OkHttpUtil.getInstance().getFromNet(ApiConstant.NEWS_DETAIL_URL + (isCommonFlag ? mStoriesBean.getId() : mTopStoriesBean.getId()), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(HomeDetailActivity.this, "" + "失败", Toast.LENGTH_SHORT).show();
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
                // Toast.makeText(HomeDetailActivity.this, "" + "失败", Toast.LENGTH_SHORT).show();
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
        emptyView = (RelativeLayout) findViewById(R.id.activity_detail_empty_view);
        mToolbar = (Toolbar) findViewById(R.id.news_detail_toolbar);
        mBigImg = (ImageView) findViewById(R.id.news_detail_big_img);
        mCommentCount = (TextView) findViewById(R.id.toolbar_comment_count);
        mZanCount = (TextView) findViewById(R.id.toolbar_zan_count);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
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
                //String src = "file:///android_asset/default_pic_content_image_loading_light.png";

                //element.attr("src", src);
                //element.attr("newssrc", imgUrl);
                element.attr("onclick", "onImageClick(this)");
            }
        }

        return document.html();
    }


    @JavascriptInterface //sdk17版本以上加上注解
    public void loadImage(final String imgUrl) {

        mWebView.post(new Runnable() {
            @Override
            public void run() {

                // Bitmap bitmap = Picasso.with(HomeDetailActivity.this).load(imgUrl).getString();
                final String arra[] = new String[2];
                Target target = new Target() {

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        String imageName = System.currentTimeMillis() + ".jpg";

                        // File dcimFile = FileUtil.getDCIMFile(FileUtil.PATH_PHOTOGRAPH,imageName);
                        File file = new File(HomeDetailActivity.this.getCacheDir() + "/image" + imageName);
                        String imgPath = "file://" + HomeDetailActivity.this.getCacheDir() + "/image" + imageName;
                        //  Log.e("bitmap="+bitmap);
                        FileOutputStream ostream = null;
                        try {
                            ostream = new FileOutputStream(file);


                            arra[0] = URLEncoder.encode(imgUrl, "utf-8");
                            arra[1] = imgPath;
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, ostream);

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (ostream != null) {
                                try {
                                    ostream.close();
                                    ostream.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Log.e("path", arra[0]);
                                Log.e("path", arra[1]);
                                //mWebView.loadUrl("javascript:onImageLoadComplete(" + arra[0] + "," + arra[1] + ")");
                                onImageLoadingComplete("onImageLoadComplete", arra);
                            }
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                Picasso.with(HomeDetailActivity.this).load(imgUrl).into(target);
            }

/*
                Glide.with(HomeDetailActivity.this).load(imgUrl)
                        .downloadOnly(new SimpleTarget<File>() {
                            @Override
                            public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                                String str = "file://" + resource.getAbsolutePath();//加载完成的图片地址
                                Log.e("path", str);
                                try {
                                    String[] arrayOfString = new String[2];
                                    arrayOfString[0] = URLEncoder.encode(imgUrl, "UTF-8");//旧url
                                    arrayOfString[1] = str;
                                    onImageLoadingComplete("onImageLoadComplete", arrayOfString);
                                } catch (Exception e) {
                                }
                            }
                        });

            }  */
        });


    }

    public final void onImageLoadingComplete(String funName, String[] paramArray) {
        String str = "'" + TextUtils.join("','", paramArray) + "'";
        //Log.e("path", str);
        mWebView.loadUrl("javascript:" + funName + "(" + str + ");");
    }

    @JavascriptInterface
    public void openImage(String imgUrl) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra(ImageActivity.IMG_URL, imgUrl);
        intent.putExtra(ImageActivity.THEME_FLAG, false);
        // intent.putParcelableArrayListExtra(ImageActivity.NEWS_DEAIL_BEAN, mImgUrlList);
        //intent.putExtra(ImageActivity.IMG_POSITION, mNewsDetailBean);
        HomeDetailActivity.this.startActivity(intent);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_news_back:
                onBackPressed();
                break;
            case R.id.toolbar_news_comment:
                Intent commentIntent = new Intent(HomeDetailActivity.this, CommentActivity.class);
                commentIntent.putExtra(CommentActivity.LONG, mLongComCount);
                commentIntent.putExtra(CommentActivity.SHORT, mShortComCount);
                commentIntent.putExtra(CommentActivity.ID, isCommonFlag ? mStoriesBean.getId() : mTopStoriesBean.getId());
                startActivity(commentIntent);
                break;
            case R.id.toolbar_news_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, mShareUrl);
                startActivity(Intent.createChooser(intent, "分享到...."));
                break;
            case R.id.toolbar_news_star:

                break;
            case R.id.toolbar_news_zan:

                break;

        }


    }
}
