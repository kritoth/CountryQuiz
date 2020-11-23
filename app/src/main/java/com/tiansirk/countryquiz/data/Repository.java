package com.tiansirk.countryquiz.data;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiansirk.countryquiz.model.Identifiable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import timber.log.Timber;

public class Repository<TEntity extends Identifiable<String>> {

    public interface EntityChangeListener{
        void onEvent(DocumentSnapshot documentSnapshot);
    }
    private EntityChangeListener listener;

    private Activity activity;
    private final Class<TEntity> entityClass;

    private final CollectionReference collectionReference;
    private final String collectionName;

    /**
     * Initializes the repository storing the data in the given collection.
     * {@param FirebaseFirestore#collection(String)}.
     */
    public Repository(Activity activity, Class<TEntity> entityClass, String collectionName) {
        this.activity = activity;
        this.collectionName = collectionName;
        this.entityClass = entityClass;

        Timber.d("Initializing FireStore");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.collectionReference = db.collection(this.collectionName);
        Timber.d("FireStore initialized");
    }

    /** READ */
    public Task<Boolean> exists(final String documentId) {
        DocumentReference documentReference = collectionReference.document(documentId);
        Timber.i("Checking existence of '" + documentId + "' in '" + collectionName + "'.");

        return documentReference.get().continueWith(new Continuation<DocumentSnapshot, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<DocumentSnapshot> task) {
                Timber.d("Checking if '" + documentId + "' exists in '" + collectionName +"'.");
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

    public void listenToChanges(TEntity entity){
        final String documentId = entity.getEntityKey();
        collectionReference.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Timber.e(error);
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    DocumentSnapshot documentSnapshot = dc.getDocument();
                    listener.onEvent(documentSnapshot);
                }
            }
        });
    }

    /** CREATE */
    public Task<Void> create(TEntity entity) {
        final String documentId = entity.getEntityKey();
        DocumentReference documentReference = collectionReference.document(documentId);
        Timber.i( "Creating '" + documentId + "' in '" + collectionName + "'.");
        return documentReference.set(entity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e, "There was an error creating '" + documentId + "' in '" + collectionName + "'!");
            }
        });
    }

    /** UPDATE */
    public Task<Void> update(TEntity entity) {
        final String documentId = entity.getEntityKey();
        DocumentReference documentReference = collectionReference.document(documentId);
        Timber.i( "Updating '" + documentId + "' in '" + collectionName + "'.");

        return documentReference.set(entity).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e, "There was an error updating '" + documentId + "' in '" + collectionName + "'.");
            }
        });
    }

    /** DELETE */
    public Task<Void> delete(final String documentId) {
        DocumentReference documentReference = collectionReference.document(documentId);
        Timber.i( "Deleting '" + documentId + "' in '" + collectionName + "'.");

        return documentReference.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e, "There was an error deleting '" + documentId + "' in '" + collectionName + "'.");
            }
        });
    }



}
