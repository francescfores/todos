package com.iesebre.dam2.francesc.todos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import android.view.ActionMode;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SHARED_PREFERENCES_TODOS = "SP_TODOS";
    private static final String TODO_LIST = "todo_list";

    private String todoList = "";
    private Gson gson;
    public TodoArrayList tasks = new TodoArrayList();
    private CustomListAdapter adapter;
    private String taskName;
    private int taskPriority;
    private boolean taskDone;
    private SwipeRefreshLayout swipeContainer;
    private static final String DEBUG_TAG = "NetworkStatusExample";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.iesebre.dam2.francesc.todos/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        //Serialitzem TaskArrayList a JSON
        Type taskArrayListType = new TypeToken<TodoArrayList>() {
        }.getType();
        String serializedData = gson.toJson(tasks, taskArrayListType);

        System.out.println("Saving: " + serializedData);

        //Guardem les tasques a SharedPreferences
        SharedPreferences preferencesReader = getSharedPreferences(SHARED_PREFERENCES_TODOS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesReader.edit();
        editor.putString(TODO_LIST, serializedData);
        editor.apply();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSharedPreferencesTodolist();


        //Executa el code Ion que obté el Json i el guarda en un camp/field de l'objecte activity
        PullToRefresh();

        //TestAsyncTask testAsyncTask = new TestAsyncTask(MainActivity.this, "http://tasksapi.app/task/10");
        //testAsyncTask.execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fabRemove = (FloatingActionButton) findViewById(R.id.fab_remove);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public static boolean checkWifi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        return isWifiConn;
    }

    public static boolean checkMobile(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        //String type = networkInfo.getSubtypeName();
        boolean isMobileConn = networkInfo.isConnected();
        return isMobileConn;
    }

    private void getSharedPreferencesTodolist() {
        SharedPreferences todos = getSharedPreferences(SHARED_PREFERENCES_TODOS, 0);
        String todoList = todos.getString(TODO_LIST, null);
        Type arrayTodoList = new TypeToken<TodoArrayList>() {
        }.getType();
        this.gson = new Gson();
        TodoArrayList temp = gson.fromJson(todoList, arrayTodoList);

        if (temp != null) {
            tasks = temp;

            ListView todoslv = (ListView) findViewById(R.id.todolistview);

            //We bind our arraylist of tasks to the adapter
            adapter = new CustomListAdapter(this, tasks);
            todoslv.setAdapter(adapter);
        } else {
            //Error TODO
        }


    }

    private void PullToRefresh() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchDownloadJson();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    //Carregar el JSON d'una pàgina remota utilitzant ion
    private void fetchDownloadJson() {
        if (!checkWifi(MainActivity.this) && !checkMobile(MainActivity.this)) {
            Toast.makeText(getBaseContext(), "Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
            swipeContainer.setRefreshing(false);
            MaterialDialog dialog = new MaterialDialog.Builder(this).
                    title("Obrir configuració de de xarxa").
                    positiveText("Wifi").
                    neutralText("Dades mòbils").
                    negativeText("Cancel·la").
                    positiveColor(Color.parseColor("#2196F3")).
                    neutralColor(Color.parseColor("#2196F3")).
                    negativeColor(Color.parseColor("#ff3333")).
                    onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                            startActivity(intent);
                        }
                    }).
                    onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                            startActivity(intent);
                        }
                    }).
                    build();
            dialog.show();
        } else if (checkMobile(MainActivity.this)) {
            MaterialDialog dialog = new MaterialDialog.Builder(this).
                    title("Connexió amb dades mòbils, desitges descarregar la llista ?").
                    negativeText("Cancel·la").
                    positiveText("Acepta").
                    negativeColor(Color.parseColor("#ff3333")).
                    positiveColor(Color.parseColor("#2196F3")).
                    onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            Ion.with(MainActivity.this)
                                    .load("http://acacha.github.io/json-server-todos/db_todos.json")
                                    .asJsonArray()
                                    .setCallback(new FutureCallback<JsonArray>() {
                                        @Override
                                        public void onCompleted(Exception e, JsonArray result) {
                                            todoList = result.toString();
                                            updateTodosList();
                                            Toast toast = Toast.makeText(MainActivity.this, "Descarrega completada!!", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    });
                        }
                    }).
                    build();
            dialog.show();
        } else {
            Ion.with(this)
                    .load("http://acacha.github.io/json-server-todos/db_todos.json")
                    .asJsonArray()
                            //Procés asíncron
                    .setCallback(new FutureCallback<JsonArray>() {
                        @Override
                        public void onCompleted(Exception e, JsonArray result) {
                            //Guardem la resposta de la consulta
                            todoList = result.toString();
                            //Actualitzem la llista
                            updateTodosList();
                            Toast toast = Toast.makeText(MainActivity.this, "Descarrega completada!!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
        }
    }

    //Recarregar el JSON al fer un Pull-to-refresh
    private void updateTodosList() {
        Type arrayTodoList = new TypeToken<TodoArrayList>() {
        }.getType();
        this.gson = new Gson();
        TodoArrayList temp = gson.fromJson(todoList, arrayTodoList);
        tasks = temp;

        ListView todoslv = (ListView) findViewById(R.id.todolistview);

        //Setejem l'arry de taskes al adapter
        adapter = new CustomListAdapter(this, tasks);
        todoslv.setAdapter(adapter);
        //Aturem el pull to refresh
        swipeContainer.setRefreshing(false);
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

        if (id == R.id.nav_camara) {
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

    public void showAddForm(View view) {
        taskDone = false;
        EditText taskNameText;
        EditText taskPriorityText;
        CheckBox taskDoneText;

        MaterialDialog dialog = new MaterialDialog.Builder(this).
                title("Afegir nova tasca").
                customView(R.layout.form_add_task, true).
                negativeText("Cancel·la").
                positiveText("Acepta").
                negativeColor(Color.parseColor("#ff3333")).
                positiveColor(Color.parseColor("#2196F3")).
                onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        final TodoItem todoItem = new TodoItem();
                        todoItem.setName(taskName);
                        todoItem.setPriority(taskPriority);
                        todoItem.setDone(taskDone);
                        tasks.add(todoItem);
                        //tasks.remove(1);
                        ListView todoslv = (ListView) findViewById(R.id.todolistview);

                        //We bind our arraylist of tasks to the adapter
                        adapter = new CustomListAdapter(MainActivity.this, tasks);
                        todoslv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        Utility.setListViewHeightBasedOnChildren(todoslv);

                    }
                }).

                build();
        dialog.show();

        taskNameText = (EditText) dialog.getCustomView().findViewById(R.id.task_tittle);
        taskNameText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        taskPriorityText = (EditText) dialog.getCustomView().findViewById(R.id.task_Priority);
        taskPriorityText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    taskPriority = Integer.parseInt(s.toString());
                } catch (Throwable e) {
                    CharSequence text = "La prioritat ha de ser un numero !!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(MainActivity.this, text, duration);
                    toast.show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        taskDoneText = (CheckBox) dialog.getCustomView().findViewById(R.id.task_Done);
        taskDoneText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    taskDone = true;
                } else {
                    taskDone = false;
                }
            }
        });
    }

    public void removeTask(View view) {
        ListView lvItems = (ListView) findViewById(R.id.todolistview);
        for (int i = tasks.size() - 1; i >= 0; i--) {
            RelativeLayout vwParentRow = (RelativeLayout) lvItems.getChildAt(i);
            CheckBox btnChild = (CheckBox) vwParentRow.getChildAt(1);
            if (btnChild.isChecked()) {
                tasks.remove(i);
            }
        }
        adapter.notifyDataSetChanged();

    }

    public void updateTask(View view) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.iesebre.dam2.francesc.todos/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    public static class Utility {
        public static void setListViewHeightBasedOnChildren(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        }
    }

}
