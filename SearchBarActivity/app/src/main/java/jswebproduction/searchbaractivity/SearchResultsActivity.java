package jswebproduction.searchbaractivity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

/**
 * Created by Jsweb Stage 2 on 10/04/2017.
 */

public class SearchResultsActivity extends Activity {
    static DatabaseTable db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.db == null)
            this.db = new DatabaseTable(this);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search
            Cursor c = db.getWordMatches(query, null);
            //startSearch(query, true, null, true);
        }
    }
}
