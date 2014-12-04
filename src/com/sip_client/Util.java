package com.sip_client;

/**
 * Utilities for the Application
 * @author Robert Thieme
 */
public class Util
{
    private Util() {};
    
    //byte[] as Identification for NFC Message
    public final static byte[]   ID_SEND_ALL            = { 0x00,  };
    public final static byte[]   ID_SEND_PW             = { 0x01,  };
    public final static byte[]   ID_SEND_ALL_WITHOUT_PW = { 0x02,  };
    
    //Request Code for activity result
    public final static int REQUEST_CODE_QRC = 50;    
    
    // ////////////////////////////////////////////////////////////////////////////// 
    //Separator for the Payload
    // //////////////////////////////////////////////////////////////////////////////   
    /**
     * Defines the first separator sign with "\\" for using in split method (regular expression)
     */
    public final static String STANDARD_SEPERATOR_SIGN = "\\|";
    /**
     * Use for setting end of sip-data
     */
    public final static String SEPERATOR_ENDSIPDATA = STANDARD_SEPERATOR_SIGN + "sd" ; 
    /**
     * Use for setting end of stunserver
     */
    public final static String SEPERATOR_ENDSTUNSERVER = STANDARD_SEPERATOR_SIGN + "ss" ; 
    /**
     * Use for setting end of WLAN
     */
    public final static String SEPERATOR_ENDWLAN = STANDARD_SEPERATOR_SIGN + "wl" ; 
    /**
     * Use for setting end of WLAN Password
     */
    public final static String SEPERATOR_ENDWLANPASSWORD = STANDARD_SEPERATOR_SIGN + "wp" ;    
    
    
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
