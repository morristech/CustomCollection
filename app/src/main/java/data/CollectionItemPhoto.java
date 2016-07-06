package data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class CollectionItemPhoto implements Parcelable {
    private int Id = -1;
    private int FkCollectionItemId = -1;
    private String photoUri = "";
    private String photosAsBase64 = "";
    private Bitmap photosAsBitmap = null;

    public CollectionItemPhoto() {}

    protected CollectionItemPhoto(Parcel in) {
        Id = in.readInt();
        FkCollectionItemId = in.readInt();
        photosAsBase64 = in.readString();
        photosAsBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        photoUri = in.readString();
    }

    public static final Creator<CollectionItemPhoto> CREATOR = new Creator<CollectionItemPhoto>() {
        @Override
        public CollectionItemPhoto createFromParcel(Parcel in) {
            return new CollectionItemPhoto(in);
        }

        @Override
        public CollectionItemPhoto[] newArray(int size) {
            return new CollectionItemPhoto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeInt(FkCollectionItemId);
        dest.writeString(photosAsBase64);
        dest.writeParcelable(photosAsBitmap, flags);
        dest.writeString(photoUri);
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getFkCollectionItemId() {
        return FkCollectionItemId;
    }

    public void setFkCollectionItemId(int fkCollectionItemId) {
        FkCollectionItemId = fkCollectionItemId;
    }

    public String getPhotosAsBase64() {
        return photosAsBase64;
    }

    public void setPhotosAsBase64(String photosAsBase64) {
        this.photosAsBase64 = photosAsBase64;
    }

    public Bitmap getPhotosAsBitmap() {
        return photosAsBitmap;
    }

    public void setPhotosAsBitmap(Bitmap photosAsBitmap) {
        this.photosAsBitmap = photosAsBitmap;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

}
