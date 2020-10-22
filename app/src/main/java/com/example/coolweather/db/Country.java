package com.example.coolweather.db;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class Country extends LitePalSupport {
    private int id;
    private String countryName;
    private String weather_Id;
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
    public String getWeather_Id()
    {
        return this.weather_Id;
    }
    public void setWeather_Id(String weather_Id)
    {
        this.weather_Id=weather_Id;
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
