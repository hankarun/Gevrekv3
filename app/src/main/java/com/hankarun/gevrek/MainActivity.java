package com.hankarun.gevrek;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseAppcompat implements onPageRefreshed, LoginDialog.onDialogFinished {

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private MainBaseFragment  mFragmentNewsGroups;
    private MainBaseFragment  mFragmentCourses;

    private int numberOfNetworkTries = 0;

    private static WeakReference<Snackbar> snackbarReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        if(!usernameAndPasswordSet(this)) {
            Snackbar f =  Snackbar.make(findViewById(R.id.coordinator),"You need to login.", Snackbar.LENGTH_INDEFINITE);
            f.setAction("Login in.", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new LoginDialog(MainActivity.this, getCurrentTheme()).show();
                        }
                    });
            snackbarReference = new WeakReference<>(f);
            f.show();
            new LoginDialog(this, getCurrentTheme()).show();
            setupViewPager(viewPager,1);
            tabLayout.setupWithViewPager(viewPager);
        }else {
            setupViewPager(viewPager,0);
            tabLayout.setupWithViewPager(viewPager);
        }
    }


    public static boolean usernameAndPasswordSet(Activity activity)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");
        return !(username.equals("") || password.equals(""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.miSettings:
                Intent intent = new Intent(this, SettingsActivty.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager, int staus) {
        mFragmentNewsGroups = new NewsGroupsFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mFragmentNewsGroups, getString(R.string.newsGroups));
        if(staus==0) {
            mFragmentCourses = new CoursesFragment();
            adapter.addFragment(mFragmentCourses, getString(R.string.courses));
        }
        viewPager.setAdapter(adapter);
    }

   /* @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Snackbar currentSnackbar = getCurrentSnackbar();
        if(currentSnackbar  == null)
        {
            Snackbar s = Snackbar.make(findViewById(R.id.coordinator), "Loading.", Snackbar.LENGTH_INDEFINITE);
            snackbarReference = new WeakReference<>(s);
            s.show();
        }
        switch (id)
        {
            case 0:
                NewsGroupLoader loader = new NewsGroupLoader(this);
                if(args != null && args.getBoolean("clean"))
                    loader.clearCache();
                return loader;
            case 1:
                return new CowCourseListLoader(this);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Snackbar currentSnackbar = getCurrentSnackbar();
        if(currentSnackbar  != null)
            currentSnackbar.dismiss();
        if(data != null)
        {
            switch (loader.getId())
            {
                case 0:
                    if(mFragmentNewsGroups!=null)
                        mFragmentNewsGroups.onloadFinished(data);
                    getSupportLoaderManager().initLoader(1,null,this);
                    break;
                case 1:
                    mFragmentCourses.onloadFinished(data);
                    break;
            }
        }else
        {
            if(numberOfNetworkTries < 1) {
                numberOfNetworkTries++;

                Snackbar s = Snackbar.make(findViewById(R.id.coordinator), "Slow network. Trying again.", Snackbar.LENGTH_INDEFINITE);
                snackbarReference = new WeakReference<>(s);
                s.show();
            } else
            {
                Snackbar snack = Snackbar.make(findViewById(R.id.coordinator), "There is something wrong with internet.", Snackbar.LENGTH_LONG);
                snackbarReference = new WeakReference<>(snack);
                snack.show();
                finish();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }*/

    @Override
    public void onPageRefreshed() {
        //getSupportLoaderManager().restartLoader(0,null,this);
    }

    @Override
    public void onDialogReturn() {
        Snackbar currentSnackbar = getCurrentSnackbar();
        if(currentSnackbar  != null)
            currentSnackbar.dismiss();

        setupViewPager(viewPager,0);
        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public static Snackbar getCurrentSnackbar() {
        if (snackbarReference != null) {
            return snackbarReference.get();
        }
        return null;
    }
}
