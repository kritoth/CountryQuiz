package com.tiansirk.countryquiz.data;

import android.content.Context;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tiansirk.countryquiz.model.Identifiable;

import androidx.annotation.NonNull;
import timber.log.Timber;

public class Repository<TEntity extends Identifiable<String>> {

    private Context activityContext;
    private final Class<TEntity> entityClass;

    private final CollectionReference collectionReference;
    private final String collectionName;

    /**
     * Initializes the repository storing the data in the given collection.
     * {@param FirebaseFirestore#collection(String)}.
     */
    public Repository(Context activityContext, Class<TEntity> entityClass, String collectionName) {
        this.activityContext = activityContext;
        this.collectionName = collectionName;
        this.entityClass = entityClass;

        Timber.d("Initializing FireStore");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.collectionReference = db.collection(this.collectionName);
        Timber.d("FireStore initialized");
    }

    /** READ */
    public Task<Boolean> exists(final String documentName) {
        DocumentReference documentReference = collectionReference.document(documentName);
        Timber.i("Checking existence of '" + documentName + "' in '" + collectionName + "'.");

        return documentReference.get().continueWith(new Continuation<DocumentSnapshot, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<DocumentSnapshot> task) {
                Timber.d("Checking if '" + documentName + "' exists in '" + collectionName +"'.");
                return task.getResult().exists();
            }
        });
    }

    public Task<TEntity> get(String id) {
        final String documentName = id;
        DocumentReference documentReference = collectionReference.document(documentName);
        Timber.i( "Getting '" + documentName + "' in '" + collectionName + "'.");

        return documentReference.get().continueWith(new Continuation<DocumentSnapshot, TEntity>() {
            @Override
            public TEntity then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    return documentSnapshot.toObject(entityClass);
                } else {
                    Timber.d( "Document '" + documentName + "' does not exist in '" + collectionName + "'.");
                    return entityClass.newInstance();
                }
            }
        });
    }

    /** CREATE */
    public Task<Void> create(TEntity entity) {
        final String documentName = entity.getEntityKey();
        DocumentReference documentReference = collectionReference.document(documentName);
        Timber.i( "Creating '" + documentName + "' in '" + collectionName + "'.");
        return documentReference.set(entity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e, "There was an error creating '" + documentName + "' in '" + collectionName + "'!");
            }
        });
    }

    /** UPDATE */
    public Task<Void> update(TEntity entity) {
        final String documentName = entity.getEntityKey();
        DocumentReference documentReference = collectionReference.document(documentName);
        Timber.i( "Updating '" + documentName + "' in '" + collectionName + "'.");

        return documentReference.set(entity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e, "There was an error updating '" + documentName + "' in '" + collectionName + "'.");
            }
        });
    }

    /** DELETE */
    public Task<Void> delete(final String documentName) {
        DocumentReference documentReference = collectionReference.document(documentName);
        Timber.i( "Deleting '" + documentName + "' in '" + collectionName + "'.");

        return documentReference.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e, "There was an error deleting '" + documentName + "' in '" + collectionName + "'.");
            }
        });
    }



}
