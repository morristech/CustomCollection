package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.GridView;

import adapters.CollectionItemAdapter;
import data.Collection;

/**
 * Created by Jeremy on 26/05/2016.
 */
public class CollectionItemsFragment extends Fragment {
    private static final String TAG = "CollectionItemsFrag";
    private GridView gridView;
    private Collection collection;
    private CollectionItemAdapter adapter;

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

        /*
    TODO: XML Resource With Gridview, populate, onItemClick inflates the item's details, Add New item, Delete item
     */
}
