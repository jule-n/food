package com.jule.food

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ver1(var name: String = "STRING", val details: String)
@Serializable
data class ver1NonOptional(val name: String, val details: String)
@Serializable
data class ver2(val details: String)

fun main() {
//    val first = ver1("first", "details")
    val second = ver2(details = "details3")

//    val firstSerialized = Json.encodeToString(first)
    val secondSerialized = Json.encodeToString(second)

//    println(firstSerialized)
    println(secondSerialized)

    val json = Json { ignoreUnknownKeys = true }

    println("First:")
    val deserialized = json.decodeFromString<ver1>(secondSerialized)
    println(deserialized)
    deserialized.name = "Not string"

    val serializedAgain = json.encodeToString<ver1>(deserialized)
    println(serializedAgain)
    val deserializedAgain = json.decodeFromString<ver1NonOptional>(serializedAgain)
    println(deserializedAgain)


}