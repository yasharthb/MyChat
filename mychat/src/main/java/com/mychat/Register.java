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

public class Register extends AppCompatActivity {
    EditText username, password,user_name;
    Button registerButton;
    String user, pass,usern;
    TextView login;
    ProgressBar pd2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        user_name=(EditText)findViewById(R.id.user_name);
        registerButton = (Button)findViewById(R.id.registerButton);
        login = (TextView)findViewById(R.id.login);
        pd2=(ProgressBar)findViewById(R.id.pd2);

        Firebase.setAndroidContext(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();
                usern= user_name.getText().toString();

                if(user.equals("")){
                    username.setError("Field can't be blank");
                }
                else if(usern.equals("")){
                    user_name.setError("Field can't be blank");
                }
                else if(pass.equals("")){
                        password.setError("Field can't be blank");
                    }
                    else if(!user.matches("[A-Za-z0-9]+")){
                            username.setError("Only alphabets and numbers allowed");
                        }
                        else if(user.length()<5){
                                username.setError("Atleast 5 characters long");
                            }

                else {
                    String url = "https://mychat-0911.firebaseio.com/users.json";
                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Firebase reference = new Firebase("https://mychat-0911.firebaseio.com/users");
                            String key=Encryption.key();
                            if (s.equals("null")) {

                                reference.child(user).child("password").setValue(Encryption.encrypt(pass,key));
                                reference.child(user).child("username").setValue(Encryption.encrypt(usern,key));
                                reference.child(user).child("flag").setValue("0");
                                reference.child(user).child("key").setValue(key);
                                Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Register.this, Login.class));
                                finish();
                            } else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(user)) {
                                        reference.child(user).child("password").setValue(Encryption.encrypt(pass,key));
                                        reference.child(user).child("username").setValue(Encryption.encrypt(usern,key));
                                        reference.child(user).child("flag").setValue("0");
                                        reference.child(user).child("key").setValue(key);
                                        Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(Register.this, Login.class));
                                        finish();
                                    } else {
                                        Toast.makeText(Register.this, "Username already exists", Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd2.setVisibility(View.GONE);
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                            pd2.setVisibility(View.GONE);
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(Register.this);
                    rQueue.add(request);
                }
            }

        });
    }
}