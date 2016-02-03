package ly.generalassemb.drewmahrt.shoppinglistwithsearch;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ListView mShoppingListView;
    private CursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShoppingListView = (ListView)findViewById(R.id.shopping_list_view);

        Cursor cursor = ShoppingSQLiteOpenHelper.getInstance(MainActivity.this).getShoppingList();

        //mCursorAdapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,cursor,new String[]{ShoppingSQLiteOpenHelper.COL_ITEM_NAME},new int[]{android.R.id.text1},0);
        mCursorAdapter = new CursorAdapter(MainActivity.this, cursor, 0) { // context, cursor, flags
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(MainActivity.this).inflate(R.layout.shopping_list_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView nameView = (TextView) view.findViewById(R.id.shopping_item_name);
                TextView typeView = (TextView) view.findViewById(R.id.shopping_item_type);

                nameView.setText(cursor.getString(cursor.getColumnIndex(ShoppingSQLiteOpenHelper.COL_ITEM_NAME)));
                typeView.setText(cursor.getString(cursor.getColumnIndex(ShoppingSQLiteOpenHelper.COL_ITEM_TYPE)));
            }
        };
        mShoppingListView.setAdapter(mCursorAdapter);

        handleIntent(getIntent());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateCursorWithSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateCursorWithSearch(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            updateCursorWithSearch(query);
        }
    }

    private void updateCursorWithSearch(String query) {
        mCursorAdapter.swapCursor(ShoppingSQLiteOpenHelper.getInstance(MainActivity.this).search(query));
    }
}
