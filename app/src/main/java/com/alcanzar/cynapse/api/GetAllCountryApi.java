package com.alcanzar.cynapse.api;

import android.content.Context;

import com.alcanzar.cynapse.utils.AppConstantClass;
import com.android.volley.VolleyError;

import org.json.JSONObject;
public class GetAllCountryApi extends HeadApi {
    public GetAllCountryApi (Context context){
        postJsonApiGet(context, AppConstantClass.getAllCountry,"getAllCountry",false);
    }
    @Override
    public void responseApi(JSONObject response) {

    }

    @Override
    public void errorApi(VolleyError error) {

    }
}
