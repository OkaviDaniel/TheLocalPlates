package com.example.thelocalplates8.Controllers;

import androidx.annotation.NonNull;

import com.example.thelocalplates8.Models.CustomerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CustomerController {
    private FirebaseFirestore db;

    public CustomerController(){
        db = FirebaseFirestore.getInstance();
    }

    public void getCustomerData(String userId, final CustomerModelCallback callback){
        DocumentReference docRef = db.collection("customers").document(userId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        CustomerModel customer = new CustomerModel();
                        customer.setFirstName(document.getString("firstName"));
                        customer.setLastName(document.getString("lastName"));
                        customer.setEmail(document.getString("email"));
                        customer.setCity(document.getString("city"));
                        customer.setPhone(document.getString("phone"));
                        if(document.contains("businessId")){
                            customer.setBusinessId(document.getString("businessId"));
                        }
                        callback.onCustomerModelCallback(customer);
                    }
                }
            }
        });
    }

    public interface CustomerModelCallback{
        void onCustomerModelCallback(CustomerModel customer);
    }
}
