package softwareProject.parkingLot.Login


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import softwareProject.parkingLot.Host.HostActivity
import softwareProject.parkingLot.Host.HostLoginActivity
import softwareProject.parkingLot.Map.MapActivity
import softwareProject.parkingLot.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity:AppCompatActivity() {
    private lateinit var btn : Button
    private lateinit var auth : FirebaseAuth
    private val btn1 : Button by lazy {
        findViewById<Button>(R.id.btn1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance() // 객체 생성
        initLoginButton()

        val sign_up=findViewById<View>(R.id.sign_up) as TextView
        sign_up.setOnClickListener{
            val intent=Intent(applicationContext, SignUpActivity::class.java)
            startActivity(intent)
        }
        val manager_mode = findViewById<View>(R.id.manager_mode) as TextView
        manager_mode.setOnClickListener{
            val intent=Intent(applicationContext, HostLoginActivity::class.java)
            startActivity(intent)
        }

        // test용 맵 버튼 => 테스트할때 로그인해서 맵넘기기 귀찮아서
        // test id : abc@naver.com
        // test pw : 123456
        testMapButton()
    }
    private fun initLoginButton() {
        btn = findViewById<Button>(R.id.btn_login)
        btn.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()
            val intent=Intent(applicationContext, MapActivity::class.java)

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this,"정보를 바르게 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }


            auth.signInWithEmailAndPassword(email, password) // firebase가 제공하는 메소드
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"환영합니다", Toast.LENGTH_SHORT).show()
                            startActivity(intent)

                        } else {
                            Toast.makeText(this,"다시 시도해주세요", Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }
    private fun getInputEmail(): String {
        return findViewById<EditText>(R.id.ID).text.toString()
    }
    private fun getInputPassword(): String {
        return findViewById<EditText>(R.id.PW).text.toString()
    }

    // test용 버튼
    private fun testMapButton() {
        btn1.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            startActivity(intent)
        }
    }
}



