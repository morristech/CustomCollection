package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import adapters.CollectionAdapter;
import ca.useful.customcollection.MainActivity;
import ca.useful.customcollection.R;
import data.Bundles;
import data.Collection;

/**
 * Created by Jeremy on 26/05/2016.
 */
public class CollectionListFragment extends Fragment {
    private static final String TAG = "CollectionListFrag";
    private ArrayList<Collection> collections = new ArrayList<>();
    private ListView listView;
    private CollectionAdapter adapter;

    public static CollectionListFragment newInstance(ArrayList<Collection> collections) {
        CollectionListFragment fragment = new CollectionListFragment();
        Bundle b = new Bundle();
        b.putParcelableArrayList(Bundles.COLLECTIONEXTRA, collections);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);
        if (getArguments() != null) {
            if (getArguments().get(Bundles.COLLECTIONEXTRA) != null) {
                collections = getArguments().getParcelableArrayList(Bundles.COLLECTIONEXTRA);
            }
        }
        setUpListView();
    }

    public void setUpListView() {
        if (getActivity() != null) {
            if (collections == null) {
                collections = new ArrayList<>();
            }
            listView = (ListView) getView().findViewById(R.id.collection_listview);
            adapter = new CollectionAdapter(getActivity(), collections);
            listView.setAdapter(adapter);
        }
    }


    public CollectionAdapter getAdapter() {
        return adapter;
    }

    public ListView getListView() {
        return listView;
    }
}
