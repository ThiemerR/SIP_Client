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
}
