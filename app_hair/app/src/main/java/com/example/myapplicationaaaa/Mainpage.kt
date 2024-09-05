package com.example.myapplicationaaaa

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

class mainpage : AppCompatActivity() {

    private val imageLinks = listOf(
        "https://sitem.ssgcdn.com/71/95/79/item/1000555799571_i2_290.jpg",
        "https://www.gimhae.go.kr/_res/portal/img/sub/05/p01047_img8.png",
        "https://w7.pngwing.com/pngs/944/50/png-transparent-cartoon-character-baikinman-anpanman-hello-kitty-animated-cartoon-anime-cartoon-bread-superman-cartoon-character-television-child.png",
        "https://previews.123rf.com/images/aratehortua/aratehortua1904/aratehortua190400476/123613752-%EB%B2%A1%ED%84%B0-%EB%A7%8C%ED%99%94-%EA%B7%80%EC%97%AC%EC%9A%B4-%EC%BA%90%EB%A6%AD%ED%84%B0-%ED%9D%B0%EC%83%89-%EB%B0%B0%EA%B2%BD%EC%97%90-%EA%B3%A0%EB%A6%BD.jpg",
        "https://sitem.ssgcdn.com/24/09/88/item/1000543880924_i2_290.jpg",
        "https://tstrillion.com/web/product/medium/202407/c2d3b8164e39fd34a8d680301a6acd90.jpg"
    )

    private val webLinks = listOf(
        "https://www.ssg.com/item/itemView.ssg?itemId=1000555799571&siteNo=6004&salestrNo=6005&tlidSrchWd=%EB%91%90%ED%94%BC%EA%B0%81%EC%A7%88%EC%83%B4%ED%91%B8&srchPgNo=1&src_area=ssglist",
        "https://www.google.com/",
        "https://www.op.gg/",
        "https://flaticon.com/",
        "https://www.ssg.com/item/itemView.ssg?itemId=1000543880924&siteNo=6001&salestrNo=2037&tlidSrchWd=%EB%B9%84%EB%93%AC%EC%83%B4%ED%91%B8&srchPgNo=1&src_area=ssglist",
        "https://tstrillion.com/product/11-ts%EA%B7%B8%EB%A6%B0%ED%94%84%EB%A1%9C%ED%8F%B4%EB%A6%AC%EC%8A%A4%EC%83%B4%ED%91%B8-500g/927/category/29/display/1/"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progressBar1: ProgressBar = findViewById(R.id.data1)
        val progressBar2: ProgressBar = findViewById(R.id.data2)
        val progressBar3: ProgressBar = findViewById(R.id.data3)
        val progressBar4: ProgressBar = findViewById(R.id.data4)
        val progressBar5: ProgressBar = findViewById(R.id.data5)
        val progressBar6: ProgressBar = findViewById(R.id.data6)
        val cardContainer: LinearLayout = findViewById(R.id.card_container)

        // 이전 화면에서 전달받은 탈모지수 값 가져오기
        val 탈모지수List = intent.getIntegerArrayListExtra("탈모지수List")
        Log.d("Response", "탈모지수: $탈모지수List")
        // 탈모지수List가 null이면 에러 처리
        if (탈모지수List == null || 탈모지수List.size < 6) {
            // 여기에서 적절한 에러 처리를 합니다. 예: 로그 출력, 기본 값 사용, 사용자에게 알림 등.
            return
        }

        val progressBars = listOf(progressBar1, progressBar2, progressBar3, progressBar4, progressBar5, progressBar6)

        val progressBarData = 탈모지수List.mapIndexed { index, value ->
            Triple(value, imageLinks[index], webLinks[index])
        }.filter { it.first > 60 }

        val sortedProgressBarData = progressBarData.sortedByDescending { it.first }

        Handler(Looper.getMainLooper()).postDelayed({
            progressBars.forEachIndexed { index, progressBar ->
                animateProgressBar(progressBar, 0, 탈모지수List[index], 2000)
            }

            sortedProgressBarData.forEachIndexed { index, data ->
                val (value, imageUrl, webUrl) = data

                val cardView = CardView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 16)
                    }
                    radius = 12f
                    cardElevation = 8f
                }

                val linearLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16, 16, 16, 16)
                }

                val imageView = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }

                val textView = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text = "Test ${index + 1}"
                }

                linearLayout.addView(imageView)
                linearLayout.addView(textView)
                cardView.addView(linearLayout)
                cardContainer.addView(cardView)

                Glide.with(this)
                    .load(imageUrl)
                    .into(imageView)

                cardView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
                    startActivity(intent)
                }
            }
        }, 1000)
    }

    private fun animateProgressBar(progressBar: ProgressBar, start: Int, end: Int, duration: Long) {
        val animator = ValueAnimator.ofInt(start, end).apply {
            this.duration = duration
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Int
                progressBar.progress = progress
                setProgressBarColor(progressBar, progress)
            }
            start()
        }
    }

    private fun setProgressBarColor(progressBar: ProgressBar, progress: Int) {
        val progressDrawable = progressBar.progressDrawable as? LayerDrawable ?: return

        val progressLayer = progressDrawable.getDrawable(1)
        val color = when {
            progress <= 20 -> R.color.color_low
            progress <= 40 -> R.color.color_medium
            progress <= 60 -> R.color.color_high
            progress <= 80 -> R.color.color_very_high
            else -> R.color.color_critical
        }

        progressLayer.setColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_IN)
    }
}
