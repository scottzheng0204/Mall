package com.brioal.gobang

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.brioal.gobang.view.Panel
import com.brioal.gobang.view.Panel.Companion.BLACK_WIN
import com.brioal.gobang.view.Panel.Companion.WHITE_WIN
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var panel // 棋盘VIew
            : Panel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) // 设置全屏
        button2.setOnClickListener{
            panel?.reStartGame()
        }
        button3.setOnClickListener{
            finish()
        }
    }
}