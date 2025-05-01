package com.example.lotyintsdapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItalokAdapter extends RecyclerView.Adapter<ItalokAdapter.ViewHolder> implements Filterable {

    private Context mContext;

    private int last = -1;

    private ArrayList<Italok> italAdatok;

    private ArrayList<Italok> mindenItalAdat;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currUser = mAuth.getCurrentUser();

    private CollectionReference mKosar = FirebaseFirestore.getInstance().collection("kosar");

    ItalokAdapter(Context context, ArrayList<Italok> itemsData) {
        this.italAdatok = itemsData;
        this.mindenItalAdat = itemsData;
        this.mContext = context;
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_shop_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItalokAdapter.ViewHolder holder, int position) {
        Italok currItem = italAdatok.get(position);
        holder.bindTo(currItem);

        if (holder.getAdapterPosition() > last) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            last = holder.getAdapterPosition();
        }

        Button buyButton = holder.itemView.findViewById(R.id.buy);

        buyButton.setOnClickListener(v -> {
            addItemToCart(currItem);

            ((ShopListActivity) mContext).updateAlertIcon();
        });
    }

    private void addItemToCart(Italok ital) {
        FirebaseUser currUser = mAuth.getCurrentUser();
        if (currUser != null) {
            String userID = currUser.getUid();

            Map<String, Object> itemData = new HashMap<>();
            itemData.put("name", ital.getName());
            itemData.put("ar", ital.getAr());
            itemData.put("kep", ital.getKep());
            itemData.put("csillag", ital.getCsillag());

            String documentID = mKosar.document().getId();

            ItalokCart cartItem = new ItalokCart(documentID, ital.getName(), ital.getAr(), ital.getCsillag(), ital.getKep());

            mKosar.document(userID).collection("user_kosar").document(documentID).set(cartItem)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ShopListActivity", "Item added to cart with ID: " + documentID);
                    })
                    .addOnFailureListener(e -> {
                        Log.w("ShopListActivity", "Failed to add item to cart");
                    });
        }
    }

    @Override
    public int getItemCount() {
        return italAdatok.size();
    }

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
            italAdatok = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView Tnev;
        private TextView T_ar;
        private ImageView Tkep;
        private RatingBar Tcsillag;

        ViewHolder(View itemView) {
            super(itemView);

            Tnev = itemView.findViewById(R.id.italNev);
            Tkep = itemView.findViewById(R.id.itemImage);
            Tcsillag = itemView.findViewById(R.id.italCsillag);
            T_ar = itemView.findViewById(R.id.italAr);

            itemView.findViewById(R.id.buy), setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ShopListActivity) mContext).updateAlertIcon();
                }
            });
        }

        void bindTo(Italok currItem) {
            Tnev.setText(currItem.getName());
            T_ar.setText(currItem.getAr());
            Tcsillag.setRating(currItem.getCsillag());

            Glide.with(mContext).load(currItem.getKep()).into(Tkep);
        }
    }
}

