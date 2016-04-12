package com.jfireframework.fose.data;

import java.util.Date;

/**
 * Device entity. @author MyEclipse Persistence Tools
 */

public class Device implements java.io.Serializable
{
    
    // Fields
    
    /**
     * 
     */
    private static final long serialVersionUID = -7833130819750178757L;
    private long              id;
    private String            sn;
    private String            udid;
    private String            openUdid;
    private String            uuid;
    private String            idfa;
    private String            imei;
    private String            mac;
    private int               majorVersion;
    private int               minorVersion;
    private int               buildVersion;
    private int               os;
    private String            osVersion;
    private int               promoPlatformCode;
    private Date              activationTime;
    private long              userId;
    private boolean           bound;
                              
    /** default constructor */
    public Device()
    {
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }
    
    public String getSn()
    {
        return sn;
    }
    
    public void setSn(String sn)
    {
        this.sn = sn;
    }
    
    public String getUdid()
    {
        return udid;
    }
    
    public void setUdid(String udid)
    {
        this.udid = udid;
    }
    
    public String getOpenUdid()
    {
        return openUdid;
    }
    
    public void setOpenUdid(String openUdid)
    {
        this.openUdid = openUdid;
    }
    
    public String getUuid()
    {
        return uuid;
    }
    
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }
    
    public String getIdfa()
    {
        return idfa;
    }
    
    public void setIdfa(String idfa)
    {
        this.idfa = idfa;
    }
    
    public String getImei()
    {
        return imei;
    }
    
    public void setImei(String imei)
    {
        this.imei = imei;
    }
    
    public String getMac()
    {
        return mac;
    }
    
    public void setMac(String mac)
    {
        this.mac = mac;
    }
    
    public int getMajorVersion()
    {
        return majorVersion;
    }
    
    public void setMajorVersion(int majorVersion)
    {
        this.majorVersion = majorVersion;
    }
    
    public int getMinorVersion()
    {
        return minorVersion;
    }
    
    public void setMinorVersion(int minorVersion)
    {
        this.minorVersion = minorVersion;
    }
    
    public int getBuildVersion()
    {
        return buildVersion;
    }
    
    public void setBuildVersion(int buildVersion)
    {
        this.buildVersion = buildVersion;
    }
    
    public int getOs()
    {
        return os;
    }
    
    public void setOs(int os)
    {
        this.os = os;
    }
    
    public String getOsVersion()
    {
        return osVersion;
    }
    
    public void setOsVersion(String osVersion)
    {
        this.osVersion = osVersion;
    }
    
    public int getPromoPlatformCode()
    {
        return promoPlatformCode;
    }
    
    public void setPromoPlatformCode(int promoPlatformCode)
    {
        this.promoPlatformCode = promoPlatformCode;
    }
    
    public Date getActivationTime()
    {
        return activationTime;
    }
    
    public void setActivationTime(Date activationTime)
    {
        this.activationTime = activationTime;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public boolean isBound()
    {
        return bound;
    }
    
    public void setBound(boolean bound)
    {
        this.bound = bound;
    }
    
}
