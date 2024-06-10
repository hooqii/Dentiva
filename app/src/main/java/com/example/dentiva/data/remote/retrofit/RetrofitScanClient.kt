import com.example.dentiva.data.remote.retrofit.ApiScanService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitScanClient {
    private const val BASE_URL = "http://34.128.108.241/"

    private val okHttpClient = OkHttpClient.Builder().build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiScanService by lazy {
        retrofit.create(ApiScanService::class.java)
    }
}
