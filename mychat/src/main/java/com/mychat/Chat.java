package com.mychat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class Chat extends AppCompatActivity {
    TextView chatUser;
    CheckedTextView textView2;
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton,uploadButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2,reference3;
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Chat.this, Users.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatUser=(TextView) findViewById(R.id.chatUser);
        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        uploadButton=(ImageView)findViewById(R.id.uploadButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
       // textView1 = new CheckedTextView(Chat.this);
        textView2 = new CheckedTextView(Chat.this);
        textView2.setClickable(true);
      //  textView1.setLinksClickable(true);
        //layout.setLongClickable(true);
        scrollView.fullScroll(View.FOCUS_DOWN);
        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://mychat-0911.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://mychat-0911.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);
        reference3 = new Firebase("https://mychat-0911.firebaseio.com/users/" + UserDetails.chatWith);

        if(!UserDetails.file.equals("")) {

            messageArea.setText(UserDetails.file);
            UserDetails.file = "";
        }
        if(!UserDetails.draft.equals("")) {

            messageArea.setText(UserDetails.draft);
            UserDetails.draft = "";
        }

        reference3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserDetails.chatWithUser=Encryption.decrypt(snapshot.child("username").getValue(String.class),snapshot.child("key").getValue(String.class));
                chatUser.setText(UserDetails.chatWithUser);
                chatUser.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();
                String key=Encryption.key();
                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", Encryption.encrypt(messageText,key));
                    map.put("user", UserDetails.username);
                    map.put("seen","0");
                    map.put("key",key);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");

                }
            }
        });


       uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Chat.this, Upload.class));
                finish();
            }
        });



        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                final String message = map.get("message").toString();
                String userName = map.get("user").toString();
                String key=map.get("key").toString();
                String seen=map.get("seen").toString();

                if(userName.equals(UserDetails.username)){
                    addMessageBox(Encryption.decrypt(message,key), 1,seen);
                }
                else{
                   if(!seen.equals("1")){
                    Query query=reference2.orderByChild("message").equalTo(message);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {
                                    for (DataSnapshot Snapshot2: dataSnapshot1.getChildren()) {
                                        Snapshot2.getRef().child("seen").setValue("1");
                                    }
                                }
                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                       dataSnapshot.getRef().child("seen").setValue("1");

                   }
                    addMessageBox(Encryption.decrypt(message,key), 2,"0");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                UserDetails.draft=messageArea.getText().toString();
                startActivity(new Intent(Chat.this, Chat.class));
                finish();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                startActivity(new Intent(Chat.this, Chat.class));
                finish();


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
      /*  textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Chat.this,"Browser",Toast.LENGTH_LONG).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(textView1.getText().toString()));//invoking Browser select intent using the link clicked in the text
                startActivity(browserIntent);
            }
    });*/
       textView2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Toast.makeText(Chat.this, "Flag 1", Toast.LENGTH_SHORT).show();
               if(!textView2.isChecked()){

                   AlertDialog.Builder builder  = new AlertDialog.Builder(Chat.this)
                           .setTitle("Alert")
                           .setMessage("Do you want to delete this Message?")
                           .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   Query query1=reference1.orderByChild("seen").equalTo("0");
                                   query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                           for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                                               if((Snapshot.child("user").toString().equals(UserDetails.username))&&(Encryption.decrypt(Snapshot.child("message").toString(),Snapshot.child("key").toString()).equals(textView2.getText().toString())))
                                               UserDetails.Crypt=Snapshot.child("message").toString();
                                                   Snapshot.getRef().removeValue();
                                           }
                                       }
                                       @Override
                                       public void onCancelled(FirebaseError firebaseError) {

                                       }
                                   });

                                   Query query2=reference2.orderByChild("message").equalTo(UserDetails.Crypt);
                                   query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                           for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                                               Snapshot.getRef().removeValue();
                                           }
                                       }
                                       @Override
                                       public void onCancelled(FirebaseError firebaseError) {

                                       }
                                   });
                               } });
                   //Toast.makeText(Chat.this, "Flag 2", Toast.LENGTH_SHORT).show();
                   UserDetails.Crypt="";
                   AlertDialog alert = builder.create();
                   // Toast.makeText(Chat.this, "Flag 3", Toast.LENGTH_SHORT).show();

                   alert.show();
                  // Toast.makeText(Chat.this, "Flag 4", Toast.LENGTH_SHORT).show();

               }
           }
       });
    }
    public void addMessageBox(String message, int type,String seen){
       CheckedTextView textView=new CheckedTextView(Chat.this);
        textView.setText(message);

       LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        textView.setTextIsSelectable(true);
        textView.setClickable(true);
        Linkify.addLinks(textView,Linkify.ALL);
        if(seen.equals("1")) {
                textView.setCheckMarkDrawable(R.drawable.check_on);
                textView.setChecked(true);
        }
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);

    }

}