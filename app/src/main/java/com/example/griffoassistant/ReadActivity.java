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
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GestureDetectorCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReadActivity extends AppCompatActivity {

    private ListView listView;
    BoxAdapter boxAdapter;
    private DatabaseReference myDatabase;
    ArrayList<Art> arts = new ArrayList<>();

    private GestureDetectorCompat lSwipeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        init();
        getData();
        setOnClickItem();
        lSwipeDetector = new GestureDetectorCompat(this, new MyGestureListener());
        listView.setOnTouchListener((v, event) -> lSwipeDetector.onTouchEvent(event));
    }

    private void init(){
        ConstraintLayout back = findViewById(R.id.back);
        back.getBackground().setAlpha(127);
        listView = findViewById(R.id.listViewArt);
        arts= new ArrayList<>();
        boxAdapter = new BoxAdapter(this, arts);
        listView.setAdapter(boxAdapter);
        String ART = "Artwork";
        myDatabase = FirebaseDatabase.getInstance().getReference(ART);
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
                    Art art = ds.getValue(Art.class);
                    assert art != null;
                    arts.add(new Art(art.id, art.nameArt, art.nameAuthor, art.textArt, art.image));
                }
                boxAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        myDatabase.addValueEventListener(vListener);
    }

    public void clickOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ReadActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void clickAdd(View view) {
        Intent intent = new Intent(ReadActivity.this, ArtworksActivity.class);
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
                Art art = arts.get(position);

                Intent intent= new Intent(ReadActivity.this, ShowActivity.class);
                intent.putExtra("art_name", art.nameArt);
                intent.putExtra("art_auth", art.nameAuthor);
                intent.putExtra("art_text", art.textArt);
                startActivity(intent);
            }
        });
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        private static final int SWIPE_MIN_DISTANCE = 430;
        private static final int SWIPE_MIN_VELOCITY = 200;
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){

            if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_MIN_VELOCITY) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ReadActivity.this, MainActivity.class);
                startActivity(intent);
            }
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_MIN_VELOCITY) {
                Intent intent = new Intent(ReadActivity.this, ArtworksActivity.class);
                startActivity(intent);
            }
            return false;
        }
    }
}