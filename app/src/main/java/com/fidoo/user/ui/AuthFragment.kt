package com.fidoo.user.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.fidoo.user.R

class AuthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val activity = context as Activity
        val layout = activity.findViewById<ConstraintLayout>(R.id.fidooTxtLayout)
        layout.visibility = View.VISIBLE

    }
}