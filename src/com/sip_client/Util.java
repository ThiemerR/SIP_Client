package com.sip_client;

/**
 * Utilities for the Application
 * @author Robert Thieme
 */
public class Util
{
    private Util() {};
    
    //byte[] as Identification for NFC Message
    public final static byte[]   ID_SEND_ALL = { 0x00,  };
    public final static byte[]   ID_SEND_PW =  { 0x01,  };
    
    //Request Code for activity result
    public final static int REQUEST_CODE_QRC = 50;    
    
    // ////////////////////////////////////////////////////////////////////////////// 
    //Separator for the Payload
    // //////////////////////////////////////////////////////////////////////////////     
    public final static String SERVER_START_SEPERATOR_SIGN = "|"; 
    
    public final static String SEPERATOR_USERNAME = "/us";
    public final static String SEPERATOR_SERVERDOMAIN = "/sd" ; 
    public final static String SEPERATOR_SERVERPORT =  "/po" ;
    public final static String SEPERATOR_USESTUNSERVER = "/is" ; 
    public final static String SEPERATOR_STUNSERVER = "/ss" ; 
    public final static String SEPERATOR_STUNPORT =  "/sp" ; 
    public final static String SEPERATOR_SIPPASSWORD =  "/pw" ;
    public final static String SEPERATOR_VALIDTIME =  "/vt" ;
    public final static String SEPERATOR_PHONENUMBER =  "/" ; 
    public final static String SEPERATOR_SSID =  "/ws" ; 
    public final static String SEPERATOR_WIFIPASSWORD = "/wp" ; 
    public final static String SEPERATOR_WIFIENCRYPTION =  "/we" ; 
    
    
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
