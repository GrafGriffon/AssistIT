package com.example.griffoassistant;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GestureDetectorCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ReadActivity extends AppCompatActivity {

    private ListView listView;
    UserAdapter boxAdapter;
    private DatabaseReference myDatabase;
    ArrayList<CheckUser> arts = new ArrayList<>();
    private FirebaseAuth mAuth;
    private String url;
    private StorageReference storageReference;
    private String name;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null) {
            Toast.makeText(this, currentUser.getEmail(), Toast.LENGTH_LONG).show();
        }
        assert currentUser != null;
        init(currentUser.getEmail());
        editText=findViewById(R.id.editTextTextPersonName2);
        getData();
        setOnClickItem();
    }

    private void init(String email){
        ConstraintLayout back = findViewById(R.id.back);
        back.getBackground().setAlpha(127);
        listView = findViewById(R.id.listViewArt);
        arts= new ArrayList<>();
        boxAdapter = new UserAdapter(this, arts);
        listView.setAdapter(boxAdapter);
        myDatabase = FirebaseDatabase.getInstance().getReference("illia2002kurb@gmailcom");
        storageReference = FirebaseStorage.getInstance().getReference("images");
    }

    private void getData(){
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (arts.size()>0){
                    testMessage("new art", new Intent());
                    arts.clear();
                }
                ArrayList mails = new ArrayList();;
                for(DataSnapshot ds: snapshot.getChildren()){
                    CheckUser message = ds.getValue(CheckUser.class);
                    assert message != null;
                    if (!message.idUser.equals(mAuth.getCurrentUser().getEmail().replaceAll("[,.]", ""))) {
                        if (editText.getText().equals("")) {
                            arts.add(new CheckUser(message.id, message.idUser, message.last, message.status, message.count));
                        } else {
                            if (message.idUser.contains(editText.getText())){
                                arts.add(new CheckUser(message.id, message.idUser, message.last, message.status, message.count));
                            }
                        }
                    }
                    mails.add(message.idUser);
                }
                if (!mails.contains(mAuth.getCurrentUser().getEmail().replaceAll("[,.]", ""))){
                    String id = myDatabase.getKey();
                    CheckUser user = new CheckUser(id, mAuth.getCurrentUser().getEmail().replaceAll("[,.]", ""), "last", true, 0);
                    myDatabase.push().setValue(user);

                }
                boxAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        myDatabase.addValueEventListener(vListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data!=null && data.getData() != null){
            System.out.println("Img URI : "+ data.getData());
            uploadFileInFireBaseStorage(data.getData());
        }
    }

    public void uploadFileInFireBaseStorage (Uri uri){
        UploadTask uploadTask = storageReference.child(System.currentTimeMillis() + FirebaseAuth.getInstance().getCurrentUser().getUid()).putFile(uri);
        uploadTask.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                task -> url = task.getResult().toString()));
    }

    public void clickOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ReadActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void testMessage (String message , Intent  intent){

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);


        String channelId = "some_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                        .setContentTitle(getString(R.string.app_name)+": add new art")
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify((int) System.currentTimeMillis() /* ID of notification */, notificationBuilder.build());
    }

    private void setOnClickItem(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckUser art = arts.get(position);

                Intent intent= new Intent(ReadActivity.this, CheckMessage.class);
                intent.putExtra("naming", art.idUser);
                startActivity(intent);
            }
        });
    }

    public void getSearch(View view) {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (arts.size()>0){
                    testMessage("new art", new Intent());
                    arts.clear();
                }
                ArrayList mails = new ArrayList();;
                for(DataSnapshot ds: snapshot.getChildren()){
                    CheckUser message = ds.getValue(CheckUser.class);
                    assert message != null;
                    if (!message.idUser.equals(mAuth.getCurrentUser().getEmail().replaceAll("[,.]", ""))) {
                        if (editText.getText().equals("")) {
                            arts.add(new CheckUser(message.id, message.idUser, message.last, message.status, message.count));
                        } else {
                            if (message.idUser.contains(editText.getText())){
                                arts.add(new CheckUser(message.id, message.idUser, message.last, message.status, message.count));
                            }
                        }
                    }
                    mails.add(message.idUser);
                }
                if (!mails.contains(mAuth.getCurrentUser().getEmail().replaceAll("[,.]", ""))){
                    String id = myDatabase.getKey();
                    CheckUser user = new CheckUser(id, mAuth.getCurrentUser().getEmail().replaceAll("[,.]", ""), "last", true, 0);
                    myDatabase.push().setValue(user);

                }
                boxAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        myDatabase.addValueEventListener(vListener);
    }
}