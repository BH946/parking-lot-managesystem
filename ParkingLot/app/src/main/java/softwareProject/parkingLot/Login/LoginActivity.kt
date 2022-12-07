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
    
    var mBackWait:Long = 0
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
    }
    //뒤로가기버튼 클릭
    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(this,"뒤로가기 버튼을 한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()

            //Snackbar.make(view,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.",Snackbar.LENGTH_LONG).show()
        } else {
            finish() //액티비티 종료
        }
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

}



