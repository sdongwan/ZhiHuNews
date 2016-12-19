package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.highspace.zhihunews.HomeDetailActivity;
import com.highspace.zhihunews.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bean.TopStoriesBean;
import util.DensityUtil;
import util.MobleUtil;

/**
 * Created by Administrator on 2016/11/20.
 */

public class TopStoriesAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<TopStoriesBean> datas;


    public TopStoriesAdapter(Context context, ArrayList<TopStoriesBean> datas) {
        this.context = context;
        this.datas = datas;

    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return datas.size() > 5 ? 5 : datas.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner_img, container, false);
        container.addView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.banner_img_iv);
        //imageView.setImageResource(R.mipmap.ic_launcher);
        if (datas.get(position).getImage() != null &&!datas.get(position).getImage().equals("")) {
            // TODO: 2016/12/4 处理没有联网时候的图片加载 
            Picasso.with(context).load(datas.get(position).getImage()).placeholder(R.mipmap.icon_empty_face).resize(MobleUtil.getScreenWidth(context), DensityUtil.dp2px(context, 250)).into(imageView);
        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HomeDetailActivity.class);
                intent.putExtra(HomeDetailActivity.VIEW_PAGER_POSITION, position);
                intent.putExtra(HomeDetailActivity.VIEW_PAGER_CONTENT, datas.get(position));
                intent.putExtra(HomeDetailActivity.CONTENT_KIND, !HomeDetailActivity.isCommon);
                context.startActivity(intent);
            }
        });
        return imageView;

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // super.destroyItem(container, position, object);
        container.removeView((View) object);


    }

}
