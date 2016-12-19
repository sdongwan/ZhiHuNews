package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.highspace.zhihunews.EditorInfoActivity;
import com.highspace.zhihunews.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import bean.EditorsBean;
import de.hdodenhof.circleimageview.CircleImageView;
import util.DensityUtil;

/**
 * Created by Administrator on 2016/12/2.
 */

public class EditorAdapter extends RecyclerView.Adapter<EditorAdapter.EditorVH> {
    private List<EditorsBean> editorsBeanList;
    private Context context;

    public EditorAdapter(Context context, List<EditorsBean> editorsBeanList) {
        this.context = context;
        this.editorsBeanList = editorsBeanList;
    }


    @Override
    public EditorAdapter.EditorVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View editorView = LayoutInflater.from(context).inflate(R.layout.item_editor, parent, false);
        return new EditorVH(editorView);
    }

    @Override
    public void onBindViewHolder(EditorAdapter.EditorVH holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditorInfoActivity.class);
                intent.putExtra(EditorInfoActivity.EDITOR_ID, editorsBeanList.get(position).getId());
                context.startActivity(intent);
            }
        });

        Picasso.with(context).
                load(editorsBeanList.get(position).getAvatar()).
                placeholder(R.mipmap.icon_head).
                resize(DensityUtil.dp2px(context, 45), DensityUtil.dp2px(context, 45)).
                into(holder.headImg);

        holder.nameTv.setText(editorsBeanList.get(position).getName());
        if (editorsBeanList.get(position).getBio() == null) {
            holder.BioTv.setText("无个人简历信息");
        } else {
            holder.BioTv.setText(editorsBeanList.get(position).getBio());
        }


    }

    @Override
    public int getItemCount() {
        return editorsBeanList.size();
    }


    class EditorVH extends RecyclerView.ViewHolder {
        protected CircleImageView headImg;
        protected TextView nameTv;
        protected TextView BioTv;

        public EditorVH(View itemView) {
            super(itemView);
            headImg = (CircleImageView) itemView.findViewById(R.id.item_editor_civ);
            nameTv = (TextView) itemView.findViewById(R.id.item_editor_name);
            BioTv = (TextView) itemView.findViewById(R.id.item_editor_bio);

        }

    }


}
