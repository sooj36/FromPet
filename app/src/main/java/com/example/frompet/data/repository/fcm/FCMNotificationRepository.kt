package com.example.frompet.data.repository.fcm

import com.example.frompet.data.model.NotificationRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


//FCM 알림 서비스를 위한  retrofit 인터페이스
interface NotificationService {
    //HTTP헤더 설정
    //FCM 키포함
    //content Type 헤더는 json형식의 데이터를 보내는것 표시
    //어노테이션으로 코드 간결화,명확성

    @Headers("Authorization:key= AAAA6d1F6-w:APA91bE57D7cO9D16KqLD-PR0dUyNObqLJKN-D_n0oMvGhglXJqYKvsuCH7pR4tU2CQA8-N-9irh-kPZOnccQ9q5RCZweoEtWQk4cXkQyIOxrv8nPX59nRnp3ByObpgHWmeIXK5CeBUs",
        "Content-Type:application/json")

    //FCM 서버에 알림을 보내는 Post요청정의
    @POST("fcm/send")
    //응답은 responseBOdy형식으로 반환
    suspend  fun sendNotification(@Body body: NotificationRequest): Response<ResponseBody>
} //애플폰트,유저디테일 아이콘 개별로,네비게이션바 아이콘개별로,모든아이콘다별로,셋팅에 쫌진한 블랙(생블랙은아님)