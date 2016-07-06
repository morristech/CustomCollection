package fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import adapters.AddItemAdapter;
import ca.useful.customcollection.R;
import data.Bundles;
import data.CollectionItem;
import data.CollectionItemPhoto;
import data.DatabaseHelper;

public class AddCollectionItemFragment extends Fragment implements View.OnClickListener {
    private ListView listView;
    private AddItemAdapter adapter;
    private Button okButton;
    private int collectionId;
    private CollectionItem item;

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
    public void onSaveInstanceState(Bundle out) {
        out.putParcelable(Bundles.COLLECTIONITEMEXTRA, item);
        super .onSaveInstanceState(out);
    }

    private void bind() {
        if (getActivity() != null && getView() != null) {
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
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
            databaseHelper.insertCollectionItem(item);
            databaseHelper.close();
            getActivity().getSupportFragmentManager().popBackStack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPhoto(CollectionItemPhoto photo) {
        photo.setFkCollectionItemId(item.getId());
        item.getPhotos().add(photo);
        bind();
    }
}
