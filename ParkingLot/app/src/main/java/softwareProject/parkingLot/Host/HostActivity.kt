package softwareProject.parkingLot.Host

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import softwareProject.parkingLot.Map.Parking
import softwareProject.parkingLot.R
import java.util.*
import kotlin.concurrent.thread

class HostActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val parkingDB = database.getReference()
    private lateinit var name : String
    private lateinit var reservation : String
    var resUserNum : Int = 0
    var allUserNum:Int =0
    lateinit var parking: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)

        val text = findViewById<TextView>(R.id.space)
        var y = 0
        var m = 0
        var d = 0
        var h = 0
        var mi = 0
        val numberAll = findViewById<TextView>(R.id.numberAll)
        val numberRest = findViewById<TextView>(R.id.numberRest)
        val check = findViewById<Button>(R.id.check)
        val out = findViewById<Button>(R.id.out)

        val host = findViewById<TextView>(R.id.host)
        name = intent.getStringExtra("name").toString()
        host.text=name

        val reservation = findViewById<TextView>(R.id.reservation)
        this.reservation = intent.getStringExtra("reservation_user").toString()
        reservation.text = this.reservation
        //Log.d("txt확인",txt+","+txt1)

        val number = intent.getStringExtra("number")
        var cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
        y = cal[Calendar.YEAR]
        m = cal[Calendar.MONTH] + 1
        d = cal[Calendar.DAY_OF_MONTH]
        h = cal[Calendar.HOUR_OF_DAY]
        mi = cal[Calendar.MINUTE]
        text.text = y.toString() + "년" + m + "월" + d + "일" + h + "시" + mi + "분"
        thread(start = true){
            var i = 0
            while(true) {
                var cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
                runOnUiThread {
                    y = cal[Calendar.YEAR]
                    m = cal[Calendar.MONTH] + 1
                    d = cal[Calendar.DAY_OF_MONTH]
                    h = cal[Calendar.HOUR_OF_DAY]
                    mi = cal[Calendar.MINUTE]
                    text.text = y.toString() + "년" + m + "월" + d + "일" + h + "시" + mi + "분"
                }
                Thread.sleep(1000)
            }
        }

        parkingDB.child("Parking").child(number.toString())
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("counting").value != null) {
                        numberRest.text =
                            snapshot.child("counting").value.toString()
                        numberAll.text=
                            snapshot.child("size").value.toString()
                        resUserNum=
                            snapshot.child("counting").value.toString().toInt()
                        allUserNum=
                            snapshot.child("size").value.toString().toInt()
                    }

                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        /* parkingDB.get().addOnSuccessListener {
             numberRest.text =
                 it.child("Parking").child(number.toString()).child("counting").getValue()
                     .toString()
             numberAll.text=
                 it.child("Parking").child(number.toString()).child("size").getValue()
                     .toString()
             resUserNum=
                     it.child("Parking").child(number.toString()).child("counting").getValue().toString().toInt()
             allUserNum=
                     it.child("Parking").child(number.toString()).child("size").getValue().toString().toInt()
         }*/



        check.setOnClickListener {
            out.setBackgroundResource(R.drawable.btn_host)
            resUserNum++
            numberRest.text = resUserNum.toString()
            out.isClickable=true
            if (resUserNum == allUserNum) {
                check.isClickable = false
                check.setBackgroundResource(R.drawable.btn_host2)
            }
            parkingDB.child("Parking").child(number.toString()).child("counting").setValue(resUserNum.toString())
        }

        out.setOnClickListener {
            check.setBackgroundResource(R.drawable.btn_host)
            resUserNum--
            numberRest.text = resUserNum.toString()
            check.isClickable=true

            if (resUserNum == 0) {
                out.isClickable = false
                out.setBackgroundResource(R.drawable.btn_host2)
            }
            parkingDB.child("Parking").child(number.toString()).child("counting").setValue(resUserNum.toString())
        }

        //parkingDB.child("Parking").child(number.toString()).child("counting").setValue(resUserNum.toString())

    }
}