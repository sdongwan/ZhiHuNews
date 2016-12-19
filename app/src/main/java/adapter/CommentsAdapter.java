package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.highspace.zhihunews.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bean.CommentsBean;
import de.hdodenhof.circleimageview.CircleImageView;
import util.DateUtil;
import util.DensityUtil;

/**
 * Created by Administrator on 2016/11/27.
 */

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CommentsBean> commentsList = new ArrayList<>();
    private Context context;
    private final static int TYPE_COMMENT = 0;
    private final static int TYPE_MOLD = 1;
    private final static int TYPE_EMPTY = 2;


    private static final int LONG_COM = 1;
    private static final int SHORT_COM = 2;
    private static final int EMPTY_VIEW = 3;
    private int longCount;
    private int shortCount;


    private boolean mOnClickFlag = true;

    private OnShortCom onShortCom;

    public void setOnShortCom(OnShortCom onShortCom) {
        this.onShortCom = onShortCom;
    }

    public CommentsAdapter(List<CommentsBean> commentsList, Context context, int longCount, int shortCount) {
        CommentsBean longTitle = new CommentsBean();
        longTitle.setType(LONG_COM);
        this.commentsList.add(0, longTitle);
        if (commentsList.size() == 0) {
            addLongEmptyView();
        }
        this.commentsList.addAll(commentsList);

        this.context = context;
        this.longCount = longCount;
        this.shortCount = shortCount;
        init();
    }

    private void init() {


        CommentsBean shortTitle = new CommentsBean();
        shortTitle.setType(SHORT_COM);
        this.commentsList.add(this.commentsList.size(), shortTitle);


    }

    public void addLongEmptyView() {
        CommentsBean emptyView = new CommentsBean();
        emptyView.setType(EMPTY_VIEW);
        commentsList.add(1, emptyView);

    }

    public void addTitle(List<CommentsBean> commentsBeans) {
        commentsList.addAll(commentsBeans);
        notifyDataSetChanged();

    }


    public interface OnShortCom {

        public void getShortCom(List<CommentsBean> commentsBeanList);

    }


    @Override
    public int getItemViewType(int position) {
        if (commentsList.get(position).getType() == LONG_COM || commentsList.get(position).getType() == SHORT_COM) {
            return TYPE_MOLD;
        } else if (commentsList.get(position).getType() == EMPTY_VIEW) {
            return TYPE_EMPTY;
        } else {
            return TYPE_COMMENT;
        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == TYPE_COMMENT) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
            return new CommentsVH(itemView);
        } else if (viewType == TYPE_EMPTY) {
            itemView = LayoutInflater.from(context).inflate(R.layout.empty_view, parent, false);
            return new EmptyVH(itemView);
        } else {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_tv, parent, false);
            return new TitleVH(itemView);

        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentsVH) {

            Picasso.with(context).load(commentsList.get(position).getAvatar()).placeholder(R.mipmap.icon_head).resize(DensityUtil.dp2px(context, 38), DensityUtil.dp2px(context, 38)).into((((CommentsVH) holder).headImg));
            ((CommentsVH) holder).title.setText(commentsList.get(position).getAuthor());
            ((CommentsVH) holder).content.setText(commentsList.get(position).getContent());
            ((CommentsVH) holder).zanCount.setText(commentsList.get(position).getLikes() == 0 ? "0" : commentsList.get(position).getLikes() + "");
            // TODO: 2016/11/27
            ((CommentsVH) holder).date.setText(DateUtil.convertTime((long) commentsList.get(position).getTime()));


        } else if (holder instanceof TitleVH) {
            if (commentsList.get(position).getType() == LONG_COM) {
                ((TitleVH) holder).text.setText(longCount + "条长评论");
            } else {
                ((TitleVH) holder).text.setText(shortCount + "条短评论");
                ((TitleVH) holder).arrow.setVisibility(View.VISIBLE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onShortCom != null && mOnClickFlag == true)
                            onShortCom.getShortCom(commentsList);
                        mOnClickFlag = false;
                    }
                });
            }
        } else if (holder instanceof EmptyVH) {
            //((EmptyVH) holder).emptyView.setImageResource(R.mipmap.ic_launcher);
            ((EmptyVH) holder).emptyView.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    class CommentsVH extends RecyclerView.ViewHolder {
        protected CircleImageView headImg;
        protected TextView title;
        protected TextView zanCount;
        protected TextView content;
        protected TextView date;

        public CommentsVH(View itemView) {
            super(itemView);

            headImg = (CircleImageView) itemView.findViewById(R.id.item_comment_head);
            title = (TextView) itemView.findViewById(R.id.item_comment_title);
            zanCount = (TextView) itemView.findViewById(R.id.item_comment_zan_count);
            content = (TextView) itemView.findViewById(R.id.item_comment_content);
            date = (TextView) itemView.findViewById(R.id.item_comment_date);


        }
    }

    class TitleVH extends RecyclerView.ViewHolder {

        protected TextView text;
        protected ImageButton arrow;

        public TitleVH(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.item_tv_date);
            arrow = (ImageButton) itemView.findViewById(R.id.item_tv_arrow);
        }
    }

    class EmptyVH extends RecyclerView.ViewHolder {

        protected ImageView emptyView;

        public EmptyVH(View itemView) {
            super(itemView);
            emptyView = (ImageView) itemView.findViewById(R.id.empty_view_iv);
        }
    }


}
