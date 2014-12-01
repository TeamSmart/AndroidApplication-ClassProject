package net.liucs.tabfragments.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity
implements ActionBar.TabListener {

    public static final String TAG = "TabFragments";
    private static final String SELECTED = "Selected";

    private static City[] cities = {
        new City("Paris", R.drawable.z4504691370_2c551aa40e_z,
                "An important settlement for more than two millennia, by the late 12th century Paris had become a walled cathedral city that was one of Europe's foremost centres of learning and the arts and the largest city in the Western world until the turn of the 18th century."),
        new City("Tokyo", R.drawable.z11717672066_5b33754acc_z,
                "Tokyo is the capital of Japan, the center of the Greater Tokyo Area, and the most populous metropolitan area in the world."),
        new City("İstanbul", R.drawable.z3581836163_934578789a_z,
                "İstanbul is the largest city in Turkey, constituting the country's economic, cultural, and historical heart")
    };

    private static Fragment[] fragments = new Fragment[cities.length];

    @Override
    protected void onCreate(Bundle in) {
        super.onCreate(in);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for(City city : cities) {
            bar.addTab(bar.newTab()
                    .setText(city.name)
                    .setTabListener(this));
        }
        if(in != null) {
            bar.setSelectedNavigationItem(in.getInt(SELECTED));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        // Save current tab
        out.putInt(SELECTED, getSupportActionBar().getSelectedNavigationIndex());
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void announce(String action, ActionBar.Tab tab) {
        Log.i(TAG, action + " " + tab.getPosition() + ": " + tab.getText());
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        for(Fragment f : fragments) {
            buf.append(null == f? '.' : 'X');
        }
        buf.append("]");
        Log.i(TAG, "cache: " + buf);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        announce("selected", tab);
        int i = tab.getPosition();
        City city = cities[i];
        if(null == fragments[i]) {
            fragments[i] = CityGuideFragment.newInstance(city);
            Log.i(TAG, "Created fragment " + fragments[i]);
        }
        ft.replace(android.R.id.content, fragments[i]);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        announce("unselected", tab);
        Fragment f = getSupportFragmentManager().findFragmentById(android.R.id.content);
        Log.i(TAG, "Found fragment " + f);
        if(f != null) {
            ft.remove(f);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        announce("reselected", tab);
    }
}
