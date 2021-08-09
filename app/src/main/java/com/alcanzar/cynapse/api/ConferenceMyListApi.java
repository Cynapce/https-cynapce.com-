package com.alcanzar.cynapse.api;

import android.content.Context;
import android.util.Log;

import com.alcanzar.cynapse.utils.AppConstantClass;
import com.android.volley.VolleyError;

import org.json.JSONObject;

public class ConferenceMyListApi extends HeadApi {
    public ConferenceMyListApi(Context context, JSONObject params){
        Log.e("conferencList",params.toString());
        postJsonApi(context, AppConstantClass.getAllMyConference,params,"getAllMyConference",true);
    }
    @Override
    public void responseApi(JSONObject response) {

    }
    @Override
    public void errorApi(VolleyError error) {

    }
}
