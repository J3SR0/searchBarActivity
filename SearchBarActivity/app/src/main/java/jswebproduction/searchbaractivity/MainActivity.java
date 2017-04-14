package jswebproduction.searchbaractivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MAIN_TAG";

    private LoaderManager.LoaderCallbacks<Cursor>       loaderCallbacks;
    private List<SearchWidget>      widgets;
    private CustomAdapter           adapter;
    private ListFragment            listFragment;
    private FragmentManager         fragmentManager;
    private SearchWidget            browser;
    private Boolean                 browserMode;
    private InputMethodManager      keyboard;
    private LoaderManager           loaderManager;
    private ContactSearchWidget     contactWidget;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        // Init variables
        this.loaderCallbacks    = this;
        this.fragmentManager    = getSupportFragmentManager();
        this.listFragment       = new MyListFragment();
        this.widgets            = new ArrayList();
        this.adapter            = new CustomAdapter(this);
        this.browser            = new Browser();
        this.browserMode        = false;
        this.keyboard           = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        this.loaderManager      = this.getSupportLoaderManager();
        this.contactWidget      = new ContactSearchWidget();

        // Add the ListFragment to the main layout
        FragmentTransaction ft = this.fragmentManager.beginTransaction();
        ft.add(R.id.main_layout, this.listFragment);
        ft.commit();

        requestPermission();
        initWidgets();
    }

    public void initWidgets() {
        this.browser.setContext(this);
        this.contactWidget.setContext (this);
        this.loaderManager.initLoader(contactWidget.getLoaderId(), null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //Config SearchView
        MenuItem searchViewItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.requestFocusFromTouch();
        setSearchViewCallbacks(searchView);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == contactWidget.getLoaderId()) {
            return (Loader<Cursor>) contactWidget.getLoader();
        }
        return null;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final int count = data.getCount();
        contactWidget.getAdapter().swapCursor(data);
        contactWidget.updateCount(count);
        contactWidget.redraw();
        Log.d(TAG, "count = " + count);
        if (contactWidget.isDisplayable() && count > 0) {
            addItemToList(contactWidget);
        } else if (!contactWidget.isDisplayable()) {
            removeItemFromList(contactWidget);
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        contactWidget.getAdapter().swapCursor(null);
    }


    private void setSearchViewCallbacks(final SearchView searchView){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //return false to perform default handling of the search.

                // Remove all fragments
                clearFragmentList();

                // Set the Url
                ((Browser)browser).setUrl("http://www.google.com/search?as_q=" + query.replace(" ", "+"));

                //Other fragments
                for (int i = 0; i < 2; i ++) {
                    SearchWidget w = new TemplateWidget();
                    widgets.add(w);
                    adapter.addItem(w);
                }

                //Call the webView fragment
                widgets.add(browser);
                adapter.addItem(browser);

                for (int i = 0; i < 2; i ++) {
                    SearchWidget w = new TemplateWidget();
                    widgets.add(w);
                    adapter.addItem(w);
                }

                listFragment.setListAdapter(adapter);

                browserMode = true;
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactWidget.updateQuery(newText);
                loaderManager.restartLoader(contactWidget.getLoaderId(), null, loaderCallbacks);

                clearFragmentList();
                if (contactWidget.isDisplayable()) {
                    widgets.add(contactWidget);
                    adapter.addItem(contactWidget);
                }

                for (int i = 0; i < 3; i++) {
                    SearchWidget w = new TemplateWidget();
                    widgets.add(w);
                    adapter.addItem(w);
                }
                listFragment.setListAdapter(adapter);

                //return false to perform default suggestion display.
                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true ) {
                    keyboard.showSoftInput(v, 0);
                    if (browserMode == true) {
                        clearFragmentList();
                        searchView.setQuery("", false);
                    }
                } else {

                }
            }
        });
    }

    private void clearFragmentList() {
        this.widgets.clear();
        this.adapter.clear();
        this.browserMode = false;
    }

    private void removeItemFromList(SearchWidget w) {
        this.widgets.remove(w);
        this.adapter.removeItem(w);
        this.listFragment.setListAdapter(this.adapter);
    }

    private void addItemToList(SearchWidget w) {
        if (!this.widgets.contains(w)) {
            Log.d(TAG, "readding elem to list");
            this.widgets.add(w);
            this.adapter.addItem(w);
            this.listFragment.setListAdapter(this.adapter);
        }
    }

    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private void requestPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

}
