package data;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Jeremy on 26/05/2016.
 */
public class Collection implements Parcelable {

    private int Id = -1;
    private String Title = "";
    private String Description = "";
    private String Base64Feature = "";
    private Bitmap FeaturePhoto = null;
    private ArrayList<CollectionItem> Items = new ArrayList<>();

    public Collection() {}

    protected Collection(Parcel in) {
        Id = in.readInt();
        Title = in.readString();
        Description = in.readString();
        Base64Feature = in.readString();
        FeaturePhoto = in.readParcelable(Bitmap.class.getClassLoader());
        Items = in.createTypedArrayList(CollectionItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(Title);
        dest.writeString(Description);
        dest.writeString(Base64Feature);
        dest.writeParcelable(FeaturePhoto, flags);
        dest.writeTypedList(Items);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Collection> CREATOR = new Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel in) {
            return new Collection(in);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getBase64Feature() {
        return Base64Feature;
    }

    public void setBase64Feature(String base64Feature) {
        Base64Feature = base64Feature;
    }

    public Bitmap getFeaturePhoto() {
        return FeaturePhoto;
    }

    public void setFeaturePhoto(Bitmap featurePhoto) {
        FeaturePhoto = featurePhoto;
    }

    public ArrayList<CollectionItem> getItems() {
        return Items;
    }

    public void setItems(ArrayList<CollectionItem> items) {
        Items = items;
    }

    public void processPhotos(Context context) {
        if (context != null) {
            for (CollectionItem item : getItems()) {
                item.populateScaledBitmapsFromUri(context);
            }
        }
    }

    public Double getValueSum() {
        double total = 0;
        for (CollectionItem item : getItems()) {
            total += item.getValue();
        }
        return total;
    }
}
