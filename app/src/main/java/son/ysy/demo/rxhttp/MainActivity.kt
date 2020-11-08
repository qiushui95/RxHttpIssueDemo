package son.ysy.demo.rxhttp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private val api by lazy {
        Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com/")
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder()
                        .add(ResponseResultJsonAdapter.FACTORY)
                        .add(PagingResultJsonAdapter.FACTORY)
                        .build())
                )
                .build()
                .create(Api::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnUseCase1).setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val list = api.getPageData()
                launch(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "获取到${list.datas.size}个数据", Toast.LENGTH_LONG).show()
                }
            }
        }

        findViewById<Button>(R.id.btnUseCase2).setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val list = api.getDirectData()
                launch(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "获取到${list.size}个数据", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}