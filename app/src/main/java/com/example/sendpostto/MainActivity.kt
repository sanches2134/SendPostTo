package com.example.sendpostto

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.create
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection


const val BASE_URL="https://api.imgur.com/3/upload/"
const val accessToken="f0ab8f6b431cb4890236854f00a08ab479c5a71b"
class MainActivity : AppCompatActivity() {

    private val PERMISSION_CODE = 1000;
    private val IMAGE_CAPTURE_CODE = 1001

    var image_uri: Uri?=null
    lateinit var bitmap:Bitmap

    private val Client_ID="af3e72c49e28232"
    private val Client_secret="3ab323064855923e035c5bdedcd84d4d5b48a5ba"

    val titleET: EditText
        get()=findViewById(R.id.title)

    val photoIV:ImageView
        get()=findViewById(R.id.photo)

    val descriptionET:EditText
        get()=findViewById(R.id.description)
val pp=""
    val makePostBTN:Button
        get()=findViewById(R.id.post)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            photoIV.setOnClickListener {
                newPhoto()
            }

            makePostBTN.setOnClickListener {
                  // uploadImageToImgur(bitmap)

               RetroObject.instance.uploadPost(bitmap,"MM","I LOVE")
                    .enqueue(object :Callback<Response>
                    {
                        override fun onResponse(
                            call: Call<Response>,
                            response: retrofit2.Response<Response>
                        ) {

                            Log.d("TAG", "Link is : ${response.body()?.link}")
                        }

                        override fun onFailure(call: Call<Response>, t: Throwable) {

                            Log.d("TAG", "Pizda")
                        }

                    })

            }

        }
    private fun getBase64Image(image: Bitmap, complete: (String) -> Unit) {
        GlobalScope.launch {
            val outputStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val b = outputStream.toByteArray()
            complete(Base64.encodeToString(b, Base64.DEFAULT))
        }
    }
    private fun uploadImageToImgur(image: Bitmap) {
        getBase64Image(image, complete = { base64Image ->
            GlobalScope.launch(Dispatchers.Default) {
                val url = URL("https://api.imgur.com/3/image")

                val boundary = "Boundary-${System.currentTimeMillis()}"

                val httpsURLConnection =
                        withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
                httpsURLConnection.setRequestProperty("Authorization", "Bearer $accessToken")
                httpsURLConnection.setRequestProperty(
                        "Content-Type",
                        "multipart/form-data; boundary=$boundary"
                )

                httpsURLConnection.requestMethod = "POST"
                httpsURLConnection.doInput = true
                httpsURLConnection.doOutput = true

                var body = ""
                body += "--$boundary\r\n"
                body += "Content-Disposition:form-data; name=\"image\""
                body += "\r\n\r\n$base64Image\r\n"
                body += "--$boundary--\r\n"


                val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
                withContext(Dispatchers.IO) {
                    outputStreamWriter.write(body)
                    outputStreamWriter.flush()
                }
                val response = httpsURLConnection.inputStream.bufferedReader()
                        .use { it.readText() }  // defaults to UTF-8
                val jsonObject = JSONTokener(response).nextValue() as JSONObject
                val data = jsonObject.getJSONObject("data")

                Log.d("TAG", "Link is : $data")

            }
        })
    }
private fun newPhoto()
{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, PERMISSION_CODE)
        }
        else{
            openCamera()
        }
    }
    else{
        openCamera()
    }
}
    private fun openCamera() {
        val values = ContentValues().apply {
        put(MediaStore.Images.Media.TITLE, "New Picture")
        put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")}
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            photoIV.setImageURI(image_uri)
             bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, image_uri)
        }
    }

}

