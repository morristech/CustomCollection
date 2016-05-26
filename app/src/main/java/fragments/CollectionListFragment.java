package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import java.util.ArrayList;

import adapters.CollectionAdapter;
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
        b.putParcelableArrayList("collections", collections);
        fragment.setArguments(b);
        return fragment;
    }

    /*
    TODO: XML Resource with listview, populate, onItemClick stores position, Add New Collection, Delete Collection
     */
}
