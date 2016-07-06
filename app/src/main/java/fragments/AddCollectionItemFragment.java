package fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import adapters.AddItemAdapter;
import ca.useful.customcollection.R;
import data.Bundles;
import data.CollectionItem;
import data.CollectionItemPhoto;

public class AddCollectionItemFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "AddItemFrag";
    public static final String POSITION_BUNDLE = "positionBundle";
    private ListView listView;
    private AddItemAdapter adapter;
    private Button okButton;
    private int collectionId;
    private CollectionItem item;
    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;

    public static AddCollectionItemFragment newInstance(int collectionId) {
        AddCollectionItemFragment fragment = new AddCollectionItemFragment();
        Bundle b = new Bundle();
        b.putInt(Bundles.COLLECTIONIDEXTRA, collectionId);
        fragment.setArguments(b);
        return fragment;
    }

    public static AddCollectionItemFragment newInstance(CollectionItem item) {
        AddCollectionItemFragment fragment = new AddCollectionItemFragment();
        Bundle b = new Bundle();
        b.putParcelable(Bundles.COLLECTIONITEMEXTRA, item);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_collection_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance) {
        super .onViewCreated(view, savedInstance);
        if (savedInstance != null) {
            item = savedInstance.getParcelable(Bundles.COLLECTIONITEMEXTRA);
        } else if (getArguments() != null) {
            if (getArguments().get(Bundles.COLLECTIONIDEXTRA) != null) {
                collectionId = getArguments().getInt(Bundles.COLLECTIONIDEXTRA);
                item = new CollectionItem();
                item.setFkCollectionId(collectionId);
            } else {
                item = getArguments().getParcelable(Bundles.COLLECTIONITEMEXTRA);
            }
        }
        bind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extra = data.getBundleExtra(Bundles.REFERENCEEXTRA);
                    if (extra != null) {
                        CollectionItem storedItem = extra.getParcelable(Bundles.COLLECTIONITEMEXTRA);
                        String uri = extra.getString(Bundles.IMAGEURI);
                        Uri selectedImage = Uri.parse(uri);
                        if (getActivity() != null) {
                            getActivity().getContentResolver().notifyChange(selectedImage, null);
                            ContentResolver cr = getActivity().getContentResolver();
                            Bitmap bitmap;

                            try {
                                //edit case
                                bitmap = android.provider.MediaStore.Images.Media
                                        .getBitmap(cr, selectedImage);
                                CollectionItemPhoto photo = new CollectionItemPhoto();
                                photo.setFkCollectionItemId(storedItem.getId());
                                photo.setPhotosAsBitmap(bitmap);
                                photo.setPhotoUri(selectedImage.toString());
                                storedItem.addOrReplacePhoto(photo);
                                item = storedItem;
                                bind();
//                                Bundle newArgs = new Bundle();
//                                newArgs.putParcelable(Bundles.COLLECTIONITEMEXTRA, storedItem);
//                                setArguments(newArgs);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            default:
                super .onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        out.putParcelable(Bundles.COLLECTIONITEMEXTRA, item);
        super .onSaveInstanceState(out);
    }

    private void bind() {
        if (getActivity() != null) {
            okButton = (Button) getView().findViewById(R.id.add_collection_item_ok_button);
            listView = (ListView)getView().findViewById(R.id.add_collection_item_listview);
            adapter = new AddItemAdapter(getActivity(), item);
            listView.setAdapter(adapter);
            okButton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        //ok button click event saves item: Convert Bitmap and URI to Base64 and write
    }
}
