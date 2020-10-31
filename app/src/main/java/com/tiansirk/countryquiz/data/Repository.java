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

    public Repository(Context activityContext) {
        this.activityContext = activityContext;
        firestoreDb = FirebaseFirestore.getInstance();;
    }

    /** CREATE */


    /** READ */
    public void loadUserData(String userName){
        user = firestoreDb.document(userName);

    }

    public void loadGame(){

    }


    /** UPDATE */


    /** DELETE */

}
