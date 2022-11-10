package softwareProject.parkingLot.Login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import softwareProject.parkingLot.Host.HostActivity
import softwareProject.parkingLot.Map.MapActivity
import softwareProject.parkingLot.R


class LoginActivity : AppCompatActivity() {
    lateinit var btn1 : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val sign_up = findViewById<View>(R.id.sign_up) as TextView
        sign_up.setOnClickListener {
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(intent)
        }
        val manager_mode = findViewById<View>(R.id.manager_mode) as TextView
        manager_mode.setOnClickListener {
            val intent = Intent(applicationContext, HostActivity::class.java)
            startActivity(intent)
        }
        btn1 = findViewById<Button>(R.id.btn1)
        btn1.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            startActivity(intent)
        }
    }
}
