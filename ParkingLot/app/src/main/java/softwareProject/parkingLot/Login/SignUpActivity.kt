package softwareProject.parkingLot.Login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import softwareProject.parkingLot.R

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }

    fun onClick(view: View?) {
        finish()
    }
}
