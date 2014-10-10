package com.sip_client.contactlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializable class which include a List of Contacts
 * @author Robert Thieme
 */
public class ContactList implements Serializable
{
    List<Contact> m_ContactList = new ArrayList<Contact>();
    
    public ContactList() {}
    
    // //////////////////////////////////////////////////////////////////////////////
    public void addContacts(String _Phonename, String _Phonenumber)
    {        
       Contact Contact = new Contact(_Phonename, _Phonenumber);
        m_ContactList.add(Contact);
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    public void addContacts(Contact _Contact)
    {        
        m_ContactList.add((_Contact));
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    public void removeAllContacts()
    {
        m_ContactList.clear();
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    public List<Contact> getContactList()
    {
        return m_ContactList;
    }
        
}
