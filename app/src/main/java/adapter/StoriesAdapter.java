package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.highspace.zhihunews.HomeDetailActivity;
import com.highspace.zhihunews.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import bean.NewsBean;
import bean.StoriesBean;
import bean.TopStoriesBean;
import callback.ILoadCallBack;

/**
 * Created by Administrator on 2016/11/19.
 */

public class StoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;

    private NewsBean newsBeans;
    private static final int HEADER_VIEW = 0;
    private static final int COMMON_ONE_VIEW = 1;
    private static final int DATE_VIEW = 2;
    private static final int FOOTER_VIEW = 3;
    private static final int COMMON_MUL_VIEW = 4;
    private List<StoriesBean> storiesList;
    private List<String> dateList;

    private Handler bannerHander;
    private Timer timer;
    private static final int SWITCH_PAGER = 10;

    private static final int TYPE_HEAD = 19;
    private static final int TYPE_DATE = 20;
    private static final int TYPE_NEWS = 21;
    private static final int TYPE_LOAD = 22;

    private int loadCount = 0;

    private static ViewPager mViewPager;


    ILoadCallBack iLoadCallBack;

    public void setiLoadCallBack(ILoadCallBack iLoadCallBack) {
        this.iLoadCallBack = iLoadCallBack;

    }


    public StoriesAdapter(Context context, NewsBean newsBeans) {
        this.context = context;
        this.newsBeans = newsBeans;
        storiesList = new ArrayList<>();
        dateList = new ArrayList<>();
        storiesList.addAll(newsBeans.getStories());
        dateList.add(newsBeans.getDate());
        timer = new Timer();
        init();

    }

    private void init() {
        StoriesBean head = new StoriesBean();
        head.setTypeItem(TYPE_HEAD);
        storiesList.add(0, head);

        StoriesBean date = new StoriesBean();
        date.setDate("今日热闻");
        date.setTypeItem(TYPE_DATE);
        storiesList.add(1, date);

        StoriesBean load = new StoriesBean();
        load.setTypeItem(TYPE_LOAD);
        storiesList.add(storiesList.size(), load);
    }

    public void addDateItem(String date) {
        StoriesBean dateBean = new StoriesBean();
        dateBean.setTypeItem(TYPE_DATE);
        dateBean.setDate(date);
        storiesList.add(storiesList.size(), dateBean);
    }

    public void removeLoadItem() {
        storiesList.remove(storiesList.size() - 1);
    }

    public void addLoadItem() {

        StoriesBean load = new StoriesBean();
        load.setTypeItem(TYPE_LOAD);
        storiesList.add(storiesList.size(), load);


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == COMMON_ONE_VIEW) {

            itemView = LayoutInflater.from(context).inflate(R.layout.item_one_img, parent, false);
            return new StoriesOneVH(itemView);
        } else if (viewType == HEADER_VIEW) {

            itemView = LayoutInflater.from(context).inflate(R.layout.header_banner, parent, false);

            return new TopStoriesVH(itemView);
        } else if (viewType == FOOTER_VIEW) {
            itemView = LayoutInflater.from(context).inflate(R.layout.footer_view, parent, false);
            return new LoadVH(itemView);

        }
        itemView = LayoutInflater.from(context).inflate(R.layout.item_tv, parent, false);
        return new DateVH(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TopStoriesVH) {
            ((TopStoriesVH) holder).viewPager.setAdapter(new TopStoriesAdapter(context, (ArrayList<TopStoriesBean>) newsBeans.getTop_stories()));

            return;
        } else if (holder instanceof DateVH) {
            if (position != 1) {
                String date = storiesList.get(position).getDate();
                // TODO: 2016/11/30
                ((DateVH) holder).date.setText(date);
            } else {

                String date = storiesList.get(position).getDate();
                ((DateVH) holder).date.setText(date);

            }


            return;

        } else if (holder instanceof LoadVH) {
            if (iLoadCallBack != null) {
                iLoadCallBack.loadNews(storiesList);


                return;
            }

            return;
        } else if (holder instanceof StoriesOneVH) {
            ((StoriesOneVH) holder).multiImg.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, HomeDetailActivity.class);
                    intent.putExtra(HomeDetailActivity.VIEW_PAGER_POSITION, position);
                    intent.putExtra(HomeDetailActivity.VIEW_PAGER_CONTENT, storiesList.get(position));
                    intent.putExtra(HomeDetailActivity.CONTENT_KIND, HomeDetailActivity.isCommon);
                    context.startActivity(intent);
                }
            });
            if (storiesList.get(position).isMultipic()) {
                ((StoriesOneVH) holder).multiImg.setVisibility(View.VISIBLE);
            }

            if (storiesList.get(position).getTitle() != null)
                ((StoriesOneVH) holder).title.setText(storiesList.get(position).getTitle());
            //处理没联网时候的图片加载
          //   if (((StoriesOneVH) holder).img.getTag() != storiesList.getString(position).getImages().getString(0)) {
                if (storiesList.get(position).getImages() != null&&!storiesList.get(position).getImages().equals("")) {
                    ((StoriesOneVH) holder).img.setTag(storiesList.get(position).getImages().get(0));
                    Picasso.with(context).load(storiesList.get(position).getImages().get(0)).placeholder(R.mipmap.icon_empty_face).resize(80, 80).into(((StoriesOneVH) holder).img);
                }
           // }

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (storiesList.get(position).getTypeItem() == TYPE_DATE) {
            return DATE_VIEW;

        } else if (storiesList.get(position).getTypeItem() == TYPE_HEAD) {
            return HEADER_VIEW;

        } else if (storiesList.get(position).getTypeItem() == TYPE_LOAD) {
            return FOOTER_VIEW;

        } else {
            return COMMON_ONE_VIEW;
        }

    }


    @Override
    public int getItemCount() {
        return storiesList.size();
    }


    class StoriesOneVH extends RecyclerView.ViewHolder {
        protected TextView title;
        protected ImageView img;
        protected ImageView multiImg;


        public StoriesOneVH(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_one_title_tv);
            img = (ImageView) itemView.findViewById(R.id.item_one_pic_iv);
            multiImg = (ImageView) itemView.findViewById(R.id.item_one_pic_multi_iv);
        }
    }

    public static void switchPage() {
        if (mViewPager != null)
            mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1) % 5);
    }

    class LoadVH extends RecyclerView.ViewHolder {

        public LoadVH(View itemView) {
            super(itemView);
        }
    }

    class TopStoriesVH extends RecyclerView.ViewHolder {
        protected ViewPager viewPager;
        protected TextView headerText;

        public TopStoriesVH(View itemView) {
            super(itemView);
            viewPager = (ViewPager) itemView.findViewById(R.id.header_vp);
            mViewPager = viewPager;
            headerText = (TextView) itemView.findViewById(R.id.header_text_tv);
            if (newsBeans.getTop_stories().size() != 0) {
                headerText.setText(newsBeans.getTop_stories().get(0).getTitle());
            }
            LinearLayout linearLayout = (LinearLayout) itemView.findViewById(R.id.circle_ll);
            final LinearLayout circleLL = (LinearLayout) linearLayout.getChildAt(1);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    for (int i = 0; i < circleLL.getChildCount(); i++) {
                        ImageView imageView = (ImageView) circleLL.getChildAt(i);
                        imageView.setImageResource(i == position ? R.drawable.shape_white_circle : R.drawable.shape_black_circle);
                    }

                    if (newsBeans.getTop_stories().size() > 0) {
                        Toast.makeText(context, "size" + newsBeans.getTop_stories().size(), Toast.LENGTH_SHORT).show();
                        headerText.setText(newsBeans.getTop_stories().get(position).getTitle());
                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }
    }

    class DateVH extends RecyclerView.ViewHolder {
        protected TextView date;

        public DateVH(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.item_tv_date);
        }
    }



      /*
        class StoriesMulVH extends RecyclerView.ViewHolder {
            protected TextView titileM;
            protected ImageView imgM1;
            protected ImageView imgM2;
            protected ImageView imgM3;

            public StoriesMulVH(View itemView) {
                super(itemView);

                titileM = (TextView) itemView.findViewById(R.id.item_mul_title_tv);
                imgM1 = (ImageView) itemView.findViewById(R.id.item_mul_pic1_iv);
                imgM2 = (ImageView) itemView.findViewById(R.id.item_mul_pic2_iv);
                imgM3 = (ImageView) itemView.findViewById(R.id.item_mul_pic3_iv);
            }
        }
    */
}
