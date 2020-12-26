package com.brioal.gobang

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.brioal.gobang.view.Panel

class MainActivity : AppCompatActivity() {
    private var panel // 棋盘VIew
            : Panel? = null
    private var builder //Dialog构建
            : AlertDialog.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) // 设置全屏
        builder = AlertDialog.Builder(this@MainActivity)
        builder!!.setTitle("游戏结束") // 设置Dialog的标题
        builder!!.setNegativeButton("退出") { dialog, which -> // 设置退出按钮和点击事件
            finish()
        }
        builder!!.setPositiveButton("再来一局") { dialog, which -> // 设置再来一局的按钮和点击事件
            panel!!.reStartGame()
        }
        panel = findViewById<View>(R.id.main_panel) as Panel
        panel!!.setOnGameListener{ // 设置监听器
                var text = ""
                if (Panel.WHITE_WIN) {
                    //白子胜利
                    text = "白子胜利"
                } else if (Panel.BLACK_WIN) {
                    //黑子胜利
                    text = "黑子胜利"
                }
                builder!!.setMessage(text) // 设置Dialog内容
                builder!!.setCancelable(false) // 设置不可返回键取消
                val dialog = builder!!.create() // 构建Dialog
                val dialogWindow = dialog.window
                val params = WindowManager.LayoutParams()
                params.x = 0 //设置x坐标
                params.y = panel!!.under //设置y坐标
                dialogWindow!!.attributes = params // 设置新的LayoutParams
                dialog.setCanceledOnTouchOutside(false) // 设置点击外部不取消
                dialog.show() // 显示Dialog
        }
    }
}