package com.sip_client.ui;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.sipdroid.sipua.base.SipdroidEngine;
import org.sipdroid.sipua.ui.CreateAccount;
import org.sipdroid.sipua.ui.Receiver;

import com.example.sip_client.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.jwetherell.quick_response_code.CaptureActivity;
import com.jwetherell.quick_response_code.data.Contents;
import com.jwetherell.quick_response_code.qrcode.QRCodeEncoder;
import com.sip_client.MainActivity;
import com.sip_client.StorageFile;
import com.sip_client.MainActivity.CallsCursor;
import com.sip_client.Util;
import com.sip_client.contactlist.ContactList;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.CallLog.Calls;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Start_Fragment extends Fragment
{
    private NfcAdapter m_NfcAdapter;
    public TextView m_StatusTextView;
    
    //Sipdroid Members
    private AutoCompleteTextView    m_Sip_uri_box;     
    private static final String[] PROJECTION = new String[]
                                             {
            Calls._ID,
            Calls.NUMBER,
            Calls.CACHED_NAME
                                             };
    
    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle _SavedInstanceState)
    {
        super.onCreate(_SavedInstanceState);
    }
    
    @Override
    public void onStart()
    {        
        super.onStart();
        ContentResolver content = getActivity().getContentResolver();
        Cursor cursor = content.query(Calls.CONTENT_URI,
                PROJECTION, Calls.NUMBER + " like ?", new String[]
                {
                    "%@%"
                }, Calls.DEFAULT_SORT_ORDER);
        CallsAdapter adapter = new CallsAdapter((MainActivity)getActivity(), cursor);
        m_Sip_uri_box.setAdapter(adapter);        
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater _Inflater, ViewGroup _Container, Bundle _SavedInstanceState)
    {
        return _Inflater.inflate(R.layout.fragment_start, _Container, false);
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityCreated(Bundle _SavedInstanceState)
    {
        super.onActivityCreated(_SavedInstanceState);         
        m_StatusTextView = (TextView) getView().findViewById(R.id.StatusTextView);
        m_NfcAdapter = NfcAdapter.getDefaultAdapter(getActivity().getApplicationContext());
        if (m_NfcAdapter == null)
        {
            m_StatusTextView.setText("NFC is not available for the device!!!");
        }
        else
        {
            m_StatusTextView.setText("NFC is available for the device");
        }
        
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getActivity().getIntent().getAction()))
        {
            Tag DetectedTag = getActivity().getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // GET NDEF MESSAGES IN THE TAG
            NdefMessage[] NdefMessages = getNdefMessages(getActivity().getIntent());
            processNdef_RTD_TEXT(NdefMessages[0], 0);
        }
        
        Button SearchQRCButton = (Button) getView().findViewById(R.id.ButtonSearchQRC);
        SearchQRCButton.setOnClickListener(new OnClickListener()
        {            
            @Override
            public void onClick(View v)
            {
                Intent QRCIntent = new Intent( getActivity(), CaptureActivity.class);
                startActivityForResult(QRCIntent, Util.REQUEST_CODE_QRC);                
            }
        });
        
        ////////////////////////////////////////////////////////////////////////////////
        // Sipdorid Code
        ////////////////////////////////////////////////////////////////////////////////
        m_Sip_uri_box = (AutoCompleteTextView) getView().findViewById(R.id.txt_callee);        
        m_Sip_uri_box.setOnKeyListener(new OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    ((MainActivity) getActivity()).call_menu(m_Sip_uri_box);
                    return true;
                }
                return false;
            }
        });
        m_Sip_uri_box.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3)
            {
                ((MainActivity) getActivity()).call_menu(m_Sip_uri_box);
            }
        });        
        MainActivity.on((MainActivity)getActivity(), true);
        

        final Context mContext = (MainActivity) getActivity();   

        if (!PreferenceManager.getDefaultSharedPreferences((MainActivity) getActivity()).getBoolean(Settings_Fragment.PREF_NOPORT,
                Settings_Fragment.DEFAULT_NOPORT))
        {
            boolean ask = false;
            for (int i = 0; i < SipdroidEngine.LINES; i++)
            {
                String j = (i != 0 ? "" + i : "");
                if (PreferenceManager.getDefaultSharedPreferences((MainActivity) getActivity())
                        .getString(Settings_Fragment.PREF_SERVER + j, Settings_Fragment.DEFAULT_SERVER).equals(Settings_Fragment.DEFAULT_SERVER)
                        && PreferenceManager.getDefaultSharedPreferences((MainActivity) getActivity())
                                .getString(Settings_Fragment.PREF_USERNAME + j, Settings_Fragment.DEFAULT_USERNAME).length() != 0
                        &&
                        PreferenceManager.getDefaultSharedPreferences((MainActivity) getActivity())
                                .getString(Settings_Fragment.PREF_PORT + j, Settings_Fragment.DEFAULT_PORT).equals(Settings_Fragment.DEFAULT_PORT))
                    ask = true;
            }
            if (ask)
                new AlertDialog.Builder((MainActivity) getActivity())
                        .setMessage(R.string.dialog_port)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                Editor edit = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                                for (int i = 0; i < SipdroidEngine.LINES; i++)
                                {
                                    String j = (i != 0 ? "" + i : "");
                                    if (PreferenceManager.getDefaultSharedPreferences(mContext)
                                            .getString(Settings_Fragment.PREF_SERVER + j, Settings_Fragment.DEFAULT_SERVER)
                                            .equals(Settings_Fragment.DEFAULT_SERVER)
                                            && PreferenceManager.getDefaultSharedPreferences(mContext)
                                                    .getString(Settings_Fragment.PREF_USERNAME + j, Settings_Fragment.DEFAULT_USERNAME)
                                                    .length() != 0
                                            &&
                                            PreferenceManager.getDefaultSharedPreferences(mContext)
                                                    .getString(Settings_Fragment.PREF_PORT + j, Settings_Fragment.DEFAULT_PORT)
                                                    .equals(Settings_Fragment.DEFAULT_PORT))
                                        edit.putString(Settings_Fragment.PREF_PORT + j, "5061");
                                }
                                edit.commit();
                                Receiver.engine(mContext).halt();
                                Receiver.engine(mContext).StartEngine();
                            }
                        })
                        .setNeutralButton(R.string.no, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {

                            }
                        })
                        .setNegativeButton(R.string.dontask, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                Editor edit = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                                edit.putBoolean(Settings_Fragment.PREF_NOPORT, true);
                                edit.commit();
                            }
                        })
                        .show();
        }
        else if (PreferenceManager.getDefaultSharedPreferences((MainActivity) getActivity())
                .getString(Settings_Fragment.PREF_PREF, Settings_Fragment.DEFAULT_PREF).equals(Settings_Fragment.VAL_PREF_PSTN)
                &&
                !PreferenceManager.getDefaultSharedPreferences((MainActivity) getActivity()).getBoolean(Settings_Fragment.PREF_NODEFAULT,
                        Settings_Fragment.DEFAULT_NODEFAULT))
            new AlertDialog.Builder((MainActivity) getActivity())
                    .setMessage(R.string.dialog_default)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            Editor edit = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                            edit.putString(Settings_Fragment.PREF_PREF, Settings_Fragment.VAL_PREF_SIP);
                            edit.commit();
                        }
                    })
                    .setNeutralButton(R.string.no, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {

                        }
                    })
                    .setNegativeButton(R.string.dontask, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            Editor edit = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                            edit.putBoolean(Settings_Fragment.PREF_NODEFAULT, true);
                            edit.commit();
                        }
                    })
                    .show();
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityResult(int _RequestCode, int _ResultCode, Intent _Data) 
    {
        Bundle Extras = _Data.getExtras();
        if(_RequestCode == Util.REQUEST_CODE_QRC && _ResultCode == Activity.RESULT_OK)
        {   
            String Payload = Extras.getString("Payload");
            if (Payload != null)
            {
                setSipPreferences(Payload);
            }
        }        
        super.onActivityResult(_RequestCode, _ResultCode, _Data);
    }

    // //////////////////////////////////////////////////////////////////////////////
    private NdefMessage[] getNdefMessages(Intent _Intent)
    {
        NdefMessage[] message = null;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(_Intent.getAction()))
        {
            Parcelable[] rawMessages =
                    _Intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null)
            {
                message = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++)
                {
                    message[i] = (NdefMessage) rawMessages[i];
                }
            }
            else
            {
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]
                {
                        record
                });
                message = new NdefMessage[]
                {
                        msg
                };
            }
        }
        else
        {
            Log.d("NFC Tag Reading Error", "Unknown intent.");
            ((MainActivity) getActivity()).finish();
        }
        return message;
    }    
    
    // //////////////////////////////////////////////////////////////////////////////
    public static class CallsAdapter extends CursorAdapter implements Filterable
    {
        public CallsAdapter(Context _Context, Cursor _Cursor)
        {
            super(_Context, _Cursor);
            m_Content = _Context.getContentResolver();
        }

        public View newView(Context _Context, Cursor _Cursor, ViewGroup _Parent)
        {
            final LayoutInflater inflater = LayoutInflater.from(_Context);
            final TextView View = (TextView) inflater.inflate(
                    android.R.layout.simple_dropdown_item_1line, _Parent, false);
            String phoneNumber = _Cursor.getString(1);
            View.setText(phoneNumber);
            return View;
        }

        @Override
        public void bindView(View View, Context _Context, Cursor _Cursor)
        {
            String phoneNumber = _Cursor.getString(1);
            ((TextView) View).setText(phoneNumber);
        }

        @Override
        public String convertToString(Cursor _Cursor)
        {
            String phoneNumber = _Cursor.getString(1);
            if (phoneNumber.contains(" <"))
                phoneNumber = phoneNumber.substring(0, phoneNumber.indexOf(" <"));
            return phoneNumber;
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence _Constraint)
        {
            if (getFilterQueryProvider() != null)
            {
                return new CallsCursor(getFilterQueryProvider().runQuery(_Constraint));
            }

            StringBuilder Buffer;
            String[] Args;
            Buffer = new StringBuilder();
            Buffer.append(Calls.NUMBER);
            Buffer.append(" LIKE ? OR ");
            Buffer.append(Calls.CACHED_NAME);
            Buffer.append(" LIKE ?");
            String arg = "%" + (_Constraint != null && _Constraint.length() > 0 ?
                    _Constraint.toString() : "@") + "%";
            Args = new String[]
            {
                    arg, arg
            };

            return new CallsCursor(m_Content.query(Calls.CONTENT_URI, PROJECTION,
                    Buffer.toString(), Args,
                    Calls.NUMBER + " asc"));
        }

        private ContentResolver m_Content;
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    public void processNdef_RTD_TEXT(NdefMessage _Ndefmessages, int _Index)
    {
        String Payload = null;        
        NdefRecord NdefRecord = _Ndefmessages.getRecords()[_Index];
        byte[] NFC_SEND_ID = NdefRecord.getId();
        byte StatusByte = NdefRecord.getPayload()[0];
        int languageCodeLength = StatusByte & 0x3F;

        int isUTF8 = StatusByte - languageCodeLength;
        if (isUTF8 == 0x00)
        {
            m_StatusTextView.append((_Index + 1) + "th. Record is UTF-8\n");
            Payload = new String(NdefRecord.getPayload(), 1 + languageCodeLength,
                    NdefRecord.getPayload().length - 1 - languageCodeLength,
                    Charset.forName("UTF-8"));
        }
        else if (isUTF8 == -0x80)
        {
            m_StatusTextView.append((_Index + 1) + "th. Record is UTF-16\n");
            Payload = new String(NdefRecord.getPayload(), 1 + languageCodeLength,
                    NdefRecord.getPayload().length - 1 - languageCodeLength,
                    Charset.forName("UTF-16"));
        }
        
        m_StatusTextView.append((_Index + 1) + "th. Record payload: " + Payload + "\n");        
        
        if (Arrays.equals(NFC_SEND_ID, Util.ID_SEND_ALL))
        {
            setSipPreferences(Payload);
        }
        else if(Arrays.equals(NFC_SEND_ID, Util.ID_SEND_PW))
        {       
            setSipPasswordPreferences(Payload);
        }        
        else
        {
            Toast.makeText(getActivity(), R.string.nfcnodidfound, Toast.LENGTH_SHORT).show();
        }        
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    private void setSipPreferences(String _Payload)
    {
        Editor SettingsEditor = null;  
        String[] Parts = null;
        String User = null;
        String ServerDomain = null;
        String ServerPort = null;
        String StunServer = null;
        String StunPort = null;        
        String[][] HotelContacts = null;
        
        SettingsEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        SettingsEditor.putBoolean(Settings_Fragment.PREF_STUN, false); // Set Stun Default to false
        SettingsEditor.putString(Settings_Fragment.PREF_PROTOCOL, Settings_Fragment.DEFAULT_PROTOCOL) ;// Set default protocol
        Parts = _Payload.split("/us");
        User = Parts[0];
        _Payload = Parts[1];
        Parts = _Payload.split("/sd");
        ServerDomain = Parts[0];
        _Payload = Parts[1];
        Parts = _Payload.split("/po");
        ServerPort = Parts[0];
        if(_Payload.contains("/ss"))// Stun is using
        {
            _Payload = Parts[1];
            Parts = _Payload.split("/ss");
            StunServer = Parts[0];
            _Payload = Parts[1];
            Parts = _Payload.split("/sp");
            StunPort = Parts[0];                
            SettingsEditor.putBoolean(Settings_Fragment.PREF_STUN, true);
            SettingsEditor.putString(Settings_Fragment.PREF_STUN_SERVER, StunServer);
            SettingsEditor.putString(Settings_Fragment.PREF_STUN_SERVER_PORT, StunPort);
        }
        if(_Payload.contains("/pn"))
        {
            _Payload = Parts[1];
            Parts = _Payload.split("/pn");
            HotelContacts = new String[Parts.length - 1][2];
            for (int Index = 0; Index < Parts.length - 1; ++Index)
            {
                String[] NewParts = Parts[Index + 1].split("/nr");
                HotelContacts[Index][0] = NewParts[0];
                HotelContacts[Index][1] = NewParts[1];
            }
            ((MainActivity) getActivity()).createNewContactlist(HotelContacts);
        }
        SettingsEditor.putString(Settings_Fragment.PREF_USERNAME, User);
        SettingsEditor.putString(Settings_Fragment.PREF_SERVER, ServerDomain);
        SettingsEditor.putString(Settings_Fragment.PREF_PORT, ServerPort);
        SettingsEditor.commit();
        //Restart the Engine
        Receiver.engine(getActivity()).halt();
        Receiver.engine(getActivity()).StartEngine();
        Toast.makeText(getActivity(), getString(R.string.setsipaccount) + User, Toast.LENGTH_SHORT).show();
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    private void setSipPasswordPreferences(String Payload)
    {
        Editor SettingsEditor = null; 
        String[] Parts = null;
        String Password = null;
        String ValidTime = null;        
        
        Parts = Payload.split("/pw");
        Password = Parts[0];
        Payload = Parts[1];
        Parts =  Payload.split("/vt");
        ValidTime =  Parts[0];
        
        StorageFile.saveValidTime(ValidTime);            
         //long FirstStart = System.currentTimeMillis() + DateUtils.MINUTE_IN_MILLIS * Integer.parseInt(ValidTime);
        
        SettingsEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        SettingsEditor.putString(Settings_Fragment.PREF_PASSWORD, Password);
        SettingsEditor.commit();
        //Restart the Engine
        Receiver.engine(getActivity()).halt();
        Receiver.engine(getActivity()).StartEngine();
        Toast.makeText(getActivity(), R.string.setsippassword, Toast.LENGTH_SHORT).show();
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    public void processNdef_RTD_URI(NdefMessage _Ndefmessages)
    {      
        String Payload = null;
        if(m_StatusTextView != null)
        {
            m_StatusTextView.append("RTF_URI_Message \n");
            for (int j = 0; j < _Ndefmessages.getRecords().length; j++)
            {
                NdefRecord Record = _Ndefmessages.getRecords()[j];
                m_StatusTextView.append((j + 1) + "th. Record Tnf: " + Record.getTnf() + "\n");
                m_StatusTextView.append((j + 1) + "th. Record type: " +
                        new String(Record.getType()) + "\n");
                m_StatusTextView.append((j + 1) + "th. Record id: " +
                        new String(Record.getId()) + "\n");
                Payload = new String(Record.getPayload(), 1,
                        Record.getPayload().length - 1, Charset.forName("UTF-8"));
                m_StatusTextView.append((j + 1) + "th. Record payload: " + Payload + "\n");
                byte payloadHeader = Record.getPayload()[0];
                m_StatusTextView.append((j + 1) + "th. Record payload header: " +
                        payloadHeader + "\n");
            }
        }
    }
}
