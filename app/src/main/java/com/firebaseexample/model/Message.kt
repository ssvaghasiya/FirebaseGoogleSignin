package com.firebaseexample.model

class Message {
    var messageId: String? = null
    var message: kotlin.String? = null
    var senderId: kotlin.String? = null
    var timestamp: Long = 0
    var feeling = -1

    constructor() {}

    constructor(message: String, senderId: String, timestamp: Long) {
        this.message = message
        this.senderId = senderId
        this.timestamp = timestamp
    }
}