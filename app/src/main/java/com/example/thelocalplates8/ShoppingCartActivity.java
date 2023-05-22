package com.example.thelocalplates8;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
            String product = cartItem.getKey();
            int quantity = cartItem.getValue();

            // Assuming you have a method to retrieve the price and image for each product
            double productPrice = getProductPrice(product);
            double totalPriceForProduct = productPrice * quantity;
            totalPrice += totalPriceForProduct;

            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            ImageView productImageView = new ImageView(this);
            productImageView.setLayoutParams(new LinearLayout.LayoutParams(
                    100, // Adjust the width and height as needed
                    100
            ));
            productImageView.setImageResource(getProductImageResource(product));
            itemLayout.addView(productImageView);

            LinearLayout detailsLayout = new LinearLayout(this);
            detailsLayout.setOrientation(LinearLayout.VERTICAL);
            detailsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            detailsLayout.setGravity(Gravity.CENTER_VERTICAL);
            detailsLayout.setPadding(16, 8, 16, 8);
            itemLayout.addView(detailsLayout);

            TextView productNameTextView = new TextView(this);
            productNameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            productNameTextView.setText(product);
            productNameTextView.setTextSize(16);
            detailsLayout.addView(productNameTextView);

            LinearLayout quantityLayout = new LinearLayout(this);
            quantityLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            quantityLayout.setGravity(Gravity.CENTER_VERTICAL);

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
            quantityLayout.addView(decreaseButton);

            TextView quantityTextView = new TextView(this);
            quantityTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            quantityTextView.setText(String.valueOf(quantity));
            quantityLayout.addView(quantityTextView);

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
            quantityLayout.addView(increaseButton);

            detailsLayout.addView(quantityLayout);

            TextView totalPriceTextView = new TextView(this);
            totalPriceTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String formattedTotalPriceForProduct = decimalFormat.format(totalPriceForProduct);
            totalPriceTextView.setText("$" + formattedTotalPriceForProduct);
            detailsLayout.addView(totalPriceTextView);

            // Add a remove button
            Button removeButton = new Button(this);
            removeButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            removeButton.setText("Remove");
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Remove the product from the cart
                    cartItems.remove(product);
                    updateCartItems();
                }
            });
            detailsLayout.addView(removeButton);

            cartItemsLayout.addView(itemLayout);
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedTotalPrice = decimalFormat.format(totalPrice);

        // Display the total price
        totalPriceTextView.setText("Total Price: $" + formattedTotalPrice);
    }

    private double getProductPrice(String product) {
        // Replace this with your logic to retrieve the price for each product
        // Example: Assuming the product price is based on a fixed value per product
        if (product.equals("Product XYZ")) {
            return 10.99;
        } else if (product.equals("Hello")) {
            return 5.99;
        } else if (product.equals("Michael")) {
            return 7.99;
        } else {
            return 0.0;
        }
    }

    private int getProductImageResource(String product) {
        // Replace this with your logic to retrieve the image resource for each product
        // Example: Assuming the product image resource is based on the product name
        if (product.equals("Product XYZ")) {
            return R.drawable.logo;
        } else if (product.equals("Hello")) {
            return R.drawable.logo;
        } else if (product.equals("Michael")) {
            return R.drawable.logo;
        } else {
            return 0;
        }
    }
}