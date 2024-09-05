import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import com.google.gson.JsonObject

interface RetrofitPath {
    @Multipart
    @POST("/upload")
    fun profileSend(
        @Part files: List<MultipartBody.Part>
    ): Call<JsonObject>
}
