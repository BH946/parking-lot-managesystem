package softwareProject.parkingLot.User

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import softwareProject.parkingLot.Map.Parking
import softwareProject.parkingLot.R
import com.google.firebase.database.FirebaseDatabase;
import softwareProject.parkingLot.Map.MapActivity


class ParkingInfoActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val parkingDB = database.getReference()
    lateinit var parking: Parking

    lateinit var parking_name: TextView
    lateinit var parking_road: TextView
    lateinit var parking_tel: TextView

    lateinit var openTime_weekday: TextView
    lateinit var closeTime_weekday: TextView
    lateinit var openTime_saturday: TextView
    lateinit var closeTime_saturday: TextView
    lateinit var openTime_holiday: TextView
    lateinit var closeTime_holiday: TextView

    lateinit var btn_showReservationActivity: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_info)

        initView()
        setViewText()
        setListener()
//        printParkingData()


    }

    fun initView() {
        parking = intent.getSerializableExtra("parking") as Parking

        parking_road = findViewById(R.id.parking_road)
        parking_tel = findViewById(R.id.parking_tel)

        openTime_weekday = findViewById(R.id.opentime_weekday)
        closeTime_weekday = findViewById(R.id.closetime_weekday)
        openTime_saturday = findViewById(R.id.opentime_saturday)
        closeTime_saturday = findViewById(R.id.closetime_saturday)
        openTime_holiday = findViewById(R.id.opentime_holiday)
        closeTime_holiday = findViewById(R.id.closetime_holiday)

        btn_showReservationActivity = findViewById(R.id.showReservation)
    }

    fun setViewText() {
        title = parking.name
        parking_road.setText(parking.road)
        parking_tel.setText(parking.tel)

        openTime_weekday.setText(parking.weekdayStartTime)
        closeTime_weekday.setText(parking.weekdayEndTime)
        openTime_saturday.setText(parking.saturdayStartTime)
        closeTime_saturday.setText(parking.saturdayEndTime)
        openTime_holiday.setText(parking.holidayStartTime)
        closeTime_holiday.setText(parking.holidayEndTime)
    }

    fun setListener() {
        btn_showReservationActivity.setOnClickListener {
            val intent = Intent(this, ReservationActivity::class.java)
            intent.putExtra("parking", parking)
            startActivity(intent)
        }
        parking_tel.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${parking.tel}")
            startActivity(intent)
        }
        parkingDB.get().addOnSuccessListener {
            var counting =
                it.child("Parking").child(parking.id.toString()).child("counting").getValue()
                    .toString().toInt()
            var size =
                it.child("Parking").child(parking.id.toString()).child("size").getValue()
                    .toString().toInt()
            if (counting >= size) {
                btn_showReservationActivity.isEnabled = false
                Toast.makeText(this, "예약 가능한 자리가 없습니다", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun printParkingData() {
        Log.d("parking_data", parking.id.toString());
        Log.d("parking_data", parking.name);
        Log.d("parking_data", parking.lat);
        Log.d("parking_data", parking.lon);
        Log.d("parking_data", parking.category);
        Log.d("parking_data", parking.way);
        Log.d("parking_data", parking.area);
        Log.d("parking_data", parking.road);
        Log.d("parking_data", parking.num);
        Log.d("parking_data", parking.day);
        Log.d("parking_data", parking.weekdayStartTime);
        Log.d("parking_data", parking.weekdayEndTime);
        Log.d("parking_data", parking.saturdayStartTime);
        Log.d("parking_data", parking.saturdayEndTime);
        Log.d("parking_data", parking.holidayStartTime);
        Log.d("parking_data", parking.holidayEndTime);
        Log.d("parking_data", parking.cost);
        Log.d("parking_data", parking.tel);
    }

}