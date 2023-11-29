package com.manilov.requester;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.manilov.requester.dto.RequestBodyDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class MainActivity extends AppCompatActivity {
    private final int studentId = 9;
    private final String[] sensors = { "TEMPERATURE", "LIGHT", "GAS", "NOISE", "DOOR"};

    private String selectedSensor = "TEMPERATURE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sensors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)parent.getItemAtPosition(position);
                selectedSensor = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Button button = this.findViewById(R.id.button);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        Runnable task = this::sendRequest;
        executorService.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
        if (button != null) {
            button.setOnClickListener(it -> {
                sendRequest();
            });
        }
        Button button2 = this.findViewById(R.id.button2);
        button2.setOnClickListener(it -> {
            Intent intent = new Intent(this, ChartActivity.class);
            startActivity(intent);
        });
    }

    private void sendRequest() {
        DataBaseHelper databaseHelper = new DataBaseHelper(getApplicationContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        OkHttpClient okHttpClient = new OkHttpClient();
        EditText editText = (EditText)findViewById(R.id.editText);
        String url = editText.getText().toString() + studentId;
        System.out.println("try to send request by url = "+url);
        Random random = new Random();
        double value = random.nextDouble();
        String sensor = selectedSensor;
        RequestBodyDTO requestBodyDTO = new RequestBodyDTO(value, sensor);
        Gson gson = new Gson();
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), gson.toJson(requestBodyDTO)))
                .build();
        try {
            Response execute = okHttpClient.newCall(request).execute();
            System.out.println(execute.body().string() + "; code = " + execute.code());
            databaseHelper.insert(db, value, sensor);
            Cursor  userCursor =  db.rawQuery("select * from "+ DataBaseHelper.TABLE, null);
            while(userCursor.getCount() > 100) {
                databaseHelper.delete(db);
                userCursor =  db.rawQuery("select * from "+ DataBaseHelper.TABLE, null);
            }
            userCursor.close();
        } catch (IOException e) {
            System.out.println("Bad response. Some errors");
            //throw new RuntimeException(e);
        }
    }
}