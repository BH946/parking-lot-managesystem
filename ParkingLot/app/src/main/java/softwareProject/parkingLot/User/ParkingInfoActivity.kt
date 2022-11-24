package softwareProject.parkingLot.User

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import softwareProject.parkingLot.Map.Parking
import softwareProject.parkingLot.R

class ParkingInfoActivity : AppCompatActivity() {

    lateinit var parking_name: TextView
    lateinit var parking_road: TextView
    lateinit var parking_tel: TextView

    lateinit var btn_reservation: Button

    var myIntent = intent
    lateinit var parking: Parking

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_info)

        initView()
        setViewText()
        setClickListener()
        printParkingData()

    }

    fun initView() {
        parking = intent.getSerializableExtra("parking") as Parking

        parking_road = findViewById(R.id.parking_road)
        parking_tel = findViewById(R.id.parking_tel)

        btn_reservation = findViewById(R.id.showReservation)
    }

    fun setViewText() {
        title = parking.name
        parking_road.setText(parking.road)
        parking_tel.setText(parking.tel)
    }

    fun setClickListener() {
        btn_reservation.setOnClickListener {
            startActivity(Intent(this, ReservationActivity::class.java))
        }
    }


    fun printParkingData() {
        Log.d("parking data!!!!!!", parking.id.toString());
        Log.d("parking data!!!!!!", parking.name);
        Log.d("parking data!!!!!!", parking.lat);
        Log.d("parking data!!!!!!", parking.lon);
        Log.d("parking data!!!!!!", parking.category);
        Log.d("parking data!!!!!!", parking.way);
        Log.d("parking data!!!!!!", parking.area);
        Log.d("parking data!!!!!!", parking.road);
        Log.d("parking data!!!!!!", parking.num);
        Log.d("parking data!!!!!!", parking.day);
        Log.d("운영시간!!!!!!!!!!!!!", parking.weekdayStartTime);
        Log.d("운영시간!!!!!!!!!!!!!", parking.weekdayEndTime);
        Log.d("운영시간!!!!!!!!!!!!!", parking.saturdayStartTime);
        Log.d("운영시간!!!!!!!!!!!!!", parking.saturdayEndTime);
        Log.d("운영시간!!!!!!!!!!!!!", parking.holidayStartTime);
        Log.d("parking data!!!!!!", parking.holidayEndTime);
        Log.d("parking data!!!!!!", parking.cost);
        Log.d("parking data!!!!!!", parking.tel);
    }

}