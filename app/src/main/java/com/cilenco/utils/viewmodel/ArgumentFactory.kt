package com.cilenco.utils.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ArgumentFactory(private val app: Application, private val args: Bundle): ViewModelProvider.AndroidViewModelFactory(app) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(AndroidViewModel::class.java.isAssignableFrom(modelClass)) return createAndroidViewModel(modelClass)
        else if(ViewModel::class.java.isAssignableFrom(modelClass)) return createViewModel(modelClass)

        throw InstantiationError("Unsupported ViewModel class ${modelClass.simpleName}")
    }

    private fun <T : ViewModel?> createAndroidViewModel(modelClass: Class<T>): T {
        try { return modelClass.getConstructor(Application::class.java, Bundle::class.java).newInstance(app, args) }
        catch(e: Exception) { throw InstantiationError("${modelClass.simpleName} has no (Application, Bundle) constructor") }
    }

    private fun <T : ViewModel?> createViewModel(modelClass: Class<T>): T {
        try { return modelClass.getConstructor(Bundle::class.java).newInstance(args) }
        catch(e: Exception) { throw InstantiationError("${modelClass.simpleName} has no Bundle constructor") }
    }
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.argViewModels(): Lazy<VM> {
    return viewModels { ArgumentFactory(requireActivity().application, requireArguments()) }
}