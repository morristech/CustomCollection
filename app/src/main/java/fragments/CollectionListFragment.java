package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import adapters.CollectionAdapter;
import ca.useful.customcollection.R;
import data.Bundles;
import data.Collection;
import data.DatabaseHelper;

/**
 * Created by Jeremy on 26/05/2016.
 */
public class CollectionListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = "CollectionListFrag";
    private ArrayList<Collection> collections = new ArrayList<>();
    private ListView listView;
    private CollectionAdapter adapter;
    private EditText etCollectionName;
    private ImageButton btnAddCollection;
    private DatabaseHelper databaseHelper;
    private CollectionItemsFragment collectionItemsFragment;

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
            etCollectionName = (EditText) getView().findViewById(R.id.main_collection_name);
            btnAddCollection = (ImageButton) getView().findViewById(R.id.main_collection_add_button);
            btnAddCollection.setOnClickListener(this);
            listView.setOnItemClickListener(this);
            listView.setOnItemLongClickListener(this);
        }
    }


    public CollectionAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onClick(View v) {
        if (getActivity() != null) {
            if (etCollectionName == null) {
                etCollectionName = (EditText) getView().findViewById(R.id.main_collection_name);
            }
            Fragment collectionList = getActivity().getSupportFragmentManager().findFragmentByTag("collectionList");
            if (collectionList != null && collectionList.isVisible()) {
                Collection collection = new Collection();
                collection.setTitle(etCollectionName.getText().toString());
                DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                databaseHelper.insertCollection(collection);
                etCollectionName.setText("");
                refreshCollections();
            }
        }
    }


    public void refreshCollections() {
        if (getActivity() != null) {
            databaseHelper = new DatabaseHelper(getActivity());
            collections = databaseHelper.getCollections();
            databaseHelper.close();
            setUpListView();
            if (listView != null) {
                listView.setOnItemClickListener(this);
            }
            if (getArguments() != null) {
                getArguments().putParcelableArrayList(Bundles.COLLECTIONEXTRA, collections);
            }
        }
    }

    public void saveCollection(Collection collection) {
        if (getActivity() != null) {
            databaseHelper = new DatabaseHelper(getActivity());
            databaseHelper.insertCollection(collection);
            databaseHelper.close();
            refreshCollections();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getActivity() != null) {
            collectionItemsFragment = CollectionItemsFragment.newInstance(getAdapter().getItem(position));
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_layout, collectionItemsFragment, "collectionItems")
                    .addToBackStack("collectionItems")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            if (getActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View dialogView = inflater.inflate(R.layout.dialog_yes_no, null);
                builder.setView(dialogView);
                final AlertDialog dialog = builder.show();
                Button btnYes = (Button)dialog.findViewById(R.id.dialog_yes_no_button_yes);
                Button btnNo = (Button)dialog.findViewById(R.id.dialog_yes_no_button_no);
                TextView description = (TextView)dialog.findViewById(R.id.dialog_yes_no_description);
                TextView title = (TextView)dialog.findViewById(R.id.dialog_yes_no_title);
                title.setText(R.string.delete_collection);
                description.setText(R.string.delete_collection_confirm);
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getActivity() != null) {
                            DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                            databaseHelper.deleteCollection(collections.get(position));
                            databaseHelper.close();
                            collections.remove(position);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        return true;
    }
}
