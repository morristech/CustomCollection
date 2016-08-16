package data;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jeremy on 26/05/2016.
 */
public class CollectionItem implements Parcelable {

    private int Id = -1;
    private String Name = "";
    private String Description = "";
    private String CustomIndexReminder = "";
    private double Value = 0D;
    private int FkCollectionId = -1;
    private int FkMaterialId = -1;
    private Material material = new Material();
    private ArrayList<CollectionItemPhoto> photos = new ArrayList<>();

    public CollectionItem() {
    }


    protected CollectionItem(Parcel in) {
        Id = in.readInt();
        Name = in.readString();
        Description = in.readString();
        CustomIndexReminder = in.readString();
        Value = in.readDouble();
        FkCollectionId = in.readInt();
        FkMaterialId = in.readInt();
        in.readTypedList(photos, CollectionItemPhoto.CREATOR);
        material = in.readParcelable(Material.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(Name);
        dest.writeString(Description);
        dest.writeString(CustomIndexReminder);
        dest.writeDouble(Value);
        dest.writeInt(FkCollectionId);
        dest.writeInt(FkMaterialId);
        dest.writeTypedList(photos);
        dest.writeParcelable(material, flags);
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

    public int getFkCollectionId() {
        return FkCollectionId;
    }

    public void setFkCollectionId(int fkCollectionId) {
        FkCollectionId = fkCollectionId;
    }

    public int getFkMaterialId() {
        return FkMaterialId;
    }

    public void setFkMaterialId(int fkMaterialId) {
        FkMaterialId = fkMaterialId;
    }

    public ArrayList<CollectionItemPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<CollectionItemPhoto> photos) {
        this.photos = photos;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void populateBase64FromBitmap() {
        for (CollectionItemPhoto photo : photos) {
            Bitmap bmp = photo.getPhotosAsBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            photo.setPhotosAsBase64(encoded);
        }
    }

    public void addPhotoItemFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        CollectionItemPhoto photo = new CollectionItemPhoto();
        photo.setPhotosAsBase64(encoded);
        photo.setFkCollectionItemId(getId());
        photos.add(photo);
    }

    public void populateBitmapsFromBase64() {
        for (CollectionItemPhoto photo : photos) {
            String base = photo.getPhotosAsBase64();
            byte[] bytes = Base64.decode(base, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            photo.setPhotosAsBitmap(decodedByte);
        }
    }

    public void addOrReplacePhoto(CollectionItemPhoto photo) {
        if (photo.getId() != -1) {
            for (int i = 0; i < photos.size(); i++) {
                CollectionItemPhoto p = photos.get(i);
                if (p.getId() == photo.getId()) {
                    photos.set(i, photo);
                    return;
                }
            }
        } else {
            photos.add(photo);
        }
    }

    public void populateBitmapsFromUri(Context context) {
        for (CollectionItemPhoto photo : photos) {
            ContentResolver cr = context.getContentResolver();
            try {
                photo.setPhotosAsBitmap(android.provider.MediaStore.Images.Media
                        .getBitmap(cr, Uri.parse(photo.getPhotoUri())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
