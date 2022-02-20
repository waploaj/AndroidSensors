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
private const val VideoFile = "Video.mp4"
class CameraAccess : AppCompatActivity() {
    private lateinit var imageView:ImageView
    private lateinit var buton:Button
    private lateinit var photoFile:File
    private lateinit var btnVideoCapt:Button
    private lateinit var btnUpload:Button
    private lateinit var videoFile:File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_access)

        buton = findViewById(R.id.button_capture)
        btnUpload = findViewById(R.id.btnUpload)
        btnVideoCapt = findViewById(R.id.btnVideoCapt)
        imageView = findViewById(R.id.camera_preview)


        //Request for camera permission
        //TODO("Implement  check for camera permission")
        //set a listener on capture image
        buton.setOnClickListener {
            val picha = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FileName)

            val fileProvider = FileProvider.getUriForFile(
                this,"com.waploaj.proximitysensor.accessory",photoFile)
            picha.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if(picha.resolveActivity(this.packageManager) != null)getResul.launch(picha) else
                throw IllegalAccessError("Unable to acccess camera")
        }

        //Set a listener for upload button
        btnUpload.setOnClickListener {
            val upload = Intent(MediaStore.ACTION_IMAGE_CAPTURE)//set an intent
            photoFile = getPhotoFile(FileName) //call a path photofile
            val fileProvider = FileProvider.getUriForFile(
                this,"com.waploaj.proximitysensor.accessory",photoFile) //call afile
            upload.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider) // put a file to an intent object
            getUploadedPicture.launch("image/*") //call back result on intent object
        }

        //Set a listener for video capture button
        btnVideoCapt.setOnClickListener {
            val video = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            videoFile = getVideoFile(VideoFile)
            val fileprovider = FileProvider.getUriForFile(
                this, "com.waploaj.proximitysensor.accessory", videoFile
            )
            video.putExtra(MediaStore.EXTRA_OUTPUT,fileprovider)
            getVideoCapture.launch(video)

        }

    }
    //return temporariy file that hold the capture image
    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg",storageDirectory)
    }
    //retun temporariy file that hodl the video capture
    private fun getVideoFile(filename:String):File{
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return File.createTempFile(filename, "mp4",storageDirectory)
    }

    //Register activity for capture image start and receive call back activity
    private val getResul = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
        if (it.resultCode == Activity.RESULT_OK){
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            imageView.setImageBitmap(takenImage)
        }else{
            Toast.makeText(this,"You didn't capture the picture", Toast.LENGTH_SHORT).show()
        }
    }

    //Register activity for image upload start and receive result on callback activity
    val getUploadedPicture = registerForActivityResult(
        ActivityResultContracts.GetContent(), ActivityResultCallback {
            imageView.setImageURI(it) })

    //Register activity for video capture start and receive result on callback activity
    val getVideoCapture = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val takenVideo = BitmapFactory.decodeFile(videoFile.absolutePath)
            imageView.setImageBitmap(takenVideo)
        }
    }
}

