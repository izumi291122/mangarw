package com.example.app_mxh_manga.homePage.component.profile.component

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.app_mxh_manga.R
import com.example.app_mxh_manga.component.GetData_id
import com.example.app_mxh_manga.component.GetNumberData
import com.example.app_mxh_manga.component.Int_Uri
import com.example.app_mxh_manga.component.ModeDataSaveSharedPreferences
import com.example.app_mxh_manga.component.adaters.Adapter_LV_iv_string
import com.example.app_mxh_manga.component.adaters.Adapter_VP2_ListFragment
import com.example.app_mxh_manga.homePage.component.common.Fragment_LV_Story
import com.example.app_mxh_manga.homePage.component.common.Fragment_RV_Post
import com.example.app_mxh_manga.homePage.component.common.showStory.Fragment_RV_Story
import com.example.app_mxh_manga.homePage.component.story.Activity_creative_zone
import com.example.app_mxh_manga.module.User
import com.example.app_mxh_manga.module.system.Image_String
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Activity_profile : AppCompatActivity() {
    private lateinit var user: User
    private var number_followers = "" // người theo dõi
    private var number_following = "" // đang theo dõi

    private lateinit var toolbar: Toolbar
    private lateinit var iv_cover: ImageView
    private lateinit var iv_avt: ImageView
    private lateinit var tv_name: TextView
    private lateinit var tv_desc : TextView
    private lateinit var tv_level: TextView
    private lateinit var tv_sex: TextView
    private lateinit var tv_followers: TextView
    private lateinit var tv_following: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var floatBtn: FloatingActionButton
    private lateinit var btn_follow: Button

    private var checkPage = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val bundle = intent.extras
        if (bundle != null) {
            val idUser = bundle.getInt("id_user")
            user = GetData_id().getUser(idUser)
        }
        val idUser = ModeDataSaveSharedPreferences(this).getIdUser()

        number_followers = GetNumberData().numFollowers(user.id_user)
        number_following = GetNumberData().numFollowing(user.id_user)

        toolbar = findViewById(R.id.toolbar)
        iv_cover = findViewById(R.id.iv_cover)
        iv_avt = findViewById(R.id.iv_avt)
        tv_name = findViewById(R.id.tv_name)
        tv_desc  = findViewById(R.id.tv_desc)
        tv_level = findViewById(R.id.tv_level)
        tv_sex = findViewById(R.id.tv_sex)
        tv_followers = findViewById(R.id.tv_followers)
        tv_following = findViewById(R.id.tv_following)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager2 = findViewById(R.id.viewPage2)
        floatBtn = findViewById(R.id.float_btn)
        btn_follow = findViewById(R.id.btn_follow)



        //
        iv_cover.setImageURI(user.uri_cover)
        iv_avt.setImageURI(user.uri_avt)
        tv_name.setText(user.name)
        tv_desc.setText("Tiểu sử: "+user.story)
        tv_level.setText("lv ${GetNumberData().formatLevel(user.score)}")
        if (user.sex){
            tv_sex.setText("Nam")
        }else{
            tv_sex.setText("Nu")
        }
        tv_followers.setText("${number_followers}")
        tv_following.setText("${number_following}")



        val listFragment = ArrayList<Fragment>()
        listFragment.add(Fragment_RV_Post.newInstance(user.id_user))
        listFragment.add(Fragment_RV_Story.newInstance(GetData_id().getListStory_user(user.id_user)))
        viewPager2.adapter = Adapter_VP2_ListFragment(this, listFragment)

        val tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2){ tab, i ->
            when(i){
                0 -> {
                    tab.setText("Bảng tin")
                }
                1 -> {
                    tab.setText("Tác phẩm")
                }
            }
            true
        }
        tabLayoutMediator.attach()

        if (user.id_user == idUser){
            profile_me()
        }else{
            profile_other()
        }
        addEvent()
    }


    private fun profile_other(){
        floatBtn.visibility = View.GONE
        btn_follow.visibility = View.VISIBLE
        iv_avt.setOnClickListener {
            //
        }

    }
    private fun profile_me(){
        floatBtn.visibility = View.VISIBLE
        btn_follow.visibility = View.GONE
        iv_avt.setOnClickListener {
            val listIv_Str = listOf(
                Image_String(Int_Uri().convertUri(R.drawable.ic_baseline_account_box_40), "Xem ảnh đại diện"),
                Image_String(Int_Uri().convertUri(R.drawable.ic_baseline_photo_library_40), "Chọn ảnh đại diện")
            )
            this.setTheme(R.style.Theme_transparent)
            val bottomSheet = BottomSheetDialog(this)
            bottomSheet.setContentView(R.layout.layout_bottom_sheeet_listview)
            val listView = bottomSheet.findViewById<ListView>(R.id.listView)
            if (listView != null) {
                listView.adapter = Adapter_LV_iv_string(bottomSheet.context,  listIv_Str )
                listView.setOnItemClickListener { parent, view, position, id ->
                    Toast.makeText(this, listIv_Str[position].str, Toast.LENGTH_SHORT).show()
                }
            }

            bottomSheet.show()
        }
        floatBtn.setOnClickListener {
            if (checkPage){
                startActivity(Intent(this, Activity_NewPost::class.java))
            }else{
                startActivity(Intent(this, Activity_creative_zone::class.java))
            }
        }
    }

    private fun addEvent() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                when(position){
                    0 -> {
                        floatBtn.setImageResource(R.drawable.ic_baseline_add_40_white)
                        checkPage = true
                    }
                    1 -> {
                        floatBtn.setImageResource(R.drawable.ic_baseline_draw_40)
                        checkPage = false
                    }
                }

            }

        })




    }
}