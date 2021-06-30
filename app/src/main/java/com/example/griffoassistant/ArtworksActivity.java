package com.example.griffoassistant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ArtworksActivity extends AppCompatActivity {

    private EditText edName, edAuthor, edText;
    private DatabaseReference myDatabase;
    private String ART="Artwork";
    private ImageView newImage;
    private StorageReference storageReference;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artworks);
        init();
    }

    private void init(){
        edName = findViewById(R.id.editName);
        edAuthor = findViewById(R.id.editGGG);
        edText = findViewById(R.id.editText);
        myDatabase = FirebaseDatabase.getInstance().getReference(ART);
        storageReference = FirebaseStorage.getInstance().getReference("images");
        newImage = findViewById(R.id.newImage);
        Picasso.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/assistant-51749.appspot.com/o/images%2F5e5845a7ead6d.jpg?alt=media&token=a4766317-7343-4549-8213-f2cc4ca7adae")
                .placeholder(R.drawable.exit)
                .error(R.drawable.exit)
                .into(newImage);
    }

    public void addImg(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(ArtworksActivity.this, MainActivity.class));
        clearStrings();
    }

    private void clearStrings(){
        edName.setText("");
        edAuthor.setText("");
        edText.setText("");
    }

    public void readData(View view) {
        startActivity(new Intent(ArtworksActivity.this, ReadActivity.class));
        clearStrings();
    }

    public void saveData(View view) {
        String id = myDatabase.getKey();
        String name = edName.getText().toString();
        String author = edAuthor.getText().toString();
        String text = edText.getText().toString();
        if (name.length()!=0 && author.length()!=0 && text.length()!=0) {
            Art newArt = new Art(id, name, author, text, url);
            myDatabase.push().setValue(newArt);
            Toast.makeText(this, R.string.successful, Toast.LENGTH_SHORT).show();
            clearStrings();
            url="https://firebasestorage.googleapis.com/v0/b/assistant-51749.appspot.com/o/images%2F5e5845a7ead6d.jpg?alt=media&token=a4766317-7343-4549-8213-f2cc4ca7adae";
        } else Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data!=null && data.getData() != null){
            System.out.println("Img URI : "+ data.getData());
            newImage.setImageURI(data.getData());
            uploadFileInFireBaseStorage(data.getData());

        }
    }

    public void uploadFileInFireBaseStorage (Uri uri){
        UploadTask uploadTask = storageReference.child(System.currentTimeMillis() + FirebaseAuth.getInstance().getCurrentUser().getUid()).putFile(uri);
        uploadTask.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                task -> url = task.getResult().toString()));
    }


}