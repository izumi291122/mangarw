package com.example.app_mxh_manga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatDelegate
import com.example.app_mxh_manga.component.GetData
import com.example.app_mxh_manga.component.ModeDataSaveSharedPreferences
import com.example.app_mxh_manga.homePage.Activity_homePage
import com.example.app_mxh_manga.login.Activity_Login
import com.example.app_mxh_manga.module.User
import kotlinx.coroutines.*

const val IDUSER = "id_user"

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(ModeDataSaveSharedPreferences(this).themersApp())
        setContentView(R.layout.activity_main)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val id_user = ModeDataSaveSharedPreferences(this).getIdUser()
        progressBar.visibility = View.VISIBLE
        GetData().getUserByID(id_user){
            progressBar.visibility = View.GONE
            if (it!=null && it.id_user == id_user ){
                startActivity(Intent(this@MainActivity, Activity_homePage::class.java))
                finish()
            }else{
                startActivity(Intent(this@MainActivity, Activity_Login::class.java))
                finish()
            }
        }



    }
}