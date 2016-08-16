package com.joeymejias.chewsit;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MainActivity.NOTIFICATION_AVAILABLE);

        final Button button = (Button) findViewById(R.id.button_retry_internet);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                retryInternet();
            }
        });
    }

    private void retryInternet() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
