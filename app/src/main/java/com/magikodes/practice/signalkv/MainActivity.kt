package com.magikodes.practice.signalkv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.magikodes.practice.signalkv.keyvalue.SignalKvStore

class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindState()
    }

    private fun bindState() {
        editText = findViewById(R.id.edit_text)

        editText.addTextChangedListener(
            afterTextChanged = { s ->
                val typed = s.toString().trim()
                SignalKvStore.misc().userTypedValue = typed
            }
        )
    }

    override fun onStart() {
        super.onStart()
        if (this::editText.isInitialized) {
            editText.setText(SignalKvStore.misc().userTypedValue)
        }
    }

}