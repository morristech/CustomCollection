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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import data.Collection;
import data.CollectionItem;
import data.CollectionItemPhoto;
import data.DatabaseHelper;
import fragments.AddCollectionItemFragment;
import fragments.CollectionItemsFragment;
import fragments.CollectionListFragment;
import fragments.CollectionSummaryFragment;

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
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
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
            CollectionSummaryFragment collectionSummaryFragment = CollectionSummaryFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_layout, collectionSummaryFragment, "collectionSummaryFragment")
                    .addToBackStack("collectionSummaryFragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        } else if (id == R.id.nav_novalue) {
            //page has grid including only items with no assigned value
            DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
            ArrayList<CollectionItem> items = databaseHelper.getCollectionItemsWithNoAssignedValue();
            databaseHelper.close();
            Collection collection = new Collection();
            collection.setTitle("Items With Missing Values");
            collection.setItems(items);
            CollectionItemsFragment collectionItemsFragment = CollectionItemsFragment.newInstance(collection);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_layout, collectionItemsFragment, "missingCollectionItems")
                    .addToBackStack("missingCollectionItems")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        } else if (id == R.id.nav_picturesize) {
            //setting to change photo size in grids
            Toast.makeText(this, R.string.photo_size_error, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_wipe_data) {
            //wipes all user data on positive prompt
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View wipeView = inflater.inflate(R.layout.dialog_yes_no, null);
            builder.setView(wipeView);
            final AlertDialog dialog = builder.show();
            Button btnYes = (Button) dialog.findViewById(R.id.dialog_yes_no_button_yes);
            Button btnNo = (Button) dialog.findViewById(R.id.dialog_yes_no_button_no);
            TextView description = (TextView) dialog.findViewById(R.id.dialog_yes_no_description);
            TextView title = (TextView) dialog.findViewById(R.id.dialog_yes_no_title);
            title.setText(R.string.delete_all_title);
            description.setText(R.string.delete_all);
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                    databaseHelper.deleteAllCollections();
                    databaseHelper.close();
                    bind();
                    dialog.dismiss();
                }

            });

        } else if (id == R.id.nav_instructions_thanks) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.dialog_disclaimer_thank_you, null);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.show();
            Button btnOk = (Button)dialog.findViewById(R.id.dialog_disclaimer_button);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
