package com.sip_client.ui;

import java.util.Calendar;

import com.sip_client.MainActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

/**
 * DialogFragment Class for showing DatePicker for setting the SIP password valid time
 * after setting Date call TimePicker for setting Date.
 * Call  setDate of  MainActivity
 * @author Robert Thieme
 */
public class DatePicker_DialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener
{
    boolean m_IsFired = false;
    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    public void onDateSet(DatePicker _View, int _Year, int _Month, int _Day)
    {
        if(!m_IsFired)
        {
            ((MainActivity)getActivity()).setDate(_Year, _Month, _Day);
            TimePicker_DialogFragment TimePicker_DialogFragment = new TimePicker_DialogFragment();        
            TimePicker_DialogFragment.show(getFragmentManager(), "TimePicker");
            m_IsFired = true;
        }
    }
}


