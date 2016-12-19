package com.highspace.zhihunews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.CommentsAdapter;
import bean.CommentsBean;
import bean.NewsCommentBean;
import constants.ApiConstant;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.OkHttpUtil;
import view.LoadingDialog;

public class CommentActivity extends SwipeBackActivity implements View.OnClickListener {
    private ImageButton mBackIB;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;


    private int mId;
    private String mLongComUrl;
    private int mLongCount;
    private String mShortComUrl;
    private int mShortCount;
    private Handler mHandler;
    private Handler mShortHandler;
    private List<CommentsBean> mLongComList;
    private List<CommentsBean> mShortComList;
    private CommentsAdapter mCommentsAdapter;
    private NewsCommentBean newsCommentBean;
    private LoadingDialog loadingDialog;

    private static final int GET_OK = 0;
    public static final String ID = "id";
    public static final String LONG = "LONG";
    public static final String SHORT = "short";
    public static final int SHORT_FLAG = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        // Bmob.initialize(this, "03487e0e22a3e6e1aef9506e4f1c32b5");
        Intent intent = getIntent();
        if (intent != null) {
            mId = intent.getIntExtra(ID, 0);
            mLongComUrl = ApiConstant.NEWS_LONG_COMMENT + mId + "/long-comments";
            mShortComUrl = ApiConstant.NEWS_SHORT_COMMENT + mId + "/short-comments";
            mLongCount = intent.getIntExtra(LONG, 0);
            mShortCount = intent.getIntExtra(SHORT, 0);
        }

        initView();
        initEvent();
    }

    public void showLoadingDialog() {
        loadingDialog = new LoadingDialog();
        loadingDialog.show(getSupportFragmentManager(), "loadingdialog");

    }

    public void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }

    }

    private void initView() {

        mBackIB = (ImageButton) findViewById(R.id.toolbar_comment_back);
        mRecyclerView = (RecyclerView) findViewById(R.id.comment_act_rcy);
    }

    private void initEvent() {
        mBackIB.setOnClickListener(this);
        mLongComList = new ArrayList<>();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == GET_OK) {
                    if (newsCommentBean != null) {
                        mLongComList = newsCommentBean.getComments();
                    } else {
                        mLongComList = null;
                    }

                    mLayoutManager = new LinearLayoutManager(CommentActivity.this);
                    mCommentsAdapter = new CommentsAdapter(mLongComList, CommentActivity.this, mLongCount, mShortCount);
                    mRecyclerView.setAdapter(mCommentsAdapter);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mRecyclerView.addItemDecoration(new DividerItemDecoration(CommentActivity.this, DividerItemDecoration.VERTICAL));


                    mCommentsAdapter.setOnShortCom(new CommentsAdapter.OnShortCom() {
                        @Override
                        public void getShortCom(final List<CommentsBean> commentsBeanList) {
                            showLoadingDialog();
                            Toast.makeText(CommentActivity.this, "回调开始", Toast.LENGTH_SHORT).show();
                            OkHttpUtil.getInstance().getFromNet(mShortComUrl, new okhttp3.Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Toast.makeText(CommentActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (response.body().toString() != null) {

                                        String content = response.body().string();
                                        Gson gson = new Gson();
                                        newsCommentBean = gson.fromJson(content, NewsCommentBean.class);
                                        if (newsCommentBean != null) {
                                            // mHandler.sendEmptyMessage(GET_OK);
                                            mShortComList = newsCommentBean.getComments();
                                            mShortHandler.sendEmptyMessage(SHORT_FLAG);
                                        }


                                    }
                                }
                            });

                            mShortHandler = new Handler(Looper.getMainLooper()) {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    if (msg.what == SHORT_FLAG) {
                                        commentsBeanList.addAll(mShortComList);
                                        mCommentsAdapter.notifyDataSetChanged();
                                        hideLoadingDialog();

                                    }

                                }
                            };


                        }
                    });
                }


            }
        };

        OkHttpUtil.getInstance().getFromNet(mLongComUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(CommentActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body().toString() != null) {

                    String content = response.body().string();
                    Gson gson = new Gson();
                    newsCommentBean = gson.fromJson(content, NewsCommentBean.class);

                    mHandler.sendEmptyMessage(GET_OK);


                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_comment_back:
                onBackPressed();
                break;
        }

    }
}
