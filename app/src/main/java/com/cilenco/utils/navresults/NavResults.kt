package com.cilenco.utils.navresults

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.navigation.fragment.findNavController

interface Converter<T>: Serializer<T>, Deserializer<T> { val key: String }

fun interface Deserializer<T> {  fun fromBundle(bundle: Bundle): T }
fun interface Serializer<T> { fun toBundle(value: T): Bundle }

fun <T: Parcelable> Fragment.setNavResult(key: String, value: T?) {
    setNavResult(key, value) { Bundle().apply { if(value != null) putParcelable(key, value) } }
}

fun <T> Fragment.setNavResult(value: T?, converter: Converter<T>) {
    setNavResult(converter.key, value) { if(it != null) converter.toBundle(it) else Bundle() }
}

fun <T> Fragment.setNavResult(key: String, value: T, serializer: Serializer<T>) {
    val bundle = if(value != null) serializer.toBundle(value) else Bundle()
    val previousBackStackEntry = findNavController().previousBackStackEntry
    previousBackStackEntry?.savedStateHandle?.set(key, bundle)
}

fun <T: Parcelable> Fragment.getNavResult(key: String): LiveData<T> {
    return getNavResult(key) { it.getParcelable<T>(key)!! }
}

fun <T> Fragment.getNavResult(converter: Converter<T>): LiveData<T> {
    return getNavResult(converter.key) { converter.fromBundle(it) }
}

fun <T> Fragment.getNavResult(key: String, deserializer: Deserializer<T>): LiveData<T> {
    val out = MediatorLiveData<T>()

    val navController = findNavController()
    val navId = navController.currentDestination!!.id

    val navBackStackEntry = navController.getBackStackEntry(navId)
    val savedStateHandle = navBackStackEntry.savedStateHandle

    out.addSource(savedStateHandle.getLiveData<Bundle>(key)) {
        if(it != null) {
            out.value = deserializer.fromBundle(it)
            savedStateHandle.set(key, null)
        }
    }

    return out
}