package ca.useful.customcollection;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;
import data.Collection;
import data.CollectionItemPhoto;
import data.DatabaseHelper;
import fragments.AddCollectionItemFragment;
import fragments.CollectionListFragment;

public class MainActivity extends FragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ArrayList<Collection> collections = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private CollectionListFragment collectionListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setActionBar(toolbar);
        bind();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            MainActivity.this.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;

                    try {
                        //edit case
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);
                        CollectionItemPhoto photo = new CollectionItemPhoto();
                        photo.setPhotosAsBitmap(bitmap);
                        photo.setPhotoUri(selectedImage.toString());
                        List<Fragment> fragments = getSupportFragmentManager().getFragments();
                        for (Fragment fragment : fragments) {
                            if (fragment instanceof AddCollectionItemFragment && fragment.isVisible()) {
                                ((AddCollectionItemFragment) fragment).addPhoto(photo);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_summary) {
            //summary has added total of items in all collections
        } else if (id == R.id.nav_novalue) {
            //page has grid including only items with no assigned value
        } else if (id == R.id.nav_photosave) {
            //setting to change photo save path
        } else if (id == R.id.nav_picturesize) {
            //setting to change photo size in grids
        } else if (id == R.id.nav_wipe_data) {
            //wipes all user data on positive prompt
        } else if (id == R.id.nav_export) {
            //exports collection in different format
        } else if (id == R.id.nav_email) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
