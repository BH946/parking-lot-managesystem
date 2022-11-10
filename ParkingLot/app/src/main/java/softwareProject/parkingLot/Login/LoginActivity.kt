package softwareProject.parkingLot.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import softwareProject.parkingLot.Host.HostActivity
import softwareProject.parkingLot.Map.MapActivity
import softwareProject.parkingLot.R


class LoginActivity : AppCompatActivity() {
    // 전역
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var auth: FirebaseAuth

    private val btn1 : Button by lazy {
        findViewById<Button>(R.id.btn1)
    }

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

        // 맵 화면 넘어가는지 테스트
        testMapButton()
        // firebase test(로그인, 회원가입)
        auth = FirebaseAuth.getInstance() // 객체 생성

        initLoginButton()
        initSignUpButton()
        initEmailAndPasswordEditText() // id, pw 입력란 비었을시 로그인 버튼 비활성화
    }

    private fun testMapButton() {
        btn1.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initSignUpButton() {
        btn2 = findViewById<Button>(R.id.btn2)
        btn2.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()

            auth.createUserWithEmailAndPassword(email, password) // firebase가 제공하는 메소드
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"회원가입 성공", Toast.LENGTH_SHORT).show()
//                        finish()
                    } else {
                        Toast.makeText(this,"회원가입 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun initLoginButton() {
        btn3 = findViewById<Button>(R.id.btn3)
        btn3.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {task->
                    if(task.isSuccessful) {
                        Toast.makeText(this,"로그인 성공", Toast.LENGTH_SHORT).show()
                        Log.d("로그인 정보", "${auth.uid}")
//                        finish() // 현재 액티비티 종료
                    } else {
                        Toast.makeText(this,"로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun initEmailAndPasswordEditText() {
        // email, password 입력했을때 버튼 활성화
        val email = findViewById<EditText>(R.id.eID)
        val password = findViewById<EditText>(R.id.ePW)
        val loginButton = findViewById<Button>(R.id.btn3)
        val signUpButton = findViewById<Button>(R.id.btn2)

        email.addTextChangedListener {
            var enable = email.text.isNotEmpty() && password.text.isNotEmpty() // t/f
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }
        password.addTextChangedListener {
            var enable = email.text.isNotEmpty() && password.text.isNotEmpty() // t/f
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }
    }

    private fun getInputEmail(): String {
        return findViewById<EditText>(R.id.eID).text.toString()
    }
    private fun getInputPassword(): String {
        return findViewById<EditText>(R.id.ePW).text.toString()
    }
}
