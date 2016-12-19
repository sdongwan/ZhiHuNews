package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.highspace.zhihunews.EditorActivity;
import com.highspace.zhihunews.R;
import com.highspace.zhihunews.ThemeDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bean.StoriesBean;
import bean.ThemesBean;
import callback.ILoadCallBack;
import db.NewsHelper;
import de.hdodenhof.circleimageview.CircleImageView;
import util.DensityUtil;
import util.MobleUtil;

/**
 * Created by Administrator on 2016/12/1.
 */

public class ThemeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;

    private ThemesBean themesBean;

    private static final int HEADER_VIEW = 0;
    private static final int COMMON_VIEW = 1;
    private static final int FOOTER_VIEW = 2;
    private static final int EDIT_VIEW = 3;
    private List<StoriesBean> storiesList;


    private static final int TYPE_EDIT = 18;
    private static final int TYPE_HEAD = 19;
    private static final int TYPE_NEWS = 20;
    private static final int TYPE_LOAD = 21;

    private int loadCount = 0;
    private boolean isFisrtIn = true;


    private ILoadCallBack iLoadCallBack;
    private int themePosition;

    public void setiLoadCallBack(ILoadCallBack iLoadCallBack) {
        this.iLoadCallBack = iLoadCallBack;
    }


    public ThemeAdapter(Context context, ThemesBean themesBean, int themePosition) {
        this.context = context;
        this.themesBean = themesBean;
        storiesList = new ArrayList<>();
        this.themePosition = themePosition;
        if(themesBean.getStories()!=null)
        storiesList.addAll(themesBean.getStories());

        init();

    }

    private void init() {
        StoriesBean head = new StoriesBean();
        head.setThemeItem(TYPE_HEAD);
        storiesList.add(0, head);

        StoriesBean edit = new StoriesBean();
        edit.setThemeItem(TYPE_EDIT);
        storiesList.add(1, edit);


        StoriesBean load = new StoriesBean();
        load.setThemeItem(TYPE_LOAD);
        storiesList.add(storiesList.size(), load);


    }

    public void addEditItem() {
        StoriesBean load = new StoriesBean();
        load.setThemeItem(TYPE_LOAD);
        storiesList.add(storiesList.size(), load);

    }

    public void removeLoadItem() {
        storiesList.remove(storiesList.size() - 1);
    }

    public void addLoadItem() {

        StoriesBean load = new StoriesBean();
        load.setThemeItem(TYPE_LOAD);
        storiesList.add(storiesList.size(), load);


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == COMMON_VIEW) {

            itemView = LayoutInflater.from(context).inflate(R.layout.item_one_img, parent, false);
            return new StoriesVH(itemView);
        } else if (viewType == HEADER_VIEW) {

            itemView = LayoutInflater.from(context).inflate(R.layout.item_theme_img, parent, false);

            return new TopThemeVH(itemView);
        } else if (viewType == FOOTER_VIEW) {
            itemView = LayoutInflater.from(context).inflate(R.layout.footer_view, parent, false);
            return new LoadVH(itemView);

        }
        //// TODO: 2016/12/1  写编辑 的item
        itemView = LayoutInflater.from(context).inflate(R.layout.item_theme_edit, parent, false);

        return new EditVH(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TopThemeVH) {
            if (themesBean != null && themesBean.getBackground() != null)
                Picasso.with(context).load(themesBean.getBackground()).resize(MobleUtil.getScreenWidth(context), DensityUtil.dp2px(context, 250)).into(((TopThemeVH) holder).bigImg);
            if (themesBean != null) {
                if ( themesBean.getDescription() == null||themesBean.getDescription().equals("") ) {

                    String title = NewsHelper.getInstance(context).getThemeTitleById(themePosition);
                    if (title != null) {
                        ((TopThemeVH) holder).descriptionTv.setText(title);
                    }

                } else {
                    ((TopThemeVH) holder).descriptionTv.setText(themesBean.getDescription() + "");
                    NewsHelper.getInstance(context).insertThemeTileById(themesBean.getDescription(), themePosition);
                }
            }


            // TODO: 2016/12/2 添加title
            return;
        } else if (holder instanceof EditVH) {
            if (isFisrtIn) {
                if (themesBean.getEditors() != null) {
                    for (int i = 0; i < themesBean.getEditors().size(); i++) {
                        addCircleView((ViewGroup) (holder.itemView), context, themesBean.getEditors().get(i).getAvatar());
                        Log.e("url", themesBean.getEditors().get(i).getAvatar());
                    }
                    // Toast.makeText(context, "编辑栏圆形头像", Toast.LENGTH_SHORT).show();
                }


                ((EditVH) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 2016/12/2
                        Toast.makeText(context, "你点击了编辑栏", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, EditorActivity.class);
                        intent.putParcelableArrayListExtra(EditorActivity.EDITOR_BEAN, (ArrayList<? extends Parcelable>) themesBean.getEditors());
                        context.startActivity(intent);


                    }
                });

                isFisrtIn = false;
            }
            return;

        } else if (holder instanceof LoadVH) {
            if (iLoadCallBack != null) {
                iLoadCallBack.loadNews(storiesList);
                return;
            }

            return;
        } else if (holder instanceof StoriesVH) {

            if (storiesList.get(position).getTitle() != null) {
                ((StoriesVH) holder).title.setText(storiesList.get(position).getTitle());
                Log.e("title", storiesList.get(position).getTitle());
            }

            if (storiesList.get(position).getImages() == null) {
                ((StoriesVH) holder).img.setVisibility(View.GONE);
                // TODO: 2016/12/6 从数据库加载图片 

            } else {
                ((StoriesVH) holder).img.setVisibility(View.VISIBLE);
                Picasso.with(context).load(storiesList.get(position).getImages().get(0)).resize(80, 80).placeholder(R.mipmap.icon_empty_face).into(((StoriesVH) holder).img);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, "Stories", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, ThemeDetailActivity.class);
                    intent.putExtra(ThemeDetailActivity.STORIES_BEAN, storiesList.get(position));
                    context.startActivity(intent);

                }
            });


        }
    }

    public void addCircleView(ViewGroup viewGroup, Context context, String imgUrl) {
        CircleImageView circleImageView = new CircleImageView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DensityUtil.dp2px(context, 30), DensityUtil.dp2px(context, 30));
        layoutParams.setMargins(DensityUtil.dp2px(context, 10), 0, 0, 0);
        circleImageView.setLayoutParams(layoutParams);
        viewGroup.addView(circleImageView);
        // TODO: 2016/12/5 磁盘缓存
        Picasso.with(context).load(imgUrl).placeholder(R.mipmap.icon_head).resize(DensityUtil.dp2px(context, 30), DensityUtil.dp2px(context, 30)).into(circleImageView);
    }

    @Override
    public int getItemViewType(int position) {
        if (storiesList.get(position).getThemeItem() == TYPE_EDIT) {
            return EDIT_VIEW;

        } else if (storiesList.get(position).getThemeItem() == TYPE_HEAD) {
            return HEADER_VIEW;

        } else if (storiesList.get(position).getThemeItem() == TYPE_LOAD) {
            return FOOTER_VIEW;

        } else {
            return COMMON_VIEW;
        }

    }


    @Override
    public int getItemCount() {
        return storiesList.size();
    }


    class StoriesVH extends RecyclerView.ViewHolder {
        protected TextView title;
        protected ImageView img;


        public StoriesVH(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_one_title_tv);
            img = (ImageView) itemView.findViewById(R.id.item_one_pic_iv);


        }
    }

    class EditVH extends RecyclerView.ViewHolder {


        public EditVH(View itemView) {
            super(itemView);


        }
    }


    class LoadVH extends RecyclerView.ViewHolder {

        public LoadVH(View itemView) {
            super(itemView);
        }
    }

    class TopThemeVH extends RecyclerView.ViewHolder {
        protected ImageView bigImg;
        private TextView descriptionTv;

        public TopThemeVH(View itemView) {
            super(itemView);
            bigImg = (ImageView) itemView.findViewById(R.id.theme_img_iv);
            descriptionTv = (TextView) itemView.findViewById(R.id.theme_img_descirption);
        }
    }


}
