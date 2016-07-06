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

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    private ArrayList<Collection> collections = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private CollectionListFragment collectionListFragment;
    private AddCollectionItemFragment addCollectionItemFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        bind();
    }

    @Override
    protected void onStart() {
        super .onStart();
        /*getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

            }
        });*/
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
