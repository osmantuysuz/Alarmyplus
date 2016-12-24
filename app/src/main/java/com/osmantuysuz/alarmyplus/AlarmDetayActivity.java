package com.osmantuysuz.alarmyplus;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.SwitchCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;


public class AlarmDetayActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private EditText edtName;
    private SwitchCompat haftalikTekrar,pazartesi,sali,carsamba,persembe,cuma,cumartesi,pazar;
    private TextView secilenMelodiIsmi;
    private AlarmModel alarmModel;
    private Veritabani veritabani;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detay);

//        getSupportActionBar().setCustomView(R.layout.toolbar);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);








        timePicker= (TimePicker)findViewById(R.id.time_picker);
        edtName= (EditText) findViewById(R.id.alarm_edittext);
        secilenMelodiIsmi= (TextView) findViewById(R.id.secilen_melodi);
        haftalikTekrar= (SwitchCompat) findViewById(R.id.alarm_tekrar);
        pazartesi= (SwitchCompat) findViewById(R.id.alarm_tekrar_pazartesi);
        sali= (SwitchCompat) findViewById(R.id.alarm_tekrar_sali);
        carsamba= (SwitchCompat) findViewById(R.id.alarm_tekrar_carsamba);
        persembe= (SwitchCompat) findViewById(R.id.alarm_tekrar_persembe);
        cuma= (SwitchCompat) findViewById(R.id.alarm_tekrar_cuma);
        cumartesi= (SwitchCompat) findViewById(R.id.alarm_tekrar_cumartesi);
        pazar= (SwitchCompat) findViewById(R.id.alarm_tekrar_pazar);

        timePicker.setIs24HourView(true);
        veritabani= new Veritabani(this);

        long id = getIntent().getExtras().getLong("id");

        if (id == -1) {
            // yeni alarm girilecekse
            alarmModel = new AlarmModel();
        } else {
            // Alarm güncellenecekse
            alarmModel = veritabani.getAlarm(id);

            timePicker.setCurrentMinute(alarmModel.timeMinute);
            timePicker.setCurrentHour(alarmModel.timeHour);

            edtName.setText(alarmModel.name);

            haftalikTekrar.setChecked(alarmModel.haftalikTekrar);
            pazar.setChecked(alarmModel.getRepeatingDay(AlarmModel.SUNDAY));
            pazartesi.setChecked(alarmModel.getRepeatingDay(AlarmModel.MONDAY));
            sali.setChecked(alarmModel.getRepeatingDay(AlarmModel.TUESDAY));
            carsamba.setChecked(alarmModel.getRepeatingDay(AlarmModel.WEDNESDAY));
            persembe.setChecked(alarmModel.getRepeatingDay(AlarmModel.THURSDAY));
            cuma.setChecked(alarmModel.getRepeatingDay(AlarmModel.FRIDAY));
            cumartesi.setChecked(alarmModel.getRepeatingDay(AlarmModel.SATURDAY));

            secilenMelodiIsmi.setText(RingtoneManager.getRingtone(this, alarmModel.alarmTone).getTitle(this));
        }

        LinearLayout ringToneContainer = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
        ringToneContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(intent , 1);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    alarmModel.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    secilenMelodiIsmi.setText(RingtoneManager.getRingtone(this, alarmModel.alarmTone).getTitle(this));
                    break;

                default:
                    break;
            }
        }
    }

    public void kaydet(View v){

        updateModelFromLayout();
        AlarmManagerHelper.cancelAlarms(this);

        if (alarmModel.id < 0) {
            veritabani.createAlarm(alarmModel);
        } else {
            veritabani.updateAlarm(alarmModel);
        }

        AlarmManagerHelper.setAlarms(this);

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save: {

                updateModelFromLayout();
                AlarmManagerHelper.cancelAlarms(this);

                if (alarmModel.id < 0) {
                    veritabani.createAlarm(alarmModel);
                } else {
                    veritabani.updateAlarm(alarmModel);
                }

                AlarmManagerHelper.setAlarms(this);

                setResult(RESULT_OK);
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateModelFromLayout() {
        alarmModel.timeMinute = timePicker.getCurrentMinute().intValue();
        alarmModel.timeHour = timePicker.getCurrentHour().intValue();
        alarmModel.name = edtName.getText().toString();
        alarmModel.haftalikTekrar = haftalikTekrar.isChecked();
        alarmModel.setRepeatingDay(AlarmModel.SUNDAY, pazar.isChecked());
        alarmModel.setRepeatingDay(AlarmModel.MONDAY, pazartesi.isChecked());
        alarmModel.setRepeatingDay(AlarmModel.TUESDAY, sali.isChecked());
        alarmModel.setRepeatingDay(AlarmModel.WEDNESDAY, carsamba.isChecked());
        alarmModel.setRepeatingDay(AlarmModel.THURSDAY, persembe.isChecked());
        alarmModel.setRepeatingDay(AlarmModel.FRIDAY, cuma.isChecked());
        alarmModel.setRepeatingDay(AlarmModel.SATURDAY, cumartesi.isChecked());
        alarmModel.isEnabled = true;
    }

}
