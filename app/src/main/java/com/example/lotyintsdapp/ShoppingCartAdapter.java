package com.example.lotyintsdapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private int last = -1;

    private ArrayList<Italok> italAdatok;
    private ArrayList<Italok> mindenItalAdat;

    ShoppingCartAdapter(Context context, ArrayList<Italok> itemsData) {
        this.italAdatok = itemsData;
        this.mindenItalAdat = itemsData;
        this.mContext = context;
    }

    @Override
    public ShoppingCartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.cart_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ShoppingCartAdapter.ViewHolder holder, int position) {
        Italok currItem = italAdatok.get(position);
        holder.bindTo(currItem);

        if (holder.getAdapterPosition() > last) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.move);
            holder.itemView.startAnimation(animation);
            last = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return italAdatok.size();
    }

    @Override
    public Filter getFilter() {return shoppingFilter;}

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Italok> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.count = mindenItalAdat.size();
                results.values = mindenItalAdat;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Italok item : mindenItalAdat) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            italAdatok = (ArrayList)results.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView Tnev;
        private TextView T_ar;
        private ImageView Tkep;
        private RatingBar Tcsilag;
        ViewHolder(View itemView) {
            super(itemView);

            Tnev = itemView.findViewById(R.id.italNev2);
            Tkep = itemView.findViewById(R.id.itemImage);
            Tcsilag = itemView.findViewById(R.id.italCsillag2);
            T_ar = itemView.findViewById(R.id.italAr);
        }

        void bindTo(Italok currItem) {
            Tnev.setText(currItem.getName());
            T_ar.setText(currItem.getAr());
            Tcsilag.setRating(currItem.getCsillag());
            Glide.with(mContext).load(currItem.getKep()).into(Tkep);
        }
    }

}
