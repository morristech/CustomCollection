package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import adapters.CollectionItemAdapter;
import ca.useful.customcollection.R;
import data.Collection;
import data.CollectionItem;

public class CollectionItemsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "CollectionItemsFrag";
    private GridView gridView;
    private Collection collection;
    private CollectionItemAdapter adapter;
    private AddCollectionItemFragment addCollectionItemFragment;

    public static CollectionItemsFragment newInstance(Collection collection) {
        CollectionItemsFragment fragment = new CollectionItemsFragment();
        Bundle b = new Bundle();
        b.putParcelable("collection", collection);
        fragment.setArguments(b);
        return fragment;
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
            if (getArguments().getParcelable("collection") != null) {
                collection = getArguments().getParcelable("collection");
            }
        }
        setUpGridView();
    }

    private void setUpGridView() {
        if (getActivity() != null) {
            gridView = (GridView)getView().findViewById(R.id.collection_item_gridview);
            if (collection != null) {
                adapter = new CollectionItemAdapter(getActivity(), collection);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(this);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO this is edit, add delete
        if (getActivity() != null) {
            if (position != 0) {
                addCollectionItemFragment = AddCollectionItemFragment.newInstance(collection.getItems().get(position));
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(addCollectionItemFragment, "addCollectionFragment")
                        .addToBackStack("addCollectionFragment")
                        .commit();
            } else {
                addCollectionItemFragment = AddCollectionItemFragment.newInstance(new CollectionItem());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_layout, addCollectionItemFragment, "addCollectionFragment")
                        .addToBackStack("addCollectionFragment")
                        .commit();
            }
        }
    }
}
