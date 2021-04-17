package com.fidoo.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fidoo.user.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private  lateinit var loginBinding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

    }
}