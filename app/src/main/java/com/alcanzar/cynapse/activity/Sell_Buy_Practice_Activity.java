package com.alcanzar.cynapse.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alcanzar.cynapse.R;
import com.alcanzar.cynapse.adapter.Adapter_Filter;
import com.alcanzar.cynapse.adapter.AddMultipleImageAV;
import com.alcanzar.cynapse.adapter.MedicalTitleAdapter;
import com.alcanzar.cynapse.adapter.PlaceAutocompleteAdapter;
import com.alcanzar.cynapse.api.AddPracticeProductApi;
import com.alcanzar.cynapse.api.GetAllCountryApi;
import com.alcanzar.cynapse.api.GetCityApi;
import com.alcanzar.cynapse.api.GetProfileApi;
import com.alcanzar.cynapse.api.GetSpecializationApi;
import com.alcanzar.cynapse.api.GetStateApi;
import com.alcanzar.cynapse.api.OtherCategoryApi;
import com.alcanzar.cynapse.appDatabase.DatabaseHelper;
import com.alcanzar.cynapse.model.CityModel;
import com.alcanzar.cynapse.model.CountryModel;
import com.alcanzar.cynapse.model.GetCategoryModel;
import com.alcanzar.cynapse.model.JobSpecializationModel;
import com.alcanzar.cynapse.model.OtherCategoryModel;
import com.alcanzar.cynapse.model.StateModel;
import com.alcanzar.cynapse.utils.AppConstantClass;
import com.alcanzar.cynapse.utils.AppCustomPreferenceClass;
import com.alcanzar.cynapse.utils.MyToast;
import com.alcanzar.cynapse.utils.PostImage;
import com.alcanzar.cynapse.utils.Util;
import com.alcanzar.cynapse.utils.Utils;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by alcanzar on 05/04/18.
 */

public class Sell_Buy_Practice_Activity extends AppCompatActivity implements View.OnClickListener, MedicalTitleInterface, GoogleApiClient.OnConnectionFailedListener {
    //TODO : header and other views


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    //    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    AutoCompleteTextView edit_street;


    TextView title, feet_two_txt, feet_one_txt, sq_feet_txt, spl_auto;
    ImageView btnBack, titleIcon, productImg, productImg2, productImg3;
    Button btnPublish;
    String categoryId = "", FeetTyp = "", FeetTypeId = "", category = "", Type = "", Type_Id, length = "", width = "", CatId = "", Category_name = "",
            specializationId = "", splStr = "", prac_Id = "", pracStr = "", country_id = "", country_str = "", state_id = "", state_Str = "", city_id = "", city_str = "", AddressStr = "";

    ArrayList<String> productCategory = new ArrayList<>();
    ArrayList<GetCategoryModel> categoryList = new ArrayList<>();
    RelativeLayout other_rel_lay, other_type_rel_lay, rel_lay_loc;
    Spinner categories, condition, type, speciality, feet_yard_spin;
    String profile_image = "";
    private Bitmap bitmap;
    List<String> list, list_categories, list_type_feet, length_list, width_list;
    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int PICK_CAMERA_REQUEST = 11;
    static String picturePath;
    long area = 1;
    ArrayList<OtherCategoryModel> OthercategoryList = new ArrayList<>();
    ArrayList<OtherCategoryModel> OtherTypeList = new ArrayList<>();
    ArrayList<String> OthercategoryNameList = new ArrayList<>();
    ArrayList<String> OtherTypeNameList = new ArrayList<>();

    AutoCompleteTextView practice_auto;
    ArrayAdapter hospital_adapter, country_adapter, adapter_specialization, state_adapter, city_adapter;
    ArrayList<String> tempPractice;
    ArrayList<String> PracticeName;

    RadioGroup radioGrp;
    RadioButton radioMyAdd, radioCurrentAdd, radioEnterAdd;
    TextView my_address_txt, current_address_txt;
    AutoCompleteTextView hospital_auto, country_auto, state_auto, city_auto;
    ArrayList<CountryModel> countryList = new ArrayList<>();
    ArrayList<StateModel> stateList = new ArrayList<>();
    ArrayList<CityModel> cityList = new ArrayList<>();
    ArrayList<JobSpecializationModel> specializationList = new ArrayList<>();
    ArrayList<String> specializationName;
    ArrayList<String> tempSplzation;
    String medicalSplId = "";
    ArrayList<String> SplNewList = new ArrayList<>();
    ArrayList<String> splNewListId = new ArrayList<>();
    ArrayList<String> countryName = new ArrayList<>();
    ArrayList<String> tempcountry = new ArrayList<>();
    ArrayList<String> stateName = new ArrayList<>();
    ArrayList<String> tempstate = new ArrayList<>();
    ArrayList<String> cityName = new ArrayList<>();
    ArrayList<String> tempcity = new ArrayList<>();
    DatabaseHelper handler;
    RecyclerView multi_title_sel_recycler;
    MedicalTitleAdapter titleAdapter_recycler_adapter;
    ArrayList<JobSpecializationModel> specializationSelectedList = new ArrayList<>();
    EditText area_length, area_width, ed_price, edit_others, edit_others_type, edit_cont_email, edit_contact_ph,
            edit_name, ed_status, edit_rooms, edit_loc, edit_price, edit_tot_area, edit_built_up_area, edit_qty;


    String addressLat = "", addressLog = "";
    private RecyclerView imageRv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_buy_practice);
        //TODO : initializing and setting the header views
        handler = new DatabaseHelper(this);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        titleIcon = findViewById(R.id.titleIcon);
        titleIcon.setImageResource(R.drawable.deals_white);
        categories = findViewById(R.id.categories);
        feet_yard_spin = findViewById(R.id.feet_yard_spin);
        speciality = findViewById(R.id.speciality);
        area_length = findViewById(R.id.area_length);
        area_width = findViewById(R.id.area_width);
        type = findViewById(R.id.type);
        sq_feet_txt = findViewById(R.id.sq_feet_txt);
        feet_two_txt = findViewById(R.id.feet_two_txt);
        feet_one_txt = findViewById(R.id.feet_one_txt);
        edit_name = findViewById(R.id.edit_name);
        productImg = findViewById(R.id.productImg);
        productImg2 = findViewById(R.id.productImg2);
        productImg3 = findViewById(R.id.productImg3);
        condition = findViewById(R.id.condition);
        other_rel_lay = findViewById(R.id.other_rel_lay);
        other_type_rel_lay = findViewById(R.id.other_type_rel_lay);
        edit_tot_area = findViewById(R.id.edit_tot_area);
        edit_tot_area.setEnabled(false);
        edit_built_up_area = findViewById(R.id.edit_built_up_area);
        edit_loc = findViewById(R.id.edit_loc);
        edit_price = findViewById(R.id.edit_price);
        edit_qty = findViewById(R.id.edit_qty);
        edit_street = findViewById(R.id.edit_street);
        ed_price = findViewById(R.id.ed_price);
        edit_others_type = findViewById(R.id.edit_others_type);
        edit_others = findViewById(R.id.edit_others);
        imageRv = findViewById(R.id.imageRv);

        ed_status = findViewById(R.id.ed_status);
        edit_rooms = findViewById(R.id.edit_rooms);
        edit_cont_email = findViewById(R.id.edit_cont_email);
        edit_contact_ph = findViewById(R.id.edit_contact_ph);
        radioGrp = findViewById(R.id.radioGrp);
        radioMyAdd = findViewById(R.id.radioMyAdd);
        radioCurrentAdd = findViewById(R.id.radioCurrentAdd);
        radioEnterAdd = findViewById(R.id.radioEnterAdd);
        my_address_txt = findViewById(R.id.my_address_txt);
        current_address_txt = findViewById(R.id.current_address_txt);
        country_auto = findViewById(R.id.country_auto);
        state_auto = findViewById(R.id.state_auto);
        city_auto = findViewById(R.id.city_auto);
        rel_lay_loc = findViewById(R.id.rel_lay_loc);

        practice_auto = findViewById(R.id.practice_auto);
        spl_auto = findViewById(R.id.spl_auto);

        title = findViewById(R.id.title);
        title.setText(R.string.sell_buy_practice_);
        btnPublish = findViewById(R.id.btnPublish);

        country_auto.setOnClickListener(this);
        city_auto.setOnClickListener(this);
        state_auto.setOnClickListener(this);

        btnPublish.setOnClickListener(this);
        productImg.setOnClickListener(this);
        productImg2.setOnClickListener(this);
        productImg3.setOnClickListener(this);

        productImg2.setVisibility(View.GONE);
        productImg3.setVisibility(View.GONE);

        practice_auto.setOnClickListener(this);
        spl_auto.setOnClickListener(this);
        addItemsOnSpinnerCategories();
        addItemsOnSpinnerFeet();
        getCountyApi();

//        imageRv = findViewById(R.id.imageRv);
//        imageRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        try {
            OtherCategoryApi();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            GetProfileApi();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        init();
//        OthercategoryList = handler.getOtherCategory(DatabaseHelper.TABLE_OTHER_CATEGORY_MASTER,"6");
        OtherTypeList = handler.getOtherCategory(DatabaseHelper.TABLE_OTHER_CATEGORY_MASTER, "5");
        if (OtherTypeList.size() > 0) {
            setArrayList();
        }

        speciality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CatId = OthercategoryList.get(i).getId();
                Category_name = OthercategoryList.get(i).getName();
                if (CatId.equalsIgnoreCase("-6")) {
                    other_rel_lay.setVisibility(View.VISIBLE);

                } else {
                    other_rel_lay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioMyAdd) {
                    my_address_txt.setVisibility(View.VISIBLE);
                    current_address_txt.setVisibility(View.GONE);
                    rel_lay_loc.setVisibility(View.GONE);
                    AddressStr = my_address_txt.getText().toString();
                } else if (checkedId == R.id.radioCurrentAdd) {
                    current_address_txt.setVisibility(View.VISIBLE);
                    rel_lay_loc.setVisibility(View.GONE);
                    my_address_txt.setVisibility(View.GONE);
                    AddressStr = current_address_txt.getText().toString();
                    try {
                        current_address_txt.setText(getCompleteAddressString(Sell_Buy_Practice_Activity.this,
                                Double.parseDouble(AppCustomPreferenceClass.readString(Sell_Buy_Practice_Activity.this, AppCustomPreferenceClass.Latitude, "")),
                                Double.parseDouble(AppCustomPreferenceClass.readString(Sell_Buy_Practice_Activity.this, AppCustomPreferenceClass.Longitude, ""))));
                    } catch (NumberFormatException nf) {
                        nf.printStackTrace();
                    }
                } else {
                    current_address_txt.setVisibility(View.GONE);
                    rel_lay_loc.setVisibility(View.VISIBLE);
                    my_address_txt.setVisibility(View.GONE);
                    AddressStr = "";
                }

            }
        });

        categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = list_categories.get(i);
                if (category.equalsIgnoreCase("Buy")) {
                    categoryId = "1";
                } else if (category.equalsIgnoreCase("Sell")) {
                    categoryId = "2";

                } else {
                    categoryId = "3";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        feet_yard_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                FeetTyp = list_type_feet.get(i);
                if (FeetTyp.equalsIgnoreCase("Feet")) {
                    FeetTypeId = "1";
                } else if (FeetTyp.equalsIgnoreCase("Meter")) {
                    FeetTypeId = "2";
                } else {
                    FeetTypeId = "3";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Type_Id = OtherTypeList.get(i).getId();
                Type = OtherTypeList.get(i).getName();
                if (Type_Id.equalsIgnoreCase("-5")) {
                    other_type_rel_lay.setVisibility(View.VISIBLE);

                } else {
                    other_type_rel_lay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        area_length.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                feet_one_txt.setText(FeetTyp);
                feet_two_txt.setText(FeetTyp);
                if (area_length.getText().toString().equalsIgnoreCase("")) {

                    edit_tot_area.setText(String.format("%s square%s", String.valueOf(area), FeetTyp));
                } else if (area_width.getText().toString().equalsIgnoreCase("")) {
                    area = Long.parseLong(area_length.getText().toString());
                    edit_tot_area.setText(String.format("%s square%s", String.valueOf(area), FeetTyp));
                } else {
                    area = Long.parseLong(area_width.getText().toString()) * Long.parseLong(area_length.getText().toString());
                    edit_tot_area.setText(String.format("%s square%s", String.valueOf(area), FeetTyp));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        area_width.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                feet_two_txt.setText(FeetTyp);
                feet_one_txt.setText(FeetTyp);
                if (area_width.getText().toString().equalsIgnoreCase("")) {

                    edit_tot_area.setText(String.format("%s square%s", String.valueOf(area), FeetTyp));
                } else if (area_length.getText().toString().equalsIgnoreCase("")) {
                    area = Long.parseLong(area_width.getText().toString());
                    edit_tot_area.setText(String.format("%s square%s", String.valueOf(area), FeetTyp));
                } else {
                    area = Long.parseLong(area_width.getText().toString()) * Long.parseLong(area_length.getText().toString());
                    edit_tot_area.setText(String.format("%s square%s", String.valueOf(area), FeetTyp));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_built_up_area.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sq_feet_txt.setText(String.format("square%s", FeetTyp));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        practice_auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.e("in ", "Country");
                    // stateC.setVisibility(View.GONE);
                    //  cityC.setVisibility(View.GONE);
                    try {
                        if (practice_auto.getText().toString().equals("")) {
                            tempPractice = new ArrayList<>();
                            tempPractice.addAll(PracticeName);
                        }
                        hospital_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempPractice);
                        practice_auto.setAdapter(hospital_adapter);
                        practice_auto.showDropDown();
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    }

                } else {
                    if (practice_auto.toString().equals("")) {

                    } else {

                    }

                }
            }
        });
        practice_auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pracStr = practice_auto.getText().toString();
                prac_Id = OtherTypeList.get(PracticeName.indexOf(pracStr)).getId();
                edit_others_type.setText("");
                if (prac_Id.equalsIgnoreCase("-5")) {
                    Type = "Others";
                    other_type_rel_lay.setVisibility(View.VISIBLE);

                } else {
                    other_type_rel_lay.setVisibility(View.GONE);
                    Type = "";
                }
                Log.d("medicalId", prac_Id);
            }
        });
        practice_auto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    try {
                        tempPractice = new ArrayList<>();
                        tempPractice.addAll(PracticeName);

                        hospital_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempPractice);
                        practice_auto.setAdapter(hospital_adapter);
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        spl_auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//
//                    Log.e("in ", "Country");
//
//
//                    // stateC.setVisibility(View.GONE);
//                    //  cityC.setVisibility(View.GONE);
//                    try {
//                        if (spl_auto.getText().toString().equals("")) {
//                            tempSplzation = new ArrayList<>();
//                            tempSplzation.addAll(specializationName);
//                        }
//                        adapter_specialization = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempSplzation);
//                        spl_auto.setAdapter(adapter_specialization);
//                        spl_auto.showDropDown();
//                    } catch (NullPointerException ne) {
//                        ne.printStackTrace();
//                    }
//                } else {
//                    if (spl_auto.toString().equals("")) {
//
//                    } else {
//
//                    }
//
//                }
//            }
//        });
//        spl_auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                splStr = spl_auto.getText().toString();
//                specializationId = specializationList.get(specializationName.indexOf(splStr)).getSpecialization_id();
//                edit_others.setText("");
//                Category_name = specializationList.get(specializationName.indexOf(splStr)).getSpecialization_name();
//                if (specializationId.equalsIgnoreCase("-2")) {
//                    other_rel_lay.setVisibility(View.VISIBLE);
//
//                } else {
//                    other_rel_lay.setVisibility(View.GONE);
//                }
//                Log.d("medicalId", specializationId);
//            }
//        });
//        spl_auto.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().equals("")) {
//                    try {
//                        tempSplzation = new ArrayList<>();
//                        tempSplzation.addAll(specializationName);
//
//                        adapter_specialization = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempSplzation);
//                        spl_auto.setAdapter(adapter_specialization);
//                    } catch (NullPointerException ne) {
//                        ne.printStackTrace();
//                    }
//
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
        country_auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    Log.e("in ", "Country");


                    // stateC.setVisibility(View.GONE);
                    //  cityC.setVisibility(View.GONE);
                    try {
                        if (country_auto.getText().toString().equals("")) {
                            tempcountry = new ArrayList<>();
                            tempcountry.addAll(countryName);
                        }
                        country_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempcountry);
                        country_auto.setAdapter(country_adapter);
                        country_auto.showDropDown();
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    }

                } else {
                    if (country_auto.toString().equals("")) {

                    } else {

                    }

                }
            }
        });

        country_auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                country_str = country_auto.getText().toString();
                country_id = countryList.get(countryName.indexOf(country_str)).getCountry_code();
                try {
                    getStateApi(country_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                state_auto.setText("");
                city_auto.setText("");
                Log.d("medicalId", country_id);
            }
        });

        country_auto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    try {
                        tempcountry = new ArrayList<>();
                        tempcountry.addAll(countryName);

                        country_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempcountry);
                        country_auto.setAdapter(country_adapter);
                        state_auto.setText("");
                        city_auto.setText("");
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    }

                } else {
                    country_id = "";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        state_auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    Log.e("in ", "Country");


                    // stateC.setVisibility(View.GONE);
                    //  cityC.setVisibility(View.GONE);
                    try {
                        if (state_auto.getText().toString().equals("")) {
                            tempstate = new ArrayList<>();
                            tempstate.addAll(stateName);
                        }
                        state_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempstate);
                        state_auto.setAdapter(state_adapter);
                        state_auto.showDropDown();
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    }

                } else {
                    if (state_auto.toString().equals("")) {

                    }

                }
            }
        });
        state_auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                state_Str = state_auto.getText().toString();
                state_id = stateList.get(stateName.indexOf(state_Str)).getState_code();
                country_id = stateList.get(stateName.indexOf(state_Str)).getCountry_code();
                try {
                    getCityApi(country_id, state_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("medicalId", country_id);
            }
        });
        state_auto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    try {
                        tempstate = new ArrayList<>();
                        tempstate.addAll(stateName);

                        state_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempstate);
                        state_auto.setAdapter(state_adapter);

                        city_auto.setText("");
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    }


                } else {
                    state_id = "";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        city_auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    Log.e("in ", "Country");


                    // stateC.setVisibility(View.GONE);
                    //  cityC.setVisibility(View.GONE);
                    try {
                        if (city_auto.getText().toString().equals("")) {
                            tempcity = new ArrayList<>();
                            tempcity.addAll(cityName);
                        }
                        city_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempcity);
                        city_auto.setAdapter(city_adapter);
                        city_auto.showDropDown();
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    }

                } else {
                    if (city_auto.toString().equals("")) {

                    } else {

                    }

                }
            }
        });
        city_auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city_str = city_auto.getText().toString();
                city_id = cityList.get(cityName.indexOf(city_str)).getCity_id();

                Log.d("city_id", city_id);
            }
        });
        city_auto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    try {
                        tempcity = new ArrayList<>();
                        tempcity.addAll(cityName);

                        city_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempcity);
                        city_auto.setAdapter(city_adapter);
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    }
                } else {
                    city_id = "";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        try {
            getProfileSpecialization("");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        clearListData();

        openGoogleLocatiion("<small>Street, House No.</small>");
    }

    private void openGoogleLocatiion(String sms) {
        PlacesClient placesClient;

        //String apiKey = "AIzaSyCUVWRZZji7uyu0xZdwYgC1q3xRJdReJ_Q";
        String apiKey = getString(R.string.googlePlaceAPI);
        Utils.sop("apiKey" + apiKey);

        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
            com.google.android.libraries.places.api.Places.initialize(Sell_Buy_Practice_Activity.this, apiKey);
        }

        // placesClient = com.google.android.libraries.places.api.Places.createClient(AddConference.this);

        final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.fragmen);
        autocompleteSupportFragment.setHint(Html.fromHtml(sms));
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS);
        autocompleteSupportFragment.setPlaceFields(fields);

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull final Place place) {
                final LatLng latLo = place.getLatLng();
                Utils.sop("latLo" + latLo + "==" + place);
                edit_street.setText(place.getAddress());

                geoLocate();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        autocompleteSupportFragment.setText("");
                    }
                }, 100);

                Utils.sop("latLo=" + place.getName() + "=====" + place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Utils.sop("onError" + status.isSuccess());
            }
        });
    }

    private void init() {
        Log.d("SellHospitalActivity", "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(Sell_Buy_Practice_Activity.this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);

        edit_street.setAdapter(mPlaceAutocompleteAdapter);
        try {


            edit_street.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    geoLocate();
                }
            });


            edit_street.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                            || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                        //execute our method for searching
                        geoLocate();
                    }

                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
//        mGps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: clicked gps icon");
//                getDeviceLocation();
//            }
//        });

//        hideSoftKeyboard();
    }

    private void geoLocate() {
        Log.d("SellHospitalActivity", "geoLocate: geolocating");

        String searchString = "";

        if (rel_lay_loc.getVisibility() == View.GONE) {
            searchString = AddressStr;
        } else {
            searchString = edit_street.getText().toString();
        }

        Geocoder geocoder = new Geocoder(Sell_Buy_Practice_Activity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e("SellHospitalActivity", "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            addressLat = String.valueOf(address.getLatitude());
            addressLog = String.valueOf(address.getLongitude());
            Log.d("SellHospitalActivity", "geoLocate: found a location: " + address.toString());


            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

//           moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
//                    address.getAddressLine(0));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:

                finish();
//                if (btnPublish.getText().toString().equals("NEXT")) {
//                    btnPublish.setText(R.string.publish);
//                    //TODO: switching to the next page
//                    finish();
//
//                } else if(btnPublish.getText().toString().equals("PUBLISH")){
//                    btnPublish.setText(R.string.next);
//                    relFirst.setVisibility(View.VISIBLE);
//                    relSecond.setVisibility(View.GONE);
//                }

                break;
            case R.id.productImg:
                productImgs = 0;
                chooserDialog("image");
                break;
            case R.id.productImg2:
                productImgs = 1;
                chooserDialog("image");
                break;
            case R.id.productImg3:
                productImgs = 2;
                chooserDialog("image");
                break;

            case R.id.practice_auto:
                try {
                    if (practice_auto.getText().toString().equals("")) {
                        tempPractice = new ArrayList<>();
                        tempPractice.addAll(PracticeName);
                    }

                    hospital_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempPractice);
                    practice_auto.setAdapter(hospital_adapter);
                    practice_auto.showDropDown();
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                }

                break;

            case R.id.spl_auto:


                showDialogSpecialization();

//                try {
//                    if (spl_auto.getText().toString().equals("")) {
//                        tempSplzation = new ArrayList<>();
//                        tempSplzation.addAll(specializationName);
//                    }
//
//                    adapter_specialization = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempSplzation);
//                    spl_auto.setAdapter(adapter_specialization);
//                    spl_auto.showDropDown();
//                } catch (NullPointerException ne) {
//                    ne.printStackTrace();
//                }

                break;
            case R.id.country_auto:
                try {
                    if (country_auto.getText().toString().equals("")) {
                        tempcountry = new ArrayList<>();
                        tempcountry.addAll(countryName);
                    }


                    country_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempcountry);
                    country_auto.setAdapter(country_adapter);
                    country_auto.showDropDown();
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                }

                break;

            case R.id.state_auto:
                try {
                    if (state_auto.getText().toString().equals("")) {
                        tempstate = new ArrayList<>();
                        tempstate.addAll(stateName);
                    }
                    state_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempstate);
                    state_auto.setAdapter(state_adapter);
                    state_auto.showDropDown();
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                }


                break;

            case R.id.city_auto:
                try {
                    if (city_auto.getText().toString().equals("")) {
                        tempcity = new ArrayList<>();
                        tempcity.addAll(cityName);
                    }
                    city_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempcity);
                    city_auto.setAdapter(city_adapter);
                    city_auto.showDropDown();
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                }


                break;
            case R.id.btnPublish:
                //TODO: moving to the next page of publish section

//                    if (btnPublish.getText().toString().equals("NEXT")) {
//                        if(isValidOne())
//                        {
//                        btnPublish.setText(R.string.publish);
//                        //TODO: switching to the next page
//                        relFirst.setVisibility(View.GONE);
//                        relSecond.setVisibility(View.VISIBLE);
//                    }
//                } else if(btnPublish.getText().toString().equals("PUBLISH")){
                //TODO : opening the success pop up
                if (isValid()) {
                    try {
                        addProductApi();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //   }
                break;
        }
    }

    private void clearListData() {
        productCategory.clear();

    }

    public void showDialogSpecialization() {

        EditText loc_search;
        LinearLayoutManager linearLayoutManager;
        TextView cancel_txt, done_txt, pref_loc_txt;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.multi_title_selection_dialog);
        //TODO: used to make the background transparent
        Window window = dialog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //TODO : initializing different views for the dialog
        multi_title_sel_recycler = dialog.findViewById(R.id.multi_city_sel_recycler);
        cancel_txt = dialog.findViewById(R.id.cancel_txt);
        pref_loc_txt = dialog.findViewById(R.id.pref_loc_txt);
        pref_loc_txt.setText("Select Specialization");
        done_txt = dialog.findViewById(R.id.done_txt);
        loc_search = dialog.findViewById(R.id.loc_search);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        multi_title_sel_recycler.setLayoutManager(linearLayoutManager);

        titleAdapter_recycler_adapter = new MedicalTitleAdapter(specializationSelectedList, this, Sell_Buy_Practice_Activity.this);
        multi_title_sel_recycler.setAdapter(titleAdapter_recycler_adapter);
        loc_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (titleAdapter_recycler_adapter != null)
                    titleAdapter_recycler_adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialog.show();
        //TODO : dismiss the on btn click and close click
        cancel_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplNewList.clear();
                splNewListId.clear();
                spl_auto.setText("");
                dialog.dismiss();
                //TODO : finishing the activity
            }
        });
//        done_txt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////            metrocityList = getModel(true);
////            Log.d("modelARRAYLIST",metrocityList+"");
////            MetroCityAdapter  recycler_adapter = new MetroCityAdapter(metrocityList,getActivity(),RequestJobFragment.this);
////            multi_city_sel_recycler.setAdapter(recycler_adapter);
//                titleNewList.clear();
//                for (int i = 0; i < titleSelectedList.size(); i++)
//
//                    if (titleSelectedList.get(i).getStatus()) {
//                        // jobLocation.setText(metrocitySelectedList.toString().replace("[","").replace("]",""));
//                        titleNewList.add(titleSelectedList.get(i).getSpecialization_name());
//                        Log.d("modelArrayList", titleList.get(i).getSpecialization_name() + "<><<" + titleList.get(i).getStatus());
//                    }
//                Title_auto.setText(titleNewList.toString().replace("[", "").replace("]", ""));
//                //Title_auto.setEnabled(false);
//                // dialog.dismiss();
//                if (titleNewList.size() > 3) {
//                    Toast toast = Toast.makeText(getActivity(), "Only 3 title can be select!", Toast.LENGTH_LONG);
//
//                    toast.show();
//                    dialog.show();
//                } else {
//                    dialog.dismiss();
//                }
//            }
//        });
        done_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//            metrocityList = getModel(true);
//            Log.d("modelARRAYLIST",metrocityList+"");
//            MetroCityAdapter  recycler_adapter = new MetroCityAdapter(metrocityList,getActivity(),RequestJobFragment.this);
//            multi_city_sel_recycler.setAdapter(recycler_adapter);
                SplNewList.clear();

                splNewListId.clear();
                for (int i = 0; i < specializationSelectedList.size(); i++)

                    if (specializationSelectedList.get(i).getStatus()) {
                        // jobLocation.setText(metrocitySelectedList.toString().replace("[","").replace("]",""));
                        SplNewList.add(specializationSelectedList.get(i).getSpecialization_name());
                        splNewListId.add(specializationSelectedList.get(i).getSpecialization_id());
                        Log.d("modelArrayList", specializationList.get(i).getSpecialization_name() + "<><<" + specializationList.get(i).getStatus());
                    }

                Log.d("ISZEOFTITLESELLIS", SplNewList.size() + "");
                medicalSplId = splNewListId.toString().replace("[", "").replace("]", "");

                Log.d("MEDICALmedicalSplId", medicalSplId);

                spl_auto.setText(SplNewList.toString().replace("[", "").replace("]", ""));
                if (SplNewList.size() > 3) {
                    Toast toast = Toast.makeText(Sell_Buy_Practice_Activity.this, "Only 3 Specializations can be selected!", Toast.LENGTH_LONG);

                    toast.show();
                    dialog.show();
                } else {
                    dialog.dismiss();
                }

            }
        });
    }

    public void addItemsOnSpinnerFeet() {


        list_type_feet = new ArrayList<>();
        list_type_feet.add("Feet");
        list_type_feet.add("Meter");
        list_type_feet.add("Yards");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list_type_feet);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feet_yard_spin.setAdapter(dataAdapter);
    }

    public void addItemsOnSpinnerCategories() {


        list_categories = new ArrayList<>();
        list_categories.add("Buy");
        list_categories.add("Sell");
        list_categories.add("Lease");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list_categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(dataAdapter);
    }

    void chooserDialog(final String type) {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Sell_Buy_Practice_Activity.this);
        dialog.setTitle(Html.fromHtml("<font color='#2FA49E'>Choose Photo From.</font>"));
        dialog.setMessage("");

        Log.e("dkd", "cropper_dialog");
        dialog.setNegativeButton("CAMERA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.e("dkd", "cropper_dialog neg");
                dialog.dismiss();


                if (ActivityCompat.checkSelfPermission(Sell_Buy_Practice_Activity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(Sell_Buy_Practice_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Sell_Buy_Practice_Activity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                            PICK_CAMERA_REQUEST);
                    return;
                }

                if (type.equalsIgnoreCase("image")) {
                    try {
                        Util.openCamera(Sell_Buy_Practice_Activity.this, PICK_CAMERA_REQUEST, "image");


                    } catch (SecurityException se) {
                        Toast.makeText(getApplicationContext(), "Camera permission needed", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        dialog.setPositiveButton("GALLERY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Log.e("dkd", "cropper_dialog pos");
                if (ActivityCompat.checkSelfPermission(Sell_Buy_Practice_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(Sell_Buy_Practice_Activity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Sell_Buy_Practice_Activity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                            PICK_IMAGE_REQUEST);
                }
                if (type.equalsIgnoreCase("image")) {
                    Util.openGallery(Sell_Buy_Practice_Activity.this, PICK_IMAGE_REQUEST, "image");
                }
            }
        });
        dialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("dkd", "onActivity pos");
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                Uri filePath = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(filePath, filePathColumn, null, null, null);

                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String yourRealPath = cursor.getString(columnIndex);


                    bitmap = BitmapFactory.decodeFile(yourRealPath);//  Util.customDecodeFile(yourRealPath, StaticItems.displayWidth, StaticItems.displayWidth);

                    if (bitmap == null)
                    {
//                        Util.imageFile = new File(yourRealPath);
//                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream().toString());
                    }

                    Log.e("Bitmap", "wi " + bitmap.getWidth() + "  he" + bitmap.getHeight());
                    Util.imageFile = new File(yourRealPath);
                    //profile_image.setImageBitmap(bitmap);
                    showCropperDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "Choose Anoother Pic", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            }
            if (requestCode == PICK_CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                Log.e("PICK_CAMERA_REQUEST", "PICK_CAMERA_REQUEST" + Util.imageFile);
                Log.e("PICK_CAMERA_REQUEST", "PICK_CAMERA_REQUEST" + Util.imageFile.getAbsolutePath());
                // bitmap = BitmapFactory.decodeFile(Util.imageFile.getAbsolutePath());//  Util.customDecodeFile(yourRealPath, StaticItems.displayWidth, StaticItems.displayWidth);
                bitmap = Util.customDecodeFile(Util.imageFile.getAbsolutePath(), 400, 400);
                //profile_image.setImageBitmap(bitmap);
                showCropperDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PICK_CAMERA_REQUEST", "PICK_CAMERA_REQUEST" + e.getMessage());
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    void showCropperDialog() {
        boolean wrapInScrollView = true;
        Log.e("resultCode", "resultCode12");

        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.cropdialog, null, false);

        final CropImageView img = (CropImageView) view.findViewById(R.id.CropImageView);
        img.setAspectRatio(1, 1);
        img.setFixedAspectRatio(true);
        img.setGuidelines(1);
        img.setCropShape(CropImageView.CropShape.RECTANGLE);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        img.setImageBitmap(bitmap);

        new MaterialDialog.Builder(Sell_Buy_Practice_Activity.this)
                .title(R.string.croppertitle)
                .customView(view, wrapInScrollView)
                .positiveText(R.string.crop)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        try {
                            bitmap = ThumbnailUtils.extractThumbnail(img.getCroppedImage(), 300, 300);
                            Util.saveImage(bitmap, Sell_Buy_Practice_Activity.this, getString(R.string.app_name));

                            if (productImgs == 0)
                                productImg.setImageBitmap(bitmap);
                            else if (productImgs == 1)
                                productImg2.setImageBitmap(bitmap);
                            else if (productImgs == 2)
                                productImg3.setImageBitmap(bitmap);

//                            multipleImageAV = new AddMultipleImageAV(Sell_Buy_Practice_Activity.this, multipleImagesAl);
//                            imageRv.setAdapter(multipleImageAV);
//                            imageRv.setVisibility(View.VISIBLE);

                            saveImage(bitmap, Sell_Buy_Practice_Activity.this, getString(R.string.app_name), "", false);

                            //uploadProfileImage();

                            //lin_lay_submit.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
                            Log.e("Exp", e.toString());
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        //lin_lay_submit.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public boolean saveImage(Bitmap save_bitmap, Context act, String rootDir, String body, boolean bool) {
        if (isStorageAvailable(act)) {

            String root = Environment.getExternalStorageDirectory().toString();
            File rootFile = new File(root, rootDir);
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }
            String fname = generateUniqueName("pic") + ".jpg";
            File imageFile = new File(rootFile, fname);

            FileOutputStream f = null;
            try {
                f = new FileOutputStream(imageFile);
                save_bitmap.compress(Bitmap.CompressFormat.PNG, 90, f);
                f.flush();
                f.close();
                picturePath = imageFile.getAbsolutePath();
                uploadProfileImage(picturePath);

                //galleryAddPic(imageFile, act, body, bool);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private static String generateUniqueName(String filename) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        filename = filename + timeStamp;
        return filename;
    }

    public static boolean isStorageAvailable(Context con) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Toast.makeText(con, "sd card is not writable", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(con, "SD is not available..!!", Toast.LENGTH_SHORT)
                    .show();
        }
        return false;
    }

    void uploadProfileImage(String picturePath) {
        final String fileName = picturePath.substring(picturePath.lastIndexOf("/") + 1);
        // final String fileName = Util.imageFile.getName();
//        String uuid = CustomPreference.readString(this, CustomPreference.USER_ID, "");

        String url = AppConstantClass.HOST + "fileUpload/productimage";
        PostImage post = new PostImage(Util.imageFile, url, fileName, Sell_Buy_Practice_Activity.this, "image") {
            @Override
            public void receiveData(String response) {
//                getProfileImage(response);
                try {
                    Log.e("profileimage", " = " + response);
                    JSONObject response1 = new JSONObject(response);
                    JSONObject data = response1.getJSONObject("Cynapse");
                    MyToast.logMsg("jsonImage", data.toString());
                    String res = data.getString("res_code");
                    String res1 = data.getString("res_msg");
                    profile_image = data.getString("file_name");

                    try {
                        multipleImagesAl.set(productImgs, profile_image);
                    } catch (Exception e) {
                        multipleImagesAl.add(profile_image);
                    }

                    if (productImgs == 0)
                        productImg2.setVisibility(View.VISIBLE);
                    else productImg3.setVisibility(View.VISIBLE);
                    System.out.println("multipleImagesAl" + multipleImagesAl.toString());
                    //   JSONObject data1 = data.getJSONObject("pharma");
//                    String profile_image = "http://162.243.205.148/pharmacist_v2/assets/images/patient_images/" + data1.getString("profile_image");
//                    Log.d("PJKLFLKKLFKSDLF",profile_image+" ");
//                    Glide.with(getApplicationContext())
//                            .load(profile_image)
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                            .error(R.drawable.default_category)
//                            .into(productImg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void receiveError() {
                Log.e("PROFILE", "ERROR");
            }
        };
        post.execute(url, null, null);
    }

    private boolean isValid() {
        boolean bool = false, bool_spl = false;
        String[] totarea = edit_tot_area.getText().toString().split(" ");
        if (radioMyAdd.isChecked()) {
            for (int i = 0; i < OtherTypeList.size(); i++) {
                if (OtherTypeList.get(i).getName().contains(practice_auto.getText().toString())) {
                    Log.d("hos_auto111", "<><<<");
                    bool = true;
                }
                Log.d("list_auto", "<><<<" + OtherTypeList.get(i).getName());
            }
//            for(int i = 0;i < specializationList.size();i++)
//            {
//                if(specializationList.get(i).getSpecialization_name().contains(spl_auto.getText().toString()))
//                {
//                    Log.d("hos_auto111","<><<<");
//                    bool_spl=true;
//                }
//            }
            if (country_id == null || country_id.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter country!");
                return false;
            } else if (state_id.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter state!");
                return false;
            } else if (city_id.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter city!");
                return false;
            } else if (categoryId.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select category!");
                return false;
            } else if (AddressStr.trim().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please provide Address!");
                return false;
            } else if (prac_Id.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter Practice!");
                return false;
            }
//            else if(!bool)
//            {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select from drop-down only!");
//                return false;
//            }
            else if (pracStr.equalsIgnoreCase("Others") && edit_others_type.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Others for Practice cannot be blank!");
                return false;
            } else if (medicalSplId.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter Speciality!");
                return false;
            }
//            else if(!bool_spl)
//            {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select from drop-down only!");
//                return false;
//            }
            else if (splStr.equalsIgnoreCase("Others") && edit_others.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Others for Speciality cannot be blank!");
                return false;
            } else if (TextUtils.isEmpty(edit_rooms.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "No.of Rooms is required!");
                return false;
            } else if (edit_name.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Name of Practice is required!");
                return false;
            } else if (area_length.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select length of land!");
                return false;
            } else if (area_width.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select width of land!");
                return false;
            } else if (TextUtils.isEmpty(edit_tot_area.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Total Area required!");
                return false;
            } else if (TextUtils.isEmpty(edit_built_up_area.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Built up area is required!");
                return false;
            } else if (Long.parseLong(edit_built_up_area.getText().toString()) > Long.parseLong(totarea[0])) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Built up area should be less or equal to Total Area!");
                return false;
            } else if (TextUtils.isEmpty(edit_cont_email.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please update email in profile");
                return false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(edit_cont_email.getText().toString()).matches()) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please enter valid Contact Email!");
                return false;
            } else if (TextUtils.isEmpty(edit_contact_ph.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please update phone no. in profile");
                return false;
            } else if (edit_contact_ph.getText().length() < 10) {
                MyToast.toastShort(Sell_Buy_Practice_Activity.this, "Invalid Phone Number");
                return false;
            } else if (TextUtils.isEmpty(ed_status.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Description is required!");
                return false;
            } else if (ed_status.getText().toString().equalsIgnoreCase(edit_name.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Description cannot be similar to name!");
                return false;
            }
        } else if (radioCurrentAdd.isChecked()) {
            for (int i = 0; i < OtherTypeList.size(); i++) {
                if (OtherTypeList.get(i).getName().contains(practice_auto.getText().toString())) {
                    Log.d("hos_auto111", "<><<<");
                    bool = true;
                }
                Log.d("list_auto", "<><<<" + OtherTypeList.get(i).getName());
            }
//            for(int i = 0;i < specializationList.size();i++)
//            {
//                if(specializationList.get(i).getSpecialization_name().contains(spl_auto.getText().toString()))
//                {
//                    Log.d("hos_auto111","<><<<");
//                    bool_spl=true;
//                }
//            }

            if (categoryId.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select category!");
                return false;
            } else if (country_id == null || country_id.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter country!");
                return false;
            } else if (state_id.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter state!");
                return false;
            } else if (city_id.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter city!");
                return false;
            } else if (AddressStr.trim().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please provide Address!");
                return false;
            } else if (prac_Id.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter Practice!");
                return false;
            }
//            else if(!bool)
//            {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select from drop-down only!");
//                return false;
//            }
            else if (pracStr.equalsIgnoreCase("Others") && edit_others_type.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Others for Practice cannot be blank!");
                return false;
            } else if (medicalSplId.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter Speciality!");
                return false;
            }
//            else if(!bool_spl)
//            {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select from drop-down only!");
//                return false;
//            }
            else if (splStr.equalsIgnoreCase("Others") && edit_others.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Others for Speciality cannot be blank!");
                return false;
            } else if (TextUtils.isEmpty(edit_rooms.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "No.of Rooms is required!");
                return false;
            } else if (edit_name.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Name of Practice is required!");
                return false;
            } else if (area_length.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select length of land!");
                return false;
            } else if (area_width.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select width of land!");
                return false;
            } else if (TextUtils.isEmpty(edit_tot_area.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Total Area required!");
                return false;
            } else if (TextUtils.isEmpty(edit_built_up_area.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Built up area is required!");
                return false;
            } else if (Long.parseLong(edit_built_up_area.getText().toString()) > Long.parseLong(totarea[0])) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Built up area should be less or equal to Total Area!");
                return false;
            } else if (TextUtils.isEmpty(edit_cont_email.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please update email in profile");
                return false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(edit_cont_email.getText().toString()).matches()) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please enter valid Contact Email!");
                return false;
            } else if (TextUtils.isEmpty(edit_contact_ph.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, " Please update phone no. in profile");
                return false;
            } else if (edit_contact_ph.getText().length() < 10) {
                MyToast.toastShort(Sell_Buy_Practice_Activity.this, "Invalid Phone Number");
                return false;
            } else if (TextUtils.isEmpty(ed_status.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Description is required!");
                return false;
            } else if (ed_status.getText().toString().equalsIgnoreCase(edit_name.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Description cannot be similar to name!");
                return false;
            }
        } else {
            for (int i = 0; i < OtherTypeList.size(); i++) {
                if (OtherTypeList.get(i).getName().contains(practice_auto.getText().toString())) {
                    Log.d("hos_auto111", "<><<<");
                    bool = true;
                }
                Log.d("list_auto", "<><<<" + OtherTypeList.get(i).getName());
            }
//            for(int i = 0;i < specializationList.size();i++)
//            {
//                if(specializationList.get(i).getSpecialization_name().contains(spl_auto.getText().toString()))
//                {
//                    Log.d("hos_auto111","<><<<");
//                    bool_spl=true;
//                }
//            }
            if (country_id == null || country_id.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter country!");
                return false;
            } else if (state_id.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter state!");
                return false;
            } else if (city_id.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter city!");
                return false;
            } else if (edit_street.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter Street,House No.!");
                return false;
            } else if (categoryId.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select category!");
                return false;
            } else if (prac_Id.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter Practice!");
                return false;
            }
//            else if(!bool)
//            {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select from drop-down only!");
//                return false;
//            }
            else if (pracStr.equalsIgnoreCase("Others") && edit_others_type.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Others for Practice cannot be blank!");
                return false;
            } else if (medicalSplId.equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter Speciality!");
                return false;
            }
//            else if(!bool_spl)
//            {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select from drop-down only!");
//                return false;
//            }
            else if (splStr.equalsIgnoreCase("Others") && edit_others.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Others for Speciality cannot be blank!");
                return false;
            } else if (TextUtils.isEmpty(edit_rooms.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "No.of Rooms is required!");
                return false;
            } else if (edit_name.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Name of Practice is required!");
                return false;
            } else if (area_length.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select length of land!");
                return false;
            } else if (area_width.getText().toString().equalsIgnoreCase("")) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select width of land!");
                return false;
            } else if (TextUtils.isEmpty(edit_tot_area.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Total Area required!");
                return false;
            } else if (TextUtils.isEmpty(edit_built_up_area.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Built up area is required!");
                return false;
            } else if (Long.parseLong(edit_built_up_area.getText().toString()) > Long.parseLong(totarea[0])) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Built up area should be less or equal to Total Area!");
                return false;
            } else if (TextUtils.isEmpty(edit_cont_email.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please update email in profile");
                return false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(edit_cont_email.getText().toString()).matches()) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please enter valid Contact Email!");
                return false;
            } else if (TextUtils.isEmpty(edit_contact_ph.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please update phone no. in profile");
                return false;
            } else if (edit_contact_ph.getText().length() < 10) {
                MyToast.toastShort(Sell_Buy_Practice_Activity.this, "Invalid Phone Number");
                return false;
            } else if (TextUtils.isEmpty(ed_status.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Description is required!");
                return false;
            } else if (ed_status.getText().toString().equalsIgnoreCase(edit_name.getText().toString())) {
                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Description cannot be similar to name!");
                return false;
            }
        }
        return true;
    }
//    private boolean isValidOne() {
//        boolean bool=false,bool_spl=false;
//        String[] totarea = edit_tot_area.getText().toString().split(" ");
//        Log.e("totarea", "<><" + totarea[0]);
//        Log.e("categoryId", "<><" + categoryId);
//        if (AddressStr.equalsIgnoreCase("")) {
//            for(int i = 0;i < OtherTypeList.size();i++)
//            {
//                if(OtherTypeList.get(i).getName().contains(practice_auto.getText().toString()))
//                {
//                    Log.d("hos_auto111","<><<<");
//                    bool=true;
//                }
//                Log.d("list_auto","<><<<"+OtherTypeList.get(i).getName());
//            }for(int i = 0;i < specializationList.size();i++)
//            {
//                if(specializationList.get(i).getSpecialization_name().contains(spl_auto.getText().toString()))
//                {
//                    Log.d("hos_auto111","<><<<");
//                    bool_spl=true;
//                }
//            }
//            if (country_id == null || country_id.equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter country!");
//                return false;
//            } else if (state_id.equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter state!");
//                return false;
//            } else if (city_id.equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter city!");
//                return false;
//            } else if (categoryId.equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select category!");
//                return false;
//            } else if (prac_Id.equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter Practice!");
//                return false;
//            }else if(!bool)
//            {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select from drop-down only!");
//                return false;
//            }
//            else if (pracStr.equalsIgnoreCase("Others") && edit_others_type.getText().toString().equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Others for Practice cannot be blank!");
//                return false;
//            } else if (specializationId.equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter Speciality!");
//                return false;
//            }else if(!bool_spl)
//            {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select from drop-down only!");
//                return false;
//            } else if (splStr.equalsIgnoreCase("Others") && edit_others.getText().toString().equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Others for Speciality cannot be blank!");
//                return false;
//            } else if (TextUtils.isEmpty(edit_rooms.getText().toString())) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "No.of Rooms is required!");
//                return false;
//            } else if (edit_name.getText().toString().equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Name of Practice is required!");
//                return false;
//            } else if (area_length.getText().toString().equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select length of land!");
//                return false;
//            } else if (area_width.getText().toString().equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select width of land!");
//                return false;
//            } else if (TextUtils.isEmpty(edit_tot_area.getText().toString())) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Total Area required!");
//                return false;
//            } else if (TextUtils.isEmpty(edit_built_up_area.getText().toString())) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Built up area is required!");
//                return false;
//            } else if (Integer.parseInt(edit_built_up_area.getText().toString()) > Integer.parseInt(totarea[0])) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Built up area should be less or equal to Total Area!");
//                return false;
//            } else if (TextUtils.isEmpty(edit_qty.getText().toString())) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Quantity is required!");
//                return false;
//            } else if (TextUtils.isEmpty(ed_status.getText().toString())) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Description is required!");
//                return false;
//            } else if (ed_status.getText().toString().equalsIgnoreCase(edit_name.getText().toString())) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Description cannot be similar to name!");
//                return false;
//            }
//        } else {
//            for(int i = 0;i < OtherTypeList.size();i++)
//            {
//                if(OtherTypeList.get(i).getName().contains(practice_auto.getText().toString()))
//                {
//                    Log.d("hos_auto111","<><<<");
//                    bool=true;
//                }
//                Log.d("list_auto","<><<<"+OtherTypeList.get(i).getName());
//            }
//            for(int i = 0;i < specializationList.size();i++)
//            {
//                if(specializationList.get(i).getSpecialization_name().contains(spl_auto.getText().toString()))
//                {
//                    Log.d("hos_auto111","<><<<");
//                    bool_spl=true;
//                }
//            }
//            if (categoryId.equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select category!");
//                return false;
//            } else if (prac_Id.equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter Practice!");
//                return false;
//            }else if(!bool)
//            {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select from drop-down only!");
//                return false;
//            } else if (pracStr.equalsIgnoreCase("Others") && edit_others_type.getText().toString().equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Others for Practice cannot be blank!");
//                return false;
//            } else if (specializationId.equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please Enter Speciality!");
//                return false;
//            }else if(!bool_spl)
//            {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select from drop-down only!");
//                return false;
//            } else if (splStr.equalsIgnoreCase("Others") && edit_others.getText().toString().equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Others for Speciality cannot be blank!");
//                return false;
//            } else if (TextUtils.isEmpty(edit_rooms.getText().toString())) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "No.of Rooms is required!");
//                return false;
//            } else if (edit_name.getText().toString().equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Name of Practice is required!");
//                return false;
//            } else if (area_length.getText().toString().equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select length of land!");
//                return false;
//            } else if (area_width.getText().toString().equalsIgnoreCase("")) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Please select width of land!");
//                return false;
//            } else if (TextUtils.isEmpty(edit_tot_area.getText().toString())) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Total Area required!");
//                return false;
//            } else if (TextUtils.isEmpty(edit_built_up_area.getText().toString())) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Built up area is required!");
//                return false;
//            } else if (Integer.parseInt(edit_built_up_area.getText().toString()) > Integer.parseInt(totarea[0])) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Built up area should be less or equal to Total Area!");
//                return false;
//            } else if (TextUtils.isEmpty(edit_qty.getText().toString())) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Quantity is required!");
//                return false;
//            } else if (TextUtils.isEmpty(ed_status.getText().toString())) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Description is required!");
//                return false;
//            } else if (ed_status.getText().toString().equalsIgnoreCase(edit_name.getText().toString())) {
//                MyToast.toastLong(Sell_Buy_Practice_Activity.this, "Description cannot be similar to name!");
//                return false;
//            }
//        }
//
//        return true;
//    }

    //    private boolean isValidTwo() {
//        String[] totarea=edit_tot_area.getText().toString().split(" ");
//        Log.e("totarea","<><"+totarea[0]);
//         if (TextUtils.isEmpty(edit_city.getText().toString())){
//            MyToast.toastLong(Sell_Buy_Practice_Activity.this,"City is required!");
//            return false;
//        }
//        else if (area_length.getText().toString().equalsIgnoreCase("") ) {
//            MyToast.toastLong(Sell_Buy_Practice_Activity.this,"Please select length of land!");
//            return false;
//        }else if (area_width.getText().toString().equalsIgnoreCase("") ) {
//            MyToast.toastLong(Sell_Buy_Practice_Activity.this,"Please select width of land!");
//            return false;
//        }
//        else if (TextUtils.isEmpty(edit_tot_area.getText().toString())) {
//            MyToast.toastLong(Sell_Buy_Practice_Activity.this,"Total Area required!");
//            return false;
//        }else if (TextUtils.isEmpty(edit_built_up_area.getText().toString())) {
//            MyToast.toastLong(Sell_Buy_Practice_Activity.this,"Built up area is required!");
//            return false;
//        }else if (Integer.parseInt(edit_built_up_area.getText().toString()) > Integer.parseInt(totarea[0])){
//             MyToast.toastLong(Sell_Buy_Practice_Activity.this,"Built up area should be less or equal to Total Area!");
//             return false;
//         }else if (TextUtils.isEmpty(ed_status.getText().toString())) {
//            MyToast.toastLong(Sell_Buy_Practice_Activity.this,"Description is required!");
//            return false;
//        }else if (ed_status.getText().toString().equalsIgnoreCase(edit_name.getText().toString())) {
//             MyToast.toastLong(Sell_Buy_Practice_Activity.this,"Description cannot be similar to name!");
//             return false;
//         }
//        return true;
//    }


    private void getCountyApi() {
        new GetAllCountryApi(this) {
            @Override
            public void responseApi(JSONObject response) {
                super.responseApi(response);
                try {
                    JSONObject header = response.getJSONObject("Cynapse");
                    String res_msg = header.getString("res_msg");
                    String res_code = header.getString("res_code");
                    Log.d("RESPONSECOUNTRY", response.toString());
                    if (res_code.equals("1")) {
                        // countrySpinner.clear();
                        //countryList.clear();
                        //  MyToast.toastLong(this,res_msg);
                        JSONArray header2 = header.getJSONArray("Country");
                        for (int i = 0; i < header2.length(); i++) {
                            JSONObject item = header2.getJSONObject(i);
                            countryList.add(new CountryModel(item.getString("country_code"), item.getString("country_name")));
                            countryName.add(item.getString("country_name"));
                        }
                        if (countryList.size() > 0) {

                            countryName = new ArrayList<>();
                            tempcountry = new ArrayList<>();
                            for (int j = 0; j < countryList.size(); j++) {
                                // countryName.add(medicalList.get(j).getProfile_category_name());
                                tempcountry.add(countryList.get(j).getCountry_name());
                                countryName.add(countryList.get(j).getCountry_name());
                            }

                            country_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempcountry);
                            country_auto.setAdapter(country_adapter);
                            country_auto.setThreshold(1);
                        }
//                        try {
//                            country.setAdapter(new ArrayAdapter<>(Sell_Buy_Practice_Activity.this,android.R.layout.simple_spinner_dropdown_item,countrySpinner));
//                        }
//                        catch (NullPointerException ne)
//                        {
//                            ne.printStackTrace();
//                        }

                    } else {
                        // countrySpinner.clear();
                        //  countryList.clear();
                        // MyToast.toastLong(Sell_Buy_Practice_Activity.this,res_msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorApi(VolleyError error) {
                super.errorApi(error);
            }
        };
    }

    private void getStateApi(String countryId) throws JSONException {
        JSONObject header = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("country_code", countryId);
        header.put("Cynapse", params);
        new GetStateApi(Sell_Buy_Practice_Activity.this, header) {
            @Override
            public void responseApi(JSONObject response) {
                super.responseApi(response);
                try {
                    JSONObject header = response.getJSONObject("Cynapse");
                    String res_msg = header.getString("res_msg");
                    String res_code = header.getString("res_code");
                    Log.d("STATERESPONSE", response.toString());
                    if (res_code.equals("1")) {
                        // stateSpinner.clear();
                        stateList.clear();
                        //  cityList.clear();
                        //   citySpinner.clear();
                        // MyToast.toastLong(Sell_Buy_Practice_Activity.this,res_msg);
                        JSONArray header2 = header.getJSONArray("State");
                        for (int i = 0; i < header2.length(); i++) {
                            JSONObject item = header2.getJSONObject(i);
                            stateList.add(new StateModel(item.getString("country_code"), item.getString("state_code"), item.getString("state_name")));

                            stateName.add(item.getString("state_name"));
                        }
                        if (stateList.size() > 0) {

                            stateName = new ArrayList<>();
                            tempstate = new ArrayList<>();
                            for (int j = 0; j < stateList.size(); j++) {
                                // countryName.add(medicalList.get(j).getProfile_category_name());
                                tempstate.add(stateList.get(j).getState_name());
                                stateName.add(stateList.get(j).getState_name());
                            }

                            state_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempstate);
                            state_auto.setAdapter(state_adapter);
                            state_auto.setThreshold(1);
                        }
//                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Sell_Buy_Practice_Activity.this,android.R.layout.simple_spinner_dropdown_item,stateSpinner);
//                        state.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
                    } else {
                        // stateSpinner.clear();
                        // stateList.clear();
                        //  cityList.clear();
                        //  citySpinner.clear();
                        //  MyToast.toastLong(Sell_Buy_Practice_Activity.this,res_msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorApi(VolleyError error) {
                super.errorApi(error);
            }
        };
    }

    private void getCityApi(String stateCountryId, String stateId) throws JSONException {
        JSONObject header = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("country_code", stateCountryId);
        params.put("state_code", stateId);
        header.put("Cynapse", params);
        Log.d("CITYHEADER", params.toString());
        new GetCityApi(Sell_Buy_Practice_Activity.this, header) {
            @Override
            public void responseApi(JSONObject response) {
                super.responseApi(response);
                try {
                    JSONObject header = response.getJSONObject("Cynapse");
                    String res_msg = header.getString("res_msg");
                    String res_code = header.getString("res_code");
                    Log.d("CITYRESPONSE", response.toString());
                    if (res_code.equals("1")) {
                        // citySpinner.clear();
                        cityList.clear();
                        // MyToast.toastLong(Sell_Buy_Practice_Activity.this,res_msg);
                        JSONArray header2 = header.getJSONArray("City");
                        for (int i = 0; i < header2.length(); i++) {
                            JSONObject item = header2.getJSONObject(i);
                            cityList.add(new CityModel(item.getString("city_id"), item.getString("country_code"), item.getString("state_code"), item.getString("city_name")));
                            cityName.add(item.getString("city_name"));
                        }
                        if (cityList.size() > 0) {

                            cityName = new ArrayList<>();
                            tempcity = new ArrayList<>();
                            for (int j = 0; j < cityList.size(); j++) {
                                // countryName.add(medicalList.get(j).getProfile_category_name());
                                tempcity.add(cityList.get(j).getCity_name());
                                cityName.add(cityList.get(j).getCity_name());
                            }

                            city_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempcity);
                            city_auto.setAdapter(city_adapter);
                            city_auto.setThreshold(1);
                        }
//                        ArrayAdapter<String> adapter =new ArrayAdapter<String>(Sell_Buy_Practice_Activity.this,android.R.layout.simple_spinner_dropdown_item,citySpinner);
//                        city.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
                    } else {
                        // citySpinner.clear();
                        //  cityList.clear();
                        // MyToast.toastLong(Sell_Buy_Practice_Activity.this,res_msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorApi(VolleyError error) {
                super.errorApi(error);
            }
        };
    }

    private void OtherCategoryApi() throws JSONException {
        JSONObject header = new JSONObject();
        JSONObject params = new JSONObject();
        // params.put("sync_time", AppCustomPreferenceClass.readString(Sell_Buy_Practice_Activity.this, AppCustomPreferenceClass.other_cat_sync_time, ""));
        params.put("sync_time", "");

        header.put("Cynapse", params);
        new OtherCategoryApi(this, header) {
            @Override
            public void responseApi(JSONObject response) {
                super.responseApi(response);
                try {
                    JSONObject header = response.getJSONObject("Cynapse");
                    String res_msg = header.getString("res_msg");
                    String res_code = header.getString("res_code");
                    String sync_time = header.getString("sync_time");
                    AppCustomPreferenceClass.writeString(Sell_Buy_Practice_Activity.this, AppCustomPreferenceClass.other_cat_sync_time, sync_time);
                    Log.d("RESPONSECOUNTRY", response.toString());
                    if (res_code.equals("1")) {
                        // OthercategoryList.clear();
                        // OthercategoryNameList.clear();
                        // OtherTypeList.clear();
                        // OtherTypeNameList.clear();
                        //  handler.deleteTableName(DatabaseHelper.TABLE_OTHER_CATEGORY_MASTER);
                        //  MyToast.toastLong(this,res_msg);
                        JSONArray header2 = header.getJSONArray("OtherCategory");
                        for (int i = 0; i < header2.length(); i++) {
                            OtherCategoryModel model = new OtherCategoryModel();
                            JSONObject item = header2.getJSONObject(i);

                            model.setId(item.getString("id"));
                            model.setType_id(item.getString("type_id"));
                            model.setName(item.getString("name"));
                            model.setStatus(item.getString("status"));
                            //countrySpinner.add(item.getString("country_name"));
                            if (!handler.CheckIsDataAlreadyInDBorNot(DatabaseHelper.TABLE_OTHER_CATEGORY_MASTER, DatabaseHelper.id, item.getString("id"))) {

                                handler.AddOtherCategory(model, true);

                                //    Log.e("ADDED_Sub_item", true + " " + model.getProduct_id());
                            } else {
                                //   Log.e("UPDATED", true + " " + model.getProduct_id());
                                handler.AddOtherCategory(model, false);
                            }
                        }
                        for (int i = 4; i < 6; i++) {
                            OtherCategoryModel model = new OtherCategoryModel();
                            model.setId(String.valueOf(-i - 1));
                            model.setType_id(String.valueOf(i + 1));
                            model.setName("Others");
                            model.setStatus("1");
                            if (!handler.CheckIsDataAlreadyInDBorNot(DatabaseHelper.TABLE_OTHER_CATEGORY_MASTER, DatabaseHelper.id, String.valueOf(-i - 1))) {

                                handler.AddOtherCategory(model, true);

                                //    Log.e("ADDED_Sub_item", true + " " + model.getProduct_id());
                            } else {
                                //   Log.e("UPDATED", true + " " + model.getProduct_id());
                                handler.AddOtherCategory(model, false);
                            }
                        }


                        setArrayList();

                    } else {
                        // OthercategoryList.clear();
                        // MyToast.toastLong(Sell_Buy_Practice_Activity.this,res_msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorApi(VolleyError error) {
                super.errorApi(error);
            }
        };
    }
//     "uuid":"S1dea",
//             "category_id":"2",
//             "practice_category":"2",
//             "practice_type":"others_practice",
//             "others_practice_name":"others_sffggsdf44",
//             "rooms":"50",
//             "practice_name":"testing",
//             "specific_locality":"Hazarat Ganj",
//             "practice_city_name":"Kanpur",
//             "price":"9000",
//             "land_length":"400",
//             "land_width":"300",
//             "total_area":"700",
//             "build_up_area":"900",
//             "speciality":"others_speciality",
//             "others_speciality_name":"others_sffggsdf44",
//             "description":"May Testing dsgsdg sdgds xdvd"

    private void addProductApi() throws JSONException {

        JSONObject header = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("uuid", AppCustomPreferenceClass.readString(Sell_Buy_Practice_Activity.this, AppCustomPreferenceClass.UserId, ""));
        params.put("category_id", "2");
        params.put("practice_category", categoryId);


        if (Category_name.equalsIgnoreCase("Others")) {

            params.put("speciality", "others_speciality");
            params.put("others_speciality_name", edit_others.getText().toString());
        } else {
            params.put("speciality", medicalSplId);
            params.put("others_speciality_name", "");
        }
        if (Type.equalsIgnoreCase("Others")) {
            params.put("practice_type", "others_practice");
            params.put("others_practice_name", edit_others_type.getText().toString());
        } else {
            params.put("practice_type", prac_Id);
            params.put("others_practice_name", "");
        }
        //params.put("practice_category", categoryId);
        params.put("quantity", edit_qty.getText().toString());
        if (AddressStr.equalsIgnoreCase("")) {
            params.put("city", city_id);
            params.put("country", country_id);
            params.put("state", state_id);

//            params.put("city", "");
//            params.put("country", "");
//            params.put("state", "");
            params.put("address", edit_street.getText().toString());
        } else {
            geoLocate();
            params.put("address", AddressStr);
            params.put("city", city_id);
            params.put("country", country_id);
            params.put("state", state_id);
        }
        params.put("contact_phone", edit_contact_ph.getText().toString());
        params.put("contact_email", edit_cont_email.getText().toString());
        params.put("house_street", edit_street.getText().toString());
        params.put("practice_name", edit_name.getText().toString());
        params.put("rooms", edit_rooms.getText().toString());
        params.put("specific_locality", edit_loc.getText().toString());
        params.put("practice_city_name", "");
        params.put("land_length", area_length.getText().toString());
        params.put("land_width", area_width.getText().toString());
        params.put("total_area", edit_tot_area.getText().toString());
        params.put("build_up_area", edit_built_up_area.getText().toString() + " " + "square" + FeetTyp);
        params.put("price", edit_price.getText().toString());
        params.put("description", ed_status.getText().toString());
        //params.put("product_image", profile_image);

        if (multipleImagesAl.size() == 0)
            params.put("product_image", "");
        else
            params.put("product_image", multipleImagesAl.toString().substring(1, multipleImagesAl.toString().length() - 1).replace(" ", ""));

        params.put("latitude", addressLat);
        params.put("longitude", addressLog);


        header.put("Cynapse", params);

        new AddPracticeProductApi(Sell_Buy_Practice_Activity.this, header) {
            @Override
            public void responseApi(JSONObject response) {
                super.responseApi(response);

                try {
                    JSONObject header = response.getJSONObject("Cynapse");
                    String res_msg = header.getString("res_msg");
                    String res_code = header.getString("res_code");

                    if (res_code.equals("1")) {
                        final Dialog dialog = new Dialog(Sell_Buy_Practice_Activity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_msg_alert);
                        //TODO: used to make the background transparent
                        Window window = dialog.getWindow();
                        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        //TODO : initializing different views for the dialog
                        TextView title = dialog.findViewById(R.id.title);
                        TextView msg = dialog.findViewById(R.id.msg);
                        ImageView close = dialog.findViewById(R.id.close);
                        Button btnGotIt = dialog.findViewById(R.id.btnGotIt);
                        //TODO :setting different views
//                        title.setText(R.string.congrats);
//                        msg.setText(R.string.publishedSuccessfully);
                        title.setText(R.string.sent);

                        if (categoryId.equalsIgnoreCase("1")) {
                            msg.setText(R.string.buy_req);
                        } else if (categoryId.equalsIgnoreCase("2")) {
                            msg.setText(R.string.sell_req);

                        } else {
                            msg.setText(R.string.lease_req);
                        }
                        //msg.setText(R.string.request_sent);
                        dialog.show();
                        //TODO : dismiss the on btn click and close click
                        btnGotIt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                //TODO : finishing the activity
                                finish();
                            }
                        });
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                finish();
                            }
                        });
//                        MyToast.toastLong(Sell_Buy_Practice_Activity.this, res_msg);
//                        finish();
                    } else {
                        MyToast.toastLong(Sell_Buy_Practice_Activity.this, res_msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorApi(VolleyError error) {
                super.errorApi(error);
            }
        };
    }

    private void getProfileSpecialization(String id) throws JSONException {
        JSONObject header = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("profile_category_id", id);
        header.put("Cynapse", params);
        new GetSpecializationApi(Sell_Buy_Practice_Activity.this, header) {
            @Override
            public void responseApi(JSONObject response) {
                super.responseApi(response);
                try {
                    JSONObject header = response.getJSONObject("Cynapse");
                    String res_msg = header.getString("res_msg");
                    String res_code = header.getString("res_code");
                    if (res_code.equals("1")) {
                        // jobSpecializationSpinner.clear();
                        // specializationList.clear();
                        JSONArray header2 = header.getJSONArray("ProfileSpecializationMaster");
                        for (int i = 0; i < header2.length(); i++) {
                            JSONObject item = header2.getJSONObject(i);
//                            specializationList.add(new JobSpecializationModel(item.getString("specialization_id"),
//                                    item.getString("profile_category_id"), item.getString("specialization_name")));

                            JobSpecializationModel model = new JobSpecializationModel();
                            model.setSpecialization_id(item.getString("specialization_id"));
                            model.setSpecialization_name(item.getString("specialization_name"));
                            specializationList.add(model);
                            specializationSelectedList = getModels(false);
                            //  jobSpecializationSpinner.add(item.getString("specialization_name"));
                            //   Log.e("jobSpecializationSize", String.valueOf(jobSpecializationSpinner.size()));
                        }

                        //specializationList.add(new JobSpecializationModel("-2", "1", "Others"));
                        //jobSpecializationSpinner.add("Others");
//                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item
//                                , jobSpecializationSpinner);
//                        specialization.setAdapter(adapter);
                        //  adapter.notifyDataSetChanged();


//                        if (specializationList.size() > 0) {
//
//                            specializationName = new ArrayList<>();
//                            tempSplzation = new ArrayList<>();
//                            for (int j = 0; j < specializationList.size(); j++) {
//                                // countryName.add(medicalList.get(j).getProfile_category_name());
//                                tempSplzation.add(specializationList.get(j).getSpecialization_name());
//                                specializationName.add(specializationList.get(j).getSpecialization_name());
//                            }
//
//                            adapter_specialization = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempSplzation);
//                            spl_auto.setAdapter(adapter_specialization);
//                            spl_auto.setThreshold(1);
//                        }


                        //MyToast.toastLong(getActivity(),res_msg);
                    } else {
                        // jobSpecializationSpinner.clear();
                        // specializationList.clear();
                        //MyToast.toastLong(getActivity(),res_msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorApi(VolleyError error) {
                super.errorApi(error);
            }
        };
    }

    public void setArrayList() {
//        OthercategoryList = handler.getOtherCategory(DatabaseHelper.TABLE_OTHER_CATEGORY_MASTER,"6");
//
//        for(int i = 0;i<OthercategoryList.size();i++)
//        {
//            OthercategoryNameList.add(OthercategoryList.get(i).getName());
//
//        }
//        // OthercategoryNameList.add("Others");
//        try {
//            speciality.setAdapter(new ArrayAdapter<>(Sell_Buy_Practice_Activity.this,android.R.layout.simple_spinner_dropdown_item,OthercategoryNameList));
//        }
//        catch (NullPointerException ne)
//        {
//            ne.printStackTrace();
//        }
        OtherTypeList = handler.getOtherCategory(DatabaseHelper.TABLE_OTHER_CATEGORY_MASTER, "5");

//        for(int i = 0;i<OtherTypeList.size();i++)
//        {
//            OtherTypeNameList.add(OtherTypeList.get(i).getName());
//
//        }
//        // OthercategoryNameList.add("Others");
//        try {
//            type.setAdapter(new ArrayAdapter<>(Sell_Buy_Practice_Activity.this,android.R.layout.simple_spinner_dropdown_item,OtherTypeNameList));
//        }
//        catch (NullPointerException ne)
//        {
//            ne.printStackTrace();
//        }
        if (OtherTypeList.size() > 0) {

            PracticeName = new ArrayList<>();
            tempPractice = new ArrayList<>();
            for (int j = 0; j < OtherTypeList.size(); j++) {
                // countryName.add(medicalList.get(j).getProfile_category_name());
                tempPractice.add(OtherTypeList.get(j).getName());
                PracticeName.add(OtherTypeList.get(j).getName());
            }

            hospital_adapter = new Adapter_Filter(Sell_Buy_Practice_Activity.this, R.layout.activity_sell_buy_practice, R.id.lbl_name, tempPractice);
            practice_auto.setAdapter(hospital_adapter);
            practice_auto.setThreshold(1);
        }

    }

    private void GetProfileApi() throws JSONException {
        JSONObject header = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("uuid", AppCustomPreferenceClass.readString(this, AppCustomPreferenceClass.UserId, ""));
        header.put("Cynapse", params);
        new GetProfileApi(Sell_Buy_Practice_Activity.this, header, false) {
            @Override
            public void responseApi(JSONObject response) {
                super.responseApi(response);
                try {
                    // arrayList.clear();
                    JSONObject header = response.getJSONObject("Cynapse");
                    String res_msg = header.getString("res_msg");
                    String res_code = header.getString("res_code");
                    String sync_time = header.getString("sync_time");

                    if (res_code.equals("1")) {
                        JSONObject item = header.getJSONObject("GetProfile");
                        item.getString("uuid");
                        edit_cont_email.setText(item.getString("email"));
                        edit_contact_ph.setText(item.getString("phone_number"));
                        my_address_txt.setText(item.getString("address") + " " + item.getString("city_name") + " " + item.getString("state_name") + " " + item.getString("country_name"));
                        AddressStr = my_address_txt.getText().toString();
                    } else {

                        //MyToast.toastLong(MainActivity.this,res_msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorApi(VolleyError error) {
                super.errorApi(error);
            }
        };
    }

    private String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {

        String strAdd = "";
//        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
//            if (addresses != null) {
//                Address returnedAddress = addresses.get(0);
//                StringBuilder strReturnedAddress = new StringBuilder("");
//
//                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
//                }
//                strAdd = strReturnedAddress.toString();
//                Log.e("My Current loction", "" + strReturnedAddress.toString());
//            } else {
//                Log.e("My Current loction", "No Address returned!");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("My Current loction", "Canont get Address!");
//        }
//        return strAdd;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            strAdd = address;
            AddressStr = address;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return strAdd;
    }

    private ArrayList<JobSpecializationModel> getModels(boolean isSelect) {
        ArrayList<JobSpecializationModel> list = new ArrayList<>();
        for (int i = 0; i < specializationList.size(); i++) {

            JobSpecializationModel model = new JobSpecializationModel();
            model.setStatus(isSelect);
            model.setSpecialization_id(specializationList.get(i).getSpecialization_id());
            model.setSpecialization_name(specializationList.get(i).getSpecialization_name());
            list.add(model);
        }
        return list;
    }

    @Override
    public void selectedTitles(ArrayList<String> titleList) {
        medicalSplId = titleList.toString().replace("[", "").replace("]", "");

        Log.d("medicalSplId", medicalSplId);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private ArrayList<String> multipleImagesAl = new ArrayList<>();
    private int productImgs = 0;
}
