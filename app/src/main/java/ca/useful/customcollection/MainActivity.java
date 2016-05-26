package ca.useful.customcollection;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import adapters.PagerCollectionAdapter;
import data.Collection;
import data.DatabaseHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private ViewPager viewPager = null;
    private PagerCollectionAdapter adapter;
    private ArrayList<Collection> collections = new ArrayList<>();
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bind();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void bind() {
        if (collections == null || collections.isEmpty()) {
            databaseHelper = new DatabaseHelper(this);
            collections = databaseHelper.getCollections();
            databaseHelper.close();
        }
        viewPager = (ViewPager)findViewById(R.id.main_viewpager);
        adapter = new PagerCollectionAdapter(getSupportFragmentManager(), this, collections);
        viewPager.setAdapter(adapter);
    }

    public void popToCollectionPage() {
        if (adapter != null) {
            viewPager.setCurrentItem(0, true);
        }
    }

    public void refreshCollections() {
        databaseHelper = new DatabaseHelper(this);
        collections = databaseHelper.getCollections();
        databaseHelper.close();
        viewPager = (ViewPager)findViewById(R.id.main_viewpager);
        adapter = new PagerCollectionAdapter(getSupportFragmentManager(), this, collections);
        viewPager.setAdapter(adapter);
    }

    public void saveCollection(Collection collection) {
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.insertCollection(collection);
        databaseHelper.close();
        int pagerIndex = 0;
        if (viewPager != null) {
            pagerIndex = viewPager.getCurrentItem();
        }
        refreshCollections();
        viewPager.setCurrentItem(pagerIndex);
    }

    public void setSelectedCollectionIndex(int selectedIndex) {
        if (adapter != null) {
            adapter.setIndex(selectedIndex);
        }
    }
}
