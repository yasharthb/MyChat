package com.mychat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Upload extends AppCompatActivity {
    ProgressBar pd3;
    Button chooseButton,uploadButton1;
    ImageView fileImage;
    Uri filepath;
    final int Select_Code=209;
    FirebaseStorage reference;
    StorageReference fileRef,FBRef;

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Upload.this, Chat.class));
        finish();
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        chooseButton=(Button)findViewById(R.id.chooseButton);
        uploadButton1=(Button)findViewById(R.id.uploadButton1);
        fileImage=(ImageView)findViewById(R.id.fileImage);
        pd3=(ProgressBar)findViewById(R.id.pd3);
        reference=FirebaseStorage.getInstance();
        fileRef=reference.getReference();


        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent();
                i.setType("image/* audio/* video/* .pdf .apk");//Can be changed to general files later on
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"Select File"),Select_Code);
            }
            });

        uploadButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Select_Code&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null)
            filepath=data.getData();
        try{
            Bitmap map= MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
            fileImage.setImageBitmap(map);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void uploadFile(){
        pd3.setContentDescription("Uploading...");
        pd3.setVisibility(View.VISIBLE);

        FBRef=fileRef.child("image/"+filepath.getLastPathSegment());
        FBRef.putFile(filepath)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Upload.this, "Upload Unsuccessful", Toast.LENGTH_LONG).show();
                        pd3.setVisibility(View.INVISIBLE);

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                @SuppressWarnings("VisibleForTests") Uri Dlink= taskSnapshot.getDownloadUrl();
                Toast.makeText(Upload.this, "Upload Successful", Toast.LENGTH_LONG).show();
                pd3.setVisibility(View.INVISIBLE);
                Toast.makeText(Upload.this,Dlink.toString(),Toast.LENGTH_LONG).show();
                UserDetails.file=Dlink.toString();
                startActivity(new Intent(Upload.this,Chat.class));
                finish();


            }
        });

        }


    }