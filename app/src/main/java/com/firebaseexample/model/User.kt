package com.firebaseexample.model

class User {

    var uid: String? = null
    var name: kotlin.String? = null
    var phoneNumber: kotlin.String? = null
    var profileImage: kotlin.String? = null

    constructor() {}

    constructor(uid: String?, name: String, phoneNumber: String, profileImage: String) {
        this.uid = uid
        this.name = name
        this.phoneNumber = phoneNumber
        this.profileImage = profileImage
    }
}