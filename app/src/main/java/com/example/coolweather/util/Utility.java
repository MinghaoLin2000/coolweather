package com.example.coolweather.util;

import android.content.pm.ProviderInfo;
import android.text.TextUtils;

import com.example.coolweather.db.City;
import com.example.coolweather.db.Country;
import com.example.coolweather.db.Province;
import com.example.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    //解析和处理服务器返回的省级数据
    public static boolean handleProvinceResponse(String response)
    {
        if(!TextUtils.isEmpty(response))
        {
            try{
                JSONArray allProvinces=new JSONArray(response); //把字符串解析成json格式，
                for(int i=0;i<allProvinces.length();i++)
                {
                    JSONObject provinceObject=allProvinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();//存入数据库
                }
                return true;
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }
    //解析和处理服务器返回的市级数据
    public static boolean handleCityResponse(String response,int provinceId) throws JSONException {
        if(!TextUtils.isEmpty(response))
        {
            JSONArray allCities=new JSONArray(response);
            for(int i=0;i<allCities.length();i++)
            {
                JSONObject jsonObject=allCities.getJSONObject(i);
                City city=new City();
                city.setCityName(jsonObject.getString("name"));
                city.setCityCode(jsonObject.getInt("id"));
                city.setProvinceId(provinceId);
                city.save();
            }
            return true;
        }
        return false;
    }
    //解析和处理服务器返回的县级数据
    public static boolean handleCountryResponse(String response,int cityId) throws JSONException {
        if(!TextUtils.isEmpty(response))
        {
            JSONArray allCountries=new JSONArray(response);
            for(int i=0;i<allCountries.length();i++)
            {
                JSONObject jsonObject=allCountries.getJSONObject(i);
                Country country=new Country();
                country.setCountryName(jsonObject.getString("name"));
                country.setWeather_Id(jsonObject.getString("weather_id"));
                country.setCityId(cityId);
                country.save();
            }
            return true;
        }
        return false;
    }
    //解析天气，将json数据解析成Weather实体类
    public static Weather handleWeatherResponse(String response)
    {
        try {
            JSONObject jsonObject=new JSONObject(response);
            String weatherContent=jsonObject.toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }


}
