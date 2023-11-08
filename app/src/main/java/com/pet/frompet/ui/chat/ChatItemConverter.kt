package com.pet.frompet.ui.chat.utils

import com.pet.frompet.data.model.ChatItem
import com.pet.frompet.data.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ChatItemConverter {

    private val sdf = SimpleDateFormat("yyyy년MM월dd일", Locale.KOREA).apply {
        timeZone = TimeZone.getTimeZone("Asia/Seoul")
    }

    fun convertToChatItems(messages: List<ChatMessage>): List<ChatItem> {
        val chatItems = mutableListOf<ChatItem>()
        var lastDate: String? = null
        val currentDate = sdf.format(Date())

        for (message in messages) {
            val messageDate = sdf.format(Date(message.timestamp))
            if (lastDate == null || messageDate != lastDate) {
                if (messageDate == currentDate) {
                    chatItems.add(ChatItem.DateHeader("오늘"))
                } else {
                    chatItems.add(ChatItem.DateHeader(messageDate))
                }
                lastDate = messageDate
            }
            chatItems.add(ChatItem.MessageItem(message))
        }

        return chatItems
    }
}
