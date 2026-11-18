package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    suspend fun syncSignedInUserProfile(
        user: FirebaseUser,
        preferredLanguage: NainaLanguage
    ): Result<Unit> {
        val uid = user.uid
        val docPath = "users/$uid"
        return try {
            val currentUid = requireUserId()
            check(currentUid == uid) { "Authenticated user mismatch." }
            val docRef = db.collection("users").document(uid)
            val existingSnapshot = docRef.get().await()

            val safeName = (user.displayName ?: "Naina User").take(100)
            val safeEmail = (user.email ?: "").take(200)
            val safePhoto = user.photoUrl?.toString()?.take(500)

            if (existingSnapshot.exists()) {
                val updateMap = mutableMapOf<String, Any>(
                    "displayName" to safeName,
                    "email" to safeEmail,
                    "preferredLanguage" to preferredLanguage.name,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                if (!safePhoto.isNullOrBlank()) {
                    updateMap["photoUrl"] = safePhoto
                }
                docRef.update(updateMap).await()
            } else {
                val createMap = mutableMapOf<String, Any>(
                    "userId" to uid,
                    "displayName" to safeName,
                    "email" to safeEmail,
                    "preferredLanguage" to preferredLanguage.name,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                if (!safePhoto.isNullOrBlank()) {
                    createMap["photoUrl"] = safePhoto
                }
                docRef.set(createMap).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updatePreferredLanguage(userId: String, language: NainaLanguage): Result<Unit> {
        val docPath = "users/$userId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch." }
            val docRef = db.collection("users").document(userId)
            if (docRef.get().await().exists()) {
                docRef.update(
                    mapOf(
                        "preferredLanguage" to language.name,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> =
        db.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(
                        UserProfile::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                } else {
                    null
                }
            }

    fun observeSavedCreations(userId: String): Flow<List<SavedCreation>> =
        db.collection("users").document(userId).collection("creations")
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.toObjects(
                    SavedCreation::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }

    suspend fun saveCreation(
        userId: String,
        kind: CreationKind,
        title: String,
        prompt: String,
        content: String
    ): Result<String> {
        var docPath = "users/$userId/creations"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch." }
            val safeTitle = title.trim().ifBlank { kind.label }.take(120)
            val safePrompt = prompt.trim().ifBlank { safeTitle }.take(1000)
            val safeContent = content.trim().take(100_000)
            require(safeContent.isNotEmpty()) { "Generated content cannot be empty." }

            val docRef = db.collection("users").document(userId)
                .collection("creations").document()
            docPath = docRef.path

            val payload = mapOf(
                "userId" to userId,
                "kind" to kind.name,
                "title" to safeTitle,
                "prompt" to safePrompt,
                "content" to safeContent,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteCreation(userId: String, creationId: String): Result<Unit> {
        val docPath = "users/$userId/creations/$creationId"
        return try {
            val uid = requireUserId()
            check(uid == userId) { "Signed-in user mismatch." }
            require(creationId.isNotBlank()) { "Creation ID cannot be blank." }
            db.collection("users").document(userId)
                .collection("creations").document(creationId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }
}
