package bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Administrator on 2016/11/21.
 */

public class StoriesBean implements Parcelable {
    private int type;
    private int id;
    private String ga_prefix;
    private String title;
    private List<String> images;
    private int typeItem = 21;//默认显示的是news item
    private int themeItem = 20;//theme 默认显示的是news item

    private String myDate;
    private String themeTitle;

    public String getThemeTitle() {
        return themeTitle;
    }

    public void setThemeTitle(String themeTitle) {
        this.themeTitle = themeTitle;
    }

    public int getThemeItem() {
        return themeItem;
    }

    public void setThemeItem(int themeItem) {
        this.themeItem = themeItem;
    }

    private String date;
    private boolean multipic;

    public String getMyDate() {
        return myDate;
    }

    public void setMyDate(String myDate) {
        this.myDate = myDate;
    }

    /*
                     ！！！！！theme storiesbean 的内容
           private int type;
            private int id;
            private String title;
            private List<String> images;
         */
    public boolean isMultipic() {
        return multipic;
    }

    public void setMultipic(boolean multipic) {
        this.multipic = multipic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    protected StoriesBean(Parcel in) {
        type = in.readInt();
        id = in.readInt();
        ga_prefix = in.readString();
        title = in.readString();
        images = in.createStringArrayList();
    }

    public StoriesBean() {


    }

    public int getTypeItem() {
        return typeItem;
    }

    public void setTypeItem(int typeItem) {
        this.typeItem = typeItem;
    }

    public static final Creator<StoriesBean> CREATOR = new Creator<StoriesBean>() {
        @Override
        public StoriesBean createFromParcel(Parcel in) {
            return new StoriesBean(in);
        }

        @Override
        public StoriesBean[] newArray(int size) {
            return new StoriesBean[size];
        }
    };

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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeInt(id);
        dest.writeString(ga_prefix);
        dest.writeString(title);
        dest.writeStringList(images);
    }
}
