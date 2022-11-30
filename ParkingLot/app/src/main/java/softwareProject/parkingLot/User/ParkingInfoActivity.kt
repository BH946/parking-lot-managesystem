package softwareProject.parkingLot.User

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import softwareProject.parkingLot.Map.Parking
import softwareProject.parkingLot.R
import com.google.firebase.database.FirebaseDatabase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import softwareProject.parkingLot.Map.MapActivity


class ParkingInfoActivity : AppCompatActivity(), OnMapReadyCallback {
    val database = FirebaseDatabase.getInstance()
    val parkingDB = database.reference
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

    private lateinit var naverMap: NaverMap
    private val mapView: MapView by lazy {
        findViewById<MapView>(R.id.mapView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_info)
        mapView.onCreate(savedInstanceState) // 액티비티 만들때 onCreate()는 반드시 호출

        initView()
        setViewText()
        setListener()
//        printParkingData()

        mapView.getMapAsync(this)
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
        parking_road.text = parking.road
        parking_tel.text = parking.tel

        openTime_weekday.text = parking.weekdayStartTime
        closeTime_weekday.text = parking.weekdayEndTime
        openTime_saturday.text = parking.saturdayStartTime
        closeTime_saturday.text = parking.saturdayEndTime
        openTime_holiday.text = parking.holidayStartTime
        closeTime_holiday.text = parking.holidayEndTime
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
            var parkingIsNull = it.child("Parking").child(parking.id.toString()).child("counting").value
            var counting: Int
            var size: Int
            if (parkingIsNull == null) {
                counting = 0
                size = Integer.parseInt(parking.num)
                btn_showReservationActivity.text="예약을 받지 않는 주차장입니다"
                btn_showReservationActivity.isEnabled = false

            } else {
                counting = it.child("Parking").child(parking.id.toString()).child("counting").value.toString().toInt()
                size = it.child("Parking").child(parking.id.toString()).child("size").value.toString().toInt()
            }
            if (counting >= size) {
                btn_showReservationActivity.isEnabled = false
                Toast.makeText(this, "예약 가능한 자리가 없습니다", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun printParkingData() {
        Log.d("parking_data", parking.id.toString())
        Log.d("parking_data", parking.name)
        Log.d("parking_data", parking.lat)
        Log.d("parking_data", parking.lon)
        Log.d("parking_data", parking.category)
        Log.d("parking_data", parking.way)
        Log.d("parking_data", parking.area)
        Log.d("parking_data", parking.road)
        Log.d("parking_data", parking.num)
        Log.d("parking_data", parking.day)
        Log.d("parking_data", parking.weekdayStartTime)
        Log.d("parking_data", parking.weekdayEndTime)
        Log.d("parking_data", parking.saturdayStartTime)
        Log.d("parking_data", parking.saturdayEndTime)
        Log.d("parking_data", parking.holidayStartTime)
        Log.d("parking_data", parking.holidayEndTime)
        Log.d("parking_data", parking.cost)
        Log.d("parking_data", parking.tel)
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map

        // default : 동아대 위치
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(parking.lat.toDouble(), parking.lon.toDouble()))
        naverMap.moveCamera(cameraUpdate)

        val marker = Marker()
        marker.position = LatLng(parking.lat.toDouble(), parking.lon.toDouble())
        marker.map = naverMap
        marker.tag = parking.id
        marker.icon = MarkerIcons.BLACK
        marker.iconTintColor = Color.RED
    }

}