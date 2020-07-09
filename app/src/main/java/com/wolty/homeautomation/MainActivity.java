package com.wolty.homeautomation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.wolty.homeautomation.deviceModel.Device;
import com.wolty.homeautomation.userModel.User;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    protected Context mContext;
    private SwipeRefreshLayout pullToRefresh;
    protected ArrayList<Device> arrayList;
    private ListView lv;
    private Utils utils;
    private RequestQueue requestQueue;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView tvHeaderUsername;
    private TextView tvHeaderEmail;
    private View headerView;
    private DevicesAdapter adapter = null;
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        pullToRefresh = findViewById(R.id.swiperefresh);
        navigationView = findViewById(R.id.navigation_view);
        drawer = findViewById(R.id.drawer);
        headerView = navigationView.getHeaderView(0);
        tvHeaderUsername = headerView.findViewById(R.id.menuHeaderUsername);
        tvHeaderEmail = headerView.findViewById(R.id.menuHeaderEmail);
        lv = findViewById(R.id.devicesList);
        arrayList = new ArrayList<>();

        mContext = getApplicationContext();
        utils = new Utils(mContext);
        requestQueue = Volley.newRequestQueue(mContext);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvHeaderUsername.setText(User.getName());
        tvHeaderEmail.setText(User.getEmail());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Toast.makeText(mContext, "aaaa", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        fetchDevices();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchDevices();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void fetchDevices() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                utils.getBaseUrl() + "/devices",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getJSONArray("devices").length() == 0) return;
                            if(!arrayList.isEmpty()) arrayList.clear();

                            JSONArray res = response.getJSONArray("devices");
                            for (int i = 0; i < res.length(); i++) {
                                JSONObject device = res.getJSONObject(i);
                                int id = device.getInt("id");
                                int status = device.getInt("status");
                                //int input = device.getInt("input");
                                int pin = device.getInt("pin");
                                String name = device.getString("name");
                                String type = device.getString("device_type_id");

                                arrayList.add(new Device(id, pin, status, name, type));
                            }

                            if(adapter != null) {
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter = new DevicesAdapter(mContext, arrayList);
                                lv.setAdapter(adapter);
                            }

                        } catch (Exception e) {
                            Toast.makeText(mContext, "Error with server occurred", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "fetchDevices: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Fetch Device Error", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, error.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return utils.setHeaders(User.getToken());
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }
}