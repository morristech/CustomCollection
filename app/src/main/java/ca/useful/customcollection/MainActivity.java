package ca.useful.customcollection;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import data.Collection;
import data.CollectionItem;
import data.DatabaseHelper;
import fragments.AddCollectionItemFragment;
import fragments.CollectionItemsFragment;
import fragments.CollectionListFragment;

public class MainActivity extends FragmentActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    private ArrayList<Collection> collections = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private CollectionListFragment collectionListFragment;
    private CollectionItemsFragment collectionItemsFragment;
    private AddCollectionItemFragment addCollectionItemFragment;
    private EditText etCollectionName;
    private ImageButton btnAddCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        bind();
    }

    @Override
    protected void onStart() {
        super .onStart();
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment test = getSupportFragmentManager().findFragmentByTag("collectionList");
                if (test != null && test.isVisible()) {
                    if (collectionListFragment != null && collectionListFragment.getListView() != null) {
                        collectionListFragment.getListView().setOnItemClickListener(MainActivity.this);
                    }
                }
            }
        });
        if (collectionListFragment != null && collectionListFragment.getListView() != null) {
            collectionListFragment.getListView().setOnItemClickListener(MainActivity.this);
        }
    }

    protected void bind() {
        if (collections == null || collections.isEmpty()) {
            databaseHelper = new DatabaseHelper(this);
            collections = databaseHelper.getCollections();
            databaseHelper.close();
        }
        collectionListFragment = CollectionListFragment.newInstance(collections);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_fragment_layout, collectionListFragment, "collectionList")
                .commit();

        etCollectionName = (EditText) findViewById(R.id.main_collection_name);
        btnAddCollection = (ImageButton) findViewById(R.id.main_collection_add_button);
        btnAddCollection.setOnClickListener(this);

    }

    public void refreshCollections() {
        databaseHelper = new DatabaseHelper(this);
        collections = databaseHelper.getCollections();
        databaseHelper.close();

        if (collectionListFragment != null && collectionListFragment.getListView() != null) {
            collectionListFragment.getListView().setOnItemClickListener(this);
        }
    }

    public void saveCollection(Collection collection) {
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.insertCollection(collection);
        databaseHelper.close();
        refreshCollections();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        collectionItemsFragment = CollectionItemsFragment.newInstance(collectionListFragment.getAdapter().getItem(position));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_layout, collectionItemsFragment, "collectionItems")
                .addToBackStack("collectionItems")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    public void onClick(View v) {
        if (etCollectionName == null) {
            etCollectionName = (EditText) findViewById(R.id.main_collection_name);
        }
        Fragment collectionList = getSupportFragmentManager().findFragmentByTag("collectionList");
        Fragment collectionItemList = getSupportFragmentManager().findFragmentByTag("collectionItems");
        if (collectionList != null && collectionList.isVisible()) {
            Collection collection = new Collection();
            collection.setTitle(etCollectionName.getText().toString());
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            databaseHelper.insertCollection(collection);
            etCollectionName.setText("");
            refreshCollections();
        }
        if (collectionItemList != null && collectionItemList.isVisible()) {
            addCollectionItemFragment = AddCollectionItemFragment.newInstance(new CollectionItem());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_layout, addCollectionItemFragment, "addCollectionFragment")
                    .addToBackStack("addCollectionFragment")
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onResume() {
        super .onResume();

    }
}
