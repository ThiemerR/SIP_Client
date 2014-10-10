package com.sip_client.ui;

import java.util.Calendar;

import com.sip_client.MainActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

/**
 * Class for showing TimePicker for setting the SIP password valid time
 * Call  setTime of  MainActivity
 * @author Robert Thieme
 *
 */
public class TimePicker_DialogFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener
{

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    // //////////////////////////////////////////////////////////////////////////////
    public void onTimeSet(TimePicker _View, int _HourOfDay, int _Minute)
    {
        ((MainActivity)getActivity()).setTime(_HourOfDay, _Minute);
    }
}