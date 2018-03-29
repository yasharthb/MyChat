package com.mychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    TextView registerUser;
    EditText username, password;
    Button loginButton;
    String user, pass;
    ProgressBar pd1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerUser = (TextView)findViewById(R.id.register);
        username = (EditText)findViewById(R.id.username);
        loginButton = (Button) findViewById(R.id.loginButton);
        password = (EditText) findViewById(R.id.password);
        pd1=(ProgressBar)findViewById(R.id.pd1);

        Firebase.setAndroidContext(this);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();

                if(user.equals("")){
                    username.setError("Field can't be blank");
                }
                else if (pass.equals("")) {
                        password.setError("Field can't be blank");
                    }
                    else {
                        String url = "https://mychat-0911.firebaseio.com/users.json";
                        pd1.setVisibility(View.VISIBLE);
                        pd1.setContentDescription("Loading....");

                        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                if (s.equals("null")) {
                                    Toast.makeText(Login.this, "User not found", Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        JSONObject obj = new JSONObject(s);
                                        Firebase reference = new Firebase("https://mychat-0911.firebaseio.com/users");
                                      // Toast.makeText(Login.this,obj.getJSONObject(user).getString("password"), Toast.LENGTH_LONG).show();
                                      //  Toast.makeText(Login.this, obj.getJSONObject(user).getString("username"), Toast.LENGTH_LONG).show();
                                      //  Toast.makeText(Login.this, obj.getJSONObject(user).getString("key"), Toast.LENGTH_LONG).show();
                                      //  Toast.makeText(Login.this, Encryption.decrypt(obj.getJSONObject(user).getString("password"),obj.getJSONObject(user).getString("key")), Toast.LENGTH_LONG).show();
                                      //  Toast.makeText(Login.this, Encryption.decrypt(obj.getJSONObject(user).getString("username"),obj.getJSONObject(user).getString("key")), Toast.LENGTH_LONG).show();
                                        if (!obj.has(user)) {
                                            Toast.makeText(Login.this, "User not found", Toast.LENGTH_LONG).show();
                                        } else if (Encryption.decrypt(obj.getJSONObject(user).getString("password"),obj.getJSONObject(user).getString("key")).equals(pass)) {
                                            UserDetails.username = user;
                                            UserDetails.password = pass;
                                           // Toast.makeText(Login.this, "Flag 3", Toast.LENGTH_SHORT).show();
                                            UserDetails.user_name = Encryption.decrypt(obj.getJSONObject(user).getString("password"),obj.getJSONObject(user).getString("key"));
                                            reference.child(user).child("flag").setValue("1");
                                            //Toast.makeText(Login.this, "Flag 4", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Login.this, Users.class));
                                            finish();
                                        } else {
                                            Toast.makeText(Login.this, "Incorrect Password", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                pd1.setVisibility(View.GONE);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                System.out.println("" + volleyError);
                                pd1.setVisibility(View.GONE);
                            }
                        });

                        RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                        rQueue.add(request);
                }

            }
        });
    }

}
