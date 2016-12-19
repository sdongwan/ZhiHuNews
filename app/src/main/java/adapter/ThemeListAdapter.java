package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.highspace.zhihunews.R;

import java.util.List;

/**
 * Created by Administrator on 2016/11/30.
 */

public class ThemeListAdapter extends BaseAdapter {

    private List<String> themeNames;
    private Context context;
    private int selectedPosition=0;

    public ThemeListAdapter(List<String> themeNames, Context context) {
        this.themeNames = themeNames;
        this.context = context;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    @Override
    public int getCount() {
        return themeNames.size();
    }

    @Override
    public Object getItem(int position) {
        return themeNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ThemeVH themeVH = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_theme, parent, false);
            themeVH = new ThemeVH();
            themeVH.themeIcon = (ImageView) convertView.findViewById(R.id.item_theme_iv);
            themeVH.themeName = (TextView) convertView.findViewById(R.id.item_theme_name);
            convertView.setTag(themeVH);
        } else {

            themeVH = (ThemeVH) convertView.getTag();
        }
        convertView.setBackgroundResource(android.R.color.white);
        if (position == selectedPosition) {
            convertView.setBackgroundResource(android.R.color.darker_gray);
        }
        themeVH.themeIcon.setVisibility(View.GONE);
        if (position == 0) {
            themeVH.themeIcon.setVisibility(View.VISIBLE);
        }
        themeVH.themeName.setText(themeNames.get(position));

        return convertView;
    }

    class ThemeVH {
        protected ImageView themeIcon;
        protected TextView themeName;
    }
}
