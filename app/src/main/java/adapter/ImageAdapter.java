package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cunoraz.gifview.library.GifView;
import com.highspace.zhihunews.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import util.MobleUtil;

/**
 * Created by Administrator on 2016/11/24.
 */

public class ImageAdapter extends PagerAdapter {

    private List<String> imgs;
    private Context context;
    private PhotoViewAttacher mPhotoViewAttacher;
    private GifView mGifView;

    public ImageAdapter(List<String> imgs, Context context) {
        this.imgs = imgs;
        this.context = context;
    }

    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View) object);

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View imgView = LayoutInflater.from(context).inflate(R.layout.item_photoview_img, container, false);
        ImageView iv = (ImageView) imgView.findViewById(R.id.item_img_iv);
       // mGifView= (GifView) imgView.findViewById(R.id.item_img_iv);
        loadImg(imgs.get(position), iv, context);
        container.addView(iv);
        // mPhotoViewAttacher = new PhotoViewAttacher(iv);

        return imgView;

    }

    private void loadImg(String url, ImageView iv, final Context context) {
        Transformation transformation = new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                int targetWith = MobleUtil.getScreenWidth(context);
                int imgWith = source.getWidth();
                int imgHeght = source.getHeight();
                if (imgHeght <= 0 || imgWith <= 0 || targetWith <= 0) {
                    //source.recycle();
                    return source;

                }
                if (imgWith < targetWith || imgWith > targetWith) {
                    double scale = targetWith / imgWith;
                    Bitmap bitmap = Bitmap.createScaledBitmap(source, targetWith, ((int) (source.getHeight() * scale) == 0 ? source.getHeight() : (int) (source.getHeight() * scale)), false);
                    source.recycle();
                    return bitmap;

                }


                return source;
            }

            @Override
            public String key() {
                return "trasformation" + "targetwith";
            }
        };

        Picasso.with(context).load(url).transform(transformation).placeholder(R.mipmap.icon_empty_face).into(iv);
    }
}
