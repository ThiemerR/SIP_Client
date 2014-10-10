package com.sip_client.contactlist;

import java.io.File;
import java.util.List;

import com.example.sip_client.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Custom Expandable ListViewListViewAdapter
 * Use for showing the SIP_Server phone number at first and separate
 * Adapter has just one GroupItem
 * @author Robert Thieme
 */
public class ContactListExpandableListAdapter extends BaseExpandableListAdapter
{
    private static final String LOG_TAG = "ContactListExpandableListAdapter";
    private LayoutInflater m_Inflater;
    private List<Contact>  m_Items;

    // //////////////////////////////////////////////////////////////////////////////
    public ContactListExpandableListAdapter(List<Contact> _Items, Context _Context)
    {
        super();
        m_Items = _Items;
        m_Inflater = LayoutInflater.from(_Context);
    }   

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public int getGroupCount()
    {
        return 1;
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public int getChildrenCount(int groupPosition)
    {
        return m_Items.size();
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public Object getGroup(int groupPosition)
    {
        return null;
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public Object getChild(int _GgroupPosition, int _ChildPosition)
    {        
        return m_Items.get(_ChildPosition);
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public long getGroupId(int groupPosition)
    {
        return 0;
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public long getChildId(int _GgroupPosition, int _ChildPosition)
    {        
        return _ChildPosition;
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public View getGroupView(int _GroupPosition, boolean isExpanded, View _ConvertView, ViewGroup _Parent)
    {
        View GroupView = m_Inflater.inflate(R.layout.simpletextview, _Parent, false);
        return GroupView;
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public View getChildView(int _GroupPosition, int _ChildPosition, boolean _IsLastChild, View _ConvertView,
            ViewGroup _Parent)
    {
        View View = _ConvertView;
        if (View == null)
        {
            View = m_Inflater.inflate(R.layout.listviewcontacts_layout, null);
        }
        Contact Contact = m_Items.get(_ChildPosition);
        if (Contact != null)
        {
            ImageView PhotoImageView = (ImageView) View.findViewById(R.id.ImageViewPhoto);
            TextView NameImageView = (TextView) View.findViewById(R.id.TextViewName);
            if (PhotoImageView != null)
            {
                int Ressource = 0;
                try
                {
                    //if contact has as PhotoPath an Integer resource try set it
                    Ressource = Integer.parseInt(Contact.getPhotoPath());
                    PhotoImageView.setImageResource(Ressource);
                }
                catch(NumberFormatException _NumberFormatException)
                {
                    //if PhotoPath is not an Integer try to get PhotoPath as string for the path
                    Log.w(LOG_TAG,"Contact.getPhotoPath() is no Int try Path");
                    File ImageFile = new  File(Contact.getPhotoPath());
                    if(ImageFile.exists())
                    {
                        Bitmap Bitmap = BitmapFactory.decodeFile(ImageFile.getAbsolutePath());                        
                        PhotoImageView.setImageBitmap(Bitmap);
                    }
                    else
                    {
                        Log.e(LOG_TAG,"Path of File dont exists");
                    }
                }                
            }
            if (NameImageView != null)
            {
                NameImageView.setText(Contact.getDisplayname());
            }
        }
        return View;
    }

    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}
