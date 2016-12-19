package bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/11/21.
 */

public class TopStoriesBean implements Parcelable {
    private String image;
    private int type;
    private int id;
    private String ga_prefix;
    private String title;

    protected TopStoriesBean(Parcel in) {
        image = in.readString();
        type = in.readInt();
        id = in.readInt();
        ga_prefix = in.readString();
        title = in.readString();
    }
    public TopStoriesBean(){


    }

    public static final Creator<TopStoriesBean> CREATOR = new Creator<TopStoriesBean>() {
        @Override
        public TopStoriesBean createFromParcel(Parcel in) {
            return new TopStoriesBean(in);
        }

        @Override
        public TopStoriesBean[] newArray(int size) {
            return new TopStoriesBean[size];
        }
    };

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGa_prefix() {
        return ga_prefix;
    }

    public void setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeInt(type);
        dest.writeInt(id);
        dest.writeString(ga_prefix);
        dest.writeString(title);
    }
}