package com.salvadorsp.nemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {
    TextView temperature, pH, turbidity, light;
    Button lightswitch, history, savestats;
    Integer lightstatus, statscount;
    Double tempstatus, phstatus, turbstatus;
    int tempgaugeval, phgaugeval, turbgaugeval;
    CustomGauge tempGauge, phGauge, turbGauge;
    DateFormat df = new SimpleDateFormat("MMM d K:mm");




    DatabaseReference dRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature = findViewById(R.id.temperatureval);
        pH = findViewById(R.id.phval);
        turbidity = findViewById(R.id.turbidityval);
        light = findViewById(R.id.lightstatus);
        lightswitch = findViewById(R.id.lightswitch);
        history = findViewById(R.id.historybutton);
        savestats = findViewById(R.id.savebutton);
        tempGauge = findViewById(R.id.tempgauge);
        phGauge = findViewById(R.id.phgauge);
        turbGauge = findViewById(R.id.turbgauge);

        dRef=FirebaseDatabase.getInstance().getReference();
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tempstatus=dataSnapshot.child("sensorvalues/temperature").getValue(Double.class);
                tempgaugeval=dataSnapshot.child("sensorvalues/temperature").getValue(int.class);
                temperature.setText(Double.toString(tempstatus));
                tempGauge.setValue(tempgaugeval);
                if(tempstatus>=22.22 && tempstatus<=27.77){
                    temperature.setTextColor(Color.parseColor("#28B463"));
                }else if((tempstatus>=20 && tempstatus<=22.21) || (tempstatus<=29.99 && tempstatus>=27.78)){
                    temperature.setTextColor(Color.parseColor("#F39C12"));
                }else if((tempstatus>=0 && tempstatus<=19.99) || (tempstatus>=30 && tempstatus<=50)){
                    temperature.setTextColor(Color.parseColor("#C0392B"));
                    tempnotification();
                }

                phstatus=dataSnapshot.child("sensorvalues/pH").getValue(Double.class);
                phgaugeval=dataSnapshot.child("sensorvalues/pH").getValue(int.class);
                pH.setText(Double.toString(phstatus));
                phGauge.setValue(phgaugeval);
                if(phstatus>=6.5 && phstatus<=7.5){
                    pH.setTextColor(Color.parseColor("#28B463"));
                }else if((phstatus>=5 && phstatus<=6.4) || (phstatus<=9 && phstatus>=7.6)){
                    pH.setTextColor(Color.parseColor("#F39C12"));
                }else if((phstatus>=0 && phstatus<=4.9) || (phstatus>=8.9 && phstatus<=14)){
                    pH.setTextColor(Color.parseColor("#C0392B"));
                    phnotification();
                }

                turbstatus=dataSnapshot.child("sensorvalues/turbidity").getValue(Double.class);
                turbgaugeval=dataSnapshot.child("sensorvalues/turbidity").getValue(int.class);
                turbidity.setText(Double.toString(turbstatus));
                turbGauge.setValue(turbgaugeval);
                if(turbstatus<=7.9){
                    turbidity.setTextColor(Color.parseColor("#28B463")); //green
                }else if(turbstatus>=8 && turbstatus<=10.99){
                    turbidity.setTextColor(Color.parseColor("#F39C12")); //yellow
                }else if(turbstatus>=11){
                    turbidity.setTextColor(Color.parseColor("#C0392B")); //red
                    turbnotification();
                }

                lightstatus=dataSnapshot.child("light").getValue(Integer.class);
                if(lightstatus==1){
                    light.setText("Light is on");
                    lightswitch.setText("TURN OFF");
                }else{
                    light.setText("Light is off");
                    lightswitch.setText("TURN ON");
                }

                statscount=dataSnapshot.child("statscount").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lightswitch.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("light");

                if(lightstatus==1){
                    myRef.setValue(0)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Light is turned off.", Toast.LENGTH_SHORT).show();

                                    }else{
                                        Toast.makeText(MainActivity.this, "Cannot turn off light.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }else{
                    myRef.setValue(1)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Light is turned on.", Toast.LENGTH_SHORT).show();
                                        lightswitch.setText("TURN OFF");
                                    }else{
                                        Toast.makeText(MainActivity.this, "Cannot turn on light.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            }
        });

        savestats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("statscount");
                DatabaseReference tempRef = database.getReference().child("tempvalues");
                DatabaseReference turbRef = database.getReference().child("turbvalues");
                DatabaseReference phRef = database.getReference().child("phvalues");
                DatabaseReference xAxisRef = database.getReference().child("xaxis");
                DatabaseReference labelRef = database.getReference().child("labeldate");
                String date = df.format(Calendar.getInstance().getTime());

                myRef.setValue(statscount+1);
                labelRef.push().setValue(date);
                xAxisRef.push().setValue(statscount+1);
                turbRef.push().setValue(turbstatus);
                phRef.push().setValue(phstatus);
                tempRef.push().setValue(tempstatus)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Successfully saved current stats! See history for more.", Toast.LENGTH_LONG).show();

                                }else{
                                    Toast.makeText(MainActivity.this, "Failed to save current stats.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });


        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDataHistory();
            }
        });
    }

    private void tempnotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("temp","temp", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "temp")
                .setContentText("Nemo")
                .setSmallIcon(R.drawable.nemo_logo)
                .setAutoCancel(true)
                .setContentText("Temperature is at a dangerous level! Check your aquarium now.");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());
    }

    private void phnotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("ph","ph", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ph")
                .setContentText("Nemo")
                .setSmallIcon(R.drawable.nemo_logo)
                .setAutoCancel(true)
                .setContentText("pH is at a dangerous level! Check your aquarium now.");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());
    }

    private void turbnotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("turb","turb", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "turb")
                .setContentText("Nemo")
                .setSmallIcon(R.drawable.nemo_logo)
                .setAutoCancel(true)
                .setContentText("Turbidity is at a dangerous level! Check your aquarium now.");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());
    }

    public void openDataHistory(){
        Intent intent = new Intent(this, DataHistory.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
