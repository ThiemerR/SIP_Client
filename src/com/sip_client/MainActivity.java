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

package com.sip_client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.sipdroid.sipua.base.UserAgent;
import org.sipdroid.sipua.ui.Checkin;
import org.sipdroid.sipua.ui.CreateAccount;
import org.sipdroid.sipua.ui.Receiver;
import org.sipdroid.sipua.ui.RegisterService;
import org.zoolu.tools.Random;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.sip_client.R;
import com.sip_client.contactlist.Contact;
import com.sip_client.contactlist.ContactList;
import com.sip_client.ui.ContactList_Fragment;
import com.sip_client.ui.Settings_Fragment;
import com.sip_client.ui.Start_Fragment;

public class MainActivity extends Activity implements ActionBar.TabListener, OnDismissListener
{  
    private static Context m_Context;
    
    private SectionsPagerAdapter m_SectionsPagerAdapter;
    private ViewPager            m_ViewPager;
    private ContactList          m_ContactList;

    private Start_Fragment       m_Start_Fragment;
    private ContactList_Fragment m_ContactList_Fragment;
    private Settings_Fragment    m_Settings_Fragment;
    
    //Use for Setting Valid Time
    private Calendar              m_ValidDareCalender = null; 

    
    // Sipdroid Members
    public static final boolean   release    = true;
    public static final boolean   market     = false;   
    
    private static AlertDialog      m_AlertDlg;
    
    /* Following the menu item constants which will be used for menu creation */
    public static final int FIRST_MENU_ID = Menu.FIRST;
    public static final int CONFIGURE_MENU_ITEM = FIRST_MENU_ID + 1;
    public static final int ABOUT_MENU_ITEM = FIRST_MENU_ID + 2;
    public static final int EXIT_MENU_ITEM = FIRST_MENU_ID + 3;     

    @Override
    protected void onCreate(Bundle _SavedInstanceState)
    {
        super.onCreate(_SavedInstanceState);
        setContentView(R.layout.activity_main);   
        
        m_Context = this;        
        m_Start_Fragment = new Start_Fragment();
        m_ContactList_Fragment = new ContactList_Fragment();
        m_Settings_Fragment = new Settings_Fragment();
        
        //Check if Password is valid

        // Set up the action bar.
        final ActionBar ActionBar = getActionBar();
        ActionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        m_SectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        m_ViewPager = (ViewPager) findViewById(R.id.pager);
        m_ViewPager.setAdapter(m_SectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        m_ViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageSelected(int _Position)
            {
                ActionBar.setSelectedNavigationItem(_Position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < m_SectionsPagerAdapter.getCount(); i++)
        {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            ActionBar.addTab(
                    ActionBar.newTab()
                            .setText(m_SectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }
    
    // ////////////////////////////////////////////////////////////////////////////// 
    @Override
    public void onStart()
    {
        super.onStart();
        Receiver.engine(this).registerMore();         
    }
    
    // ////////////////////////////////////////////////////////////////////////////// 
    @Override
    public void onResume()
    {
        super.onResume();
        if (Receiver.call_state != UserAgent.UA_STATE_IDLE)
        {
            Receiver.moveTop();
        }
        String Text;
        Text = Integer.parseInt(Build.VERSION.SDK) >= 5 ? CreateAccount.isPossible(this) : null;
        if (Text != null && !Text.contains("Google Voice") &&
                (Checkin.createButton == 0 || Random.nextInt(Checkin.createButton) != 0))
        {
            Text = null;        
        }
    }
    
    // ////////////////////////////////////////////////////////////////////////////// 
    @Override
    public boolean onCreateOptionsMenu(Menu _Menu)
    {
        boolean result = super.onCreateOptionsMenu(_Menu);

        MenuItem m = _Menu.add(0, ABOUT_MENU_ITEM, 0, R.string.menu_about);
        m.setIcon(android.R.drawable.ic_menu_info_details);
        m = _Menu.add(0, EXIT_MENU_ITEM, 0, R.string.menu_exit);
        m.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        m = _Menu.add(0, CONFIGURE_MENU_ITEM, 0, R.string.menu_settings);
        m.setIcon(android.R.drawable.ic_menu_preferences);
        return result;
    }

    // ////////////////////////////////////////////////////////////////////////////// 
    @Override
    public boolean onOptionsItemSelected(MenuItem _Item)
    {
        boolean result = super.onOptionsItemSelected(_Item);

        switch (_Item.getItemId())
        {
            case ABOUT_MENU_ITEM:
                if (m_AlertDlg != null)
                {
                    m_AlertDlg.cancel();
                }
                m_AlertDlg = new AlertDialog.Builder(this)
                        .setMessage(
                                getString(R.string.about).replace("\\n", "\n").replace("${VERSION}", getVersion(this)))
                        .setTitle(getString(R.string.menu_about))
                        .setIcon(R.drawable.icon22)
                        .setCancelable(true)
                        .show();
                break;

            case EXIT_MENU_ITEM:
                on(this, false);
                Receiver.pos(true);
                Receiver.engine(this).halt();
                Receiver.mSipdroidEngine = null;
                Receiver.reRegister(0);
                stopService(new Intent(this, RegisterService.class));
                finish();
                break;

            case CONFIGURE_MENU_ITEM:
            {
                m_ViewPager.setCurrentItem(2);
            }
                break;
        }

        return result;
    }
    
    
    
    // //////////////////////////////////////////////////////////////////////////////
    public void call_menu(AutoCompleteTextView _View)
    {
        if( !(PreferenceManager.getDefaultSharedPreferences(this).getString(Settings_Fragment.PREF_PASSWORD, "").equals(""))
                && checkIsPasswordValid())
        {  
        String Target = _View.getText().toString();
        if (m_AlertDlg != null) 
        {
            m_AlertDlg.cancel();
        }
        if (Target.length() == 0)
            m_AlertDlg = new AlertDialog.Builder(this)
                .setMessage(R.string.empty)
                .setTitle(R.string.app_name)
                .setIcon(R.drawable.icon22)
                .setCancelable(true)
                .show();
        else if (!Receiver.engine(this).call(Target,true))
            m_AlertDlg = new AlertDialog.Builder(this)
                .setMessage(R.string.notfast)
                .setTitle(R.string.app_name)
                .setIcon(R.drawable.icon22)
                .setCancelable(true)
                .show();
        }
        else
        {
            Toast.makeText(this, R.string.pwnotvaild, Toast.LENGTH_SHORT).show();
        }
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    public void call_menu(String _Number)
    {        
        
        if( !(PreferenceManager.getDefaultSharedPreferences(this).getString(Settings_Fragment.PREF_PASSWORD, "").equals(""))
                && checkIsPasswordValid())
        {        
            if (m_AlertDlg != null) 
            {
                m_AlertDlg.cancel();
            }
            if (_Number.length() == 0)
                m_AlertDlg = new AlertDialog.Builder(this)
                    .setMessage(R.string.empty)
                    .setTitle(R.string.app_name)
                    .setIcon(R.drawable.icon22)
                    .setCancelable(true)
                    .show();
            else if (!Receiver.engine(this).call(_Number,true))
                m_AlertDlg = new AlertDialog.Builder(this)
                    .setMessage(R.string.notfast)
                    .setTitle(R.string.app_name)
                    .setIcon(R.drawable.icon22)
                    .setCancelable(true)
                    .show();
        }
        else
        {
            Toast.makeText(this, R.string.pwnotvaild, Toast.LENGTH_SHORT).show();
        }
    }

    // ////////////////////////////////////////////////////////////////////////////// 
    public static boolean on(Context _Context)
    {
        return PreferenceManager.getDefaultSharedPreferences(_Context)
                .getBoolean(Settings_Fragment.PREF_ON, Settings_Fragment.DEFAULT_ON);
    }
    
    // ////////////////////////////////////////////////////////////////////////////// 
    public static void on(Context _Context, boolean _IsOn)
    {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(_Context).edit();
        edit.putBoolean(Settings_Fragment.PREF_ON, _IsOn);
        edit.commit();
        if (_IsOn)
            Receiver.engine(_Context).isRegistered();
    }

    // ////////////////////////////////////////////////////////////////////////////// 
    public static String getVersion()
    {
        return getVersion(Receiver.mContext);
    }
    
    // ////////////////////////////////////////////////////////////////////////////// 
    public static String getVersion(Context _Context)
    {
        final String Unknown = "Unknown";

        if (_Context == null)
        {
            return Unknown;
        }

        try
        {
            String ret = _Context.getPackageManager()
                    .getPackageInfo(_Context.getPackageName(), 0)
                    .versionName;
            if (ret.contains(" + "))
                ret = ret.substring(0, ret.indexOf(" + ")) + "b";
            return ret;
        }
        catch (NameNotFoundException ex)
        {
        }

        return Unknown;
    }   
    
    // ////////////////////////////////////////////////////////////////////////////// 
    @Override
    public void onDismiss(DialogInterface _Dialog)
    {
        onResume();
    }
    
    // //////////////////////////////////////////////////////////////////////////////    
    public static Context getContext()
    {
        return m_Context;
    }
    
    
    // ////////////////////////////////////////////////////////////////////////////// 
    /**
     * Load the m_ContactList from file and return it
     * @return m_ContactList
     */
    public List<Contact> loadHotelContactList()
    {
        m_ContactList = StorageFile.loadContacts();
        return m_ContactList.getContactList();
    }
    
    // ////////////////////////////////////////////////////////////////////////////// 
    /**
     * Overwrite the old  m_ContactList and create new ContactList and save it in file
     * @param Contacts must have format String [][2] String[Index][0] = Phonename, String[Index][1] = Phonenumber
     */
    public void createNewContactlist(String[][] Contacts)
    {
        m_ContactList = StorageFile.loadContacts();
        m_ContactList.removeAllContacts();
        for(String[] Contact : Contacts)
        {
            m_ContactList.addContacts(Contact[0], Contact[1]);
        }        
        StorageFile.saveContacts(m_ContactList);
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Check the valid time for SIP password, delete the password when the current valid time is exceeded
     * @return is SIP password valid
     */
    public boolean checkIsPasswordValid()
    {
        boolean IsValid = true;
        if (StorageFile.loadValidTime() < System.currentTimeMillis())
        {
            Editor SettingsEditor = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContext()).edit();
            SettingsEditor.putString(Settings_Fragment.PREF_PASSWORD, "");
            SettingsEditor.commit();
            IsValid = false;
            //Restart the Engine
            Receiver.engine(this).halt();
            Receiver.engine(this).StartEngine();            
        }
        return IsValid;
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Update the Date from m_ValidDareCalender which is uses for setting the valid time of the SIP Password
     * @param _Year
     * @param _Month
     * @param _Day
     */
    public void setDate(int _Year, int _Month, int _Day)
    {
        m_ValidDareCalender = Calendar.getInstance();
        m_ValidDareCalender.set(_Year, _Month, _Day);
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Set time of m_ValidDareCalender for valid time of the SIP Password
     * and save valid time in Storage File
     * @param _HourOfDay
     * @param _Minute
     */
    public void setTime(int _HourOfDay, int _Minute)
    {        
        m_ValidDareCalender.set(Calendar.HOUR_OF_DAY, _HourOfDay);
        m_ValidDareCalender.set(Calendar.MINUTE, _Minute);
        String ValidTime = String.valueOf(m_ValidDareCalender.getTimeInMillis());
        StorageFile.saveValidTime(ValidTime);        
        m_ValidDareCalender = null;        
    }
    
    // ////////////////////////////////////////////////////////////////////////////// 
    public static class CallsCursor extends CursorWrapper
    {
        List<String> List;

        // ////////////////////////////////////////////////////////////////////////////// 
        public int getCount()
        {
            return List.size();
        }

        // ////////////////////////////////////////////////////////////////////////////// 
        public String getString(int i)
        {
            return List.get(getPosition());
        }

        // ////////////////////////////////////////////////////////////////////////////// 
        public CallsCursor(Cursor _Cursor)
        {
            super(_Cursor);
            List = new ArrayList<String>();
            for (int i = 0; i < _Cursor.getCount(); i++)
            {
                moveToPosition(i);
                String PhoneNumber = super.getString(1);
                String CachedName = super.getString(2);
                if (CachedName != null && CachedName.trim().length() > 0)
                    PhoneNumber += " <" + CachedName + ">";
                if (List.contains(PhoneNumber))
                    continue;
                List.add(PhoneNumber);
            }
            moveToFirst();
        }
    }    
    
    // //////////////////////////////////////////////////////////////////////////////
    // FragmentPagerAdapter/Tabs Methods Class
    // ////////////////////////////////////////////////////////////////////////////// 
    
    // ////////////////////////////////////////////////////////////////////////////// 
    @Override
    public void onTabSelected(ActionBar.Tab _Tab, FragmentTransaction _FragmentTransaction)
    {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        m_ViewPager.setCurrentItem(_Tab.getPosition());
    }
    
    // ////////////////////////////////////////////////////////////////////////////// 
    @Override
    public void onTabUnselected(ActionBar.Tab _Tab, FragmentTransaction _FragmentTransaction)
    {
    }

    // ////////////////////////////////////////////////////////////////////////////// 
    @Override
    public void onTabReselected(ActionBar.Tab _Tab, FragmentTransaction _FragmentTransaction)
    {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        // ////////////////////////////////////////////////////////////////////////////// 
        public SectionsPagerAdapter(FragmentManager _FragmentManager)
        {
            super(_FragmentManager);
        }

        // ////////////////////////////////////////////////////////////////////////////// 
        @Override
        public Fragment getItem(int _Index)
        {
            switch (_Index)
            {
                case 0:
                    return m_Start_Fragment;
                case 1:
                    return m_ContactList_Fragment;
                case 2:
                    return m_Settings_Fragment;
            }
            return null;
        }

        // ////////////////////////////////////////////////////////////////////////////// 
        @Override
        public int getCount()
        {
            return 3;
        }

        // ////////////////////////////////////////////////////////////////////////////// 
        @Override
        public CharSequence getPageTitle(int _Index)
        {
            Locale l = Locale.getDefault();
            switch (_Index)
            {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }
    
}
