package com.sip_client.ui;

import java.io.File;
import java.io.FileOutputStream;

import com.example.sip_client.R;
import com.sip_client.MainActivity;
import com.sip_client.contactlist.Contact;
import com.sip_client.contactlist.ContactListExpandableListAdapter;

import android.R.string;
import android.app.Fragment;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Fragment Class for showing contacts
 * This Fragments implements two different contact views
 * The first elements implements expandable ListView with own contacts (HotelExpandableListView)
 * The second elements implements a ListView of the users phone contacts from content provider (ListViewContacts)
 * Both ListViews start a call_menu from MainActivity if they are clicked
 * ListViewContacts will loaded with an AsyncTask
 * @author Robert Thieme
 */
public class ContactList_Fragment extends Fragment
{
    private SimpleCursorAdapter             m_Adapter;
    private ContactListExpandableListAdapter m_HotelContactsAdapter;
    private MatrixCursor                    m_MatrixCursor;   
    private Cursor                          m_ContactsCursor;

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater _Inflater, ViewGroup _Container, Bundle _SavedInstanceState)
    {
        return _Inflater.inflate(R.layout.fragment_contactlist, _Container, false);
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle _SavedInstanceState)
    {
        super.onCreate(_SavedInstanceState);
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityCreated(Bundle _SavedInstanceState)
    {
        super.onActivityCreated(_SavedInstanceState);   
        
        //Get the Contact Cursor from Activity to avoid Exception in AsycnTask
        Uri ContactsUri = ContactsContract.Contacts.CONTENT_URI;
        // Querying the table ContactsContract.Contacts to retrieve all the contacts
        m_ContactsCursor = getActivity().getContentResolver().query(ContactsUri, null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC ");
        
        ExpandableListView HotelExpandableListView = (ExpandableListView) getView() .findViewById(R.id.ExpandableListView_HotelContacts);  
        m_HotelContactsAdapter = new ContactListExpandableListAdapter(((MainActivity) getActivity()).loadHotelContactList()
                , getActivity().getBaseContext());
        HotelExpandableListView.setAdapter(m_HotelContactsAdapter);
        HotelExpandableListView.setOnChildClickListener(new OnChildClickListener()
        {            
            @Override
            public boolean onChildClick(ExpandableListView _Parent, View _View, int _GroupPosition, int _ChildPosition, long _Id)
            {
                Contact ClickedContactObject = (Contact) m_HotelContactsAdapter.getChild(_GroupPosition, _ChildPosition); 
                ((MainActivity) getActivity()).call_menu(ClickedContactObject.getPhoneNumber()); 
                return true;
            }
        });
        
        // The contacts from the contacts content provider is stored in this cursor
        m_MatrixCursor = new MatrixCursor(new String[]
        {
                "_id", "name", "photoPath", "defaultNumber", "homePhone", "mobilePhone", "workPhone"
        });     

        // Adapter to set data in the listview
        m_Adapter = new SimpleCursorAdapter(getActivity().getBaseContext(),
                R.layout.listviewcontacts_layout,
                null,
                new String[]
                {
                        "name", "photoPath"
                },
                new int[]
                {
                        R.id.TextViewName, R.id.ImageViewPhoto,
                }, 0);

        // Getting reference to listview
        ListView ListViewContacts = (ListView) getView().findViewById(R.id.ListView_MainContacts);

        // Setting the adapter to listview
        ListViewContacts.setAdapter(m_Adapter);    
        
        // Setting the OnClickListner
        ListViewContacts.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> _Parent, View _View, int _Position, long _Id)
            {
                Cursor ClickedCursorObject = (Cursor) m_Adapter.getItem(_Position); 
                ((MainActivity) getActivity()).call_menu(ClickedCursorObject.getString(ClickedCursorObject.getColumnIndex("defaultNumber")));                               
            }
        });        

        // Creating an AsyncTask object to retrieve and load listview with contacts
        ListViewContactsLoader listViewContactsLoader = new ListViewContactsLoader();

        // Starting the AsyncTask process to retrieve and load listview with contacts
        listViewContactsLoader.execute();
        
        
    }

    // //////////////////////////////////////////////////////////////////////////////
    /** An AsyncTask class to retrieve and load listview with contacts */
    private class ListViewContactsLoader extends AsyncTask<Void, Void, Cursor>
    {
        // //////////////////////////////////////////////////////////////////////////////
        @Override
        protected Cursor doInBackground(Void... _Params)
        {
            if (m_ContactsCursor.moveToFirst())
            {
                do
                {
                    long contactId = m_ContactsCursor.getLong(m_ContactsCursor.getColumnIndex("_ID"));

                    Uri dataUri = ContactsContract.Data.CONTENT_URI;

                    // Querying the table ContactsContract.Data to retrieve individual items like
                    // home phone, mobile phone, work email etc corresponding to each contact
                    Cursor DataCursor = getActivity().getContentResolver().query(dataUri, null,
                            ContactsContract.Data.CONTACT_ID + "=" + contactId,
                            null, null);

                    String displayName = "";
                    String photoPath = "" + R.drawable.blank_contact_icon;
                    byte[] photoByte = null;

                    String homePhone = "";
                    String mobilePhone = "";
                    String workPhone = "";
                    String DefaultNumber = "";

                    if (DataCursor.moveToFirst())
                    {
                        // Getting Display Name
                        displayName = DataCursor.getString(DataCursor
                                .getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                        do
                        {
                            // Getting Phone numbers
                            if (DataCursor.getString(DataCursor.getColumnIndex("mimetype")).equals(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE))
                            {
                                if(DataCursor.getInt(DataCursor.getColumnIndex("is_super_primary")) > 0);
                                {
                                    DefaultNumber = DataCursor.getString(DataCursor.getColumnIndex("data1")); 
                                }
                                switch (DataCursor.getInt(DataCursor.getColumnIndex("data2")))
                                {
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:                                        
                                        homePhone = DataCursor.getString(DataCursor.getColumnIndex("data1"));                                        
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:                                       
                                        mobilePhone = DataCursor.getString(DataCursor.getColumnIndex("data1"));                                        
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:                                        
                                        workPhone = DataCursor.getString(DataCursor.getColumnIndex("data1"));                                        
                                        break;
                                }
                            }

                            // Getting Photo
                            if (DataCursor.getString(DataCursor.getColumnIndex("mimetype")).equals(
                                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE))
                            {
                                photoByte = DataCursor.getBlob(DataCursor.getColumnIndex("data15"));

                                if (photoByte != null)
                                {
                                    Bitmap Bittmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);

                                    // Getting Caching directory
                                    File cacheDirectory = getActivity().getBaseContext().getCacheDir();

                                    //Create Scaled Bitmap
                                    Bitmap ScaledBitMap = Bitmap.createScaledBitmap(Bittmap, 72, 72, false);
                                    
                                    // Temporary file to store the contact image
                                    File TmpFile = new File(cacheDirectory.getPath() + "/wpta_" + contactId + ".png");                                    

                                    // The FileOutputStream to the temporary file
                                    try
                                    {
                                        FileOutputStream FileOutStream = new FileOutputStream(TmpFile);

                                        // Writing the bitmap to the temporary file as png file
                                        ScaledBitMap.compress(Bitmap.CompressFormat.PNG, 100, FileOutStream);

                                        // Flush the FileOutputStream
                                        FileOutStream.flush();

                                        // Close the FileOutputStream
                                        FileOutStream.close();

                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                    photoPath = TmpFile.getPath();
                                }
                            }
                        }
                        while (DataCursor.moveToNext());
                        DataCursor.close();

                        // Adding id, display name, path to photo and other details to cursor
                        m_MatrixCursor.addRow(new Object[]
                        {
                                Long.toString(contactId), displayName, photoPath, DefaultNumber, homePhone, mobilePhone, workPhone
                        });
                    }
                }
                while (m_ContactsCursor.moveToNext());
            }
            m_ContactsCursor.close();
            return m_MatrixCursor;
        }

        // //////////////////////////////////////////////////////////////////////////////
        /**
         *  Setting the cursor containing contacts to ListView
         */
        @Override
        protected void onPostExecute(Cursor _Result)
        {            
            m_Adapter.swapCursor(_Result);
        }     
    }   
}
