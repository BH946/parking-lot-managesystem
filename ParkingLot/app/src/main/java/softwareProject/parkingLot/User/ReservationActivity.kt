package softwareProject.parkingLot.User

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import softwareProject.parkingLot.Map.MapActivity
import softwareProject.parkingLot.Map.Parking
import softwareProject.parkingLot.R
import java.util.*
import java.util.Calendar.*

class ReservationActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val parkingDB = database.getReference()
    lateinit var parking: Parking

    private val auth = FirebaseAuth.getInstance() // firebase auth 객체

    lateinit var selectDate: TextView
    lateinit var selectTime: TextView
    lateinit var calView: CalendarView
    lateinit var timePicker: TimePicker
    lateinit var selectReservationTime: TextView
    lateinit var numberPicker: NumberPicker
    lateinit var btnReservation: Button

    var cal_already_ON = false
    var tPicker_already_ON = false
    var numberPicker_already_ON = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)
        title = "예약등록"

        setViewId()
        initView()
        setListener()


    }

    fun setViewId() {
        selectDate = findViewById(R.id.selectDate)
        calView = findViewById<CalendarView>(R.id.calView)
        selectTime = findViewById(R.id.selectTime)
        timePicker = findViewById(R.id.timePicker)
        selectReservationTime = findViewById(R.id.selectReservationTime)
        numberPicker = findViewById(R.id.numberPicker)
        btnReservation = findViewById(R.id.btn_reservation)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initView() {
        parking = intent.getSerializableExtra("parking") as Parking

        calView.visibility = View.GONE
        timePicker.visibility = View.GONE
        numberPicker.visibility = View.GONE

        val cal: Calendar = Calendar.getInstance()
        val month = (cal.get(Calendar.MONTH) + 1).toString()
        val date = cal.get(Calendar.DATE).toString()
        val dayOfWeek = cal.getDisplayName(DAY_OF_WEEK, SHORT, Locale.KOREA)
        numberPicker.minValue = 1
        numberPicker.maxValue = 12

        selectDate.setText("${month}월 ${date}일 (${dayOfWeek})")
    }

    fun setListener() {
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
        selectReservationTime.setOnClickListener(View.OnClickListener {
            if (numberPicker_already_ON) {
                selectReservationTime.setTypeface(null, Typeface.NORMAL)
                selectReservationTime.setBackgroundResource(R.drawable.button_click_off)
                numberPicker.visibility = View.GONE
                numberPicker_already_ON = false
            } else {
                selectReservationTime.setTypeface(null, Typeface.BOLD)
                selectReservationTime.setBackgroundResource(R.drawable.button_click_on)
                numberPicker.visibility = View.VISIBLE
                numberPicker_already_ON = true
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
        numberPicker.setOnValueChangedListener { numberPicker, i1, i2 ->
            selectReservationTime.setText("${i2} 시간")

        }
        btnReservation.setOnClickListener {
            parkingDB.get().addOnSuccessListener {
                val currentUser = auth.currentUser?.uid.toString()
                val currentUser_name =
                    it.child("user").child(currentUser).child("name").getValue().toString()
                var counting =
                    it.child("Parking").child(parking.id.toString()).child("counting").getValue()
                        .toString().toInt()

                // host -> parking id -> reservation_user에 유저 닉네임으로 설정
                parkingDB.child("host").child(parking.id.toString()).child("reservation_user")
                    .setValue(currentUser_name)
                // user -> id -> reservation에 예약한 예약한 주차장 id 저장
                parkingDB.child("user").child(currentUser).child("reservation")
                    .setValue(parking.id.toString())

                if (counting == null) {
                    parkingDB.child("Parking").child(parking.id.toString()).child("counting")
                        .setValue(1)
                } else {
                    counting += 1
                    parkingDB.child("Parking").child(parking.id.toString()).child("counting")
                        .setValue(counting)
                }
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
            }
        }


    }

}