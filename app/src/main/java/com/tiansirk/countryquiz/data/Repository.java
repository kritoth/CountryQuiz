package com.tiansirk.countryquiz.data;

import android.app.Activity;

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
import com.google.firebase.firestore.WriteBatch;
import com.tiansirk.countryquiz.model.Identifiable;
import com.tiansirk.countryquiz.model.Level;

import java.util.List;

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

    private final FirebaseFirestore db;
    private final CollectionReference collectionReference;
    private final String collectionName;
    private final String LEVELS_SUBCOLLECTION_NAME = "levels";

    /**
     * Initializes the repository storing the data in the given collection.
     * {@param FirebaseFirestore#collection(String)}.
     */
    public Repository(Activity activity, Class<TEntity> entityClass, String collectionName) {
        this.activity = activity;
        this.collectionName = collectionName;
        this.entityClass = entityClass;

        Timber.d("Initializing FireStore");
        this.db = FirebaseFirestore.getInstance();
        this.collectionReference = this.db.collection(this.collectionName);
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
                    Timber.i("returning user");
                    return documentSnapshot.toObject(entityClass);
                } else {
                    Timber.d( "Document '" + documentName + "' does not exist in '" + collectionName + "'.");
                    return entityClass.newInstance();
                }
            }
        });
    }

    public Task<QuerySnapshot> getAllLevels(String id){
        final String documentName = id;
        DocumentReference documentReference = collectionReference.document(documentName);
        Timber.i( "Getting levels of " + documentName + ".");

        return documentReference.collection(LEVELS_SUBCOLLECTION_NAME).get().continueWith(new Continuation<QuerySnapshot, QuerySnapshot>() {
            @Override
            public QuerySnapshot then(@NonNull Task<QuerySnapshot> task) throws Exception {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot.isEmpty()) {
                    Timber.d( "Levels DocumentSnapshot does not exist.");
                    return null;
                } else {
                    Timber.i("Returning levels documentSnapshot");
                    return querySnapshot;
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
        return documentReference.set(entity).addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e, "There was an error creating '" + documentId + "' in '" + collectionName + "'!");
            }
        });
    }

    public Task<Void> saveLevels(String parentDocumentId, List<? extends TEntity> entities) {
        WriteBatch batch = db.batch();
        DocumentReference userDocumentReference = collectionReference.document(parentDocumentId);

        for(TEntity tEntity : entities){
            if(Level.class.isInstance(tEntity)){
                ((Level)tEntity).setUserId(parentDocumentId);
            }
            DocumentReference levelDocumentReference = userDocumentReference.collection(LEVELS_SUBCOLLECTION_NAME).document();
            batch.set(levelDocumentReference, tEntity);
        }
        return batch.commit().addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e, "There was an error creating levels subcollection!");
            }
        });
    }

    public Task<Void> saveLevel(String userId, TEntity entity) {
        ((Level)entity).setUserId(userId);
        DocumentReference userDocumentReference = collectionReference.document(userId);
        DocumentReference levelDocumentReference = userDocumentReference.collection(LEVELS_SUBCOLLECTION_NAME).document();
        final String levelDocId = levelDocumentReference.getId();
        Timber.i( "Creating level '" + levelDocId + "' in '" + LEVELS_SUBCOLLECTION_NAME + "'.");
        return levelDocumentReference.set(entity).addOnFailureListener(activity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Timber.e(e, "There was an error creating " + levelDocId + " levels subcollection!");
                }
            });
    }

    /** UPDATE */
    public Task<Void> update(TEntity entity) {
        final String documentId = entity.getEntityKey();
        DocumentReference documentReference = collectionReference.document(documentId);
        Timber.i( "Updating '" + documentId + "' in '" + collectionName + "'.");

        return documentReference.set(entity).addOnFailureListener(activity, new OnFailureListener() {
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

        return documentReference.delete().addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e, "There was an error deleting '" + documentId + "' in '" + collectionName + "'.");
            }
        });
    }



}
