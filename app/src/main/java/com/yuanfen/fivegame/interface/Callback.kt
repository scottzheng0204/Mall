package com.yuanfen.fivegame.`interface`

import android.graphics.Point

public interface Callback {
    /**
     * 游戏结束
     *
     * @param winner 胜利玩家
     */
    fun gameOver(winner: Int)

    /**
     * 落子
     *
     * @param p       落子
     * @param isAi    ai落子
     * @param isBlack 是否黑子
     */
    fun atBell(p: Point?, isAi: Boolean, isBlack: Boolean)
}