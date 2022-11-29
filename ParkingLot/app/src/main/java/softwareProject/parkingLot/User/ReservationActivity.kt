package softwareProject.parkingLot.User

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
    lateinit var current_Calendar: Calendar
    lateinit var reservation_Calendar: Calendar

    // 테스트 코드
    val TEST = true


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

        // 캘린더뷰, 타임피커 숨기기
        calView.visibility = View.GONE
        timePicker.visibility = View.GONE

        // 현재시간, 예약시간 설정, 예약시간은 현재시간으로부터 1시간 추가
        current_Calendar = Calendar.getInstance()
        reservation_Calendar = Calendar.getInstance()

        // 예약시간은 현재시간으로부터 1시간 추가
        val reservation_time = current_Calendar.get(Calendar.HOUR) + 1
        reservation_Calendar.apply { set(Calendar.HOUR, reservation_time) }

        // timePicker 설정
        timePicker.setIs24HourView(true)
        timePicker.hour = reservation_Calendar.get(Calendar.HOUR)
        timePicker.minute = reservation_Calendar.get(Calendar.MINUTE)

        // 예약날짜 TextView 설정
        val current_month = (reservation_Calendar.get(Calendar.MONTH) + 1).toString()
        val current_date = reservation_Calendar.get(Calendar.DATE)
        val current_dayOfWeek = reservation_Calendar.getDisplayName(DAY_OF_WEEK, SHORT, Locale.KOREA)
        selectDate.setText("${current_month}월 ${current_date}일 (${current_dayOfWeek})")

        // 예약시간 TextView 설정
        val current_hour = reservation_Calendar.get(Calendar.HOUR)
        val current_minute = reservation_Calendar.get(Calendar.MINUTE)
        selectTime.setText("${current_hour}시 ${current_minute}분")
    }

    fun setListener() {
        selectDate.setOnClickListener(View.OnClickListener {
            if (cal_already_ON) {
                selectDate.setBackgroundResource(R.drawable.button_click_off)
                calView.visibility = View.GONE
                cal_already_ON = false
            } else {
                selectDate.setBackgroundResource(R.drawable.button_click_on)
                calView.visibility = View.VISIBLE
                cal_already_ON = true
            }
        })
        selectTime.setOnClickListener(View.OnClickListener {
            if (tPicker_already_ON) {
                selectTime.setBackgroundResource(R.drawable.button_click_off)
                timePicker.visibility = View.GONE
                tPicker_already_ON = false
            } else {
                selectTime.setBackgroundResource(R.drawable.button_click_on)
                timePicker.visibility = View.VISIBLE
                tPicker_already_ON = true
            }
        })

        calView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            reservation_Calendar.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
            val day = reservation_Calendar.getDisplayName(DAY_OF_WEEK, SHORT, Locale.KOREA)

            selectDate.setText("${month + 1}월 ${dayOfMonth}일 (${day})")
        }
        timePicker.setOnTimeChangedListener { view, hour, min ->
            reservation_Calendar.apply {
                set(Calendar.HOUR, hour)
                set(Calendar.MINUTE, hour)
            }
            selectTime.setText("${hour}시 ${min}분")
        }
        // 예약하기 버튼 클릭 리스너
        btnReservation.setOnClickListener {
            // 현재시간보다 과거시간을 예약할 경우 알림
            // TEST를 통해 해제 가능
            current_Calendar = Calendar.getInstance()
            if (TEST && reservation_Calendar.timeInMillis.toInt() + 60000 < current_Calendar.timeInMillis.toInt()) {
                // 현재시간과 예약시간의 1분차이까지는 허용
                Toast.makeText(this, "예약 날짜,시간을 확인해 주세요", Toast.LENGTH_LONG).show()
            } else {
                // DB에 데이터 저장
                parkingDB.get().addOnSuccessListener {
                    // user -> [currentUser] -> name 값 받아오기
                    val currentUser_name =
                        it.child("user").child(currentUser).child("name").getValue().toString()
                    // Parking -> [parking.id] -> counting 값 받아오기
                    var counting =
                        it.child("Parking").child(parking.id.toString()).child("counting")
                            .getValue()
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
                        .setValue(reservation_Calendar.timeInMillis.toString())
                    Log.d("cal!!", reservation_Calendar.timeInMillis.toString())

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

}