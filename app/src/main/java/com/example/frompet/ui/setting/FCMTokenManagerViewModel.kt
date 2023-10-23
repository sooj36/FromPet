import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class FCMTokenManagerViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private val fcm = FirebaseMessaging.getInstance()

    private val currentUserId: String? get() = auth.currentUser?.uid
    private val fcmTokensRef = database.child("fcmToken")

    // FCM 토큰을 저장하는 함수
    private fun saveFCMTokenForUser(userId: String, fcmToken: String) {
        fcmTokensRef.child(userId).setValue(fcmToken)
            .addOnSuccessListener {
                println("FCM 토큰이 성공적으로 저장되었습니다.")
            }
            .addOnFailureListener {
                println("FCM 토큰 저장 중 오류 발생: $it")
            }
    }



    // FCM 토큰을 가져오고 저장하는 함수
    fun retrieveAndSaveFCMToken() {
        fcm.token.addOnSuccessListener { token ->
            currentUserId?.let { uid ->
                saveFCMTokenForUser(uid, token)
            }
        }
    }

    // FCM 토큰을 제거하는 함수
    fun removeFCMToken(userId: String) {
        fcmTokensRef.child(userId).removeValue()
            .addOnCompleteListener(OnCompleteListener<Void> { task ->
                if (task.isSuccessful) {
                    println("FCM 토큰이 성공적으로 제거되었습니다.")
                } else {
                    println("FCM 토큰 제거 중 오류 발생: ${task.exception}")
                }
            })
    }
    fun getFirebaseToken() {
        // 비동기 방식 (추천)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.d(TAG, "token=$token")
            // 토큰을 사용하여 원하는 작업을 수행
        }
    }
}