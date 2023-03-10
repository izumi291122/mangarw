package com.example.app_mxh_manga.homePage.component.profile

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.app_mxh_manga.IDUSER
import com.example.app_mxh_manga.R
import com.example.app_mxh_manga.component.GetData
import com.example.app_mxh_manga.component.NumberData
import com.example.app_mxh_manga.component.Int_Uri
import com.example.app_mxh_manga.component.ModeDataSaveSharedPreferences
import com.example.app_mxh_manga.component.adaters.Adapter_LV_iv_string

import com.example.app_mxh_manga.homePage.Activity_homePage
import com.example.app_mxh_manga.homePage.component.profile.component.Activity_EditProfile
import com.example.app_mxh_manga.homePage.component.profile.component.Activity_profile
import com.example.app_mxh_manga.homePage.component.story.Activity_creative_zone
import com.example.app_mxh_manga.login.Activity_Login
import com.example.app_mxh_manga.module.User
import com.example.app_mxh_manga.module.User_Get
import com.example.app_mxh_manga.module.system.Image_String
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*


class Fragment_Profile : Fragment() {
    private lateinit var user: User_Get
    private lateinit var activityHomepage: Activity_homePage
    private lateinit var toolbar: Toolbar
    private lateinit var iv_avt: ImageView
    private lateinit var floatBtn: FloatingActionButton
    private lateinit var listView: ListView
    private lateinit var idUser:String
    private lateinit var tv_name:TextView
    private lateinit var tv_level:TextView
    private lateinit var tv_followers:TextView
    private lateinit var tv_following:TextView
    private lateinit var progressBar: ProgressBar

    val listIv_Str = arrayListOf(
        Image_String(Int_Uri().convertUri(R.drawable.ic_baseline_account_box_40), "H??? s??"),
        Image_String(Int_Uri().convertUri(R.drawable.ic_baseline_monetization_on_40), "N???p ti???n"),
        Image_String(Int_Uri().convertUri(R.drawable.ic_baseline_priority_high_40), "Gi???i thi???u ch??ng t??i"),
        Image_String(Int_Uri().convertUri(R.drawable.ic_baseline_logout_24), "????ng xu???t"),
    )
    init {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        toolbar = view.findViewById(R.id.toolbar)
        iv_avt = view.findViewById(R.id.iv_avt)
        floatBtn = view.findViewById(R.id.float_btn)
        listView = view.findViewById(R.id.listView)
        tv_name = view.findViewById(R.id.tv_name)
        tv_level = view.findViewById(R.id.tv_level)
        tv_followers = view.findViewById(R.id.tv_followers)
        tv_following = view.findViewById(R.id.tv_following)
        progressBar = view.findViewById(R.id.progressBar)
        activityHomepage = activity as Activity_homePage
        progressBar.visibility = View.VISIBLE
        idUser = ModeDataSaveSharedPreferences(activityHomepage).getIdUser()
        GetData().getUserByID(idUser){
            progressBar.visibility = View.GONE
            if (it != null) {
                tv_name.text = it.user.name
                tv_followers.setText("${NumberData().formatInt(it.user.follow_users.size)}")
                tv_following.setText("${NumberData().formatInt(it.user.follow_users.size)}")
                tv_level.setText("LV ${NumberData().formatLevel(it.user.score)}")
                progressBar.visibility = View.VISIBLE
                GetData().getImage(it.user.uri_avt){ uri ->
                    progressBar.visibility = View.GONE
                    if (uri != null){
                        Picasso.with(activityHomepage).load(uri).into(iv_avt)
                    }
                }
                addEvent()
            }else{
                Toast.makeText(activityHomepage, "M???t k???t n???i!", Toast.LENGTH_SHORT).show()
            }
        }
        listView.adapter = Adapter_LV_iv_string(activityHomepage, listIv_Str)

        return view
    }

    private fun addEvent() {
        floatBtn.setOnClickListener {
            startActivity(Intent(activityHomepage, Activity_creative_zone::class.java))
        }
        iv_avt.setOnClickListener {
            val i = Intent(activityHomepage, Activity_profile::class.java)
            val bundle = Bundle()
            bundle.putString(IDUSER, idUser)
            i.putExtras(bundle)
            startActivity(i)
        }
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.item_edtProfile -> {
                    startActivity(Intent(context, Activity_EditProfile::class.java))
                }
                R.id.item_setting -> {

                }
            }
            true
        }
        listView.setOnItemClickListener { parent, view, position, id ->
            when(position){
                0 -> {
                    val i = Intent(activityHomepage, Activity_profile::class.java)
                    val bundle = Bundle()
                    bundle.putString(IDUSER, idUser)
                    i.putExtras(bundle)
                    startActivity(i)
                }
                1 -> {

                }
                2 -> {

                }
                3 -> {
                    // d??ng xu???t
                    ModeDataSaveSharedPreferences(activityHomepage).logout()
                    startActivity(Intent(context, Activity_Login::class.java))
                    activityHomepage.finish()
                }
            }
        }


    }

}