package com.brioal.gobang

import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import com.brioal.gobang.view.Panel

class MainActivity : AppCompatActivity() {
    private var panel // 棋盘VIew
            : Panel? = null
    private var builder //Dialog构建
            : androidx.appcompat.app.AlertDialog.Builder? = null

    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val window: Window = getWindow()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) // 设置全屏
        builder = androidx.appcompat.app.AlertDialog.Builder(this@MainActivity)
        builder.setTitle("游戏结束") // 设置Dialog的标题
        builder.setNegativeButton("退出", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) { // 设置退出按钮和点击事件
                this@MainActivity.finish()
            }
        })
        builder.setPositiveButton("再来一局", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) { // 设置再来一局的按钮和点击事件
                panel!!.reStartGame()
            }
        })
        panel = findViewById(R.id.main_panel) as Panel?
        panel!!.setOnGameListener(object : Panel.onGameListener {
            override fun onGameOVer(i: Int) { // 设置监听器
                var text = ""
                if (i == Panel.Companion.WHITE_WIN) {
                    //白子胜利
                    text = "白子胜利"
                } else if (i == Panel.Companion.BLACK_WIN) {
                    //黑子胜利
                    text = "黑子胜利"
                }
                builder.setMessage(text) // 设置Dialog内容
                builder.setCancelable(false) // 设置不可返回键取消
                val dialog: androidx.appcompat.app.AlertDialog = builder.create() // 构建Dialog
                val dialogWindow: Window = dialog.getWindow()
                val params: WindowManager.LayoutParams = WindowManager.LayoutParams()
                params.x = 0 //设置x坐标
                params.y = panel.getUnder() //设置y坐标
                dialogWindow.attributes = params // 设置新的LayoutParams
                dialog.setCanceledOnTouchOutside(false) // 设置点击外部不取消
                dialog.show() // 显示Dialog
            }
        })
    }
}