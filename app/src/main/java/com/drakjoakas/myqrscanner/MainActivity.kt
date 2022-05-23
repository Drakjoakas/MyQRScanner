package com.drakjoakas.myqrscanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.drakjoakas.myqrscanner.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnClickListener()

    }

    private fun setOnClickListener() {
        binding.btnScan.setOnClickListener { performAction()}
    }

    private fun performAction() {
        //Codigo para cuando se presiona el botón
        startActivity(Intent(this,QR::class.java))
    }




}