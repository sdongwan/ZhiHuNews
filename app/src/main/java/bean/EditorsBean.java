package bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/12/1.
 */

public class EditorsBean implements Parcelable {
    private String url;
    private String bio;
    private int id;
    private String avatar;
    private String name;

    protected EditorsBean(Parcel in) {
        url = in.readString();
        bio = in.readString();
        id = in.readInt();
        avatar = in.readString();
        name = in.readString();
    }

    public static final Creator<EditorsBean> CREATOR = new Creator<EditorsBean>() {
        @Override
        public EditorsBean createFromParcel(Parcel in) {
            return new EditorsBean(in);
        }

        @Override
        public EditorsBean[] newArray(int size) {
            return new EditorsBean[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(bio);
        dest.writeInt(id);
        dest.writeString(avatar);
        dest.writeString(name);
    }
}
