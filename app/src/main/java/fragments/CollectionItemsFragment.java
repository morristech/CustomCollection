package fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;

import adapters.CollectionItemAdapter;
import ca.useful.customcollection.MainActivity;
import ca.useful.customcollection.R;
import data.Bundles;
import data.Collection;
import data.CollectionItem;
import data.DatabaseHelper;

public class CollectionItemsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "CollectionItemsFrag";
    private GridView gridView;
    private Collection collection;
    private CollectionItemAdapter adapter;
    private String message = "";
    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;

    public static CollectionItemsFragment newInstance(Collection collection) {
        CollectionItemsFragment fragment = new CollectionItemsFragment();
        Bundle b = new Bundle();
        b.putParcelable("collection", collection);
        fragment.setArguments(b);
        return fragment;
    }

    public static CollectionItemsFragment newInstance(String message) {
        CollectionItemsFragment fragment = new CollectionItemsFragment();
        Bundle b = new Bundle();
        b.putString("message", message);
        fragment.setArguments(b);
        return fragment;
    }

    public void changeArguments(Collection collection) {
        if (getArguments().containsKey("collection")) {
            getArguments().remove("collection");
            getArguments().putParcelable("collection", collection);
        }
    }

    public void changeArguments(String message) {
        if (getArguments().containsKey("message")) {
            getArguments().remove("message");
            getArguments().putString("message", message);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance) {
        super .onViewCreated(view, savedInstance);
        if (getArguments() != null) {
            if (getArguments().getString("message") != null) {
                message = getArguments().getString("message");
            }
            if (getArguments().getParcelable("collection") != null) {
                collection = getArguments().getParcelable("collection");
            }
        }
        setUpGridView();
    }

    private void setUpGridView() {
        if (getActivity() != null) {
            gridView = (GridView)getActivity().findViewById(R.id.collection_item_gridview);
            if (collection != null) {
                adapter = new CollectionItemAdapter(getActivity(), collection);
                gridView.setAdapter(adapter);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getActivity().getString(R.string.collection_item_select_collection));
                builder.setMessage(message);
                builder.setPositiveButton(getActivity().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity)getActivity()).popToCollectionPage();
                        }
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO zoom view as per here: https://developer.android.com/training/animation/zoom.html
        //TODO edit/Delete
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                                collection.getItems().get(position).setPhoto(bitmap);
                                collection.getItems().get(position).populateBase64FromBitmap();
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).saveCollection(collection);
                                }
                            } else {
                                CollectionItem item = new CollectionItem();
                                String name = extra.getString(Bundles.NAMEEXTRA);
                                String description = extra.getString(Bundles.DESCRIPTIONEXTRA);
                                double value = extra.getDouble(Bundles.VALUEEXTRA);
                                String reference = extra.getString(Bundles.REFERENCEEXTRA);
                                item.setName(name);
                                item.setDescription(description);
                                item.setValue(value);
                                item.setCustomIndexReminder(reference);
                                item.setPhoto(bitmap);
                                item.populateBase64FromBitmap();
                                item.setFkCollectionId(collection.getId());
                                DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                                long id = databaseHelper.insertCollectionItem(item);
                                item.setId((int)id);
                                collection.getItems().add(item);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    public void takePhoto(int position) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        Bundle b = new Bundle();
        b.putInt("position", position);
        intent.putExtra("positionBundle", b);
        startActivityForResult(intent, TAKE_PICTURE);
    }
        /*
    TODO: Add New item, Delete item
     */
}
