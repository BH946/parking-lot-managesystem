package softwareProject.parkingLot.Host

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import softwareProject.parkingLot.R
import java.util.*

class HostActivity : AppCompatActivity() {
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
        y = cal[Calendar.YEAR]
        m = cal[Calendar.MONTH] + 1
        d = cal[Calendar.DAY_OF_MONTH]
        h = cal[Calendar.HOUR_OF_DAY]
        mi = cal[Calendar.MINUTE]
        text.text = y.toString() + "년" + m + "월" + d + "일" + h + "시" + mi + "분"
    }
}

//https://haruple.tistory.com/150