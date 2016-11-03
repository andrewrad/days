package com.radicaldroids.days;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import layout.AddTaskFragment;
import layout.ChronFaceFragment;
import layout.RemoveTaskFragment;
import models.SubTask;
import models.Task;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener, FragmentManager.OnBackStackChangedListener{

    List taskList;
    private Realm realm;
    private AddTaskFragment addTaskFragment;
    private RemoveTaskFragment removeTaskFragment;
    private ChronFaceFragment chronFaceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskList = new ArrayList();

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        chronFaceFragment = new ChronFaceFragment();
        replaceFragment(chronFaceFragment);
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, fragment)
                .addToBackStack(null)
                .commit();
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

        if (id == R.id.add_task) {
            addTaskFragment = new AddTaskFragment();
            replaceFragment(addTaskFragment);

        } else if (id == R.id.remove_task) {
            removeTaskFragment = new RemoveTaskFragment();
            replaceFragment(removeTaskFragment);

        } else if (id == R.id.current_task) {
            replaceFragment(chronFaceFragment);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void createTask(final String task, final String subTask) {

        if(!task.equals("")) {
            Realm.init(this);
            Realm realm = Realm.getDefaultInstance();

            final Task result = realm.where(Task.class).equalTo("name", task).findFirst();

            //if task is already in DB
            if (result != null) {
                //if subtask is already under task
                if (realm.where(Task.class).equalTo("subTasks.name", subTask).findFirst() != null) {
                    Toast.makeText(this, "Already contains task and subtask", Toast.LENGTH_SHORT).show();
                } else {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            SubTask realmSubTask = realm.createObject(SubTask.class);
                            if(!subTask.isEmpty()) {
                                realmSubTask.setName(subTask);
                                result.getSubTasks().add(realmSubTask);
                            }
                            realm.close();

                            getSupportFragmentManager().popBackStack();
                            getSupportFragmentManager().beginTransaction().remove(addTaskFragment).commit();
                        }
                    });
                }
            } else {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Task realmTask = realm.createObject(Task.class);
                        realmTask.setName(task);

                        if(!subTask.isEmpty()) {
                            SubTask realmSubTask = realm.createObject(SubTask.class);
                            realmSubTask.setName(subTask);
                            realmTask.getSubTasks().add(realmSubTask);
                        }

                        realm.close();
                        getSupportFragmentManager().popBackStack();
                        getSupportFragmentManager().beginTransaction().remove(addTaskFragment).commit();
                    }
                });
            }
        } else {
            Toast.makeText(this, "Please create a task", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackStackChanged() {
        Log.e("backstack", "backstack");
    }
}