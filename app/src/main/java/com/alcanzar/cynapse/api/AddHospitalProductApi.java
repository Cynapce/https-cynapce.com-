package com.alcanzar.cynapse.api;

import android.content.Context;
import android.util.Log;

import com.alcanzar.cynapse.utils.AppConstantClass;
import com.android.volley.VolleyError;

import org.json.JSONObject;

public class AddHospitalProductApi extends HeadApi {
    public AddHospitalProductApi(Context context, JSONObject params){
        Log.e("addHospitalProduct",params.toString());
        postJsonApi(context, AppConstantClass.addHospitalProduct,params,"addHospitalProduct",true);
    }
    @Override
    public void responseApi(JSONObject response) {

    }

    @Override
    public void errorApi(VolleyError error) {

    }
}
