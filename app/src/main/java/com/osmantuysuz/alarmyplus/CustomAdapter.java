package com.osmantuysuz.alarmyplus;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;

import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.ToggleButton;

import java.util.List;

public class CustomAdapter extends BaseAdapter{

    private Context context;
    List<AlarmModel> modelList;

    public CustomAdapter(Context context,List<AlarmModel> modelList){

        this.context=context;
        this.modelList=modelList;

    }

    public void setAlarms(List<AlarmModel> alarms) {

        modelList = alarms;
    }

    @Override
    public int getCount()
    {
        if (modelList != null) {
            return modelList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {

        if (modelList != null) {
            return modelList.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        if (modelList!= null) {
            return modelList.get(i).id;
        }
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if (modelList!=null) {

            AlarmModel model = (AlarmModel) getItem(i);
            LinearLayout container = (LinearLayout) ((Activity) context).getLayoutInflater().
                    inflate(R.layout.custom_alarm_listesi, null);

            TextView txtTime = (TextView) container.findViewById(R.id.alarm_time);
            txtTime.setText(String.format("%02d : %02d", model.timeHour, model.timeMinute));

            TextView txtName = (TextView) container.findViewById(R.id.alarm_ismi);
            txtName.setText(model.name);


            updateTextColor((TextView) container.findViewById(R.id.alarm_pazar), model.getRepeatingDay(AlarmModel.SUNDAY));
            updateTextColor((TextView) container.findViewById(R.id.alarm_pazartesi), model.getRepeatingDay(AlarmModel.MONDAY));
            updateTextColor((TextView) container.findViewById(R.id.alarm_sali), model.getRepeatingDay(AlarmModel.TUESDAY));
            updateTextColor((TextView) container.findViewById(R.id.alarm_carsamba), model.getRepeatingDay(AlarmModel.WEDNESDAY));
            updateTextColor((TextView) container.findViewById(R.id.alarm_persembe), model.getRepeatingDay(AlarmModel.THURSDAY));
            updateTextColor((TextView) container.findViewById(R.id.alarm_cuma), model.getRepeatingDay(AlarmModel.FRIDAY));
            updateTextColor((TextView) container.findViewById(R.id.alarm_cumartesi), model.getRepeatingDay(AlarmModel.SATURDAY));

            SwitchCompat alarmOnOff = (SwitchCompat) container.findViewById(R.id.alarmOnOff);
            alarmOnOff.setChecked(model.isEnabled);
            alarmOnOff.setTag(Long.valueOf(model.id));
            alarmOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ((MainListActivity) context).setAlarmEnabled(((Long) buttonView.getTag()).longValue(), isChecked);

                }
            });

            container.setTag(Long.valueOf(model.id));
            container.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    ((MainListActivity) context).startAlarmDetailsActivity(((Long) view.getTag()).longValue());
                }
            });

            container.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {
                    ((MainListActivity) context).deleteAlarm(((Long) view.getTag()).longValue());
                    return true;
                }
            });


            return container;
        }


        return null;
    }


    private void updateTextColor(TextView view, boolean isOn) {
        if (isOn) {
            view.setTextColor(Color.parseColor("#FF5252"));
        } else {
            view.setTextColor(Color.GRAY);
        }
    }
}


