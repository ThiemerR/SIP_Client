/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * Copyright (C) 2008 Hughes Systique Corporation, USA (http://www.hsc.com)
 * 
 * This file include source code of Sipdroid.java (http://www.sipdroid.org)
 * 
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Author Robert Thieme
 */

package com.sip_client.ui;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.sipdroid.sipua.base.SipdroidEngine;
import org.sipdroid.sipua.ui.Receiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.CallLog.Calls;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sip_client.R;
import com.jwetherell.quick_response_code.CaptureActivity;
import com.sip_client.MainActivity;
import com.sip_client.MainActivity.CallsCursor;
import com.sip_client.StorageFile;
import com.sip_client.Util;
import com.sip_client.Wifi;

/**
 * Fragment Class include will shown when the Application is started Implements some Code of Sipdroid from Sipdroid.java
 * 
 * @author Robert Thieme
 * @author Copyright (C) 2009 The Sipdroid Open Source Project
 * @author Copyright (C) 2008 Hughes Systique Corporation, USA (http://www.hsc.com)
 */
public class Start_Fragment extends Fragment
{
    private final static String LOG_TAG = "Start_Fragment";
    // Sipdroid Members
    private AutoCompleteTextView  m_Sip_uri_box;
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
        
        //Use after AAR is called
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getActivity().getIntent().getAction()))
        {
            // get NDEF message from intent
            NdefMessage[] NdefMessages = getNdefMessages(getActivity().getIntent());
            processNdef_RTD_TEXT(NdefMessages[0], 0);
        }
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
        
        Button SearchQRCButton = (Button) getView().findViewById(R.id.ButtonSearchQRC);
        SearchQRCButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Start CaptureActivity for detection QR-Codes
                Intent QRCIntent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(QRCIntent, Util.REQUEST_CODE_QRC);
            }
        });

        // //////////////////////////////////////////////////////////////////////////////
        // Sipdorid Code
        // //////////////////////////////////////////////////////////////////////////////
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
        MainActivity.on((MainActivity) getActivity(), true);

        final Context mContext = (MainActivity) getActivity();

        if (!PreferenceManager.getDefaultSharedPreferences((MainActivity) getActivity()).getBoolean(
                Settings_Fragment.PREF_NOPORT,
                Settings_Fragment.DEFAULT_NOPORT))
        {
            boolean ask = false;
            for (int i = 0; i < SipdroidEngine.LINES; i++)
            {
                String j = (i != 0 ? "" + i : "");
                if (PreferenceManager.getDefaultSharedPreferences((MainActivity) getActivity())
                        .getString(Settings_Fragment.PREF_SERVER + j, Settings_Fragment.DEFAULT_SERVER)
                        .equals(Settings_Fragment.DEFAULT_SERVER)
                        && PreferenceManager.getDefaultSharedPreferences((MainActivity) getActivity())
                                .getString(Settings_Fragment.PREF_USERNAME + j, Settings_Fragment.DEFAULT_USERNAME)
                                .length() != 0
                        &&
                        PreferenceManager.getDefaultSharedPreferences((MainActivity) getActivity())
                                .getString(Settings_Fragment.PREF_PORT + j, Settings_Fragment.DEFAULT_PORT)
                                .equals(Settings_Fragment.DEFAULT_PORT))
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
                                    if (PreferenceManager
                                            .getDefaultSharedPreferences(mContext)
                                            .getString(Settings_Fragment.PREF_SERVER + j,
                                                    Settings_Fragment.DEFAULT_SERVER)
                                            .equals(Settings_Fragment.DEFAULT_SERVER)
                                            && PreferenceManager
                                                    .getDefaultSharedPreferences(mContext)
                                                    .getString(Settings_Fragment.PREF_USERNAME + j,
                                                            Settings_Fragment.DEFAULT_USERNAME)
                                                    .length() != 0
                                            &&
                                            PreferenceManager
                                                    .getDefaultSharedPreferences(mContext)
                                                    .getString(Settings_Fragment.PREF_PORT + j,
                                                            Settings_Fragment.DEFAULT_PORT)
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
                .getString(Settings_Fragment.PREF_PREF, Settings_Fragment.DEFAULT_PREF)
                .equals(Settings_Fragment.VAL_PREF_PSTN)
                &&
                !PreferenceManager.getDefaultSharedPreferences((MainActivity) getActivity()).getBoolean(
                        Settings_Fragment.PREF_NODEFAULT,
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
    /**
     * Used after CaptureActivity (called with startActivityForResult) is finished 
     * and check if a QR-Code with TEXT Content is found
     * If it is found and the Payload is not null it will set the settings
     * @param _RequestCode 
     * @param _ResultCode
     * @param _Data
     */
    @Override
    public void onActivityResult(int _RequestCode, int _ResultCode, Intent _Data)
    {
        Bundle Extras = _Data.getExtras();
        if (_RequestCode == Util.REQUEST_CODE_QRC && _ResultCode == Activity.RESULT_OK)
        {
          
            String Payload = Extras.getString("Payload");
            Log.i(LOG_TAG, "Got SIP-Data over QR-code");
            if (Payload != null)
            {                
                setSipPreferencesWithoutPw(Payload);                
            }
        }
        super.onActivityResult(_RequestCode, _ResultCode, _Data);
    }

    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Create an NDEF Message from Intent and check for a valid format
     * @param _Intent which included NDEF Message 
     * @return NdefMessage[] 
     */
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
    /**
     * Process one NdefRecord in a NDEF Messages with RTD TEXT Format
     * This methods get the Payload as String with depending code Format (UFT-8 or UFT-16) 
     * Depends on the ID of the NDEF Record will extract the data
     * If u want support more functionality add at com.sip_client Util and 
     * com.sip_server Util class (other Application) more IDs and process it in this method 
     * @param _Ndefmessages
     * @param _Index of the NdefRecord in _Ndefmessages
     */
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
            Payload = new String(NdefRecord.getPayload(), 1 + languageCodeLength,
                    NdefRecord.getPayload().length - 1 - languageCodeLength,
                    Charset.forName("UTF-8"));
        }
        else if (isUTF8 == -0x80)
        {
            // m_StatusTextView.append((_Index + 1) + "th. Record is UTF-16\n");
            Payload = new String(NdefRecord.getPayload(), 1 + languageCodeLength,
                    NdefRecord.getPayload().length - 1 - languageCodeLength,
                    Charset.forName("UTF-16"));
        }        

        if (Arrays.equals(NFC_SEND_ID, Util.ID_SEND_ALL))
        {
            setSipPreferences(Payload);
        }
        else if (Arrays.equals(NFC_SEND_ID, Util.ID_SEND_ALL_WITHOUT_PW))
        {
            setSipPreferencesWithoutPw(Payload);
            Log.i(LOG_TAG, "Process SIP-Data without PWj over NFC");
        }
        else if (Arrays.equals(NFC_SEND_ID, Util.ID_SEND_PW))
        {
            setSipPasswordPreferences(Payload);
            Log.i(LOG_TAG, "Process SIP-Data over NFC");
        }
        else
        {
            Toast.makeText(getActivity(), R.string.nfcnodidfound, Toast.LENGTH_SHORT).show();
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Set different preference settings from the Payload
     * Use this Method if NDEF Record Message has the ID of Util.ID_SEND_ALL
     * The formation of the Payload String depends on SIP_Server
     * @param _Payload specialized String from SIP_Server Application     
     */
    private void setSipPreferencesWithoutPw(String _Payload)
    {
        Editor SettingsEditor = null;
        String[] Parts = null;
        String[] Parts2 = null;
        String SipData = null;
        String StunData = null;
        String WlanData = null;        
        
        String User = null;
        String ServerDomain = null;
        String IsStunUse = null;
        String ServerPort = null;
        String StunServer = null;
        String StunPort = null;        
        String SSID = null;
        String WifiPassword = null;
        int Encryption = -1;
        String[][] HotelContacts = null;

        SettingsEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        SettingsEditor.putBoolean(Settings_Fragment.PREF_STUN, false); // Set Stun Default to false
        SettingsEditor.putString(Settings_Fragment.PREF_PROTOCOL, Settings_Fragment.DEFAULT_PROTOCOL);// Set default protocol
        
        //Process Sip-data
        Parts = _Payload.split(Util.SEPERATOR_ENDSIPDATA);
        SipData = Parts[0];
        Parts2 = SipData.split(Util.STANDARD_SEPERATOR_SIGN);  
        User = Parts2[0];
        ServerDomain = Parts2[1];
        ServerPort = Parts2[2];
        
        if(_Payload.contains(Util.SEPERATOR_ENDSTUNSERVER.replace("\\", "")))// Stun is using
        {
            Parts = Parts[1].split(Util.SEPERATOR_ENDSTUNSERVER);
            StunData = Parts[0];
            Parts2 = StunData.split(Util.STANDARD_SEPERATOR_SIGN); 
            IsStunUse = Parts2[0];
            StunServer = Parts2[1];
            StunPort = Parts2[2];
            //TO-DO: Check value of IsStunUse and change IsStunUseBoolean if necessary
            boolean IsStunUseBoolean = IsStunUse.equals("0") ? false : true;
            SettingsEditor.putBoolean(Settings_Fragment.PREF_STUN, IsStunUseBoolean);
            SettingsEditor.putString(Settings_Fragment.PREF_STUN_SERVER, StunServer);
            SettingsEditor.putString(Settings_Fragment.PREF_STUN_SERVER_PORT, StunPort);            
        }
        
        if(_Payload.contains(Util.SEPERATOR_ENDWLAN.replace("\\", ""))) // WIFI setting is using
        {
            //TO-DO: Add Multiple WIFI settings
            Parts = Parts[1].split(Util.SEPERATOR_ENDWLAN);
            WlanData = Parts[0];
            Parts2 = WlanData.split(Util.STANDARD_SEPERATOR_SIGN); 
            SSID = Parts2[0];
            Encryption = Integer.parseInt(Parts2[1]);             
        }
        
        if(_Payload.contains(Util.SEPERATOR_ENDWLANPASSWORD.replace("\\", ""))) // WIFI suse encryption with pw
        {
            Parts = Parts[1].split(Util.SEPERATOR_ENDWLANPASSWORD);
            WifiPassword = Parts[0];
        }
        if(SSID != null) //Add WLAN
        {
            Wifi Wifi = new Wifi(getActivity());
            //TO-DO: Ask User really delete Network (maybe other network with same SSID already exists)
            Wifi.removeWifiNetwork(Wifi.seachrWifiNetwork(SSID));        
            Wifi.addWiFiNetwork( SSID , WifiPassword , Util.WiFiEncryption.values()[Encryption] );
        }
        if(Parts.length > 1) // Hotel Numbers is using
        {            
            Parts = Parts[1].split(Util.STANDARD_SEPERATOR_SIGN);
            HotelContacts = new String[Parts.length / 2][2];
            int ContactIndex = 0;
            for (int Index = 0; Index < Parts.length ; Index +=2)
            {
                HotelContacts[ContactIndex][0] = Parts[Index];
                HotelContacts[ContactIndex][1] = Parts[Index +1];
                ContactIndex++;
            }
            ((MainActivity) getActivity()).createNewContactlist(HotelContacts);
        }
        
        SettingsEditor.putString(Settings_Fragment.PREF_USERNAME, User);
        SettingsEditor.putString(Settings_Fragment.PREF_SERVER, ServerDomain);
        SettingsEditor.putString(Settings_Fragment.PREF_PORT, ServerPort);
        SettingsEditor.commit();
        // Restart the Engine
        Receiver.engine(getActivity()).halt();
        Receiver.engine(getActivity()).StartEngine();
        Log.i(LOG_TAG, "Process SIP-Data");
        Toast.makeText(getActivity(), getString(R.string.setsipaccount) + User, Toast.LENGTH_SHORT).show();
    }

    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Set the preference settings from the Payload for Password and save the valid Time
     * Use this Method if NDEF Record Message has the ID of Util.ID_SEND_PW
     * The formation of the Payload String depends on SIP_Server
     * @param _Payload specialized String from SIP_Server Application     
     */
    private void setSipPasswordPreferences(String Payload)
    {
        Editor SettingsEditor = null;
        String[] Parts = null;
        String Password = null;
        String ValidTime = null;

        Parts = Payload.split(Util.STANDARD_SEPERATOR_SIGN);
        Password = Parts[0];
        ValidTime = Parts[1];

        StorageFile.saveValidTime(ValidTime);
        
        SettingsEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        SettingsEditor.putString(Settings_Fragment.PREF_PASSWORD, Password);
        SettingsEditor.commit();
        // Restart the Engine
        Receiver.engine(getActivity()).halt();
        Receiver.engine(getActivity()).StartEngine();
        Toast.makeText(getActivity(), R.string.setsippassword, Toast.LENGTH_SHORT).show();
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Set different preference settings from the Payload
     * Use this Method if NDEF Record Message has the ID of Util.ID_SEND_ALL
     * The formation of the Payload String depends on SIP_Server
     * @param _Payload specialized String from SIP_Server Application     
     */
    private void setSipPreferences(String _Payload)
    {
        Editor SettingsEditor = null;
        String[] Parts = null;
        String[] Parts2 = null;
        String SipData = null;
        String StunData = null;
        String WlanData = null;        
        
        String User = null;
        String ServerDomain = null;
        String ServerPort = null;
        String Password = null;
        String ValidTime = null;
        String IsStunUse = null;
        String StunServer = null;
        String StunPort = null;        
        String SSID = null;
        String WifiPassword = null;
        int Encryption = -1;
        String[][] HotelContacts = null;

        SettingsEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        SettingsEditor.putBoolean(Settings_Fragment.PREF_STUN, false); // Set Stun Default to false
        SettingsEditor.putString(Settings_Fragment.PREF_PROTOCOL, Settings_Fragment.DEFAULT_PROTOCOL);// Set default protocol
        
        //Process Sip-data
        Parts = _Payload.split(Util.SEPERATOR_ENDSIPDATA);
        SipData = Parts[0];
        Parts2 = SipData.split(Util.STANDARD_SEPERATOR_SIGN);  
        User = Parts2[0];
        ServerDomain = Parts2[1];
        ServerPort = Parts2[2];
        Password = Parts2[3];
        ValidTime = Parts2[4];
        StorageFile.saveValidTime(ValidTime);     
        
        if(_Payload.contains(Util.SEPERATOR_ENDSTUNSERVER.replace("\\", "")))// Stun is using
        {
            Parts = Parts[1].split(Util.SEPERATOR_ENDSTUNSERVER);
            StunData = Parts[0];
            Parts2 = StunData.split(Util.STANDARD_SEPERATOR_SIGN); 
            IsStunUse = Parts2[0];
            StunServer = Parts2[1];
            StunPort = Parts2[2];            
            //TO-DO: Check value of IsStunUse and change IsStunUseBoolean if necessary
            boolean IsStunUseBoolean = IsStunUse.equals("0") ? false : true;
            SettingsEditor.putBoolean(Settings_Fragment.PREF_STUN, IsStunUseBoolean);
            SettingsEditor.putString(Settings_Fragment.PREF_STUN_SERVER, StunServer);
            SettingsEditor.putString(Settings_Fragment.PREF_STUN_SERVER_PORT, StunPort);            
        }
        
        if(_Payload.contains(Util.SEPERATOR_ENDWLAN.replace("\\", ""))) // WIFI setting is using
        {
            //TO-DO: Add Multiple WIFI settings
            Parts = Parts[1].split(Util.SEPERATOR_ENDWLAN);
            WlanData = Parts[0];
            Parts2 = WlanData.split(Util.STANDARD_SEPERATOR_SIGN); 
            SSID = Parts2[0];
            Encryption = Integer.parseInt(Parts2[1]);             
        }
        
        if(_Payload.contains(Util.SEPERATOR_ENDWLANPASSWORD.replace("\\", ""))) // WIFI suse encryption with pw
        {
            Parts = Parts[1].split(Util.SEPERATOR_ENDWLANPASSWORD);
            WifiPassword = Parts[0];
        }
        if(SSID != null) //Add WLAN
        {
            Wifi Wifi = new Wifi(getActivity());
            //TO-DO: Ask User really delete Network (maybe other network with same SSID already exists)
            Wifi.removeWifiNetwork(Wifi.seachrWifiNetwork(SSID));        
            Wifi.addWiFiNetwork( SSID , WifiPassword , Util.WiFiEncryption.values()[Encryption] );
        }
        if(Parts.length > 1) // Hotel Numbers is using
        {            
            Parts = Parts[1].split(Util.STANDARD_SEPERATOR_SIGN);
            HotelContacts = new String[Parts.length / 2][2];
            int ContactIndex = 0;
            for (int Index = 0; Index < Parts.length ; Index +=2)
            {
                HotelContacts[ContactIndex][0] = Parts[Index];
                HotelContacts[ContactIndex][1] = Parts[Index +1];
                ContactIndex++;
            }
            ((MainActivity) getActivity()).createNewContactlist(HotelContacts);
        }
        
        SettingsEditor.putString(Settings_Fragment.PREF_USERNAME, User);
        SettingsEditor.putString(Settings_Fragment.PREF_SERVER, ServerDomain);
        SettingsEditor.putString(Settings_Fragment.PREF_PORT, ServerPort);        
        SettingsEditor.putString(Settings_Fragment.PREF_PASSWORD, Password);       
        SettingsEditor.commit();
        // Restart the Engine
        Receiver.engine(getActivity()).halt();
        Receiver.engine(getActivity()).StartEngine();
        Log.i(LOG_TAG, "Process SIP-Data");
        Toast.makeText(getActivity(), getString(R.string.setsipaccount) + User, Toast.LENGTH_SHORT).show();
    }

    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Process one NdefRecord in a NDEF Messages with RTD URI Format
     * This method is not use for the Application because of using AAR
     * Depends on other operating system for the SIP_Server Application 
     * could this method be useful
     * @param _Ndefmessages
     */
    public void processNdef_RTD_URI(NdefMessage _Ndefmessages)
    {
//        String Payload = null;
//        if (m_StatusTextView != null)
//        {
//            m_StatusTextView.append("RTF_URI_Message \n");
//            for (int j = 0; j < _Ndefmessages.getRecords().length; j++)
//            {
//                NdefRecord Record = _Ndefmessages.getRecords()[j];
//                m_StatusTextView.append((j + 1) + "th. Record Tnf: " + Record.getTnf() + "\n");
//                m_StatusTextView.append((j + 1) + "th. Record type: " +
//                        new String(Record.getType()) + "\n");
//                m_StatusTextView.append((j + 1) + "th. Record id: " +
//                        new String(Record.getId()) + "\n");
//                Payload = new String(Record.getPayload(), 1,
//                        Record.getPayload().length - 1, Charset.forName("UTF-8"));
//                m_StatusTextView.append((j + 1) + "th. Record payload: " + Payload + "\n");
//                byte payloadHeader = Record.getPayload()[0];
//                m_StatusTextView.append((j + 1) + "th. Record payload header: " +
//                        payloadHeader + "\n");
//            }
//        }
    }
    
    

    // //////////////////////////////////////////////////////////////////////////////
    // Sipdorid Code
    // //////////////////////////////////////////////////////////////////////////////

    // //////////////////////////////////////////////////////////////////////////////
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
        CallsAdapter adapter = new CallsAdapter((MainActivity) getActivity(), cursor);
        m_Sip_uri_box.setAdapter(adapter);
    }

    // //////////////////////////////////////////////////////////////////////////////
    public static class CallsAdapter extends CursorAdapter implements Filterable
    {
        public CallsAdapter(Context _Context, Cursor _Cursor)
        {
            super(_Context, _Cursor);
            m_Content = _Context.getContentResolver();
        }
        
        // //////////////////////////////////////////////////////////////////////////////
        public View newView(Context _Context, Cursor _Cursor, ViewGroup _Parent)
        {
            final LayoutInflater inflater = LayoutInflater.from(_Context);
            final TextView View = (TextView) inflater.inflate(
                    android.R.layout.simple_dropdown_item_1line, _Parent, false);
            String phoneNumber = _Cursor.getString(1);
            View.setText(phoneNumber);
            return View;
        }
        
        // //////////////////////////////////////////////////////////////////////////////
        @Override
        public void bindView(View View, Context _Context, Cursor _Cursor)
        {
            String phoneNumber = _Cursor.getString(1);
            ((TextView) View).setText(phoneNumber);
        }

        // //////////////////////////////////////////////////////////////////////////////
        @Override
        public String convertToString(Cursor _Cursor)
        {
            String phoneNumber = _Cursor.getString(1);
            if (phoneNumber.contains(" <"))
                phoneNumber = phoneNumber.substring(0, phoneNumber.indexOf(" <"));
            return phoneNumber;
        }

        // //////////////////////////////////////////////////////////////////////////////
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
}
