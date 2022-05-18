package com.example.mediaplayer.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.viewbinding.ViewBinding
import com.example.presentation.base.BaseViewModel

abstract class BaseFragment< ViewBind : ViewBinding,ViewModel : BaseViewModel,>() : Fragment() {
    abstract val viewModel: ViewModel
    protected lateinit var binding: ViewBinding
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBind

    protected open lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
     binding = bindingInflater(inflater, container, false)
      //  binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onEach()
        onView()
        onViewClick()
    }

    protected open fun onView() {}

    protected open fun onViewClick() {}

    protected open fun onEach() {}

    protected fun navigateFragment(destinations: NavDirections) {
        navController.navigate(destinations)
    }
    protected fun popBackStack() {
        navController.popBackStack()
    }

    protected fun navigateFragment(destinationId: Int, arg: Bundle? = null) {
        navController.navigate(destinationId, arg)
    }
}