package com.sip_client.contactlist;

import java.io.Serializable;

import com.example.sip_client.R;

public class Contact implements Serializable
{
    private String m_Displayname;
    private String m_PhoneNumber;
    private String m_PhotoPath = "" + R.drawable.blank_contact_icon;
    
    public Contact() {}
    
    // //////////////////////////////////////////////////////////////////////////////
    public Contact(String _Displayname, String _PhoneNumber)
    {
        m_PhoneNumber = _PhoneNumber;
        m_Displayname = _Displayname;        
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    public String getDisplayname()
    {
        return m_Displayname;
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    public void setDisplayname(String _Displayname)
    {
        this.m_Displayname = _Displayname;
    }

    // //////////////////////////////////////////////////////////////////////////////
    public String getPhoneNumber()
    {
        return m_PhoneNumber;
    }

    // //////////////////////////////////////////////////////////////////////////////
    public void setPhoneNumber(String _PhoneNumber)
    {
        this.m_PhoneNumber = _PhoneNumber;
    }

    // //////////////////////////////////////////////////////////////////////////////
    public String getPhotoPath()
    {
        return m_PhotoPath;
    }

    // //////////////////////////////////////////////////////////////////////////////
    public void setPhotoPath(String _PhotoPath)
    {
        this.m_PhotoPath = _PhotoPath;
    }
    
    
}
