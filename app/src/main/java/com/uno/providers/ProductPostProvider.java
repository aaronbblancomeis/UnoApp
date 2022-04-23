package com.uno.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uno.models.Product;

public class ProductPostProvider {

    CollectionReference mColletion;

    public ProductPostProvider() {
        mColletion = FirebaseFirestore.getInstance().collection("Products");
    }

    public Task<Void> save(Product product) {
        return mColletion.document().set(product);
    }
}
