package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import adapters.AddItemAdapter;
import data.Bundles;

public class AddCollectionItemFragment extends Fragment {
    private static final String TAG = "AddItemFrag";
    //TODO add another, same properties/not same
    private ListView listView;
    private AddItemAdapter adapter;

    public static AddCollectionItemFragment newInstance(int collectionId) {
        AddCollectionItemFragment fragment = new AddCollectionItemFragment();
        Bundle b = new Bundle();
        b.putInt(Bundles.COLLECTIONIDEXTRA, collectionId);
        fragment.setArguments(b);
        return fragment;
    }
}
