package softwareProject.parkingLot

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.map.*
import softwareProject.parkingLot.Login.LoginActivity

//class MainActivity : AppCompatActivity() {
//
//    lateinit var btn1 : Button
//    lateinit var btn2 : Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        title = "붕붕이"
//
//        btn1 = findViewById<Button>(R.id.btn1)
//        btn2 = findViewById<Button>(R.id.btn2)
//        btn1.setOnClickListener {
//            var intent = Intent(applicationContext, ReservationActivity::class.java)
//            startActivity(intent)
//        }
//        btn2.setOnClickListener {
//            var intent = Intent(applicationContext, MapActivity::class.java)
//            startActivity(intent)
//        }
//    }
//}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "붕붕아"
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
