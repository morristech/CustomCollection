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

}
