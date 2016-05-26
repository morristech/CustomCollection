package data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jeremy on 26/05/2016.
 */
public class CollectionItem implements Parcelable {

    private int Id = -1;
    private String Name = "";
    private String Description = "";
    private String CustomIndexReminder = "";
    private double Value = 0D;
    private Bitmap Photo = null;
    private String Base64Photo = "";
    private int FkCollectionId = -1;

    public CollectionItem() {}

    protected CollectionItem(Parcel in) {
        Id = in.readInt();
        Name = in.readString();
        Description = in.readString();
        CustomIndexReminder = in.readString();
        Value = in.readDouble();
        Photo = in.readParcelable(Bitmap.class.getClassLoader());
        Base64Photo = in.readString();
        FkCollectionId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(Name);
        dest.writeString(Description);
        dest.writeString(CustomIndexReminder);
        dest.writeDouble(Value);
        dest.writeParcelable(Photo, flags);
        dest.writeString(Base64Photo);
        dest.writeInt(FkCollectionId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CollectionItem> CREATOR = new Creator<CollectionItem>() {
        @Override
        public CollectionItem createFromParcel(Parcel in) {
            return new CollectionItem(in);
        }

        @Override
        public CollectionItem[] newArray(int size) {
            return new CollectionItem[size];
        }
    };

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCustomIndexReminder() {
        return CustomIndexReminder;
    }

    public void setCustomIndexReminder(String customIndexReminder) {
        CustomIndexReminder = customIndexReminder;
    }

    public double getValue() {
        return Value;
    }

    public void setValue(double value) {
        Value = value;
    }

    public Bitmap getPhoto() {
        return Photo;
    }

    public void setPhoto(Bitmap photo) {
        Photo = photo;
    }

    public String getBase64Photo() {
        return Base64Photo;
    }

    public void setBase64Photo(String base64Photo) {
        Base64Photo = base64Photo;
    }

    public int getFkCollectionId() {
        return FkCollectionId;
    }

    public void setFkCollectionId(int fkCollectionId) {
        FkCollectionId = fkCollectionId;
    }
}
