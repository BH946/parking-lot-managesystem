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
import androidx.core.view.marginLeft
import androidx.core.view.setPadding
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
    // DB 객체 설정
    private val database = FirebaseDatabase.getInstance()
    private val parkingDB = database.getReference()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser?.uid.toString()

    // intent로 받아온 Parking 데이터 저장할 객체
    lateinit var parking: Parking

    // xml 뷰 연결 객체
    lateinit var selectDate: TextView
    lateinit var selectTime: TextView
    lateinit var calView: CalendarView
    lateinit var timePicker: TimePicker
    lateinit var btnReservation: Button

    // 해당 뷰의 오픈 유무 체크용 변수
    var cal_already_ON = false
    var tPicker_already_ON = false

    // 캘린더 객체
    lateinit var cal: Calendar

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

        btnReservation = findViewById(R.id.btn_reservation)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initView() {
        parking = intent.getSerializableExtra("parking") as Parking

        calView.visibility = View.GONE
        timePicker.visibility = View.GONE

        // 기본 예약 날짜 설정
        cal = Calendar.getInstance()
        val month = (cal.get(Calendar.MONTH) + 1).toString()
        val date = cal.get(Calendar.DATE).toString()
        val dayOfWeek = cal.getDisplayName(DAY_OF_WEEK, SHORT, Locale.KOREA)
        selectDate.setText("${month}월 ${date}일 (${dayOfWeek})")

        // 기본 예약 시간 설정
        timePicker.setIs24HourView(true)
        var hour = timePicker.hour
        var minute = timePicker.minute
        selectTime.setText("${hour}시 ${minute}분")
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

        calView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            val day: String = cal.getDisplayName(DAY_OF_WEEK, SHORT, Locale.KOREA)

            selectDate.setText("${month + 1}월 ${dayOfMonth}일 (${day})")
        }
        timePicker.setOnTimeChangedListener { view, hour, min ->
            cal.set(
                cal[Calendar.YEAR],
                cal[Calendar.MONTH],
                cal[Calendar.DAY_OF_MONTH],
                hour,
                min,
                0
            )

            selectTime.setText("${hour}시 ${min}분")
        }
        // 예약하기 버튼 클릭 시
        btnReservation.setOnClickListener {
            parkingDB.get().addOnSuccessListener {
                // user -> [currentUser] -> name 값 받아오기
                val currentUser_name =
                    it.child("user").child(currentUser).child("name").getValue().toString()
                // Parking -> [parking.id] -> counting 값 받아오기
                var counting =
                    it.child("Parking").child(parking.id.toString()).child("counting").getValue()
                        .toString().toInt()

                // host -> parking id -> reservation_user에 유저 닉네임으로 설정
                parkingDB.child("host").child(parking.id.toString()).child("reservation_user")
                    .setValue(currentUser_name)
                // user -> id -> reservation에 예약한 주차장 id 저장
                parkingDB.child("user").child(currentUser).child("parking_name")
                    .setValue(parking.name.toString())
                // user -> id -> reservation_time에 예약시간 저장
                parkingDB.child("user").child(currentUser).child("reservation_time")
                    .setValue(selectDate.text.toString() + " " + selectTime.text.toString())
                // user -> id -> reservation_time_mills에 예약시간 저장
                parkingDB.child("user").child(currentUser).child("reservation_time_mills")
                    .setValue(cal.timeInMillis.toString())
                Log.d("cal!!", cal.timeInMillis.toString())

                // 예약자수+1 후 DB에 저장
                counting += 1
                parkingDB.child("Parking").child(parking.id.toString()).child("counting")
                    .setValue(counting)

                // 첫 화면으로 돌아감
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
            }
        }


    }

}