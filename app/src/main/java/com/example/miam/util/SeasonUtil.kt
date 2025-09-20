
package com.example.miam.util
import java.util.Calendar
enum class Season { Winter, Spring, Summer, Autumn }
object SeasonUtil {
  fun fromMonth(month: Int): Season = when(month){0,1,11->Season.Winter;2,3,4->Season.Spring;5,6,7->Season.Summer;else->Season.Autumn}
}
object SeasonProvider { fun current(): Season = SeasonUtil.fromMonth(Calendar.getInstance().get(Calendar.MONTH)) }
