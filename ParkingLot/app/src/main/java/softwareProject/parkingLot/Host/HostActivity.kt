package softwareProject.parkingLot.Host

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import softwareProject.parkingLot.R
import java.util.*

class HostActivity : AppCompatActivity() {
    private lateinit var dbRef : DatabaseReference
    private lateinit var name : String
    private lateinit var reservation : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        title = "붕붕아-관리자 모드"
        val text = findViewById<TextView>(R.id.space)
        val cal = Calendar.getInstance()
        var y = 0
        var m = 0
        var d = 0
        var h = 0
        var mi = 0
        val numberAll = findViewById<TextView>(R.id.numberAll)
        val numberRest = findViewById<TextView>(R.id.numberRest)
        val check = findViewById<Button>(R.id.check)
        val edit = findViewById<EditText>(R.id.edit)
        val set = findViewById<Button>(R.id.set)
        val out = findViewById<Button>(R.id.out)

        val host = findViewById<TextView>(R.id.host)
        name = intent.getStringExtra("name").toString()
        host.text=name

        val reservation = findViewById<TextView>(R.id.reservation)
        this.reservation = intent.getStringExtra("reservation_user").toString()
        reservation.text = this.reservation
        //Log.d("txt확인",txt+","+txt1)

        y = cal[Calendar.YEAR]
        m = cal[Calendar.MONTH] + 1
        d = cal[Calendar.DAY_OF_MONTH]
        h = cal[Calendar.HOUR_OF_DAY]
        mi = cal[Calendar.MINUTE]
        text.text = y.toString() + "년" + m + "월" + d + "일" + h + "시" + mi + "분"

        var resUserNum = 0     // 현재 예약 손님
        var allUserNum = 0    //최대 예약손님

        set.setOnClickListener {
            allUserNum = edit.text.toString().toInt()
            numberAll.text = allUserNum.toString()
        }

        check.setOnClickListener {
            resUserNum++
            numberRest.text = resUserNum.toString()
            out.isClickable=true
            if (resUserNum == allUserNum) {
                check.isClickable = false
            }
        }

        out.setOnClickListener {
            resUserNum--
            numberRest.text = resUserNum.toString()
            check.isClickable=true
            if (resUserNum == 0) {
                out.isClickable = false
            }
        }

    }
}