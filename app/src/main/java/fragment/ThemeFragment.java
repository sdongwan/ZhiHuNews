package fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.highspace.zhihunews.HomeActivity;
import com.highspace.zhihunews.R;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import adapter.ThemeAdapter;
import bean.StoriesBean;
import bean.ThemesBean;
import callback.ILoadCallBack;
import constants.ApiConstant;
import db.NewsHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.OkHttpUtil;


public class ThemeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String THEME_ID = "theme_detail_url";
    private static final String THEME_POSITION = "theme_detail_positon";


    // TODO: Rename and change types of parameters
    private int mThemeId;

    private String mThemeUrl;
    private int mThemePosition;


    private OnFragmentInteractionListener mListener;


    private RecyclerView mThemeRcy;
    private ThemeAdapter mThemeAdapter;


    private RecyclerView.LayoutManager mLayoutManager;


    private SwipeRefreshLayout mSwipeRefreshLayout;


    private Handler mThemeNetHandler;


    private static final String Get_FLAG = "getString";
    private static final String Load_FLAG = "load";

    private static final int FAIL_FLAG = 2;

    private static final int GET_DATA = 0;

    private int mLoadTimes = 1;

    private ThemesBean mThemesBean;
    private OkHttpUtil mOkHttpUtil;
    private RelativeLayout mEmptyView;

    public ThemeFragment() {
        // Required empty public constructor
    }


    public static ThemeFragment newInstance(int param1, int param2) {
        ThemeFragment fragment = new ThemeFragment();
        Bundle args = new Bundle();
        args.putInt(THEME_ID, param1);
        args.putInt(THEME_POSITION, param2);
        fragment.setArguments(args);


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mThemeId = getArguments().getInt(THEME_ID, 1);
            mThemePosition = getArguments().getInt(THEME_POSITION, 1);

            mThemeUrl = ApiConstant.THEME_DETAIL_URL + mThemeId;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View themeView = inflater.inflate(R.layout.fragment_theme, container, false);
        initView(themeView);
        initEvent();

          mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });



        return themeView;
    }


    private void initView(View themeView) {
        mThemesBean = new ThemesBean();
        mEmptyView = (RelativeLayout) themeView.findViewById(R.id.theme_empty_view);
        mThemeRcy = (RecyclerView) themeView.findViewById(R.id.theme_rcy);
        mSwipeRefreshLayout = (SwipeRefreshLayout) themeView.findViewById(R.id.theme_swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

    }

    private void initEvent() {
        //loadNewsData(null, Get_FLAG);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mThemeNetHandler = new Handler(getActivity().getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == GET_DATA) {
                    if (msg.obj.equals(Get_FLAG)) {
                        if (HomeActivity.isNetWorkOk) {
                            if(mThemesBean.getStories()!=null)
                            NewsHelper.getInstance(getActivity()).insertStoriesBeanByIDd(mThemesBean.getStories(), mThemePosition, mLoadTimes - 1);
                        }

                        mThemeAdapter = new ThemeAdapter(getActivity(), mThemesBean,mThemePosition);

                        mThemeAdapter.setiLoadCallBack(new ILoadCallBack() {
                            @Override
                            public void loadNews(List<StoriesBean> newsDatas) {

                                mThemeAdapter.removeLoadItem();
                                if (HomeActivity.isNetWorkOk) {
                                    int index = mThemesBean.getStories().size() - 1;
                                    int storyId = mThemesBean.getStories().get(index).getId();
                                    loadNewsData(ApiConstant.THEME_LOAD_MORE_URL + mThemeId + "/before/" + storyId, newsDatas, Load_FLAG);
                                } else {
                                    // TODO: 2016/12/4

                                    List<StoriesBean> list = NewsHelper.getInstance(getActivity()).getStoriesById(mThemePosition, mLoadTimes);
                                    if (list.size() == 0) {
                                        Toast.makeText(getActivity(), "无法获取更多", Toast.LENGTH_SHORT).show();
                                        mThemeNetHandler.sendEmptyMessage(FAIL_FLAG);
                                    } else {
                                        newsDatas.addAll(list);
                                        Message msg1 = Message.obtain();
                                        msg1.what = GET_DATA;
                                        msg1.obj = Load_FLAG;
                                        mThemeNetHandler.sendMessage(msg1);
                                    }
                                }


                                //mStoriesAdapter.addDateItem(DateUtil.getBeforeNFormatDate(mLoadTimes));

                                mLoadTimes++;
                            }
                        });

                        mThemeRcy.setAdapter(mThemeAdapter);
                        mThemeRcy.setLayoutManager(mLayoutManager);
                       // Toast.makeText(getActivity(), "刷新完成", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);

                    } else {

                        //newsData.addAll(mThemesBean.getStories());
                        mThemeAdapter.addLoadItem();
                        mThemeAdapter.notifyDataSetChanged();
                        if (HomeActivity.isNetWorkOk)
                            NewsHelper.getInstance(getActivity()).insertStoriesBeanByIDd(mThemesBean.getStories(), mThemePosition, mLoadTimes - 1);

                        // Toast.makeText(getActivity(), "加载完成", Toast.LENGTH_SHORT).show();
                    }
                } else if (msg.what == FAIL_FLAG) {

                    Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
                    mThemeAdapter.removeLoadItem();
                    mThemeAdapter.notifyDataSetChanged();

                    // TODO: 2016/12/3 弹出设置对话框

                }

            }
        };


    }


    private void loadNewsData(String url, final List<StoriesBean> storiesBeans, final String loadFlag) {
        mOkHttpUtil = OkHttpUtil.getInstance();

        mOkHttpUtil.getFromNet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Toast.makeText(getActivity(), "数据获取失败了。。。", Toast.LENGTH_SHORT).show();
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
                    try {
                        mThemesBean = gson.fromJson(result, ThemesBean.class);
                    }catch(Exception e){

                    }

                    if (storiesBeans != null) {
                        storiesBeans.addAll(mThemesBean.getStories());
                    }


                    //  Log.e("result", mThemesBean.getStories().getString(0).getTitle());
                    Message msg = Message.obtain();
                    msg.what = GET_DATA;
                    msg.obj = loadFlag;
                    mThemeNetHandler.sendMessage(msg);
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
            // TODO: 2016/12/1  
            /*
             throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
        if (HomeActivity.isNetWorkOk) {
            mEmptyView.setVisibility(View.GONE);
            loadNewsData(mThemeUrl, null, Get_FLAG);

        } else {
            // TODO: 2016/12/4 从数据库中获取数据
            List<StoriesBean> list = NewsHelper.getInstance(getActivity()).getStoriesById(mThemePosition, mLoadTimes - 1);
            if (list.size() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                mThemesBean.setStories(list);
                Message msg = Message.obtain();
                msg.what = GET_DATA;
                msg.obj = Get_FLAG;
                mThemeNetHandler.sendMessage(msg);
            }
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
