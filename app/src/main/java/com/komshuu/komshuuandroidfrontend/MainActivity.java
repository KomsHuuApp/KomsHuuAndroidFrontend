package com.komshuu.komshuuandroidfrontend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.komshuu.komshuuandroidfrontend.adapters.AnnouncementAdapter;
import com.komshuu.komshuuandroidfrontend.models.Announcement;
import com.komshuu.komshuuandroidfrontend.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button sendButton;
    EditText editText;
    RecyclerView recyclerView;
    ArrayList<Announcement> announcementList;
    AnnouncementAdapter announcementAdapter;
    String server_url = "https://enigmatic-atoll-89666.herokuapp.com/addComplaint";
    TextView textViewName;
    TextView textViewUserName;
    View mHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear_layout);

                final Snackbar snackbar = Snackbar.make(linearLayout, "", Snackbar.LENGTH_INDEFINITE);

                Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
                LayoutInflater objLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View snackView = objLayoutInflater.inflate(R.layout.activity_complaint_box, null); // custom_snac_layout is your custom xml

                layout.addView(snackView, 0);
                snackbar.show();

                editText = (EditText) snackView.findViewById(R.id.et);
                sendButton = (Button) snackView.findViewById(R.id.btn);

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                sendButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.HOUR_OF_DAY, 3);
                        String time = new SimpleDateFormat("dd MMM yyyy HH:mm").format(cal.getTime());

                        try {
                            URL url = new URL(server_url);
                            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                            httpCon.setDoOutput(true);
                            httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            httpCon.setRequestMethod("POST");
                            httpCon.setRequestProperty("Content-Type", "application/json");
                            JSONObject eventObject = new JSONObject();
                            eventObject.put("apartmentId", new Long(2));
                            eventObject.put("date", time);
                            eventObject.put("personId", new Long(2));
                            eventObject.put("text", editText.getText().toString());

                            String json = eventObject.toString();

                            byte[] outputInBytes = json.getBytes("UTF-8");
                            OutputStream os = httpCon.getOutputStream();
                            os.write(outputInBytes);
                            os.close();

                            int responseCode = httpCon.getResponseCode();
                            System.out.println("response code: " + responseCode);
                            if(responseCode == 200){
                                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear_layout);
                                Snackbar.make(linearLayout, "Şikayetiniz gönderildi.", Snackbar.LENGTH_LONG)
                                        .setAction("DISMISS", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                            }
                                        }).show();
                            }
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mHeaderView = navigationView.getHeaderView(0);
        textViewName = (TextView) mHeaderView.findViewById(R.id.textViewName);
        textViewUserName = (TextView) mHeaderView.findViewById(R.id.textViewUserName);
        Intent intent = getIntent();
        final User user = (User) intent.getSerializableExtra("user");
        textViewName.setText(user.getName() + " " + user.getSurname());
        textViewUserName.setText(user.getUsername());
        navigationView.setNavigationItemSelectedListener(this);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://enigmatic-atoll-89666.herokuapp.com/getAnnouncements?apartmentId=1";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        announcementList = new ArrayList<>();
                        try {
                            JSONArray announcements = new JSONArray(response);
                            for(int i = 0; i < announcements.length(); i++) {
                                JSONObject announcement = announcements.getJSONObject(i);
                                Announcement temp = new Announcement();
                                temp.setAnnouncementId(announcement.getLong("announcementId"));
                                temp.setAnnouncementDate(announcement.getString("announcementDate"));
                                temp.setAnnouncementDescription(announcement.getString("text"));
                                temp.setApartmentId(announcement.getLong("apartmentId"));
                                int importance = announcement.getInt("announcementImportance");
                                if(importance == 1)
                                    temp.setImageID(R.drawable.ic_outline_priority_red);
                                else if (importance == 2)
                                    temp.setImageID(R.drawable.ic_outline_priority_yellow);
                                else
                                    temp.setImageID(R.drawable.ic_outline_priority_green);
                                temp.setAnnouncementImportance(importance);
                                temp.setAnnouncerId(announcement.getLong("announcerId"));
                                announcementList.add(temp);
                            }
                            recyclerView = (RecyclerView) findViewById(R.id.recylerview);

                            announcementAdapter = new AnnouncementAdapter(MainActivity.this, announcementList, user.getRole());
                            recyclerView.setAdapter(announcementAdapter);

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            recyclerView.setLayoutManager(linearLayoutManager);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //bos iken
            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        if(user.getRole() == 1) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = getIntent();
        final User user = (User) intent.getSerializableExtra("user");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_announcement) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.announcement_add_layout, null);
            final EditText editTextAnnouncementDescription = (EditText) view.findViewById(R.id.add_announcement_description);
            builder.setView(view)
                    .setTitle("Yeni Duyuru")
                    .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Paylaş", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                            String url ="https://enigmatic-atoll-89666.herokuapp.com/addAnnouncement";
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("text", editTextAnnouncementDescription.getText());
                                jsonObject.put("announcementDate", "10/03/2019");
                                jsonObject.put("announcementImportance", 3);
                                jsonObject.put("announcerId", 1);
                                jsonObject.put("apartmentId", 1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(MainActivity.this, "Duyuru paylaşıldı.", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                                    i.putExtra("user", user);
                                    startActivity(i);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, "Duyuru paylaşılamadı.", Toast.LENGTH_LONG).show();
                                }
                            });
                            queue.add(jsonObjectRequest);
                        }
                    });
            AlertDialog addDialog = builder.create();
            addDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_apartment) {
            Intent intent = new Intent(this, ApartmentActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.nav_flat) {
            System.out.print(this.toString());
            Intent intent = new Intent(this, FlatActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.nav_dues) {

        } else if (id == R.id.nav_order) {
            Intent intent = new Intent(this, UserOrderActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_numbers) {
            Intent intent = new Intent(this, EmergencyNumbersActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else if (id == R.id.complaints) {
            Intent intent = new Intent(this, ComplaintActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_poll) {
            Intent intent = new Intent(this, PollActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


        } else if (id == R.id.nav_warn) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            super.onBackPressed();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
