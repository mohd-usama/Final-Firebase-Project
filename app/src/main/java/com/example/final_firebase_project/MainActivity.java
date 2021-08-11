package com.example.final_firebase_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText name;
    EditText date_Picker;
    Button upload, photo, sign;
    ImageView pic, sin;

    //spinner value
    TextView text;
    Spinner spin;
    DatabaseReference databaseReference;
    String item;
    Member member;

    String[] qualification = {"choose", "BCA", "MCA", "BTech", "MTech", "BBA", "BA", "BCom"};


    DatePickerDialog.OnDateSetListener setListener;

    Uri filepath, filepath2;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.namex);
        spin = findViewById(R.id.spinner);
        date_Picker = findViewById(R.id.datepicker);
        upload = findViewById(R.id.uplaodx);
        photo = findViewById(R.id.getphoto);
        sign = findViewById(R.id.getsign);
        pic = findViewById(R.id.photox);
        sin = findViewById(R.id.signx);

        //Spinner Implements
        text = findViewById(R.id.spintext);
        spin = findViewById(R.id.spinner);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("value qualification");
        spin.setOnItemSelectedListener(this);

        member = new Member();
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, qualification);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);


        //calender implements
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        date_Picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        //year = year+1;
                        String date = day + "/" + month + "/" + year;
                        date_Picker.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });


        //upload photo to firebase
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadtofirebase();
                valuesave(item);
                nameDOBtofirebase();
            }
        });

        //Photo browse
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(MainActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "select your image"), 1);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        //signature browse
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(MainActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "select your image"), 2);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });
    }

    private void nameDOBtofirebase()
    {
        //name and dob to firebase
        String name2=name.getText().toString().trim();
        String dob=date_Picker.getText().toString().trim();

        DataHolder dataHolder=new DataHolder(name2,dob);

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        databaseReference=db.getReference("value name and dob");
        databaseReference.child("").setValue(dataHolder);

        name.setText("");
        date_Picker.setText("");

        Toast.makeText(getApplicationContext(),"Value inserted",Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            filepath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                pic.setImageBitmap(bitmap);
            } catch (Exception e) {

            }
        }

        if (requestCode == 2 && resultCode == RESULT_OK) {
            filepath2 = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                sin.setImageBitmap(bitmap);
            } catch (Exception e) {

            }
        }
    }

    private void uploadtofirebase() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("File Uploader");
        progressDialog.show();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference().child("photo1");
        StorageReference reference2 = firebaseStorage.getReference().child("sign1");

        reference.putFile(filepath);
        reference2.putFile(filepath2)

                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        float percent = (100 * snapshot.getTotalByteCount()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("upload" + (int) percent + "%");
                    }
                });

    }
    //spinner 2
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        item = spin.getSelectedItem().toString();
        text.setText(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void valuesave(String item) {
        if (item == "choose") {
            Toast.makeText(this, "Select qualification", Toast.LENGTH_LONG).show();
        } else {
            member.setQualification(item);
            //String id = databaseReference.push().getKey();
            databaseReference.child("").setValue(member);
            Toast.makeText(this, "value saved", Toast.LENGTH_SHORT).show();
        }
    }
}



