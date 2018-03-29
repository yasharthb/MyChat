package com.mychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Users extends AppCompatActivity {
    ListView usersList;
    TextView noUsersText;
    Button logoutButton,refreshButton;
    ProgressBar pd;
    ArrayList<String> al = new ArrayList<>();
    ArrayList<String> al1= new ArrayList<>();
    int totalUsers = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        usersList = (ListView)findViewById(R.id.usersList);
        noUsersText = (TextView)findViewById(R.id.noUsersText);
        logoutButton = (Button)findViewById(R.id.logoutButton);
        refreshButton = (Button)findViewById(R.id.refreshButton);

        pd=(ProgressBar)findViewById(R.id.pd);
        pd.setVisibility(View.VISIBLE);
        final Firebase reference = new Firebase("https://mychat-0911.firebaseio.com/users");
        String url = "https://mychat-0911.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(Users.this);
        rQueue.add(request);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UserDetails.username.equals("")) {

                    reference.child(UserDetails.username).child("flag").setValue("0");
                }
                startActivity(new Intent(Users.this, Login.class));
                finish();
            }
        });
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Users.this, Users.class));
                finish();
            }
        });

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith = al1.get(position);
                startActivity(new Intent(Users.this, Chat.class));
                finish();
            }
        });
    }

    public void doOnSuccess(String s){
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();

                if(!key.equals(UserDetails.username)) {
                    if(obj.getJSONObject(key).getString("flag").equals("1"))
                     al.add(Encryption.decrypt(obj.getJSONObject(key).getString("username"),obj.getJSONObject(key).getString("key"))+"\t\t(Online)");
                    else
                        al.add(Encryption.decrypt(obj.getJSONObject(key).getString("username"),obj.getJSONObject(key).getString("key")));
                    al1.add(key);
                }

                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(totalUsers <=1){
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            refreshButton.setVisibility(View.VISIBLE);
        }
        else{
            noUsersText.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            refreshButton.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));
        }

       pd.setVisibility(View.GONE);
    }

}