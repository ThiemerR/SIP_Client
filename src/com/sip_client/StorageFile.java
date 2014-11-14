package com.sip_client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sip_client.contactlist.ContactList;
import com.sip_client.ui.Settings_Fragment;

/**
 * Class for saving File at the Internal Storage
 * Uses only static methods
 * @author Robert Thieme
 */
public class StorageFile
{
    private static final String FILE_NAME = "ContactList.obj";
    private static final String FILE_NAME_VALIDTIME = "validtime_file";
    private static final String LOG_TAG   = "StorageFile";
    
    private StorageFile(){}
   
    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Load the saved Object file
     * @return the saved ContactList
     */
    public static ContactList loadContacts()
    {
        ContactList ContactList = new ContactList();        
        FileInputStream FileInputStream = null;

        try
        {
            FileInputStream = MainActivity.getContext().openFileInput(FILE_NAME);
            ObjectInputStream ObjectInputStream = new ObjectInputStream(FileInputStream);
            ContactList = (ContactList) ObjectInputStream.readObject();
        }
        catch (Exception ioe)
        {
            Log.e(LOG_TAG, "Error while reading InputStream");
        }
        finally
        {
            if (FileInputStream != null)
            {
                try
                {
                    FileInputStream.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, "Error while closing FileInputStream");
                }
            }
        }
        return ContactList;
    }

    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Save the Hotel ContactList as Object file
     * @param _ContactList the saved ContactList
     */
    public static void saveContacts(ContactList _ContactList)
    {

        FileOutputStream FileOutputStream = null;
        try
        {
            FileOutputStream = MainActivity.getContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream ObjectOutputStream = new ObjectOutputStream(FileOutputStream);

            ObjectOutputStream.writeObject(_ContactList);
            ObjectOutputStream.flush();
        }
        catch (IOException ioe)
        {
            Log.e(LOG_TAG, "Error while writing OutputStream");
        }
        finally
        {
            if (FileOutputStream != null)
            {
                try
                {
                    FileOutputStream.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, "Error while Closing FileOutputStream");
                }
            }
        }
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Save The ValidTime in File and declare until the Sip Password is valid
     * Important: Must called before setting password because this method delete the SIP Password
     * @param _ValidTime String as long time ticks 
     */
    public static void saveValidTime(String _ValidTime)
    {  
        FileOutputStream FileOutputStream = null;
        try
        {
            FileOutputStream = MainActivity.getContext().openFileOutput(FILE_NAME_VALIDTIME, Context.MODE_PRIVATE);            
            File CheckFile = new File(MainActivity.getContext().getFilesDir(),FILE_NAME_VALIDTIME);
            if(CheckFile.exists())
            {
                Editor SettingsEditor = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContext()).edit();
                SettingsEditor.putString(Settings_Fragment.PREF_PASSWORD, "");
                SettingsEditor.commit(); 
            }
            FileOutputStream.write(_ValidTime.getBytes());
        }
        catch (IOException ioe)
        {
            Log.e(LOG_TAG, "Error while writing OutputStream");
        }        
        finally
        {
            if (FileOutputStream != null)
            {
                try
                {
                    FileOutputStream.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, "Error while Closing FileOutputStream");
                }
            }
        }
    }    
    
    // //////////////////////////////////////////////////////////////////////////////    
    /**
     * Load the valid time from until the Sip is valid
     * @return Valid time as long time ticks or null
     */
    public static long loadValidTime()
    {        
        String ValidTimeString = null;
        long ValidTime = 0;
        FileInputStream FileInputStream = null;
        StringBuffer FileContent = new StringBuffer("");
        int ByteCount = 0;
        byte[] Buffer = new byte[1024];
        try
        {
            FileInputStream = MainActivity.getContext().openFileInput(FILE_NAME_VALIDTIME);
            while ((ByteCount = FileInputStream.read(Buffer)) != -1) 
            { 
              FileContent.append(new String(Buffer, 0, ByteCount)); 
            }
            FileInputStream.read();
            
            ValidTimeString = FileContent.toString();  
            if(ValidTimeString != null)
            {
                ValidTime = Long.parseLong(ValidTimeString);
            }
        }
        catch (IOException ioe)
        {
            Log.e(LOG_TAG, "Error while reading InputStream");
        }        
        finally
        {
            if (FileInputStream != null)
            {
                try
                {
                    FileInputStream.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, "Error while Closing FileInputStream");
                }
            }
        }
        return ValidTime;
    }
}
