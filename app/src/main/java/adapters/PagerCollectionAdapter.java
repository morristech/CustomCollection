package adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import ca.useful.customcollection.R;
import data.Collection;
import fragments.CollectionItemsFragment;
import fragments.CollectionListFragment;

/**
 * Created by Jeremy on 26/05/2016.
 */
public class PagerCollectionAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Collection> collections = new ArrayList<>();
    private CollectionListFragment collectionListFragment;
    private CollectionItemsFragment collectionItemsFragment;
    private int savedPosition = -1;
    private Context context;

    public PagerCollectionAdapter(FragmentManager fm, Context context, ArrayList<Collection> collections) {
        super(fm);
        this.context = context;
        this.collections = collections;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (collectionListFragment == null) {
                    collectionListFragment = CollectionListFragment.newInstance(collections);
                }
                return collectionListFragment;
            case 1:
                if (collectionItemsFragment == null) {
                    if (savedPosition == -1) {
                        collectionItemsFragment = CollectionItemsFragment.newInstance(context.getString(R.string.select_collection));
                    } else {
                        collectionItemsFragment = CollectionItemsFragment.newInstance(collections.get(savedPosition));
                    }
                } else {
                    if (savedPosition == -1) {
                        collectionItemsFragment.changeArguments(context.getString(R.string.select_collection));
                    } else {
                        collectionItemsFragment.changeArguments(collections.get(savedPosition));
                    }
                }
                return collectionItemsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Collections";
            case 1:
                return "Collection Details";
            default:
                return "N/A";
        }
    }
}
