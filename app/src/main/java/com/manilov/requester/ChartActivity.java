package com.manilov.requester;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        drawChart();
        Button button = this.findViewById(R.id.button);
        button.setOnClickListener(it -> {
            this.finish();
        });
    }

    private void drawChart(){
        DataBaseHelper databaseHelper = new DataBaseHelper(getApplicationContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<Entry> values = new ArrayList<>();
        Cursor userCursor =  db.rawQuery("select value from "+ DataBaseHelper.TABLE + " order by _id desc limit 10", null);
        for (float i = 0f; i < userCursor.getCount(); i++) {
            userCursor.moveToNext();
            values.add(new Entry(i + 1, (float) userCursor.getDouble(0)));
        }
        userCursor.close();
        LineDataSet dataset = new LineDataSet(values, "График значений");
        dataset.setColor(Color.GREEN);
        dataset.setMode(LineDataSet.Mode.LINEAR);
        LineData data = new LineData(dataset);
        LineChart chart = findViewById(R.id.chart);
        chart.setData(data);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.animateY(500);
    }
}