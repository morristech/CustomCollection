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
import data.Collection;

/**
 * Created by Jeremy on 26/05/2016.
 */
public class CollectionListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "CollectionListFrag";
    private ArrayList<Collection> collections = new ArrayList<>();
    private ListView listView;
    private CollectionAdapter adapter;
    private int selectedIndex = -1;

    public static CollectionListFragment newInstance(ArrayList<Collection> collections) {
        CollectionListFragment fragment = new CollectionListFragment();
        Bundle b = new Bundle();
        b.putParcelableArrayList("collections", collections);
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
            if (getArguments().getParcelable("collection") != null) {
                collections = getArguments().getParcelableArrayList("collections");
            }
        }
        if (savedInstance != null && savedInstance.get("selectedIndex") != null) {
            selectedIndex = savedInstance.getInt("selectedIndex");
            if (getActivity() != null) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity)getActivity()).setSelectedCollectionIndex(selectedIndex);
                }
            }
        }
        setUpListView();
    }

    private void setUpListView() {
        if (getActivity() != null) {
            listView = (ListView) getActivity().findViewById(R.id.collection_listview);
            adapter = new CollectionAdapter(getActivity(), collections);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedIndex = position;
        if (getActivity() != null) {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity)getActivity()).setSelectedCollectionIndex(selectedIndex);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        out.putInt("selectedIndex", selectedIndex);
    }
    /*
    TODO: Add New Collection, Delete Collection
     */
}
