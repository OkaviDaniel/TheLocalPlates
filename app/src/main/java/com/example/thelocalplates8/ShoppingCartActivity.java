package com.example.thelocalplates8;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;

import com.example.thelocalplates8.R;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCartActivity extends AppCompatActivity {

    private LinearLayout cartItemsLayout;
    private TextView totalPriceTextView;
    private Map<String, Integer> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        cartItemsLayout = findViewById(R.id.cartItemsLayout);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        cartItems = new HashMap<>();
        // Example of adding items to the cart with quantities
        cartItems.put("Product XYZ", 2); // Product XYZ with quantity 2
        cartItems.put("Hello", 1); // Hello with quantity 1
        cartItems.put("Michael", 3); // Michael with quantity 3

        updateCartItems();
    }

    private void updateCartItems() {
        cartItemsLayout.removeAllViews();

        double totalPrice = 0.0;
        for (Map.Entry<String, Integer> cartItem : cartItems.entrySet()) {
            String item = cartItem.getKey();
            int quantity = cartItem.getValue();

            // Assuming you have a method to retrieve the price for each item
            double itemPrice = getItemPrice(item);
            double totalPriceForItem = itemPrice * quantity;
            totalPrice += totalPriceForItem;

            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            TextView itemNameTextView = new TextView(this);
            itemNameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            ));
            itemNameTextView.setText(item);
            itemNameTextView.setTextSize(16);
            itemNameTextView.setPadding(16, 8, 16, 8);
            itemLayout.addView(itemNameTextView);

            Button increaseButton = new Button(this);
            increaseButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            increaseButton.setText("+");
            increaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Increase the quantity by 1
                    cartItem.setValue(quantity + 1);
                    updateCartItems();
                }
            });
            itemLayout.addView(increaseButton);

            TextView quantityTextView = new TextView(this);
            quantityTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            quantityTextView.setText(String.valueOf(quantity));
            itemLayout.addView(quantityTextView);

            Button decreaseButton = new Button(this);
            decreaseButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            decreaseButton.setText("-");
            decreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (quantity > 1) {
                        // Decrease the quantity by 1
                        cartItem.setValue(quantity - 1);
                        updateCartItems();
                    }
                }
            });
            itemLayout.addView(decreaseButton);

            TextView totalPriceTextView = new TextView(this);
            totalPriceTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String formattedTotalPriceForItem = decimalFormat.format(totalPriceForItem);
            totalPriceTextView.setText("$" + formattedTotalPriceForItem);
            itemLayout.addView(totalPriceTextView);

            cartItemsLayout.addView(itemLayout);
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedTotalPrice = decimalFormat.format(totalPrice);

        // Display the total price
        totalPriceTextView.setText("Total Price: $" + formattedTotalPrice);
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