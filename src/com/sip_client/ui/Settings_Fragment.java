/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * 
 * This file include source code of Settings_Fragment.java (http://www.sipdroid.org)
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
 * Author: Robert Thieme
 * Edit Change Many Setting, Use PreferenceFragment,  Update Summaries
 */
package com.sip_client.ui;

import org.sipdroid.codecs.Codecs;
import org.sipdroid.media.RtpStreamReceiver;
import org.sipdroid.sipua.base.SipdroidEngine;
import org.sipdroid.sipua.ui.Checkin;
import org.sipdroid.sipua.ui.InstantAutoCompleteTextView;
import org.sipdroid.sipua.ui.Receiver;
import org.zoolu.sip.provider.SipStack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.widget.EditText;

import com.example.sip_client.R;
import com.sip_client.MainActivity;
/**
 * PreferenceFragment class uses some code of Sipdroid Settings_Fragment.java
 * Saves the Settings for the Client like SIP profile data
 * Use for the the layout /res/xml/preferences.xml
 * @author Robert Thieme
 * @author Copyright (C) 2009 The Sipdroid Open Source Project 
 */
public class Settings_Fragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, OnClickListener
{  
    // All possible values of the PREF_PREF preference (see bellow)
    public static final String       VAL_PREF_PSTN            = "PSTN";
    public static final String       VAL_PREF_SIP             = "SIP";
    public static final String       VAL_PREF_SIPONLY         = "SIPONLY";
    public static final String       VAL_PREF_ASK             = "ASK";

    /*
     * ****************************************
     * **** HOW TO USE SHARED PREFERENCES *****
     * ****************************************
     * 
     * If you need to check the existence of the preference key
     *   in this class:     contains(PREF_USERNAME)
     *   in other classes:  PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).contains(Settings_Fragment.PREF_USERNAME) 
     * If you need to check the existence of the key or check the value of the preference
     *   in this class:     getString(PREF_USERNAME, "").equals("")
     *   in other classes:  PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getString(Settings_Fragment.PREF_USERNAME, "").equals("")
     * If you need to get the value of the preference
     *   in this class:     getString(PREF_USERNAME, DEFAULT_USERNAME)
     *   in other classes:  PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getString(Settings_Fragment.PREF_USERNAME, Settings_Fragment.DEFAULT_USERNAME)
     */

    // Name of the keys in the Preferences XML file
    public static final String       PREF_USERNAME            = "username";
    public static final String       PREF_PASSWORD            = "password";
    public static final String       PREF_SERVER              = "server";
    public static final String       PREF_DOMAIN              = "domain";
    public static final String       PREF_FROMUSER            = "fromuser";
    public static final String       PREF_PORT                = "port";
    public static final String       PREF_PROTOCOL            = "protocol";
    public static final String       PREF_WLAN                = "wlan";
    public static final String       PREF_3G                  = "3g";
    public static final String       PREF_EDGE                = "edge";
    public static final String       PREF_VPN                 = "vpn";
    public static final String       PREF_PREF                = "pref";
    public static final String       PREF_AUTO_ON             = "auto_on";
    public static final String       PREF_AUTO_ONDEMAND       = "auto_on_demand";
    public static final String       PREF_AUTO_HEADSET        = "auto_headset";
    public static final String       PREF_MWI_ENABLED         = "MWI_enabled";
    public static final String       PREF_REGISTRATION        = "registration";
    public static final String       PREF_NOTIFY              = "notify";
    public static final String       PREF_NODATA              = "nodata";
    public static final String       PREF_SIPRINGTONE         = "sipringtone";
    public static final String       PREF_SEARCH              = "search";
    public static final String       PREF_EXCLUDEPAT          = "excludepat";
    public static final String       PREF_EARGAIN             = "eargain";
    public static final String       PREF_MICGAIN             = "micgain";
    public static final String       PREF_HEARGAIN            = "heargain";
    public static final String       PREF_HMICGAIN            = "hmicgain";
    public static final String       PREF_OWNWIFI             = "ownwifi";
    public static final String       PREF_STUN                = "stun";
    public static final String       PREF_STUN_SERVER         = "stun_server";
    public static final String       PREF_STUN_SERVER_PORT    = "stun_server_port";

    // MMTel configurations (added by mandrajg)
    public static final String       PREF_MMTEL               = "mmtel";
    public static final String       PREF_MMTEL_QVALUE        = "mmtel_qvalue";

    // Call recording preferences.
    public static final String       PREF_CALLRECORD          = "callrecord";

    public static final String       PREF_PAR                 = "par";
    public static final String       PREF_IMPROVE             = "improve";
    public static final String       PREF_POSURL              = "posurl";
    public static final String       PREF_POS                 = "pos";
    public static final String       PREF_CALLBACK            = "callback";
    public static final String       PREF_CALLTHRU            = "callthru";
    public static final String       PREF_CALLTHRU2           = "callthru2";
    public static final String       PREF_CODECS              = "codecs_new";
    public static final String       PREF_DNS                 = "dns";
    public static final String       PREF_VQUALITY            = "vquality";
    public static final String       PREF_MESSAGE             = "vmessage";
    public static final String       PREF_BLUETOOTH           = "bluetooth";
    public static final String       PREF_KEEPON              = "keepon";
    public static final String       PREF_SELECTWIFI          = "selectwifi";
    public static final String       PREF_ACCOUNT             = "account";

    // Default values of the preferences
    public static final String       DEFAULT_USERNAME         = "";
    public static final String       DEFAULT_PASSWORD         = "";
    public static final String       DEFAULT_SERVER           = "pbxes.org";
    public static final String       DEFAULT_DOMAIN           = "";
    public static final String       DEFAULT_FROMUSER         = "";
    public static final String       DEFAULT_PORT             = "" + SipStack.default_port;
    public static final String       DEFAULT_PROTOCOL         = "tcp";
    public static final boolean      DEFAULT_WLAN             = true;
    public static final boolean      DEFAULT_3G               = false;
    public static final boolean      DEFAULT_EDGE             = false;
    public static final boolean      DEFAULT_VPN              = false;
    public static final String       DEFAULT_PREF             = VAL_PREF_PSTN;
    public static final boolean      DEFAULT_AUTO_ON          = false;
    public static final boolean      DEFAULT_AUTO_ONDEMAND    = false;
    public static final boolean      DEFAULT_AUTO_HEADSET     = false;
    public static final boolean      DEFAULT_MWI_ENABLED      = true;
    public static final boolean      DEFAULT_REGISTRATION     = true;
    public static final boolean      DEFAULT_NOTIFY           = false;
    public static final boolean      DEFAULT_NODATA           = false;
    public static final String       DEFAULT_SIPRINGTONE      = "";
    public static final String       DEFAULT_SEARCH           = "";
    public static final String       DEFAULT_EXCLUDEPAT       = "";
    public static final float        DEFAULT_EARGAIN          = (float) 0.25;
    public static final float        DEFAULT_MICGAIN          = (float) 0.25;
    public static final float        DEFAULT_HEARGAIN         = (float) 0.25;
    public static final float        DEFAULT_HMICGAIN         = (float) 1.0;
    public static final boolean      DEFAULT_OWNWIFI          = false;
    public static final boolean      DEFAULT_STUN             = false;
    public static final String       DEFAULT_STUN_SERVER      = "stun.ekiga.net";
    public static final String       DEFAULT_STUN_SERVER_PORT = "3478";

    // MMTel configuration (added by mandrajg)
    public static final boolean      DEFAULT_MMTEL            = false;
    public static final String       DEFAULT_MMTEL_QVALUE     = "1.00";

    // Call recording preferences.
    public static final boolean      DEFAULT_CALLRECORD       = false;

    public static final boolean      DEFAULT_PAR              = false;
    public static final boolean      DEFAULT_IMPROVE          = false;
    public static final String       DEFAULT_POSURL           = "";
    public static final boolean      DEFAULT_POS              = false;
    public static final boolean      DEFAULT_CALLBACK         = false;
    public static final boolean      DEFAULT_CALLTHRU         = false;
    public static final String       DEFAULT_CALLTHRU2        = "";
    public static final String       DEFAULT_CODECS           = null;
    public static final String       DEFAULT_DNS              = "";
    public static final String       DEFAULT_VQUALITY         = "low";
    public static final boolean      DEFAULT_MESSAGE          = false;
    public static final boolean      DEFAULT_BLUETOOTH        = false;
    public static final boolean      DEFAULT_KEEPON           = false;
    public static final boolean      DEFAULT_SELECTWIFI       = false;
    public static final int          DEFAULT_ACCOUNT          = 0;

    // An other preference keys (not in the Preferences XML file)
    public static final String       PREF_OLDVALID            = "oldvalid";
    public static final String       PREF_SETMODE             = "setmode";
    public static final String       PREF_OLDVIBRATE          = "oldvibrate";
    public static final String       PREF_OLDVIBRATE2         = "oldvibrate2";
    public static final String       PREF_OLDPOLICY           = "oldpolicy";
    public static final String       PREF_OLDRING             = "oldring";
    public static final String       PREF_AUTO_DEMAND         = "auto_demand";
    public static final String       PREF_WIFI_DISABLED       = "wifi_disabled";
    public static final String       PREF_ON_VPN              = "on_vpn";
    public static final String       PREF_NODEFAULT           = "nodefault";
    public static final String       PREF_NOPORT              = "noport";
    public static final String       PREF_ON                  = "on";
    public static final String       PREF_PREFIX              = "prefix";
    public static final String       PREF_COMPRESSION         = "compression";
    // public static final String PREF_RINGMODEx = "ringmodeX";
    // public static final String PREF_VOLUMEx = "volumeX";

    // Default values of the other preferences
    public static final boolean      DEFAULT_OLDVALID         = false;
    public static final boolean      DEFAULT_SETMODE          = false;
    public static final int          DEFAULT_OLDVIBRATE       = 0;
    public static final int          DEFAULT_OLDVIBRATE2      = 0;
    public static final int          DEFAULT_OLDPOLICY        = 0;
    public static final int          DEFAULT_OLDRING          = 0;
    public static final boolean      DEFAULT_AUTO_DEMAND      = false;
    public static final boolean      DEFAULT_WIFI_DISABLED    = false;
    public static final boolean      DEFAULT_ON_VPN           = false;
    public static final boolean      DEFAULT_NODEFAULT        = false;
    public static final boolean      DEFAULT_NOPORT           = false;
    public static final boolean      DEFAULT_ON               = false;
    public static final String       DEFAULT_PREFIX           = "";
    public static final String       DEFAULT_COMPRESSION      = null;

    // public static final String DEFAULT_RINGTONEx = "";
    // public static final String DEFAULT_VOLUMEx = "";

    // //////////////////////////////////////////////////////////////////////////////
    public static float getEarGain()
    {
        try
        {
            return Float.valueOf(PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getString(
                    Receiver.headset > 0 ? PREF_HEARGAIN : PREF_EARGAIN, "" + DEFAULT_EARGAIN));
        }
        catch (NumberFormatException i)
        {
            return DEFAULT_EARGAIN;
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    public static float getMicGain()
    {
        if (Receiver.headset > 0 || Receiver.bluetooth > 0)
        {
            try
            {
                return Float.valueOf(PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getString(
                        PREF_HMICGAIN, "" + DEFAULT_HMICGAIN));
            }
            catch (NumberFormatException i)
            {
                return DEFAULT_HMICGAIN;
            }
        }

        try
        {
            return Float.valueOf(PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getString(
                    PREF_MICGAIN, "" + DEFAULT_MICGAIN));
        }
        catch (NumberFormatException i)
        {
            return DEFAULT_MICGAIN;
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle _SavedInstanceState)
    {
        super.onCreate(_SavedInstanceState);

        if (Receiver.mContext == null)
            Receiver.mContext = getActivity();
        addPreferencesFromResource(R.xml.preferences);
        setDefaultValues();
        initSummary(getPreferenceScreen());
    }

    // //////////////////////////////////////////////////////////////////////////////
    private void reload()
    {
        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.preferences);
    }

    // //////////////////////////////////////////////////////////////////////////////
    private void setDefaultValues()
    {
        for (int i = 0; i < SipdroidEngine.LINES; i++)
        {
            String j = (i != 0 ? "" + i : "");
            if (PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getString(PREF_SERVER + j, "").equals(""))
            {
                CheckBoxPreference cb = (CheckBoxPreference) getPreferenceScreen().findPreference(PREF_WLAN + j);
                cb.setChecked(true);
                Editor edit = PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).edit();

                edit.putString(PREF_PORT + j, "5061");
                edit.putString(PREF_SERVER + j, DEFAULT_SERVER);
                edit.putString(PREF_PREF + j, DEFAULT_PREF);
                edit.putString(PREF_PROTOCOL + j, DEFAULT_PROTOCOL);
                edit.commit();
                Receiver.engine(getActivity()).updateDNS();
                reload();
            }
        }
        if (PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getString(PREF_STUN_SERVER, "").equals(""))
        {
            Editor edit = PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).edit();

            edit.putString(PREF_STUN_SERVER, DEFAULT_STUN_SERVER);
            edit.putString(PREF_STUN_SERVER_PORT, DEFAULT_STUN_SERVER_PORT);
            edit.commit();
            reload();
        }

        if (!PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).contains(PREF_MWI_ENABLED))
        {
            CheckBoxPreference cb = (CheckBoxPreference) getPreferenceScreen().findPreference(PREF_MWI_ENABLED);
            cb.setChecked(true);
        }
        if (!PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).contains(PREF_REGISTRATION))
        {
            CheckBoxPreference cb = (CheckBoxPreference) getPreferenceScreen().findPreference(PREF_REGISTRATION);
            cb.setChecked(true);
        }
        if (MainActivity.market)
        {
            CheckBoxPreference cb = (CheckBoxPreference) getPreferenceScreen().findPreference(PREF_3G);
            cb.setChecked(false);
            CheckBoxPreference cb2 = (CheckBoxPreference) getPreferenceScreen().findPreference(PREF_EDGE);
            cb2.setChecked(false);
            getPreferenceScreen().findPreference(PREF_3G).setEnabled(false);
            getPreferenceScreen().findPreference(PREF_EDGE).setEnabled(false);
        }
        Codecs.check();
    }    

    // //////////////////////////////////////////////////////////////////////////////
    public static String getProfileNameString(SharedPreferences s)
    {
        String provider = s.getString(PREF_SERVER, DEFAULT_SERVER);

        if (!s.getString(PREF_DOMAIN, "").equals(""))
        {
            provider = s.getString(PREF_DOMAIN, DEFAULT_DOMAIN);
        }

        return s.getString(PREF_USERNAME, DEFAULT_USERNAME) + "@" + provider;
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Call a DatePicker_DialogFragment and set the Date before Password will be set
     */
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen _PreferenceScreen, Preference _Preference)
    {
        if (_PreferenceScreen.getKey() != null && _Preference != null)
        {
            //Check is Password set if yes then set Valid Time
            if (_PreferenceScreen.getKey().equals(PREF_ACCOUNT) && _Preference.getKey().equals(PREF_PASSWORD))
            {
                DatePicker_DialogFragment DatePicker_DialogFragment = new DatePicker_DialogFragment();
                DatePicker_DialogFragment.show(getFragmentManager(), "DatePicker");
                return false;
            }
        }
        return super.onPreferenceTreeClick(_PreferenceScreen, _Preference);
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public void onResume()
    {
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public void onPause()
    {

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
    EditText transferText;
    String   mKey;

    // //////////////////////////////////////////////////////////////////////////////
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (!Thread.currentThread().getName().equals("main"))
            return;
        if (key.startsWith(PREF_PORT) && sharedPreferences.getString(key, DEFAULT_PORT).equals("0"))
        {
            Editor edit = sharedPreferences.edit();
            edit.putString(key, DEFAULT_PORT);
            edit.commit();

            transferText = new InstantAutoCompleteTextView(getActivity(), null);
            transferText.setInputType(InputType.TYPE_CLASS_NUMBER);
            mKey = key;

            new AlertDialog.Builder(getActivity())
                    .setTitle(Receiver.mContext.getString(R.string.settings_port))
                    .setView(transferText)
                    .setPositiveButton(android.R.string.ok, this)
                    .show();
            return;
        }
        else if (key.startsWith(PREF_SERVER) || key.startsWith(PREF_PROTOCOL))
        {
            Editor edit = sharedPreferences.edit();
            for (int i = 0; i < SipdroidEngine.LINES; i++)
            {
                edit.putString(PREF_DNS + i, DEFAULT_DNS);
                String j = (i != 0 ? "" + i : "");
                if (key.equals(PREF_SERVER + j))
                {
                    ListPreference lp = (ListPreference) getPreferenceScreen().findPreference(PREF_PROTOCOL + j);
                    lp.setValue(sharedPreferences.getString(PREF_SERVER + j, DEFAULT_SERVER).equals(DEFAULT_SERVER) ? "tcp"
                            : "udp");
                    lp = (ListPreference) getPreferenceScreen().findPreference(PREF_PORT + j);
                    lp.setValue(sharedPreferences.getString(PREF_SERVER + j, DEFAULT_SERVER).equals(DEFAULT_SERVER) ? "5061"
                            : DEFAULT_PORT);
                }
                if (key.equals(PREF_PROTOCOL + j))
                {
                    if (sharedPreferences.getString(PREF_SERVER + j, DEFAULT_SERVER).equals(DEFAULT_SERVER))
                    {
                        ListPreference lp = (ListPreference) getPreferenceScreen().findPreference(PREF_PORT + j);
                        lp.setValue(sharedPreferences.getString(PREF_PROTOCOL + j, DEFAULT_PROTOCOL).equals("tls") ? "5070"
                                : "5061");
                    }
                    else
                    {
                        Receiver.engine(getActivity()).halt();
                        Receiver.engine(getActivity()).StartEngine();
                    }
                }
            }
            edit.commit();
            Receiver.engine(getActivity()).updateDNS();
            Checkin.checkin(false);
        }
        else if (sharedPreferences.getBoolean(PREF_CALLBACK, DEFAULT_CALLBACK)
                && sharedPreferences.getBoolean(PREF_CALLTHRU, DEFAULT_CALLTHRU))
        {
            CheckBoxPreference cb = (CheckBoxPreference) getPreferenceScreen().findPreference(
                    key.equals(PREF_CALLBACK) ? PREF_CALLTHRU : PREF_CALLBACK);
            cb.setChecked(false);
        }
        else if (key.startsWith(PREF_WLAN) ||
                key.startsWith(PREF_3G) ||
                key.startsWith(PREF_EDGE) ||
                key.startsWith(PREF_USERNAME) ||
                key.startsWith(PREF_PASSWORD) ||
                key.startsWith(PREF_DOMAIN) ||
                key.startsWith(PREF_SERVER) ||
                key.startsWith(PREF_PORT) ||
                key.equals(PREF_STUN) ||
                key.equals(PREF_STUN_SERVER) ||
                key.equals(PREF_STUN_SERVER_PORT) ||
                key.equals(PREF_MMTEL) || // (added by mandrajg)
                key.equals(PREF_MMTEL_QVALUE) || // (added by mandrajg)
                key.startsWith(PREF_PROTOCOL) ||
                key.startsWith(PREF_VPN) ||
                key.equals(PREF_POS) ||
                key.equals(PREF_POSURL) ||
                key.startsWith(PREF_FROMUSER) ||
                key.equals(PREF_AUTO_ONDEMAND) ||
                key.equals(PREF_MWI_ENABLED) ||
                key.equals(PREF_REGISTRATION) ||
                key.equals(PREF_KEEPON))
        {
            Receiver.engine(getActivity()).halt();
            Receiver.engine(getActivity()).StartEngine();
        }
        //update the Summaries
        Preference Preference = findPreference(key);
        updatePrefSummary(Preference);  
    }

    // //////////////////////////////////////////////////////////////////////////////
    void fill(String pref, String def, int val, int disp)
    {
        for (int i = 0; i < getResources().getStringArray(val).length; i++)
        {
            if (PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getString(pref, def).equals(getResources().getStringArray(val)[i]))
            {
                getPreferenceScreen().findPreference(pref).setSummary(getResources().getStringArray(disp)[i]);
            }
        }
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Init the summary text
     * Example: with the current settings -> getPreferenceScreen()
     * @param _Preference
     */
    private void initSummary(Preference _Preference)
    {
        if (_Preference instanceof PreferenceGroup)
        {
            PreferenceGroup PreferenceGroup = (PreferenceGroup) _Preference;
            for (int Index = 0; Index < PreferenceGroup.getPreferenceCount(); Index++)
            {
                initSummary(PreferenceGroup.getPreference(Index));
            }
        }
        else
        {
            updatePrefSummary(_Preference);
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Update summary depends on preference class
     * Set Password Display to "*****"
     * @param _Preference
     */
    private void updatePrefSummary(Preference _Preference)
    {
        if (_Preference instanceof ListPreference)
        {
            ListPreference ListPreference = (ListPreference) _Preference;
            _Preference.setSummary(ListPreference.getEntry());
        }

        if (_Preference instanceof EditTextPreference)
        {
            EditTextPreference EditTextPreference = (EditTextPreference) _Preference;
            if (_Preference.getTitle().toString().contains(getString(R.string.settings_password)))
            {
                _Preference.setSummary("******");
            } else 
            {
                _Preference.setSummary(EditTextPreference.getText());
            }
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(DialogInterface arg0, int arg1)
    {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).edit();
        edit.putString(mKey, transferText.getText().toString());
        edit.commit();
    }
}
