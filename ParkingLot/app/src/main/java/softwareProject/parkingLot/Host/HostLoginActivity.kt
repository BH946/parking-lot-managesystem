package softwareProject.parkingLot.Host

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import softwareProject.parkingLot.Map.MapActivity
import softwareProject.parkingLot.R
import softwareProject.parkingLot.User.UserDB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HostLoginActivity : AppCompatActivity() {
    private lateinit var btn : Button
    private lateinit var dbRef : DatabaseReference
    private lateinit var txt : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hostlogin)
        title = "붕붕아 관리자 모드"
        initLoginButton()

        dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("host").child("5678").setValue(HostDB("부산대 주차장","5678",""))
    }
    private fun initLoginButton() {
        btn = findViewById<Button>(R.id.btn_host)
        btn.setOnClickListener {
            val number = findViewById<EditText>(R.id.Number).text
            val intent= Intent(applicationContext, HostActivity::class.java)

            if (TextUtils.isEmpty(number)) {
                Toast.makeText(this,"정보를 바르게 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }

            dbRef.child("host").child(number.toString())
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.child("name").value == null) {
                            return
                        }else {
                            val data = snapshot.getValue(HostDB::class.java)
                            txt = data!!.name
                            intent.putExtra("name",txt)
                            startActivity(intent)
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })


        }
    }
}