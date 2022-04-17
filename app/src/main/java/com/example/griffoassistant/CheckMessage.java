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

public class CheckMessage extends AppCompatActivity {

    private ListView listView;
    BoxAdapter boxAdapter;
    private DatabaseReference myDatabase;
    private DatabaseReference myDatabase2;
    ArrayList<UserMessage> arts = new ArrayList<>();
    private FirebaseAuth mAuth;
    private String url;
    private StorageReference storageReference;
    private String name;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_message);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        init(currentUser.getEmail());
        getData();
        editText = findViewById(R.id.editTextTextPersonName);
    }

    private void init(String email){
        ConstraintLayout back = findViewById(R.id.back);
        back.getBackground().setAlpha(127);
        listView = findViewById(R.id.listViewArt);
        arts= new ArrayList<>();
        boxAdapter = new BoxAdapter(this, arts);
        listView.setAdapter(boxAdapter);
        Intent intent = getIntent();
        myDatabase = FirebaseDatabase.getInstance().getReference(mAuth.getCurrentUser().getEmail().replaceAll("[,.]", "") + "-" + intent.getStringExtra("naming").replaceAll("[,.]", ""));
        myDatabase2 = FirebaseDatabase.getInstance().getReference(intent.getStringExtra("naming").replaceAll("[,.]", "")+"-"+mAuth.getCurrentUser().getEmail().replaceAll("[,.]", ""));
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
                for(DataSnapshot ds: snapshot.getChildren()){
                    UserMessage art = ds.getValue(UserMessage.class);
                    assert art != null;
                    arts.add(new UserMessage(art.id, art.message, art.from, art.image));
                }
                boxAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        myDatabase.addValueEventListener(vListener);
        myDatabase2.addValueEventListener(vListener);
    }

    public void addImg(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void saveData(View view) {
        String id = myDatabase.getKey();
        String id2 = myDatabase2.getKey();
        String from = mAuth.getCurrentUser().getEmail().replaceAll("[,.]", "");
        String text = String.valueOf(editText.getText());
        if (text.length()!=0) {
            UserMessage user = new UserMessage(id, text, from, url);
            myDatabase.push().setValue(user);
            Toast.makeText(this, R.string.successful, Toast.LENGTH_SHORT).show();
            url="https://firebasestorage.googleapis.com/v0/b/assistant-51749.appspot.com/o/images%2F5e5845a7ead6d.jpg?alt=media&token=a4766317-7343-4549-8213-f2cc4ca7adae";
             user = new UserMessage(id2, text, from, url);
            myDatabase2.push().setValue(user);
            Toast.makeText(this, R.string.successful, Toast.LENGTH_SHORT).show();
            url="https://firebasestorage.googleapis.com/v0/b/assistant-51749.appspot.com/o/images%2F5e5845a7ead6d.jpg?alt=media&token=a4766317-7343-4549-8213-f2cc4ca7adae";
        }
        editText.setText("");
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
        Intent intent = new Intent(CheckMessage.this, MainActivity.class);
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
}