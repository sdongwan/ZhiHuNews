package fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.highspace.zhihunews.HomeActivity;
import com.highspace.zhihunews.R;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import adapter.StoriesAdapter;
import bean.NewsBean;
import bean.StoriesBean;
import bean.TopStoriesBean;
import callback.ILoadCallBack;
import constants.ApiConstant;
import db.NewsHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.DateUtil;
import util.NetworkUtil;
import util.OkHttpUtil;

import static com.highspace.zhihunews.HomeActivity.isNetWorkOk;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String HOME_URL = "home_url";


    // TODO: Rename and change types of parameters
    private String mUrl;


    private OnFragmentInteractionListener mListener;


    private RecyclerView mNewsRecycleView;
    private StoriesAdapter mStoriesAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private SwipeRefreshLayout mSwipeRefreshLayout;


    private Handler mNetHandler;


    private static final String Get_FLAG = "getString";
    private static final String Load_FLAG = "load";

    private static final int DB_FLAG = 1;

    private static final int GET_DATA = 0;

    private int mLoadTimes = 1;

    private NewsBean mNewsBean;
    private OkHttpUtil mOkHttpUtil;


    private TextView mEmptyView;


    private Handler bannerHander;
    private static final int SWITCH_PAGER = 10;

    private static final int FAIL_FLAG = 2;


    private LinearLayout circleLL;
    private TextView headerText;
    private ViewPager mBanner;
    private Timer timer;

    private String freshTime;
    private TimerTask mTimerTask;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(HOME_URL, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(HOME_URL);

        }

        isNetWorkOk = NetworkUtil.isConnected(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View homeView = inflater.inflate(R.layout.fragment_home, container, false);
        initView(homeView);
        initEvent();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });

        return homeView;
    }

    private void initView(View homeView) {
        mEmptyView = (TextView) homeView.findViewById(R.id.home_empty_view);
        mNewsRecycleView = (RecyclerView) homeView.findViewById(R.id.recycle_news);
        mSwipeRefreshLayout = (SwipeRefreshLayout) homeView.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        View bannerView = LayoutInflater.from(getActivity()).inflate(R.layout.header_banner, new FrameLayout(getActivity()));
        RelativeLayout relativeLayout = (RelativeLayout) bannerView.findViewById(R.id.header_parent_view);
        mBanner = (ViewPager) bannerView.findViewById(R.id.header_vp);
        timer = new Timer();

    }

    private void initEvent() {
        //loadNewsData(null, Get_FLAG);
        mNewsBean = new NewsBean();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mNetHandler = new Handler(getActivity().getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == GET_DATA) {
                    if (msg.obj.equals(Get_FLAG)) {

                        if (isNetWorkOk) {
                            NewsHelper.getInstance(getActivity()).insertStoriesBeanByCount(mNewsBean.getStories(), mLoadTimes, "今日热闻");
                            NewsHelper.getInstance(getActivity()).insertTopStories(mNewsBean.getTop_stories());

                        }

                        mStoriesAdapter = new StoriesAdapter(getActivity(), mNewsBean);
                        mStoriesAdapter.setiLoadCallBack(new ILoadCallBack() {
                            @Override
                            public void loadNews(List<StoriesBean> newsDatas) {
                                freshTime = DateUtil.getBeforeNFormatDate(mLoadTimes);
                                mStoriesAdapter.removeLoadItem();
                                mStoriesAdapter.addDateItem(freshTime);

                                if (HomeActivity.isNetWorkOk) {
                                    loadNewsData(ApiConstant.NEWS_BEFORE_URL + DateUtil.getBeforeNStringDate(mLoadTimes), newsDatas, Load_FLAG);
                                } else {
                                    List<StoriesBean> list = NewsHelper.getInstance(getActivity()).getStoriesByCount(mLoadTimes);

                                    if (list.size() == 0) {
                                        Toast.makeText(getActivity(), "无法获取更多", Toast.LENGTH_SHORT).show();
                                        mNetHandler.sendEmptyMessage(FAIL_FLAG);

                                    } else {

                                        newsDatas.addAll(list);
                                        Message msg1 = Message.obtain();
                                        msg1.what = GET_DATA;
                                        msg1.obj = Load_FLAG;
                                        mNetHandler.sendMessage(msg1);
                                    }
                                }


                                mLoadTimes++;
                            }
                        });
                        mNewsRecycleView.setAdapter(mStoriesAdapter);
                        mNewsRecycleView.setLayoutManager(mLayoutManager);
                        Toast.makeText(getActivity(), "刷新完成", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    } else {

                        mStoriesAdapter.addLoadItem();
                        mStoriesAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "加载完成", Toast.LENGTH_SHORT).show();
                        if (HomeActivity.isNetWorkOk)
                            NewsHelper.getInstance(getActivity()).insertStoriesBeanByCount(mNewsBean.getStories(), mLoadTimes - 1, freshTime);
                    }
                } else if (msg.what == FAIL_FLAG) {

                    Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
                    mStoriesAdapter.removeLoadItem();
                    mStoriesAdapter.notifyDataSetChanged();

                    // TODO: 2016/12/3 弹出设置对话框

                }

            }
        };


        bannerHander = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == SWITCH_PAGER) {

                   /*
                   还是通过layoutinflater引用不到viewpager
                    */

                    StoriesAdapter.switchPage();

                }

            }
        };


    }


    private void loadNewsData(String url, final List<StoriesBean> newsData, final String loadFlag) {
        mOkHttpUtil = OkHttpUtil.getInstance();

        mOkHttpUtil.getFromNet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                /*

                if (!isNetWorkOk) {
                    mNetHandler.sendEmptyMessage(FAIL_FLAG);
                }
                */
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body().toString() != null) {
                    final Gson gson = new Gson();
                    String result = response.body().string();
                    /*
                       try {
                        Thread.currentThread().sleep(1500L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                     */
                    Log.e("result", result);
                    mNewsBean = gson.fromJson(result, NewsBean.class);

                    if (newsData != null) {
                        newsData.addAll(mNewsBean.getStories());

                    }


                    Log.e("result", mNewsBean.getStories().get(0).getTitle());
                    Message msg = Message.obtain();
                    msg.what = GET_DATA;
                    msg.obj = loadFlag;
                    mNetHandler.sendMessage(msg);
                }
            }
        });


    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

            // TODO: 2016/12/1   interesting
            /*
               throw new RuntimeException(context.toString() +
                    " must implement OnFragmentInteractionListener");
             */

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        mLoadTimes = 1;
        if (isNetWorkOk) {
            mEmptyView.setVisibility(View.GONE);
            loadNewsData(ApiConstant.NEWS_LASTNEST_UTL, null, Get_FLAG);
        } else {
            // TODO: 2016/12/3 弹出设置网络对话框

            Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
            List<TopStoriesBean> topStories = NewsHelper.getInstance(getActivity()).getTopStories();
            List<StoriesBean> storiesBeen = NewsHelper.getInstance(getActivity()).getStoriesByCount(mLoadTimes - 1);
            mNewsBean.setTop_stories(topStories);
            mNewsBean.setStories(storiesBeen);
            if (topStories.size() == 0 || storiesBeen.size() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                Message msg = Message.obtain();
                msg.what = GET_DATA;
                msg.obj = Get_FLAG;
                mNetHandler.sendMessage(msg);

            }
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    public void onStart() {
        super.onStart();
        startTimer();
    }

    public void stopTimer() {

        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
        if (timer != null) {
            timer.cancel();
        }

    }

    public void startTimer() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                bannerHander.sendEmptyMessage(SWITCH_PAGER);
            }
        };
        timer = new Timer();
        timer.schedule(mTimerTask, 0, 6000);

    }
}
