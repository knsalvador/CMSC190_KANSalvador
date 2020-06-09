package com.salvadorsp.nemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;


public class DataHistory extends AppCompatActivity {
    Button resetstats;
    Integer statscount;
    double tempValRef, phValRef, turbValRef;
    int xValRef;
    String dateValRef;
    List<Double> tempValues = new ArrayList<>();
    List<Double> phValues = new ArrayList<>();
    List<Double> turbValues = new ArrayList<>();
    List<Integer> xValues = new ArrayList<>();
    List<String> labelDate = new ArrayList<>();
    LineChartView lineChartView, lineChartView2, lineChartView3;
    DatabaseReference dref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_history);

        resetstats= findViewById(R.id.resetbutton);
        lineChartView = findViewById(R.id.tempchart);
        lineChartView2 = findViewById(R.id.phchart);
        lineChartView3 = findViewById(R.id.turbchart);

        dref=FirebaseDatabase.getInstance().getReference();
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                statscount=dataSnapshot.child("statscount").getValue(Integer.class);

                if(dataSnapshot.exists()) {
                    tempValues.clear();
                    for (DataSnapshot dss : dataSnapshot.child("tempvalues").getChildren()) {
                        tempValRef = dss.getValue(double.class);
                        tempValues.add(tempValRef);
                    }
                    phValues.clear();
                    for (DataSnapshot dss : dataSnapshot.child("phvalues").getChildren()) {
                        phValRef = dss.getValue(double.class);
                        phValues.add(phValRef);
                    }
                    turbValues.clear();
                    for (DataSnapshot dss : dataSnapshot.child("turbvalues").getChildren()) {
                        turbValRef = dss.getValue(double.class);
                        turbValues.add(turbValRef);
                    }
                    xValues.clear();
                    for (DataSnapshot dss : dataSnapshot.child("xaxis").getChildren()) {
                        xValRef = dss.getValue(int.class);
                        xValues.add(xValRef);
                    }
                    labelDate.clear();
                    for(DataSnapshot dss : dataSnapshot.child("labeldate").getChildren()){
                        dateValRef=dss.getValue(String.class);
                        labelDate.add(dateValRef);
                    }

                }
                //------CHART 1
                double[] yAxisData = new double[tempValues.size()];
                String[] axisData = new String[labelDate.size()];

                for(int i=0;i<tempValues.size();i++){
                    yAxisData[i]=tempValues.get(i);
                }

                for(int i=0;i<labelDate.size();i++){
                    axisData[i]=labelDate.get(i);
                }

                List yAxisValues = new ArrayList();
                List axisValues = new ArrayList();

                Line line = new Line(yAxisValues).setColor(Color.parseColor("#2471A3"));

                for(int i = 0; i < axisData.length; i++){
                    axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
                }

                for (int i = 0; i < yAxisData.length; i++){
                    yAxisValues.add(new PointValue(i, (float) yAxisData[i]));
                }

                List lines = new ArrayList();
                lines.add(line);

                LineChartData data = new LineChartData();
                data.setLines(lines);

                Axis axis = new Axis();
                axis.setValues(axisValues);
                data.setAxisXBottom(axis);

                Axis yAxis = new Axis();
                data.setAxisYLeft(yAxis);

                lineChartView.setLineChartData(data);
                //------CHART 2
                double[] yAxisData2 = new double[phValues.size()];
                String[] axisData2 = new String[labelDate.size()];

                for(int i=0;i<phValues.size();i++){
                    yAxisData2[i]=phValues.get(i);
                }

                for(int i=0;i<labelDate.size();i++){
                    axisData2[i]=labelDate.get(i);
                }

                List yAxisValues2 = new ArrayList();
                List axisValues2 = new ArrayList();

                Line line2 = new Line(yAxisValues2).setColor(Color.parseColor("#2471A3"));

                for(int i = 0; i < axisData2.length; i++){
                    axisValues2.add(i, new AxisValue(i).setLabel(axisData2[i]));
                }

                for (int i = 0; i < yAxisData2.length; i++){
                    yAxisValues2.add(new PointValue(i, (float) yAxisData2[i]));
                }

                List lines2 = new ArrayList();
                lines2.add(line2);

                LineChartData data2 = new LineChartData();
                data2.setLines(lines2);

                Axis axis2 = new Axis();
                axis2.setValues(axisValues2);
                data2.setAxisXBottom(axis2);

                Axis yAxis2 = new Axis();
                data2.setAxisYLeft(yAxis2);

                lineChartView2.setLineChartData(data2);
                //------CHART 3
                double[] yAxisData3 = new double[turbValues.size()];
                String[] axisData3 = new String[labelDate.size()];

                for(int i=0;i<turbValues.size();i++){
                    yAxisData3[i]=turbValues.get(i);
                }

                for(int i=0;i<labelDate.size();i++){
                    axisData3[i]=labelDate.get(i);
                }

                List yAxisValues3 = new ArrayList();
                List axisValues3 = new ArrayList();

                Line line3 = new Line(yAxisValues3).setColor(Color.parseColor("#2471A3"));

                for(int i = 0; i < axisData3.length; i++){
                    axisValues3.add(i, new AxisValue(i).setLabel(axisData3[i]));
                }

                for (int i = 0; i < yAxisData3.length; i++){
                    yAxisValues3.add(new PointValue(i, (float) yAxisData3[i]));
                }

                List lines3 = new ArrayList();
                lines3.add(line3);

                LineChartData data3 = new LineChartData();
                data3.setLines(lines3);

                Axis axis3 = new Axis();
                axis3.setValues(axisValues3);
                data3.setAxisXBottom(axis3);

                Axis yAxis3 = new Axis();
                data3.setAxisYLeft(yAxis3);

                lineChartView3.setLineChartData(data3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

            //reset stats
        resetstats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("statscount");
                DatabaseReference tempRef = database.getReference().child("tempvalues");
                DatabaseReference turbRef = database.getReference().child("turbvalues");
                DatabaseReference phRef = database.getReference().child("phvalues");
                DatabaseReference xAxisRef = database.getReference().child("xaxis");
                DatabaseReference labelRef = database.getReference().child("labeldate");
                labelRef.removeValue();
                tempRef.removeValue();
                turbRef.removeValue();
                phRef.removeValue();
                xAxisRef.removeValue();
                myRef.setValue(0)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(DataHistory.this, "Successfully reset history!", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(DataHistory.this, "Failed to reset history.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });
    }
}
