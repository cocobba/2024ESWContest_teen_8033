package com.example.myapplicationaaaa

import RetrofitPath
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ppi.RetrofitSetting
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.JsonObject
import java.io.File

class Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainpage)

        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener { getProFileImages() }
    }

    fun getProFileImages() {
        Log.d(ContentValues.TAG, "사진 변경 호출")
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        launcher.launch(intent)
    }

    fun absolutelyPath(path: Uri?, context: Context): String {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    val index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    result = c.getString(index)
                }
            } finally {
                c.close()
            }
        }
        return result ?: ""
    }

    var launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val clipData = result.data?.clipData
                val uris = mutableListOf<Uri>()
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        uris.add(clipData.getItemAt(i).uri)
                    }
                } else {
                    result.data?.data?.let { uris.add(it) }
                }

                val imageParts = uris.map { uri ->
                    val file = File(absolutelyPath(uri, this))
                    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    MultipartBody.Part.createFormData("files", file.name, requestFile)
                }

                sendImages(imageParts)
            }
        }

    fun sendImages(images: List<MultipartBody.Part>) {
        val service = RetrofitSetting.createBaseService(RetrofitPath::class.java)
        val call = service.profileSend(images)

        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val 탈모지수Element = responseBody.getAsJsonArray("탈모지수")

                        if (탈모지수Element != null && 탈모지수Element.isJsonArray) {
                            val 탈모지수List = List(탈모지수Element.size()) { index ->
                                탈모지수Element[index].asInt
                            }

                            Log.d("Response", "탈모지수: $탈모지수List")

                            val intent = Intent(this@Activity, mainpage::class.java)
                            intent.putIntegerArrayListExtra("탈모지수List", ArrayList(탈모지수List))
                            startActivity(intent)
                        } else {
                            Log.d("Response", "탈모지수 is null or not a valid array")
                            Toast.makeText(
                                applicationContext,
                                "탈모지수를 찾을 수 없습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.d("Response", "Response body is null")
                        Toast.makeText(applicationContext, "응답 본문이 비어 있습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Log.d(
                        "Response",
                        "Upload failed with code: ${response.code()} and message: ${response.message()}"
                    )
                    Toast.makeText(applicationContext, "통신 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("로그", "Network error: ${t.message}")
                Toast.makeText(applicationContext, "통신 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}