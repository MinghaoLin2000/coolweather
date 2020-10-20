package com.example.coolweather.db;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class Country extends LitePalSupport {
    private int id;
    private String countryName;
    private int weatherId;
    private int cityId;
    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id=id;
    }
    public String getCountryName()
    {
        return this.countryName;
    }
    public void setCountryName(String countryName)
    {
        this.countryName=countryName;
    }
    public int getWeatherId()
    {
        return this.weatherId;
    }
    public void setWeatherId(int weatherId)
    {
        this.weatherId=weatherId;
    }
    public int getCityId()
    {
        return this.cityId;
    }
    public void setCityId(int cityId)
    {
        this.cityId=cityId;
    }
}
