package softwareProject.parkingLot.Host

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import softwareProject.parkingLot.R
import com.google.firebase.database.*

class HostLoginActivity : AppCompatActivity() {
    private lateinit var btn: Button
    private lateinit var dbRef: DatabaseReference
    private lateinit var name: String
    private lateinit var reservation_user: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hostlogin)
        initLoginButton()

        dbRef = FirebaseDatabase.getInstance().reference

    }

    private fun initLoginButton() {
        btn = findViewById<Button>(R.id.btn_host)
        btn.setOnClickListener {
            val number = findViewById<EditText>(R.id.Number).text
            val intent = Intent(applicationContext, HostActivity::class.java)

            intent.putExtra("number", number.toString())
            if (TextUtils.isEmpty(number)) {
                Toast.makeText(this, "정보를 바르게 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }

            dbRef.child("host").child(number.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value == null) {
                            Toast.makeText(this@HostLoginActivity, "정보를 바르게 입력해주세요", Toast.LENGTH_SHORT).show()

                        } else {
                            if (snapshot.child("name").value != null) {
                                val data = snapshot.getValue(HostDB::class.java)
                                name = data!!.name
                                intent.putExtra("name", name)

                            }
                            if (snapshot.child("reservation_user").value != null) {
                                val data = snapshot.getValue(HostDB::class.java)
                                reservation_user = data!!.reservation_user
                                intent.putExtra("reservation_user", reservation_user)
                            }
                            startActivity(intent)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }
}