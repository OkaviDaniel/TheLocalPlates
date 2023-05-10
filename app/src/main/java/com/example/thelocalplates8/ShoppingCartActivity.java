package com.example.thelocalplates8;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thelocalplates8.R;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    private LinearLayout cartItemsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        cartItemsTextView = findViewById(R.id.cartItemsLayout);
        updateCartItems();
    }

    private void updateCartItems() {
        // Retrieve the cart items from your data source
        List<String> cartItems = new ArrayList<>();
        // Example of adding an item to the cart
        String itemToAdd = "Product XYZ";
        cartItems.add(itemToAdd);
        cartItems.add("Hello");
        cartItems.add("Michael");
        // You can use a List or any other data structure to store the items

        // Assuming you have a List<String> cartItems
        StringBuilder sb = new StringBuilder();
        for (String item : cartItems) {
            sb.append(item).append("\n");
        }

        for (String item : cartItems) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textView.setText(item);
            textView.setTextSize(16);
            textView.setPadding(16, 8, 16, 8);

            cartItemsTextView.addView(textView);
        }
    }
}