package com.example.coolweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.coolweather.db.City;
import com.example.coolweather.db.Country;
import com.example.coolweather.db.Province;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.json.JSONException;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTRY=2;
    private ProgressBar progressBar;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countryList;
    //被选中的省
    private Province selectedProvince;
    //被选中的城市
    private City selectedCity;
    //当前的选中级别
    private int currentLevel;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
      View view=inflater.inflate(R.layout.choose_area,container,false);
      titleText=(TextView)view.findViewById(R.id.title_text);
      backButton=(Button)view.findViewById(R.id.back_button);
      listView=(ListView)view.findViewById(R.id.list_view);
      progressBar=(ProgressBar)view.findViewById(R.id.progress_bar);
      adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
      listView.setAdapter(adapter);
      return view;
    }
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel==LEVEL_PROVINCE)
                {
                selectedProvince=provinceList.get(i);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(i);
                    queryCounties();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel==LEVEL_COUNTRY){
                    queryCities();
                }else if(currentLevel==LEVEL_CITY)
                {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }
    //查询全国的所有省，先从数据库里取数据，如果没有，再发请求给服务器，
    private void queryProvinces()
    {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList= LitePal.findAll(Province.class);
        if(provinceList.size()>0)
        {
            dataList.clear();
            for(Province province:provinceList)
            {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else
        {
            String address="http://guolin.tech/api/china"; //发送请求会响应回省份数据的
            queryFromServer(address,"province");
        }
    }

    //查询所有的市，和上一个方法同理
    private void queryCities()
    {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList=LitePal.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size()>0)
        {
            dataList.clear();
            for(City city:cityList)
            {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();//鼠标的位置不会改变，只改变对应的内容
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else
        {
            int provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode; //发送请求并响应返回市级的数据
            queryFromServer(address,"city");
        }
    }
    //查询所有的县级数据，先从数据库中取，如果没有的话，再发请求
    private void queryCounties()
    {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList=LitePal.where("cityid = ?",String.valueOf(selectedCity.getId())).find(Country.class); //查询数据库中县表中的cityid为当前选中city的id，
        if(countryList.size()>0)
        {
            dataList.clear();
            for(Country country:countryList)
            {
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTRY;
        }else
        {
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http:guolin/tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"country");
        }
    }
    public void queryFromServer(String address,final String type)
    {
        //进度条
        progressBar.setVisibility(View.VISIBLE);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //关闭进度条
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
                if("province".equals(type))
                {
                    result= Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type))
                {
                    try {
                        result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if("country".equals(type))
                {
                    try {
                        result=Utility.handleCountryResponse(responseText,selectedCity.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(result)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭进度条
                            progressBar.setVisibility(View.GONE);
                            if("province".equals(type))
                            {
                                queryProvinces();
                            }else if("city".equals(type))
                            {
                                queryCities();
                            }else if("country".equals(type))
                            {
                                queryCounties();
                            }
                        }
                    });
                }

            }
        });
    }




}
