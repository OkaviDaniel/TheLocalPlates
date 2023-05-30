package com.example.thelocalplates8.Controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.thelocalplates8.Models.ChatsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatsController {

    private FirebaseFirestore db;

    public ChatsController(){
        db = FirebaseFirestore.getInstance();
    }

    public void getChat(String businessId, String customerId, final GetChatData callback){
        Query usersBusiness = db.collection("chats").
                whereEqualTo("customerId", customerId)
                .whereEqualTo("businessId", businessId);

        usersBusiness.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()){
                        ChatsModel chatsModel = document.toObject(ChatsModel.class);
                        callback.OnGetChatData(chatsModel);
                    }
                }
            }
        });
    }

    public void getCustomerChats(String customerId, final GetCustomerChats callback){
        Query usersBusiness = db.collection("chats").
                whereEqualTo("customerId", customerId);

        ArrayList<ChatsModel> chats = new ArrayList<>();
        usersBusiness.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()){
                        ChatsModel chatsModel = document.toObject(ChatsModel.class);
                        chats.add(chatsModel);
                    }
                    callback.OnGetCustomerChats(chats);
                }else{
                    callback.OnGetCustomerChats(chats);
                }
            }
        });
    }

    public void getBusinessChats(String businessId, final GetBusinessChats callback){
        Query usersBusiness = db.collection("chats").
                whereEqualTo("businessId", businessId);
        ArrayList<ChatsModel> chats = new ArrayList<>();
        usersBusiness.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()){
                        ChatsModel chatsModel = document.toObject(ChatsModel.class);
                        chats.add(chatsModel);
                    }
                    callback.OnGetBusinessChats(chats);
                }else{
                    callback.OnGetBusinessChats(chats);
                }
            }
        });
    }

    public void initializeChat(String customerId, String businessId, final InitializeChat callback){
        Map<String, Object> chatMap = new HashMap<String, Object>();
        chatMap.put("businessId", businessId);
        chatMap.put("customerId", customerId);
        db.collection("chats").add(chatMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                chatMap.put("participantsStr", documentReference.getId());
                db.collection("chats").document(documentReference.getId()).update(chatMap);
                ChatsModel chatsModel = new ChatsModel();
                chatsModel.setBusinessId(businessId);
                chatsModel.setCustomerId(customerId);
                chatsModel.setParticipantsStr(documentReference.getId());
                callback.OnInitializeChat(chatsModel);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                callback.OnInitializeChat(null);
            }
        });
    }

    public void checkIfChatExists(String customerId, String businessId, final CheckIfChatExist callback){
        Query query = db.collection("chats").whereEqualTo("businessId", businessId)
                .whereEqualTo("customerId", customerId);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Document exists
                        Log.d("Firestore", "Document exists");
                        callback.OnCheckIfChatExist(true);
                    } else {
                        // Document does not exist
                        Log.d("Firestore", "Document does not exist");
                        callback.OnCheckIfChatExist(false);
                    }
                }else{
                    Log.e("Firestore", "Error getting document: " + task.getException());
                    callback.OnCheckIfChatExist(false);
                }
            }
        });
    }

    public void getChatById(String chatId, final GetChatById callback){
        Query query = db.collection("chats").whereEqualTo("participantsStr", chatId);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()){
                        ChatsModel chatsModel = document.toObject(ChatsModel.class);
                        callback.OnGetChatById(chatsModel);
                    }
                }else{
                    callback.OnGetChatById(null);
                }
            }
        });
    }

    public interface GetChatData{
        void OnGetChatData(ChatsModel chatsModel);
    }
    public interface GetCustomerChats{
        void OnGetCustomerChats(ArrayList<ChatsModel> chats);
    }
    public interface GetBusinessChats{
        void OnGetBusinessChats(ArrayList<ChatsModel> chats);
    }
    public interface InitializeChat{
        void OnInitializeChat(ChatsModel chatsModel);
    }
    public interface CheckIfChatExist{
        void OnCheckIfChatExist(boolean exist);
    }
    public interface GetChatById{
        void OnGetChatById(ChatsModel chatsModel);
    }
}
