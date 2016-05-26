package adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

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

    public PagerCollectionAdapter(FragmentManager fm, ArrayList<Collection> collections) {
        super(fm);
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
                    collectionItemsFragment = CollectionItemsFragment.newInstance(collections.get(position));
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
