package com.alcanzar.cynapse.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcanzar.cynapse.R;
import com.alcanzar.cynapse.model.AddForgeinPackageMYConferenceModel;

import java.util.ArrayList;

public class TicketsPackageForgeinAdapter2 extends RecyclerView.Adapter<TicketsPackageForgeinAdapter2.MyViewHolder> {

    public Context context;

    private ArrayList<AddForgeinPackageMYConferenceModel> packageList;

    boolean flag;

    public TicketsPackageForgeinAdapter2(Context context, ArrayList<AddForgeinPackageMYConferenceModel> packageList, boolean flag) {

        this.context = context;
        this.packageList = packageList;
        this.flag = flag;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tickects_packages_items, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TicketsPackageForgeinAdapter2.MyViewHolder holder, int position) {


        holder.ticketsname.setText(packageList.get(position).getConference_pack_day());
        holder.ticketsprice.setText(packageList.get(position).getConference_pack_charge());
        if (flag) {
            holder.cardticketpackageitems.setBackgroundColor(Color.parseColor("#FFF0EDED"));
            holder.imgarrow.setVisibility(View.VISIBLE);
//            holder.ticketsprice.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.priceindicator.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView ticketsname, ticketsprice;
        CardView cardticketpackageitems;
        ImageView imgarrow;
        ImageView priceindicator;

        public MyViewHolder(View view) {
            super(view);
            ticketsname = view.findViewById(R.id.tickectspackagename);
            ticketsprice = view.findViewById(R.id.tickectspackageprice);
            cardticketpackageitems = view.findViewById(R.id.cardticketpackageitems);
            imgarrow = view.findViewById(R.id.imgarrow);
            priceindicator = view.findViewById(R.id.priceindicator);

        }
    }
}