package softwareProject.parkingLot.User

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import okhttp3.internal.Internal.instance
import softwareProject.parkingLot.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Calendar.*

class ReservationActivity : AppCompatActivity() {

    lateinit var selectDate: TextView
    lateinit var selectTime: TextView
    lateinit var calView: CalendarView
    lateinit var timePicker: TimePicker

    var cal_already_ON = false
    var tPicker_already_ON = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)
        title = "예약등록"

        setViewId()
        viewInit()

        selectDate.setOnClickListener(View.OnClickListener {
            if (cal_already_ON) {
                selectDate.setTypeface(null, Typeface.NORMAL)
                selectDate.setBackgroundResource(R.drawable.button_click_off)
                calView.visibility = View.GONE
                cal_already_ON = false
            } else {
                selectDate.setTypeface(null, Typeface.BOLD)
                selectDate.setBackgroundResource(R.drawable.button_click_on)
                calView.visibility = View.VISIBLE
                cal_already_ON = true
            }
        })
        selectTime.setOnClickListener(View.OnClickListener {
            if (tPicker_already_ON) {
                selectTime.setTypeface(null, Typeface.NORMAL)
                selectTime.setBackgroundResource(R.drawable.button_click_off)
                timePicker.visibility = View.GONE
                tPicker_already_ON = false
            } else {
                selectTime.setTypeface(null, Typeface.BOLD)
                selectTime.setBackgroundResource(R.drawable.button_click_on)
                timePicker.visibility = View.VISIBLE
                tPicker_already_ON = true
            }
        })

        calView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val cal: Calendar = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            val day: String = cal.getDisplayName(DAY_OF_WEEK, SHORT, Locale.KOREA)

            selectDate.setText("${month + 1}월 ${dayOfMonth}일 (${day})")
        }
        timePicker.setOnTimeChangedListener { view, hour, min ->
            if (hour < 12) {
                selectTime.setText("오전 ${hour}:${min}")
            } else {
                selectTime.setText("오후 ${hour - 12}:${min}")
            }
        }
    }

    fun setViewId() {
        selectDate = findViewById(R.id.selectDate)
        selectTime = findViewById(R.id.selectTime)
        calView = findViewById<CalendarView>(R.id.calView)
        timePicker = findViewById(R.id.timePicker)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun viewInit() {
        calView.visibility = View.GONE
        timePicker.visibility = View.GONE

        val cal: Calendar = Calendar.getInstance()
        val month = (cal.get(Calendar.MONTH) + 1).toString()
        val date = cal.get(Calendar.DATE).toString()
        val dayOfWeek = cal.getDisplayName(DAY_OF_WEEK, SHORT, Locale.KOREA)

        selectDate.setText("${month}월 ${date}일 (${dayOfWeek})")
    }


}