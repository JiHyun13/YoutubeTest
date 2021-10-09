package com.example.youtubetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class MainActivity : AppCompatActivity() {
    private val retrofitService = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/youtube/v3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(YoutubeInfoAPI::class.java)

    private val txtTitle : TextView by lazy { findViewById(R.id.tvTitle) }
    private val txtDescription : TextView by lazy { findViewById(R.id.tvDescription) }
    private val txtChannel : TextView by lazy { findViewById(R.id.tvChannel) }

    private val imageView : ImageView by lazy { findViewById(R.id.imageView) }

    private val youtubeUrlWatcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            val matchResult = Regex("^((?:https?:)?//)?((?:www|m)\\.)?((?:youtube\\.com|youtu.be))(\\/(?:[\\w\\-]+\\?v=|embed/|v/)?)([\\w\\-]+)(\\S+)?\$")
                .find(s.toString())
            if(matchResult!=null){
                if(matchResult.groupValues.size>5){
                    if(matchResult.groupValues[5].length >10 ){
                        CoroutineScope(Dispatchers.Main).launch {
                            val data = getVideoInfo(matchResult.groupValues[5]).itemEntity[0]
                            txtTitle.text = data.snippet.title
                            txtDescription.text = data.snippet.description
                            txtChannel.text = data.snippet.channelTitle
                            Glide.with(this@MainActivity).load(data.snippet.thumbnails.high.url).into(imageView)
                        }
                    }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<EditText>(R.id.editText).addTextChangedListener(youtubeUrlWatcher)
    }

    suspend fun getVideoInfo(id : String) : YoutubeRetrofitEntity{
        return CoroutineScope(Dispatchers.IO).async{
            retrofitService.getVideoInfo(id, "AIzaSyDElOeACCdl4NLWdpsKjzXYuL0nylogLS4")
        }.await()
    }
}