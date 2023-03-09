package com.example.app_mxh_manga.homePage.component.profile.component

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.app_mxh_manga.R
import com.example.app_mxh_manga.component.ModeDataSaveSharedPreferences
import com.example.app_mxh_manga.component.OnItemClick
import com.example.app_mxh_manga.module.Posts
import java.util.Calendar

class Activity_NewPost : AppCompatActivity() {
    private lateinit var rv_uri: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var edt_content : EditText
    private lateinit var tv_countImg: TextView
    private lateinit var ib_addUri: ImageButton
    private lateinit var startGallery_images: ActivityResultLauncher<Intent>

    private var id_user: Int = 0
    private var listUri = ArrayList<Uri>()
    private lateinit var adapterRvImgClose: Adapter_RV_img_close


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newpost)
        toolbar = findViewById(R.id.toolbar)
        rv_uri = findViewById(R.id.rv_img)
        edt_content = findViewById(R.id.edt_content)
        tv_countImg = findViewById(R.id.tv_countIv)
        ib_addUri = findViewById(R.id.ib_addUri)
        id_user = ModeDataSaveSharedPreferences(this).getIdUser()

        adapterRvImgClose = Adapter_RV_img_close(listUri, object : OnItemClick{
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemClick(position: Int) {
                listUri.removeAt(position)
                adapterRvImgClose.notifyDataSetChanged()
                tv_countImg.setText("${listUri.size}/20")
            }
        })
        rv_uri.adapter = adapterRvImgClose
        startGallery_images = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                if (result.data!!.clipData != null) {
                    // Get multiple images
                    for (i in 0 until result.data!!.clipData!!.itemCount) {
                        val imageUri = result.data!!.clipData!!.getItemAt(i).uri
                        // Do something with imageUri
                        listUri.add(imageUri)
                    }
                } else {
                    // Get single image
                    val imageUri = result.data!!.data
//                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    // Do something with imageUri
                    if (imageUri!=null){
                        listUri.add(imageUri)
                    }
                }
                tv_countImg.setText("${listUri.size}/20")
                adapterRvImgClose.notifyDataSetChanged()
            }
        }

        addEvent()
    }

    private fun addEvent() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.check -> {
                    // thêm vào csdl
                    val post = Posts(0,id_user, edt_content.text.toString(), Calendar.getInstance().time)

                    finish()
                }
            }
            true
        }
        ib_addUri.setOnClickListener {
            if (listUri.size > 20){
                ib_addUri.setImageResource(R.drawable.ic_baseline_hide_image_40)
            }else{
                ib_addUri.setImageResource(R.drawable.ic_baseline_image_40)
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                startGallery_images.launch(intent)
            }
        }
    }
}