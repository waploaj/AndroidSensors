package com.waploaj.proximitysensor.accessory

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.waploaj.proximitysensor.R
import java.io.File



private const val FileName = "photo.jpg"
private const val PICK_IMAGE = 100
class CameraAccess : AppCompatActivity() {
    private lateinit var imageView:ImageView
    private lateinit var buton:Button
    private lateinit var photoFile:File
    private lateinit var btnVideoCapt:Button
    private lateinit var btnUpload:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_access)

        buton = findViewById(R.id.button_capture)
        btnUpload = findViewById(R.id.btnUpload)
        btnVideoCapt = findViewById(R.id.btnVideoCapt)
        imageView = findViewById(R.id.camera_preview)
        val getUploadedPicture = registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback { imageView.setImageURI(it) })

        //Request for camera permission
        //TODO("Implement camera permission")

        buton.setOnClickListener {
            val picha = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FileName)

            val fileProvider = FileProvider.getUriForFile(this,"com.waploaj.proximitysensor.accessory",photoFile)
            picha.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if(picha.resolveActivity(this.packageManager) != null)getResul.launch(picha) else
                throw IllegalAccessError("Unable to acccess camera")
        }

        btnUpload.setOnClickListener {
            val upload = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getUploadedPicture.launch("image/*")
        }



    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg",storageDirectory)
    }


    private val getResul = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
        if (it.resultCode == Activity.RESULT_OK){
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            imageView.setImageBitmap(takenImage)
        }else{
            Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show()
        }
    }



}

