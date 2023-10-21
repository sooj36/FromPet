import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class FCMTokenManagerViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private val fcm = FirebaseMessaging.getInstance()

    // 현재 사용자의 UID
    private val currentUserId: String? = auth.currentUser?.uid

    // 사용자별 FCM 토큰을 저장할 노드
    private val fcmTokensRef = database.child("fcmTokens")

    // FCM 토큰을 저장하는 함수
    fun saveFCMToken(fcmToken: String) {
        val uid = currentUserId ?: return
        fcmTokensRef.child(uid).setValue(fcmToken)
            .addOnSuccessListener {
                // 성공적으로 저장된 경우
                println("FCM 토큰이 성공적으로 저장되었습니다.")
            }
            .addOnFailureListener {
                // 저장 중 오류가 발생한 경우
                println("FCM 토큰 저장 중 오류 발생: $it")
            }
    }

    // 사용자가 로그인한 후 FCM 토큰을 저장
    fun saveUserFCMToken(fcmToken: String) {
        // 현재 사용자의 UID가 존재할 때만 저장
        currentUserId?.let { uid ->
            // 실제 사용자의 UID와 FCM 토큰으로 저장
            saveFCMToken(fcmToken)
        }
    }

    // FCM 토큰을 가져오고 저장하는 함수
    fun retrieveAndSaveFCMToken() {
        // FCM 토큰을 비동기적으로 가져옴
        fcm.token.addOnSuccessListener { token ->
            // FCM 토큰을 가져온 후 저장
            saveUserFCMToken(token)
        }
    }
    fun removeFCMToken(userId: String) {
        // 특정 사용자의 FCM 토큰을 Firebase Realtime Database에서 제거
        fcmTokensRef.child(userId).removeValue()
            .addOnCompleteListener(OnCompleteListener<Void> { task ->
                if (task.isSuccessful) {
                    println("FCM 토큰이 성공적으로 제거되었습니다.")
                } else {
                    println("FCM 토큰 제거 중 오류 발생: ${task.exception}")
                }
            })
    }
}