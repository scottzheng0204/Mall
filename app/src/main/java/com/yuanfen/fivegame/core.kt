package com.yuanfen.fivegame

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.yuanfen.fivegame.`interface`.Callback
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_main.*


/**
 * @author :Reginer in  2018/7/9 20:00.
 * 联系方式:QQ:282921012
 * 功能描述:
 */
class core @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    val BLACK_CHESS = 1
    val WHITE_CHESS = 2
    val N0_CHESS = 0

    /**
     * 人机对战
     */
    val HUMAN_COMPUTER = 0

    /**
     * 人人对战
     */
    val HUMAN_HUMAN = 1

    /**
     * 五子棋ai级别
     */
    val RENJU_LEVEL = "renju_level"
    /**
     * 棋盘面板宽度
     */
    private var mPanelWidth = 0

    /**
     * 棋盘每格的行高
     */
    private var mLineHeight = 0f

    /**
     * 棋子占行高比例
     */
    private val ratioPieceOfLineHeight = 0.9f
    private var startPos = 0f
    private var endPos = 0f

    /**
     * 白棋数组
     */
    private val mWhiteArray = ArrayList<Point>()

    /**
     * 黑棋数组
     */
    private val mBlackArray = ArrayList<Point>()

    /**
     * 棋盘数组
     */
    private val chessArray = ArrayList<Point>()
    private val mBoard = Array(MAX_LINE) {
        IntArray(
            MAX_LINE
        )
    }

    /**
     * 胜利玩家
     */
    private var mWinner = 0

    /**
     * 连成五个的棋子
     */
    private val fiveArray = ArrayList<Point>()

    /**
     * 游戏是否结束
     */
    var isGameOver = false
        private set

    var gameMode = HUMAN_COMPUTER

    /**
     * 玩家以及AI得分纪录
     */
    var userScore: Int
        private set
    var aiScore: Int
        private set

    /**
     * 玩家执子
     */
    var userChess = 0

    /**
     * 当前回合是否轮到玩家
     */
    private var isUserBout = false

    /**
     * 显示棋子编号
     */
    private var isDrawChessNum = true
    private var callBack: Callback? = null
    private val mPaint = Paint()
    private var mWhitePiece: Bitmap
    private var mBlackPiece: Bitmap
    private var suggestPoint: Point? = null
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        var measureSize = Math.min(widthSize, heightSize)
        if (widthMode == View.MeasureSpec.UNSPECIFIED) {
            measureSize = heightSize
        } else if (heightMode == View.MeasureSpec.UNSPECIFIED) {
            measureSize = widthSize
        }
        setMeasuredDimension(measureSize, measureSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        mPanelWidth = w
        val outLine = 10.0f
        mLineHeight = (mPanelWidth * 1.0f - 2.0f * outLine) / MAX_LINE
        startPos = outLine
        endPos = w - outLine
        val pieceSize = (mLineHeight * ratioPieceOfLineHeight).toInt()
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceSize, pieceSize, false)
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceSize, pieceSize, false)
    }

    private fun getValidPoint(x: Int, y: Int): Point {
        return Point((x / mLineHeight).toInt(), (y / mLineHeight).toInt())
    }

    val chessCount: Int
        get() = chessArray.size

    fun removeLastChess() {
        val p = chessArray[chessArray.size - 1]
        chessArray.removeAt(chessArray.size - 1)
        if (mBoard[p.y][p.x] == BLACK_CHESS) {
            mBlackArray.removeAt(mBlackArray.size - 1)
        } else if (mBoard[p.y][p.x] == WHITE_CHESS) {
            mWhiteArray.removeAt(mWhiteArray.size - 1)
        }
        mBoard[p.y][p.x] = N0_CHESS
    }

    fun undo() {
        val canUndoSize = 2
        if (chessArray.size >= canUndoSize) {
            isGameOver = false
            suggestPoint = null
            removeLastChess()
            removeLastChess()
        }
        postInvalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isGameOver || !isUserBout) {
            return false
        }
        val action = event.action
        if (action == MotionEvent.ACTION_DOWN) {
            return true
        }
        if (action == MotionEvent.ACTION_UP) {
            val x = event.x.toInt()
            val y = event.y.toInt()
            val p = getValidPoint(x, y)
            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false
            }
            suggestPoint = null
            addChess(p, userChess)
            setUserBout(!isHumanComputer)
            if (!isHumanComputer) {
                userChess =
                    if (userChess == WHITE_CHESS) BLACK_CHESS else WHITE_CHESS
            }
            checkGameOver()
            invalidate()
            if (!isGameOver) {
                callBack?.atBell(p, false, isUserBlack)
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBoard(canvas)
        drawPiece(canvas)
    }

    fun addChess(point: Point, chessType: Int) {
        mBoard[point.y][point.x] = chessType
        chessArray.add(point)
        if (chessType == BLACK_CHESS) {
            mBlackArray.add(point)
        } else if (chessType == WHITE_CHESS) {
            mWhiteArray.add(point)
        }
        invalidate()
    }

    fun checkGameOver() {
        val blackWin = checkFiveInLine(mBlackArray)
        val whiteWin = !blackWin && checkFiveInLine(mWhiteArray)
        if (whiteWin || blackWin) {
            isGameOver = true
            mWinner = if (whiteWin) WHITE_CHESS else BLACK_CHESS
            if (mWinner == userChess) {
                userScore++
            } else {
                aiScore++
            }
            callBack?.gameOver(mWinner)
            invalidate()
        } else if (isFull) {
            isGameOver = true
            mWinner = N0_CHESS
            callBack?.gameOver(mWinner)
            invalidate()
        }
    }

    fun checkFiveInLine(points: List<Point>): Boolean {
        val dirArray: MutableList<Point> = ArrayList()
        dirArray.add(Point(1, 0))
        dirArray.add(Point(0, 1))
        dirArray.add(Point(1, 1))
        dirArray.add(Point(1, -1))
        for (p in points) {
            for (dir in dirArray) {
                if (checkFiveOneLine(dir, p.x, p.y, points)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 检查棋子在某个方向是否已经连五
     *
     * @param dir    dir
     * @param x      x
     * @param y      y
     * @param points points
     * @return 胜利
     */
    fun checkFiveOneLine(dir: Point, x: Int, y: Int, points: List<Point>): Boolean {
        var count = 1
        fiveArray.clear()
        fiveArray.add(Point(x, y))
        val successSize = 5
        for (i in 1 until successSize) {
            val p = Point(x + dir.x * i, y + dir.y * i)
            if (points.contains(p)) {
                fiveArray.add(p)
                count++
            } else {
                break
            }
        }
        for (i in 1 until successSize) {
            val p = Point(x - dir.x * i, y - dir.y * i)
            if (points.contains(p)) {
                fiveArray.add(p)
                count++
            } else {
                break
            }
        }
        return count >= 5
    }

    private fun drawPiece(canvas: Canvas) {
        mPaint.strokeWidth = 2.0f
        mPaint.textSize = 24f
        val fontMetrics = mPaint.fontMetricsInt
        for (i in mWhiteArray.indices) {
            val whitePoint = mWhiteArray[i]
            val left = startPos + (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight
            val top = startPos + (whitePoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight
            canvas.drawBitmap(mWhitePiece, left, top, null)
            if (isDrawChessNum) {
                mPaint.color = Color.BLACK
                val textTop = startPos + whitePoint.y * mLineHeight
                val textBottom = textTop + mLineHeight
                val baseline = (textTop + textBottom - fontMetrics.ascent - fontMetrics.descent) / 2
                val centerX = startPos + (whitePoint.x + 0.5f) * mLineHeight
                canvas.drawText(String.format("%S", 2 + i * 2), centerX, baseline, mPaint)
            }
        }
        for (i in mBlackArray.indices) {
            val blackPoint = mBlackArray[i]
            val left = startPos + (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight
            val top = startPos + (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight
            canvas.drawBitmap(mBlackPiece, left, top, null)
            if (isDrawChessNum) {
                mPaint.color = Color.WHITE
                val textTop = startPos + blackPoint.y * mLineHeight
                val textBottom = textTop + mLineHeight
                val baseline = (textTop + textBottom - fontMetrics.ascent - fontMetrics.descent) / 2
                val centerX = startPos + (blackPoint.x + 0.5f) * mLineHeight
                canvas.drawText(String.format("%S", 1 + i * 2), centerX, baseline, mPaint)
            }
        }
    }

    private fun drawBoard(canvas: Canvas) {
        mPaint.color = Color.BLACK
        mPaint.strokeWidth = 2.0f
        val w = mPanelWidth
        val lineHeight = mLineHeight
        //画棋盘线
        for (i in 0 until MAX_LINE) {
            val startX = (startPos + lineHeight / 2).toInt()
            val endX = (endPos - lineHeight / 2).toInt()
            val y = (startPos + (0.5 + i) * lineHeight).toInt()
            canvas.drawLine(startX.toFloat(), y.toFloat(), endX.toFloat(), y.toFloat(), mPaint)
        }
        for (i in 0 until MAX_LINE) {
            val startY = (startPos + lineHeight / 2).toInt()
            val endY = (endPos - lineHeight / 2).toInt()
            val x = (startPos + (0.5 + i) * lineHeight).toInt()
            canvas.drawLine(x.toFloat(), startY.toFloat(), x.toFloat(), endY.toFloat(), mPaint)
        }
        //画棋盘坐标
        mPaint.textSize = 20f
        mPaint.strokeWidth = 1.7f
        val fontMetrics = mPaint.fontMetricsInt
        for (i in 0 until MAX_LINE) {
            val y = startPos + (0.5f + i) * lineHeight
            val textTop = y - 0.5f * mLineHeight
            val textBottom = y + 0.5f * mLineHeight
            val baseline = (textTop + textBottom - fontMetrics.ascent - fontMetrics.descent) / 2
            val x = 12f
            canvas.drawText(String.format("%S", MAX_LINE - i), x, baseline, mPaint)
        }
        for (i in 0 until MAX_LINE) {
            val y = (w - 12).toFloat()
            val textTop = y - 0.5f * mLineHeight
            val textBottom = y + 0.5f * mLineHeight
            val baseline = (textTop + textBottom - fontMetrics.ascent - fontMetrics.descent) / 2
            val x = startPos + (0.5f + i) * lineHeight
            val text: String = ('A'.toInt() + i).toString()
            canvas.drawText(text, x, baseline, mPaint)
        }
        //棋盘边缘线
        mPaint.strokeWidth = 4.0f
        val min = lineHeight.toInt() / 2 + 4
        val max = w - min
        canvas.drawLine(
            (min - 2).toFloat(),
            min.toFloat(),
            (max + 2).toFloat(),
            min.toFloat(),
            mPaint
        )
        canvas.drawLine(
            (min - 2).toFloat(),
            max.toFloat(),
            (max + 2).toFloat(),
            max.toFloat(),
            mPaint
        )
        canvas.drawLine(min.toFloat(), min.toFloat(), min.toFloat(), max.toFloat(), mPaint)
        canvas.drawLine(max.toFloat(), min.toFloat(), max.toFloat(), max.toFloat(), mPaint)
        //画五个小黑点
        mPaint.strokeWidth = 8f
        val startSize = 3.5f
        val endSize = MAX_LINE - 3.5f
        val middleSize = MAX_LINE / 2f
        //左上
        canvas.drawCircle(
            startPos + startSize * lineHeight,
            startPos + startSize * lineHeight,
            5f,
            mPaint
        )
        //右上
        canvas.drawCircle(
            startPos + endSize * lineHeight,
            startPos + startSize * lineHeight,
            5f,
            mPaint
        )
        //左下
        canvas.drawCircle(
            startPos + startSize * lineHeight,
            startPos + endSize * lineHeight,
            5f,
            mPaint
        )
        //右下
        canvas.drawCircle(
            startPos + endSize * lineHeight,
            startPos + endSize * lineHeight,
            5f,
            mPaint
        )
        //中间
        canvas.drawCircle(
            startPos + middleSize * lineHeight,
            startPos + middleSize * lineHeight,
            5f,
            mPaint
        )
        mPaint.color = Color.GREEN
        mPaint.strokeWidth = 4.0f
        if (!isGameOver) {
            //标识最后一子
            if (chessArray.size > 0) {
                drawCircle(canvas, chessArray[chessArray.size - 1])
            }
            //标识建议位置
            if (suggestPoint != null) {
                mPaint.color = Color.GRAY
                drawCircle(canvas, suggestPoint!!)
            }
        } else {
            //标识连五
            for (point in fiveArray) {
                drawCircle(canvas, point)
            }
        }
    }

    fun drawCircle(canvas: Canvas, point: Point) {
        val cx = startPos + (0.5f + point.x) * mLineHeight
        val cy = startPos + (0.5f + point.y) * mLineHeight
        val radius = ratioPieceOfLineHeight * mLineHeight / 2
        canvas.drawCircle(cx, cy, radius, mPaint)
    }

    fun showSuggest(point: Point?) {
        suggestPoint = point
        postInvalidate()
    }

    fun showChessNum() {
        isDrawChessNum = !isDrawChessNum
        postInvalidate()
    }

    fun start() {
        for (i in 0 until MAX_LINE) {
            for (j in 0 until MAX_LINE) {
                mBoard[i][j] = 0
            }
        }
        mBlackArray.clear()
        mWhiteArray.clear()
        chessArray.clear()
        suggestPoint = null
        isGameOver = false
        mWinner = 0
        userChess = BLACK_CHESS
        invalidate()
    }

    val isFull: Boolean
        get() {
            for (i in 0 until MAX_LINE) {
                for (j in 0 until MAX_LINE) {
                    if (mBoard[i][j] == N0_CHESS) {
                        return false
                    }
                }
            }
            return true
        }

    fun setUserBout(userBout: Boolean) {
        isUserBout = userBout
    }

    fun setCallBack(callBack: Callback?) {
        this.callBack = callBack
    }

    /**
     * 玩家是否执黑
     *
     * @return boolean
     */
    val isUserBlack: Boolean
        get() = userChess == if (isHumanComputer) BLACK_CHESS else WHITE_CHESS

    /**
     * 是否人机
     *
     * @return 人机
     */
    val isHumanComputer: Boolean
        get() = gameMode == HUMAN_COMPUTER

    companion object {
        /**
         * 棋盘尺寸
         */
        private const val MAX_LINE = 15
    }

    init {
        mPaint.color = Color.BLACK
        //抗锯齿
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.style = Paint.Style.STROKE
        mPaint.textSize = 24f
        mPaint.textAlign = Paint.Align.CENTER
        mWhitePiece = BitmapFactory.decodeResource(resources, R.drawable.ic_white_chess)
        mBlackPiece = BitmapFactory.decodeResource(resources, R.drawable.ic_black_chess)
        userScore = 0
        aiScore = 0
        start()
    }
}