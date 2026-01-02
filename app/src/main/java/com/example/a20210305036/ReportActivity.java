package com.example.a20210305036;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.provider.AlarmClock;
import android.widget.Toast;

public class ReportActivity extends AppCompatActivity {
    TextView tvReportContent;
    Button btnBack, btnSetAlarm;
    DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        tvReportContent = findViewById(R.id.tvReportContent);
        btnBack = findViewById(R.id.btnBack);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);
        dbHelper = new DataBaseHelper(this);


        String raporMetni = dbHelper.getCompletedItems();


        tvReportContent.setText(raporMetni);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createAlarm("Go Shopping!", 10, 0);
            }
        });
    }

    private void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, false);

        // Alarm Denemesi
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(ReportActivity.this, "Alarm uygulaması bulunamadı", Toast.LENGTH_SHORT).show();
        }
    }
}

