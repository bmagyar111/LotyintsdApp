package com.example.lotyintsdapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static final String LOG_TAG = ShoppingCartActivity.class.getName();
    private ArrayList<Italok> kosarTartalom = new ArrayList<>();
    private ShoppingCartAdapter mAdapter;
    private int gridNumber = 1;
    private CollectionReference mKosar;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private static final int PERMISSION_REQUEST_CODE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_shopping_cart);

        //Firestore kosár init
        mKosar = FirebaseFirestore.getInstance().collection("kosar");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(
                this, gridNumber));
        mAdapter = new ShoppingCartAdapter(this, kosarTartalom); // Kosár tartalom adapter init
        recyclerView.setAdapter(mAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Megrendelés
        Button buyButton = findViewById(R.id.buy);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Kosár tartalom törlés
                deleteAllItemsFromCart();
            }
        });

        //Kosártartalom lekérdezés és megjelenítés
        queryKosarTartalom();
    }

    private void queryKosarTartalom() {
        kosarTartalom.clear();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mKosar.document(userId).collection("user_kosar")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Italok ital = document.toObject(Italok.class);
                            kosarTartalom.add(ital);
                        }
                        mAdapter.notifyDataSetChanged(); // Adapter frissítése az új tartalommal
                    })
                    .addOnFailureListener(e -> {
                        Log.e(LOG_TAG, "Hiba: " + e.getMessage());
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.lotyintsd_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.log_out_button) {
            //kijelentjkezés
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.home) {
            Intent intent = new Intent(this, ShopListActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.cart) {
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAllItemsFromCart() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mKosar.document(userId).collection("user_kosar")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                        //Oldal frissítése majd köszönő üzenet
                        sendNotification("Köszönjük megrendelését!");
                        Intent intent = new Intent(this, ShopListActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ShoppingCartActivity", "Hiba: " + e.getMessage());
                    });
        }
    }

    private void sendNotification(String message) {
        // Ellenőrizzük, hogy van-e értesítési engedély
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_BOOT_COMPLETED) == PackageManager.PERMISSION_GRANTED) {
            // Az értesítés építése és küldése
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                    .setSmallIcon(R.drawable.ic_shopping_cart)
                    .setContentTitle("Rendelés")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            // Az értesítési menedzser eléréséhez
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            // Értesítés megjelenítése
            notificationManager.notify(1, builder.build());
            // ...
        } else {
            // Az engedély hiányában megjelenített üzenet
            Toast.makeText(this, "Az értesítési engedély hiányzik", Toast.LENGTH_SHORT).show();
            // Engedélykérési logika
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, PERMISSION_REQUEST_CODE);
        }

    }


}