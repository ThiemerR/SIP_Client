package com.sip_client.ui;

import com.example.sip_client.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

/**
 * DialogFragment Class for showing simple alerts information for the user
 * @author Robert Thieme
 */
public class Alert_DialogFragment extends DialogFragment 
{

    static String m_Message;
    public static Alert_DialogFragment newInstance(int _Title, String _Message) 
    {
        m_Message = _Message;
        Alert_DialogFragment Frag = new Alert_DialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", _Title);
        Frag.setArguments(args);        
        return Frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle _SavedInstanceState) 
    {
        int Title = getArguments().getInt("title");
        
        TextView TextView = new TextView(getActivity());
        TextView.setText(m_Message);
       
        return new AlertDialog.Builder(getActivity())
                .setNeutralButton(R.string.ok,
                        new DialogInterface.OnClickListener()
                        {    
                            public void onClick(DialogInterface _Dialog, int _Which)
                            {
                                 //If necessary call methods from MainActivity after user perform click                        
                            }
                        })
                .setIcon(R.drawable.ic_action_error)
                .setTitle(Title)
                .setView(TextView)
                .create();
    }
}
