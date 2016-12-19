package com.highspace.zhihunews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adapter.ThemeListAdapter;
import bean.OthersBean;
import bean.ThemeListBean;
import constants.ApiConstant;
import fragment.HomeFragment;
import fragment.ThemeFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.DoubleClickExitHelper;
import util.NetworkUtil;
import util.OkHttpUtil;
import util.SpUtil;


public class HomeActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    public static DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;


    private ListView mThemeLv;
    private List<String> themeLists;
    private ThemeListAdapter mThemeListAdapter;
    private RelativeLayout mHeadView;

    private FragmentManager mFragmentManager;

    private ThemeListBean mThemeListBean;
    private static List<OthersBean> othersBeanList;
    private Handler mThemeHandler;


    private static final int THEME_GET_FLAG = 0;
    private static final String FAIL_FLAG = "fail";


    private DoubleClickExitHelper mDoubleClickExitHelper;

    public static boolean isNetWorkOk = true;

    private BroadcastReceiver mBroadcastReceiver;
    private int lastPosition;
    private Fragment mCurrentFragment;


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Bmob.initialize(this, "03487e0e22a3e6e1aef9506e4f1c32b5");
        mDoubleClickExitHelper = new DoubleClickExitHelper(this);
        isNetWorkOk = NetworkUtil.isConnected(this);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                //NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                // NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo activeInfo = manager.getActiveNetworkInfo();

// TODO: 2016/12/5 判断有网络不能用的情况
                if (activeInfo != null) {
                    if (activeInfo.isConnected()) {
                        isNetWorkOk = true;
                    } else {
                        isNetWorkOk = false;
                    }
                } else {
                    isNetWorkOk = false;
                }

            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mBroadcastReceiver, intentFilter);


        initView();
        initEvent();
        setSupportActionBar(mToolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawlayout_open, R.string.drawlayout_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        mFragmentManager = getSupportFragmentManager();
        Fragment fragment = HomeFragment.newInstance(ApiConstant.NEWS_LASTNEST_UTL);
        mCurrentFragment = fragment;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.news_place_to_replace, fragment);
        fragmentTransaction.commit();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean flag = true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否退出应用
            return mDoubleClickExitHelper.onKeyDown(keyCode, event);
        }

        return flag;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

    }


    private void initEvent() {
        mThemeListAdapter = new ThemeListAdapter(themeLists, this);
        mThemeLv.addHeaderView(mHeadView);
        mThemeLv.setAdapter(mThemeListAdapter);

        mThemeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == THEME_GET_FLAG) {
                    if (msg.obj == null) {
                        Toast.makeText(HomeActivity.this, "获取列表信息成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HomeActivity.this, "数据获取失败了。。。", Toast.LENGTH_SHORT).show();
                    }
                    if (isNetWorkOk && othersBeanList != null) {
                        int position;
                        for (int i = 1; i < othersBeanList.size(); i++) {
                            position = othersBeanList.get(i).getId();
                            SpUtil.putInt(i + "", position, HomeActivity.this);
                        }
                    }

                    mThemeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (position == lastPosition) {
                                //donothing
                            } else {


                                switch (position) {
                                    case 0:
                                        // TODO: 2016/12/5 写登陆界面
                                        //startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                        break;
                                    case 1:

                                        chageBackground(position - 1);
                                        setTitle("知乎日报");
                                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                                        Fragment fragment1 = HomeFragment.newInstance(ApiConstant.NEWS_LASTNEST_UTL);
                                        fragmentTransaction.replace(R.id.news_place_to_replace, fragment1).hide(mCurrentFragment);
                                        mCurrentFragment = fragment1;
                                        fragmentTransaction.commit();


                                        break;
                                    case 2:

                                        chageBackground(position - 1);
                                        setTitle(mThemeListAdapter.getItem(position - 1) + "");
                                        replaceFragment(position, mCurrentFragment);
                                        break;
                                    case 3:

                                        chageBackground(position - 1);
                                        setTitle(mThemeListAdapter.getItem(position - 1) + "");
                                        replaceFragment(position, mCurrentFragment);
                                        break;
                                    case 4:

                                        chageBackground(position - 1);
                                        setTitle(mThemeListAdapter.getItem(position - 1) + "");
                                        replaceFragment(position, mCurrentFragment);
                                        break;
                                    case 5:

                                        chageBackground(position - 1);
                                        setTitle(mThemeListAdapter.getItem(position - 1) + "");
                                        replaceFragment(position, mCurrentFragment);
                                        break;
                                    case 6:

                                        chageBackground(position - 1);
                                        setTitle(mThemeListAdapter.getItem(position - 1) + "");
                                        replaceFragment(position, mCurrentFragment);
                                        break;
                                    case 7:

                                        chageBackground(position - 1);
                                        setTitle(mThemeListAdapter.getItem(position - 1) + "");
                                        replaceFragment(position, mCurrentFragment);
                                        break;
                                    case 8:

                                        chageBackground(position - 1);
                                        setTitle(mThemeListAdapter.getItem(position - 1) + "");
                                        replaceFragment(position, mCurrentFragment);
                                        break;
                                    case 9:

                                        chageBackground(position - 1);
                                        setTitle(mThemeListAdapter.getItem(position - 1) + "");
                                        replaceFragment(position, mCurrentFragment);
                                        break;
                                    case 10:
                                        chageBackground(position - 1);
                                        setTitle(mThemeListAdapter.getItem(position - 1) + "");
                                        replaceFragment(position, mCurrentFragment);
                                        break;
                                    case 11:
                                        chageBackground(position - 1);
                                        setTitle(mThemeListAdapter.getItem(position - 1) + "");
                                        replaceFragment(position, mCurrentFragment);
                                        break;
                                    case 12:
                                        chageBackground(position - 1);

                                        setTitle(mThemeListAdapter.getItem(position - 1) + "");
                                        replaceFragment(position, mCurrentFragment);


                                        break;
                                    case 13:
                                        chageBackground(position - 1);
                                        setTitle(mThemeListAdapter.getItem(position - 1) + "");
                                        replaceFragment(position, mCurrentFragment);
                                        break;
                                }
                            }

                            lastPosition = position;
                            mDrawerLayout.closeDrawer(Gravity.LEFT);
                        }
                    });

                }
            }
        };

        getThemeList(ApiConstant.THEME_LIST_URL);


    }

    public void replaceFragment(int position, Fragment mCurrentFragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment;
        if (isNetWorkOk) {
            if (othersBeanList == null || othersBeanList.size() == 0) {
                fragment = ThemeFragment.newInstance(SpUtil.getInt((position - 2) + "", 0, HomeActivity.this), position - 2);
            } else {
                fragment = ThemeFragment.newInstance(othersBeanList.get(position - 2).getId(), position - 2);
            }
        } else {
            fragment = ThemeFragment.newInstance(-1, position - 2);
        }
        fragmentTransaction.replace(R.id.news_place_to_replace, fragment).hide(mCurrentFragment);
        mCurrentFragment = fragment;
        fragmentTransaction.commit();
    }


    public void chageBackground(int position) {
        mThemeListAdapter.setSelectedPosition(position);
        mThemeListAdapter.notifyDataSetChanged();
    }

    private void initView() {

        themeLists = new ArrayList<>();
        String theme[] = new String[]{"首页", "日常心理学", "用户推荐报", "电影日报", "不许无聊", "设计日报", "大公司日报", "财经日报", "互联网安全", "开始游戏", "音乐日报", "动漫日报", "体育日报"};
        themeLists = Arrays.asList(theme);
        mThemeLv = (ListView) findViewById(R.id.drawlayou_lv);
        mHeadView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.theme_ls_head, mThemeLv, false).findViewById(R.id.drawlayout_lv_headview);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawlayout);
        mToolbar = (Toolbar) findViewById(R.id.toorbar);


    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }


    private void getThemeList(String url) {
        OkHttpUtil.getInstance().getFromNet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Message msg = Message.obtain();
                msg.what = THEME_GET_FLAG;
                msg.obj = FAIL_FLAG;
                mThemeHandler.sendMessage(msg);


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body().toString() != null) {
                    final Gson gson = new Gson();
                    String result = response.body().string();
                    ThemeListBean themeListBean = gson.fromJson(result, ThemeListBean.class);
                    othersBeanList = themeListBean.getOthers();
                    mThemeHandler.sendEmptyMessage(THEME_GET_FLAG);
                }
            }
        });


    }


}

