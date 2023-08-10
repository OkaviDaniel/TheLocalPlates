package com.example.thelocalplates8.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.thelocalplates8.Controllers.ProductController;
import com.example.thelocalplates8.Models.ProductModel;
import com.example.thelocalplates8.R;
import com.example.thelocalplates8.adapters.ProductResultAdapter;

import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    private ProductController productController;
    private RecyclerView resultsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        productController = new ProductController();
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String searchQuery = getIntent().getStringExtra("SEARCH_QUERY");

        productController.getProductsByTitle(searchQuery).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ProductModel> products = task.getResult().toObjects(ProductModel.class);

                // Set the fetched products to RecyclerView using the ProductAdapter
                ProductResultAdapter adapter = new ProductResultAdapter(products);
                resultsRecyclerView.setAdapter(adapter);
            } else {
                // Handle the error here
                Toast.makeText(this, "Error fetching products", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
