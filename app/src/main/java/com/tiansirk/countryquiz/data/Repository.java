package com.tiansirk.countryquiz.data;

import android.content.Context;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tiansirk.countryquiz.model.Country;

public class Repository {

    private static final String TAG = Repository.class.getSimpleName();
    FirebaseFirestore firestoreDb;
    CollectionReference allUsers;
    DocumentReference user;

    private Context activityContext;
    private Country[] mCountries;

    public Repository(Context activityContext, FirebaseFirestore firebaseFirestore) {
        this.activityContext = activityContext;
        firestoreDb = firebaseFirestore;
    }

    /** CREATE */


    /** READ */
    public void loadUserData(String userName){
        user = firestoreDb.document(userName);

    }


    /** UPDATE */


    /** DELETE */

}
