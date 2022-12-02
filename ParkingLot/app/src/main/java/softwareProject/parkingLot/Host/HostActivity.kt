package softwareProject.parkingLot.Host

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.database.*
import softwareProject.parkingLot.Login.LoginActivity
import softwareProject.parkingLot.R
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class HostActivity : AppCompatActivity() {
    // DB 객체
    val database = FirebaseDatabase.getInstance()
    val parkingDB = database.getReference()

    // intent data
    lateinit var parking_id: String // 주차장 id
    lateinit var userNickName: String   // 유저 닉네임

    // 주차장 자리 수
    var resUserNum: Int = 0 // 현재 차량 수
    var allUserNum: Int = 0 // 총 자리 수

    // 스레드 관리 변수
    private var threadFlag = true

    // 뷰 연결할 객체
    lateinit var hostName: TextView
    lateinit var reservationUser: TextView
    lateinit var numberAll: TextView
    lateinit var numberRest: TextView
    lateinit var check: Button
    lateinit var out: Button
    lateinit var timeView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        Log.d("액티비티", "HostActivity onCreate")

        setViewId()
        initView()
        realtimeChangeTime()
        setListener()

    }

    // 객체와 뷰 연결
    fun setViewId() {
        hostName = findViewById(R.id.host)
        reservationUser = findViewById(R.id.reservationUser)
        numberAll = findViewById(R.id.numberAll)
        numberRest = findViewById(R.id.numberRest)
        check = findViewById(R.id.check)
        out = findViewById(R.id.out)
        timeView = findViewById(R.id.timeView)
    }

    // 뷰 초기설정
    fun initView() {
        parking_id = intent.getStringExtra("number").toString()    // 주차장 id
        userNickName = intent.getStringExtra("reservation_user").toString()
        hostName.text = intent.getStringExtra("name").toString() // 예약자 이름
        if (userNickName == "null") {
            reservationUser.text = ""
        } else {
            reservationUser.text = userNickName + "님"
        }
    }

    // 실시간 시간 변경
    fun realtimeChangeTime() {
        thread(start = true) {
            while (threadFlag) {
                var cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
                runOnUiThread {
                    val year = cal[Calendar.YEAR]
                    val month = cal[Calendar.MONTH] + 1
                    val day = cal[Calendar.DAY_OF_MONTH]
                    val hour = cal[Calendar.HOUR_OF_DAY]
                    val minute = cal[Calendar.MINUTE]
                    timeView.text = year.toString() + "년 " + month + "월 " + day + "일 " + hour + "시 " + minute + "분"
                }
                threadFlag=false
                Thread.sleep(1000)
            }
        }
    }

    // 리스너 설정
    fun setListener() {
        parkingDB.child("Parking").child(parking_id.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("counting").value != null) {
                        numberRest.text =
                            snapshot.child("counting").value.toString()
                        numberAll.text =
                            snapshot.child("size").value.toString()
                        resUserNum =
                            snapshot.child("counting").value.toString().toInt()
                        allUserNum =
                            snapshot.child("size").value.toString().toInt()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        // 예약확인 버튼 리스너
        check.setOnClickListener {
            out.setBackgroundResource(R.drawable.btn_host)
            resUserNum++
            numberRest.text = resUserNum.toString()
            out.isClickable = true
            if (resUserNum == allUserNum) {
                check.isClickable = false
                check.setBackgroundResource(R.drawable.btn_host2)
            }
            parkingDB.child("Parking").child(parking_id.toString()).child("counting").setValue(resUserNum.toString())
        }

        // 출차하기 버튼 리스너
        out.setOnClickListener {
            check.setBackgroundResource(R.drawable.btn_host)
            resUserNum--
            numberRest.text = resUserNum.toString()
            check.isClickable = true

            if (resUserNum == 0) {
                out.isClickable = false
                out.setBackgroundResource(R.drawable.btn_host2)
            }
            parkingDB.child("Parking").child(parking_id.toString()).child("counting").setValue(resUserNum.toString())
        }
    }

    override fun onStart() {
        threadFlag = true
        realtimeChangeTime()
        super.onStart()
    }

    override fun onPause() {
        threadFlag = false
        super.onPause()
    }

    override fun onStop() {
        threadFlag = false
        super.onStop()
    }

    override fun onDestroy() {
        threadFlag = false
        super.onDestroy()
        Log.d("액티비티", "HostActivity onDestroy")
    }

    //뒤로가기버튼 클릭
    override fun onBackPressed() {
        //finish() /* 액티비티 종료 */
        // 해당 앱의 루트 액티비티를 종료
// (API  16미만은 ActivityCompat.finishAffinity())
        ActivityCompat.finishAffinity(this)

// 현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어
        System.runFinalization()

        //재시작
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        Toast.makeText(this, "관리자 모드가 종료되었습니다.", Toast.LENGTH_SHORT).show()
// 현재 액티비티를 종료시킨다.
        exitProcess(0)
    }
}