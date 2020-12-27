package com.brioal.gobang.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.brioal.gobang.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*

class Panel @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) : View(context, attrs) {
    private val MAXLINE = 20 // 格子的数量
    private val MAX_IN_LINE = 5 //胜利的条件
    private var panleWidth // 棋盘的宽度
            = 0
    private var lineHeight // 方格的高度
            = 0f
    private var mPaint // 用于绘制线的画笔
            : Paint? = null
    private var mPaint_point // 用于绘制点的画笔
            : Paint? = null
    private var mWhite // 白色棋子
            : Bitmap? = null
    private var mBlack //黑色棋子
            : Bitmap? = null
    private var pieceWidth // 棋子要显示的高度
            = 0
    private var offset // 棋盘离组件边界的偏移
            = 0
    private var mWhites // 存储棋盘上的白子
            : MutableList<Point>? = null
    private var mBlacks // 存储棋盘上的黑子
            : MutableList<Point>? = null
    private var isWhite = false // 存储是否是白子 , 默认黑子先行
    private var isGameOver = false // 存储游戏是否已经结束

    var under // 组件的底部的位置 , 用于确定Dialog的显示文职
            = 0
        private set

    private var onGameListener: ((View) -> Unit)? = null

    fun setOnGameListener(onGameListener: (View) -> Unit) {
        this.onGameListener = onGameListener
        fun onGameOVer(i: Int) {}
    }

    //重新开始游戏
    fun reStartGame() {
        mWhites!!.clear() // 清理白子
        mBlacks!!.clear() // 清理黑子
        isGameOver = false // 游戏未结束
        isWhite = false // 黑子先手
        invalidate() // 重绘
    }

    private fun init() {
        mPaint = Paint()
        mPaint!!.color = -0x78000000 // 设置画笔的颜色
        mPaint!!.isAntiAlias = true // 画笔设置抗锯齿
        mPaint!!.isDither = true // 画笔设置图像抖动处理,使图像更加平滑
        mPaint!!.style = Paint.Style.STROKE // 设置绘制方式
        mPaint!!.strokeWidth = 2f // 设置画笔的边界宽度
        mPaint_point = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.isDither = true
        mWhite = BitmapFactory.decodeResource(resources, R.drawable.stone_w2) // 获取白棋的资源文件
        mBlack = BitmapFactory.decodeResource(resources, R.drawable.stone_b1) // 获取黑棋的资源文件
        mWhites = ArrayList()
        mBlacks = ArrayList()
    }

    //判断是否游戏结束,在onDraw方法内调用
    fun checkGameOver() {
        val whiteWin = checkFiveInLine(mWhites)
        val blackWin = checkFiveInLine(mBlacks)
        if (whiteWin || blackWin) {
            isGameOver = true
            if (onGameListener != null) {
                if (whiteWin) textView.text = "白棋胜" else textView.text = "黑棋胜"
            }
        }
    }

    private fun checkFiveInLine(points: List<Point>?): Boolean {
        for (point in points!!) {
            val x = point.x
            val y = point.y
            //水平方向的检查
            val isWin1 = checkHorizontal(x, y, points)
            //检查垂直方向
            val isWin2 = checkVertical(x, y, points)
            //左斜方向的检查
            val isWin3 = checkDiagonalLeft(x, y, points)
            //右斜方向的检查
            val isWin4 = checkDiagonalRight(x, y, points)
            //任意方向五子连珠 , 游戏结束
            if (isWin1 || isWin2 || isWin3 || isWin4) {
                return true
            }
        }
        return false
    }
    //右斜方向判断
    private fun checkDiagonalRight(x: Int, y: Int, points: List<Point>?): Boolean {
        var count = 1
        //往右上方向的判断
        for (i in 1 until MAX_IN_LINE) {
            val point = Point(x + i, y - i)
            if (points!!.contains(point)) {//判断有无子有加一
                count++
            }
        }
        if (count == MAX_IN_LINE) {
            return true
        }
        //往左下方向的判断
        for (i in 1 until MAX_IN_LINE) {
            val point = Point(x - i, y + i)
            if (points!!.contains(point)) {
                count++
            }
        }
        return count == MAX_IN_LINE
    }

    private fun checkVertical(x: Int, y: Int, points: List<Point>?): Boolean {
        var count = 1
        //往上遍历
        for (i in 1 until MAX_IN_LINE) {
            val point = Point(x, y - i)
            if (points!!.contains(point)) {
                count++
            }
        }
        if (count == MAX_IN_LINE) {
            return true
        }
        //往下遍历
        for (i in 1 until MAX_IN_LINE) {
            val point = Point(x, y + i)
            if (points!!.contains(point)) {
                count++
            }
        }
        return count == MAX_IN_LINE
    }

    //左斜方向的判断
    private fun checkDiagonalLeft(x: Int, y: Int, points: List<Point>?): Boolean {
        var count = 1
        //往左上方向遍历
        for (i in 1 until MAX_IN_LINE) {
            val point = Point(x - i, y - i)
            if (points!!.contains(point)) {
                count++
            }
        }
        if (count == MAX_IN_LINE) {
            return true
        }
        //往右下方向的判断
        for (i in 1 until MAX_IN_LINE) {
            val point = Point(x + i, y + i)
            if (points!!.contains(point)) {
                count++
            }
        }
        return count == MAX_IN_LINE
    }

    //检查水平方向
    private fun checkHorizontal(x: Int, y: Int, points: List<Point>?): Boolean {
        var count = 1
        //往左遍历
        for (i in 1 until MAX_IN_LINE) {
            val point = Point(x - i, y)
            if (points!!.contains(point)) { // 是否包含点
                count++
            }
        }
        if (count == MAX_IN_LINE) {
            return true
        }
        //往右遍历
        for (i in 1 until MAX_IN_LINE) {
            val point = Point(x + i, y)
            if (points!!.contains(point)) {
                count++
            }
        }
        return count == MAX_IN_LINE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        panleWidth = w // 获取棋盘的宽度
        under = h - (h - panleWidth) / 2
        lineHeight = panleWidth * 1.0f / MAXLINE // 获取格子的高度 10行,则直线有10条 ,格子只有9个
        offset = (lineHeight / 2).toInt()
        pieceWidth = (lineHeight * 3 / 4).toInt() // 棋子的高度为格子高度的3/4
        mWhite = Bitmap.createScaledBitmap(mWhite!!, pieceWidth, pieceWidth, false) // 根据棋子宽度进行缩放
        mBlack = Bitmap.createScaledBitmap(mBlack!!, pieceWidth, pieceWidth, false) //根据棋子宽度进行缩放
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isGameOver) {
            return false
        }
        val action = event.action
        if (action == MotionEvent.ACTION_DOWN) {
            val point = Point(((event.x - offset) / lineHeight).toInt(), ((event.y - offset) / lineHeight).toInt())
            if (!mWhites!!.contains(point) && !mBlacks!!.contains(point)) { // 如果没有点
                isWhite = if (isWhite) {
                    mWhites!!.add(point) // 存入白子
                    false
                } else {
                    mBlacks!!.add(point) //存入黑子
                    true
                }
            }
            invalidate()
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) // 获取宽度
        val widthMode = MeasureSpec.getMode(widthMeasureSpec) // 获取宽度的类型
        val heightSize = MeasureSpec.getSize(heightMeasureSpec) // 获取高度
        val heightMode = MeasureSpec.getMode(heightMeasureSpec) // 获取高度的类型
        var width = widthMeasureSpec.coerceAtMost(heightMeasureSpec) //取宽高的最小值
        if (widthMode == MeasureSpec.UNSPECIFIED) {         //如果宽度是wrap_content , 则最终宽度置高度
            width = heightSize
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {             //如果高度是wrap_content , 最终宽度置宽度
            width = widthSize
        }
        setMeasuredDimension(width, width) //设置值
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBoard(canvas)
        drawPoints(canvas)
        checkGameOver()
    }

    //绘制棋子
    private fun drawPoints(canvas: Canvas) {
        for (point in mWhites!!) {
            canvas.drawBitmap(mWhite!!, offset + point.x * lineHeight - pieceWidth / 2, offset + point.y * lineHeight - pieceWidth / 2, mPaint_point)
        }
        for (point in mBlacks!!) {
            canvas.drawBitmap(mBlack!!, offset + point.x * lineHeight - pieceWidth / 2, offset + point.y * lineHeight - pieceWidth / 2, mPaint_point)
        }
    }

    //绘制棋盘
    private fun drawBoard(canvas: Canvas) {
        val startx = offset // 起始 x坐标
        val endx = panleWidth - offset // 终止x坐标
        for (i in 0 until MAXLINE) {
            val starty = i * lineHeight + offset // 起始的y坐标
            val endy = i * lineHeight + offset // 终止的y坐标
            canvas.drawLine(startx.toFloat(), starty, endx.toFloat(), endy, mPaint!!) // 绘制横向的
            canvas.drawLine(starty, startx.toFloat(), endy, endx.toFloat(), mPaint!!) // 纵向只需要把 横向的xy交换就行
        }
    }

    companion object {
//        private const val TAG = "PanelInfo"
        var WHITE_WIN = 1 //白子胜利的标志
        var BLACK_WIN = 0 // 黑子胜利的标志
    }

    init {
        init()
    }
}