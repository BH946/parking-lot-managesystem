package softwareProject.parkingLot.Login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import softwareProject.parkingLot.R
import softwareProject.parkingLot.User.UserDB

class SignUpActivity : AppCompatActivity() {
    private lateinit var btn : Button
    private lateinit var auth : FirebaseAuth
    private lateinit var dbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        title = "붕붕아 회원가입"

        // Authentication, Database 초기화
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference
        initSignUpButton()
    }

    private fun initSignUpButton() {
        btn = findViewById<Button>(R.id.btn_sign_in)
        btn.setOnClickListener {
            val name = getInputNickname()
            val email = getInputEmail()
            val password = getInputPassword()
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(password)) {
                Toast.makeText(this,"정보를 바르게 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }

            val intent= Intent(applicationContext, LoginActivity::class.java)

            auth.createUserWithEmailAndPassword(email, password) // firebase가 제공하는 메소드
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"회원가입 완료되었습니다", Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                            addUserToDatabase(name,email,auth.currentUser?.uid!!)

                        } else {
                            Toast.makeText(this,"다시 시도해주세요", Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }
    private fun getInputNickname():String{
        return findViewById<EditText>(R.id.eName).text.toString()
    }
    private fun getInputEmail(): String {
        return findViewById<EditText>(R.id.eID).text.toString()
    }
    private fun getInputPassword(): String {
        return findViewById<EditText>(R.id.ePW).text.toString()
    }
    private fun addUserToDatabase(name:String, email:String, uId: String){
        dbRef.child("user").child(uId).setValue(UserDB(name,email,uId))
    }
}
