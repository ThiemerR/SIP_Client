package com.sip_client;

import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

/**
 * Class for using Android WIFI functionality
 * @author Robert Thieme
 */
public class Wifi 
{
    private WifiManager m_WifiManager = null;
    // //////////////////////////////////////////////////////////////////////////////
    public Wifi(Context _Context)
    {
         m_WifiManager = (WifiManager)_Context.getSystemService(Context.WIFI_SERVICE); 
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Add a WIFI network
     * @param _SSID the SSID of the WLAN/WIFI network
     * @param _Password the password of the WLAN/WIFI network
     * @param _Encryption enum of Encryption
     */
    public void addWiFiNetwork(String _SSID, String _WifiPassword, Util.WiFiEncryption _Encryption)
    {        
        WifiConfiguration HotelWifiConfiguration = new WifiConfiguration();
        // Please note the quotes. String should contain ssid in quotes
        HotelWifiConfiguration.SSID = "\"" + _SSID + "\"";  
        
        switch(_Encryption)
        {
            case NONE:
                HotelWifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case WEP:
                HotelWifiConfiguration.wepKeys[0] = _WifiPassword ; 
                HotelWifiConfiguration.wepTxKeyIndex = 0;
                HotelWifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                HotelWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);                        
                break;            
            case WPA:
                HotelWifiConfiguration.preSharedKey = "\""+ _WifiPassword +"\"";
                break;                
           default:     
               //To-DO: Error Message
                break;
        }
        
        //Add WIFI network
        m_WifiManager.addNetwork(HotelWifiConfiguration);

        //Enable new WIFI newtwork
        List<WifiConfiguration> WifiList = m_WifiManager.getConfiguredNetworks();
        for (WifiConfiguration Configurations : WifiList)
        {
            if (Configurations.SSID != null && Configurations.SSID.equals("\"" + _SSID + "\""))
            {
                m_WifiManager.disconnect();
                m_WifiManager.enableNetwork(Configurations.networkId, true);
                m_WifiManager.reconnect();
                m_WifiManager.saveConfiguration();
                break;
            }
        }
    }
    
    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Search for a WIFI Network depends on SSID
     * @param _SSID the SSID of the WLAN/WIFI network
     * @return Network of WifiConfiguration depends on SSID or -1 if no WLAN/WIFI network is found
     */
    public int seachrWifiNetwork(String _SSID)
    {
        int FoundId = -1;     
        List<WifiConfiguration> WifiList = m_WifiManager.getConfiguredNetworks();
        for (WifiConfiguration Configurations : WifiList)
        {
            if (Configurations.SSID != null && Configurations.SSID.equals("\"" + _SSID + "\""))
            {
                FoundId = Configurations.networkId;
                break;
            }
        }
        return FoundId;
    }
    // //////////////////////////////////////////////////////////////////////////////
    /**
     * Remove Network depends on WifiConfiguration NetworkId
     * Important: Take care the NetworkId of WifiConfiguration can change after using saveConfiguration()
     * @param _NetworkId WifiConfiguration NetworkId
     */
    public void removeWifiNetwork(int _NetworkId)
    {
        m_WifiManager.removeNetwork(_NetworkId);
        m_WifiManager.saveConfiguration();
    }
}
