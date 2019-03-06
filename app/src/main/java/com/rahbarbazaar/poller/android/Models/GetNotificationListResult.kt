package com.rahbarbazaar.poller.android.Models

class GetNotificationListResult {

    var unread: Int = 0
    var messages: List<Messages>? = null

    class Messages {

        var id: Int = 0
        var title: String? = null
        var content: String? = null
        var active: String? = null
        var created_at: String? = null
        var updated_at: String? = null
        var pivot: Pivot? = null

        class Pivot {

            var user_id: Int = 0
            var message_id: Int = 0
            var status: String? = null
            var created_at: String? = null
            var updated_at: String? = null
        }
    }
}
