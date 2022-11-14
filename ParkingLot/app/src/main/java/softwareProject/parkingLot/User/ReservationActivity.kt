package softwareProject.parkingLot.User

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import softwareProject.parkingLot.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Calendar.*

class ReservationActivity : AppCompatActivity() {

    lateinit var selectDate1: TextView
    lateinit var selectDate2: TextView
    lateinit var calView1: CalendarView
    lateinit var calView2: CalendarView

    var selectYear1: Int = 0
    var selectMonth1: Int = 0
    var selectDay1: Int = 0
    var selectYear2: Int = 0
    var selectMonth2: Int = 0
    var selectDay2: Int = 0

    var cal1_already_ON = false
    var cal2_already_ON = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        setViewId()
        calViewInit()

        selectDate1.setOnClickListener(View.OnClickListener {
            calView2.visibility = View.GONE
            cal2_already_ON = false

            selectDate2.setTypeface(null, Typeface.NORMAL)
            selectDate2.setBackgroundResource(R.drawable.button_click_off)

            if (cal1_already_ON) {
                selectDate1.setTypeface(null, Typeface.NORMAL)
                selectDate1.setBackgroundResource(R.drawable.button_click_off)
                calView1.visibility = View.GONE
                cal1_already_ON = false
            } else {
                selectDate1.setTypeface(null, Typeface.BOLD)
                selectDate1.setBackgroundResource(R.drawable.button_click_on)
                calView1.visibility = View.VISIBLE
                cal1_already_ON = true
            }
        })
        selectDate2.setOnClickListener(View.OnClickListener {
            calView1.visibility = View.GONE
            cal1_already_ON = false
            selectDate1.setTypeface(null, Typeface.NORMAL)
            selectDate1.setBackgroundResource(R.drawable.button_click_off)


            if (cal2_already_ON) {
                selectDate2.setTypeface(null, Typeface.NORMAL)
                selectDate2.setBackgroundResource(R.drawable.button_click_off)
                calView2.visibility = View.GONE
                cal2_already_ON = false
            } else {
                selectDate2.setTypeface(null, Typeface.BOLD)
                selectDate2.setBackgroundResource(R.drawable.button_click_on)
                calView2.visibility = View.VISIBLE
                cal2_already_ON = true
            }
        })

        calView1.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val cal:Calendar = Calendar.getInstance()
            cal.set(year,month,dayOfMonth)
            val day: String = cal.getDisplayName(DAY_OF_WEEK, SHORT, Locale.KOREA)

            selectDate1.setText("${month+1}월 ${dayOfMonth}일 (${day})")
        }
        calView2.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val cal:Calendar = Calendar.getInstance()
            cal.set(year,month,dayOfMonth)
            val day: String = cal.getDisplayName(DAY_OF_WEEK, SHORT, Locale.KOREA)
            selectDate2.setText("${month+1}월 ${dayOfMonth}일 (${day})")

        }
    }
    fun setViewId(){
        selectDate1 = findViewById(R.id.selectDate1)
        selectDate2 = findViewById(R.id.selectDate2)
        calView1 = findViewById<CalendarView>(R.id.calView1)
        calView2 = findViewById<CalendarView>(R.id.calView2)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun calViewInit(){
        calView2.visibility = View.GONE

        var current=LocalDateTime.now()
        var formatter=DateTimeFormatter.ofPattern("MM월 dd일 (E)").withLocale(Locale.forLanguageTag("ko"))
        var formatted=current.format(formatter)
        selectDate1.setText(formatted)
        selectDate2.setText(formatted)
    }


}