package com.osmantuysuz.alarmyplus;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainListActivity extends AppCompatActivity  {

    private CustomAdapter adapter;
    private ListView liste;
    private FloatingActionButton fab;
    private Veritabani veritabani=new Veritabani(this);
    /*private AdapterView.OnItemClickListener dinleyici=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            startAlarmDetailsActivity(id);
        }
    };
    private AdapterView.OnItemLongClickListener dinleyiciLong = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            deleteAlarm(id);
            return true;
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        liste= (ListView) findViewById(R.id.liste);
        fab= (FloatingActionButton) findViewById(R.id.fab);

        adapter=new CustomAdapter(this,veritabani.getAlarms());
        liste.setAdapter(adapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startAlarmDetailsActivity(-1);
            }
        });

    }



    public void startAlarmDetailsActivity(long id) {
        Intent intent = new Intent(this, AlarmDetayActivity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, 0);
    }



    public void deleteAlarm(long id) {
        final long alarmId = id;

        new AlertDialog.Builder(MainListActivity.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Sil")
                .setMessage("Alarm silinecek, onaylıyor musunuz?")
                .setNegativeButton("Hayır",null)
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlarmManagerHelper.cancelAlarms(MainListActivity.this);
                        veritabani.deleteAlarm(alarmId);
                        adapter.setAlarms(veritabani.getAlarms());
                        adapter.notifyDataSetChanged();
                        AlarmManagerHelper.setAlarms(MainListActivity.this);
                    }
                }).show();

    }

    public void setAlarmEnabled(long id, boolean isEnabled) {

        AlarmManagerHelper.cancelAlarms(this);

        AlarmModel model = veritabani.getAlarm(id);
        model.isEnabled = isEnabled;
        veritabani.updateAlarm(model);

        AlarmManagerHelper.setAlarms(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            adapter.setAlarms(veritabani.getAlarms());
            adapter.notifyDataSetChanged();
        }
    }
}