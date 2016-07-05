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

import adapters.AddItemAdapter;
import ca.useful.customcollection.R;
import data.Bundles;
import data.CollectionItem;
import data.CollectionItemPhoto;

public class AddCollectionItemFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "AddItemFrag";
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
        super .onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getActivity().getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getActivity().getContentResolver();
                    Bitmap bitmap;
                    try {
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);
                        if (data.getBundleExtra("positionBundle") != null) {
                            Bundle extra = data.getBundleExtra("positionBundle");
                            int position = extra.getInt("position");
                            if (position != -1) {
                                CollectionItemPhoto photo = new CollectionItemPhoto();
                                photo.setFkCollectionItemId(item.getId());
                                photo.setPhotosAsBitmap(bitmap);
                                item.getPhotos().add(photo);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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

    public void takePhoto(int position) {
        if (getActivity() != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photo));
            imageUri = Uri.fromFile(photo);
            getActivity().startActivityForResult(intent, TAKE_PICTURE);
        }
    }
}
