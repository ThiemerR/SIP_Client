package com.sip_client;

/**
 * Utilities for the Application
 * 
 * @author Robert Thieme *
 */
public class Util
{
    private Util() {};
    
    //byte[] as Identification for NFC Message
    public final static byte[]   ID_SEND_ALL = { 0x00,  };
    public final static byte[]   ID_SEND_PW =  { 0x01,  };
    
    //Request Code for activity result
    public final static int REQUEST_CODE_QRC = 50;
    
    /**
     * Enumeration for usable WiFiEncryption
     * Important: If u want add more encryption functions u must do if after the last entry because the position
     * in the enum is used for the assignment
     */   
    public static enum WiFiEncryption 
    {
        NONE,
        WEP,
        WPA,
    }
}
