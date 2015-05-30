package hr.tvz.zavrsni.vlahovic.recorder.activities.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import hr.tvz.zavrsni.vlahovic.recorder.R;

public class MainActivity extends AppCompatActivity {

    private Button disconnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        disconnectButton=(Button) findViewById(R.id.disconnectButton);

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,DeviceConnectionActivity.class));
            }
        });
    }
}
