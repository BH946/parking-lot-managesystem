package softwareProject.parkingLot.User

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import softwareProject.parkingLot.R

class ReservationActivity : AppCompatActivity() {

    lateinit var selectDate1: TextView
    var dateString = ""
    var timeString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        selectDate1 = findViewById(R.id.selectDate1)
        selectDate1.setOnClickListener {
            val textData: String = selectDate1.text.toString()
            val builder = SpannableStringBuilder(textData)
            val boldSpan = StyleSpan(Typeface.BOLD)
        }
    }
}