package com.example.thelocalplates8;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thelocalplates8.R;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    private LinearLayout cartItemsLayout;
    private TextView totalPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        cartItemsLayout = findViewById(R.id.cartItemsLayout);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        updateCartItems();
    }

    private void updateCartItems() {
        // Retrieve the cart items from your data source
        List<Pair<String, Integer>> cartItems = new ArrayList<>();
        // Example of adding items to the cart with quantities
        cartItems.add(new Pair<>("Product XYZ", 2)); // Product XYZ with quantity 2
        cartItems.add(new Pair<>("Hello", 1)); // Hello with quantity 1
        cartItems.add(new Pair<>("Michael", 3)); // Michael with quantity 3

        double totalPrice = 0.0;
        for (Pair<String, Integer> cartItem : cartItems) {
            String item = cartItem.first;
            int quantity = cartItem.second;

            // Assuming you have a method to retrieve the price for each item
            double itemPrice = getItemPrice(item);
            double totalPriceForItem = itemPrice * quantity;
            totalPrice += totalPriceForItem;

            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textView.setText(item + " (Quantity: " + quantity + ") - $" + totalPriceForItem);
            textView.setTextSize(16);
            textView.setPadding(16, 8, 16, 8);

            cartItemsLayout.addView(textView);
        }

        // Display the total price
        totalPriceTextView.setText("Total Price: $" + totalPrice);
    }

    private double getItemPrice(String item) {
        // Replace this with your logic to retrieve the price for each item
        // Example: Assuming the item price is based on a fixed value per item
        if (item.equals("Product XYZ")) {
            return 10.99;
        } else if (item.equals("Hello")) {
            return 5.99;
        } else if (item.equals("Michael")) {
            return 7.99;
        } else {
            return 0.0;
        }
    }
}