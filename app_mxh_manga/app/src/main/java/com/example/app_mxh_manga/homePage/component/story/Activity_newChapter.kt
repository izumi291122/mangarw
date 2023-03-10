package com.example.app_mxh_manga.homePage.component.story

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.app_mxh_manga.R
import com.example.app_mxh_manga.component.AddData
import com.example.app_mxh_manga.component.GetData
import com.example.app_mxh_manga.component.OnItemClick
import com.example.app_mxh_manga.component.UpdateData
import com.example.app_mxh_manga.module.Chapter
import com.example.app_mxh_manga.module.Posts
import java.text.SimpleDateFormat
import java.util.Calendar

class Activity_newChapter : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var edt_title: EditText
    private lateinit var edt_content: EditText
    private lateinit var rv_content: RecyclerView
    private lateinit var ib_add: ImageButton
    private lateinit var ib_save: ImageButton
    private lateinit var id_story: String
    private var type: Boolean = true
    private var listUri: ArrayList<Uri> = ArrayList()
    private lateinit var startGallery: ActivityResultLauncher<Intent>
    private lateinit var adapterNewimagechapter: Adapter_NewImageChapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chapter)
        toolbar = findViewById(R.id.toolbar)
        edt_title = findViewById(R.id.edt_title)
        edt_content = findViewById(R.id.edt_content)
        rv_content = findViewById(R.id.rv_content)
        ib_add = findViewById(R.id.ib_addImg)
        ib_save = findViewById(R.id.ib_save)
        val bundle = intent.extras
        if (bundle!=null){
            id_story = bundle.getString(IDStory).toString()
            type = bundle.getBoolean("type")
        }else{
            Toast.makeText(this, "L???i ??i...", Toast.LENGTH_SHORT).show()
            finish()
        }

        adapterNewimagechapter = Adapter_NewImageChapter(listUri, object : OnItemClick{
            override fun onItemClick(position: Int) {
                listUri.removeAt(position)
            }
        })

        startGallery = registerForActivityResult(
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
                adapterNewimagechapter.notifyDataSetChanged()
            }
        }
        if (type){
            ib_add.visibility = View.GONE
            rv_content.visibility = View.GONE
            edt_content.visibility = View.VISIBLE
        }else{
            ib_add.visibility = View.VISIBLE
            rv_content.visibility = View.VISIBLE
            edt_content.visibility = View.GONE
            rv_content.adapter = adapterNewimagechapter

            ib_add.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                startGallery.launch(intent)
            }
        }
        addEvent()
    }

    private fun addEvent() {
        toolbar.setNavigationOnClickListener {
            finish()
        }

        ib_save.setOnClickListener {
            val textContent = edt_content.text.toString().trim()
            val textTitle = edt_title.text.toString().trim()

            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("??ang t???o m???i...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            val simpleDateFormat = SimpleDateFormat("dd_mm_yyyy_hh_mm_ss")
            var index = 0

            var chapter = Chapter(textTitle, id_story, textContent)

            AddData().newChapter(chapter){ id_chapter->
                progressDialog.dismiss()
                if (id_chapter != null){
                    if (listUri.size > 0){
                        progressDialog.show()
                        for (i in listUri){
                            val pathName = "images_chapter/${simpleDateFormat.format(Calendar.getInstance().time)}_${id_chapter}_${index}.jpg"
                            AddData().newImage(i, pathName){
                                UpdateData().oneImageChapter(id_chapter, pathName){
                                }
                            }
                            index++
                        }
                        progressDialog.dismiss()
                    }
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    Toast.makeText(this, "Th??m ch????ng m???i th??nh c??ng!", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    Toast.makeText(this, "Th??m ch????ng th???t b???i!", Toast.LENGTH_SHORT).show()
                }
            }


        }
    }
}