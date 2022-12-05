package softwareProject.parkingLot.Map

import android.app.TabActivity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import softwareProject.parkingLot.R
import java.util.*
import kotlin.concurrent.thread

class MapActivity : TabActivity(), OnMapReadyCallback, Overlay.OnClickListener, View.OnClickListener {
    private lateinit var parking: Parking

    // DB 객체 설정
    private val database = FirebaseDatabase.getInstance()
    private val parkingDB = database.getReference()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser?.uid.toString()

    // lazy : lateinit과 동일. 다만, val에 사용
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource // 위치 반환 객체

    // Thread Control Flag
    private var threadFlag = true

    private val mapView: MapView by lazy {
        findViewById<MapView>(R.id.mapView)
    }
    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.mapViewPager2)
    }
    private lateinit var parkingDto: ParkingDto
    private val viewPagerAdapter = ParkingViewPagerAdapter()

    private lateinit var user_name: TextView
    private lateinit var parking_lot_name: TextView
    private lateinit var startTime: TextView
    private lateinit var usedTimeLayout: LinearLayout
    private lateinit var remainingTime: TextView
    private lateinit var calcelReservationLayout: LinearLayout
    private lateinit var btn_CancelReservation_: Button
    private lateinit var reservationInfoLayout: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mapView.onCreate(savedInstanceState) // 액티비티 만들때 onCreate()는 반드시 호출

        initFun()
        realtimeChangeInfo()

        // 네이버 맵 객체 가져오기(콜백 방식 사용)
        mapView.getMapAsync(this)
        // viewpager적용
        viewPager.adapter = viewPagerAdapter

        // viewPager에 보이는 페이지가 변화될때 내용
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 페이지 바뀌면 데이터 이용해 맵 위치도 이동
                val selectedParking = viewPagerAdapter.currentList[position]
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(selectedParking.lat.toDouble(), selectedParking.lon.toDouble()))
                    .animate(CameraAnimation.Easing)
                naverMap.moveCamera(cameraUpdate)
            }
        })

        // 탭
        var tabHost = this.tabHost
        var tabSpecMap = tabHost.newTabSpec("Map").setIndicator("Map")
        tabSpecMap.setContent(R.id.tabMap)
        tabHost.addTab(tabSpecMap)

        var tabSpecMy = tabHost.newTabSpec("My").setIndicator("My")
        tabSpecMy.setContent(R.id.tabMy)
        tabHost.addTab(tabSpecMy)

        tabHost.currentTab = 0

    }

    // 오버레이는 마커 총집합을 의미. 즉, 마커 클릭 때 동작 설정
    override fun onClick(overlay: Overlay): Boolean {
        val selectedParking = viewPagerAdapter.currentList.firstOrNull {
            // 전체 리스트 돌다가 아래 조건문에 맞는게 있으면 바로 반환(first로 첫번째만)
            // 없으면 Null반환 하는 함수이다.
            it.id == overlay.tag // 마커에 tag속성에 id를 저장했었음
        }
        selectedParking?.let {
            // selectedParking이 null이 아니라면 수행
            val position = viewPagerAdapter.currentList.indexOf(it) // index 구함
            viewPager.currentItem = position // viewPager에 해당 item을 줌으로써 viewPager가 보여주는 뷰가 변경
        }
        return true
    }

    // 예약취소 버튼 클릭 이벤트
    override fun onClick(view: View?) {
        if (view != null) {
            if (view.id == R.id.btn_CancelReservation) {
                parkingDB.get().addOnSuccessListener {
                    var parking_id = it.child("user").child(currentUser).child("parking_id").value.toString()
                    var counting = it.child("Parking").child(parking_id.toString()).child("counting").value.toString().toInt()

                    // host의 예약유저와 user의 주차장 예약정보들을 제거
                    parkingDB.child("Parking").child(parking_id.toString()).child("counting").setValue(counting-1)
                    parkingDB.child("host").child(parking_id.toString()).child("reservation_user").setValue(null)
                    parkingDB.child("user").child(currentUser).child("parking_id").setValue(null)
                    parkingDB.child("user").child(currentUser).child("parking_name").setValue(null)
                    parkingDB.child("user").child(currentUser).child("reservation_time").setValue(null)
                    parkingDB.child("user").child(currentUser).child("reservation_time_mills").setValue(null)

                    // 주차장 정보 및 예약취소 레이아웃 안보이게 설정
                    reservationInfoLayout.visibility = View.GONE
                    calcelReservationLayout.visibility = View.GONE
                    Toast.makeText(this, "예약을 취소하였습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun parkingMarking() {
        // 마커 찍기
        parkingDto.parkings.forEach { parking ->
            val marker = Marker()
            marker.position = LatLng(parking.lat.toDouble(), parking.lon.toDouble())
            marker.onClickListener = this
            marker.map = naverMap
            marker.tag = parking.id
            marker.icon = MarkerIcons.BLACK
            marker.iconTintColor = Color.RED
        }
    }


    private fun mapAPI(accessKeyNum: Int) {
        // api test
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.odcloud.kr/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val parkingService = retrofit.create(ParkingService::class.java)
        var apiKey: String
        if (accessKeyNum == 0) apiKey = "noPG7qG7dO3ftTu/rjrDZxXLyQ3EwSpIUGmkLTfbCleCIgtBkt2GQFv+bcGhS/+65IKs4pdQ0VqWlRrDiDVmpw=="
        else apiKey = "noPG7qG7dO3ftTu%2FrjrDZxXLyQ3EwSpIUGmkLTfbCleCIgtBkt2GQFv%2BbcGhS%2F%2B65IKs4pdQ0VqWlRrDiDVmpw%3D%3D"
        parkingService.getParkingLocation(
            apiKey,
            1, 10, "26380"
        ) // 사하구청 : 26380, 테스트로 데이터 10개만
            .enqueue(object : Callback<ParkingDto> {
                override fun onResponse(call: Call<ParkingDto>, response: Response<ParkingDto>) {
                    // success
                    if (!response.isSuccessful) {
                        Log.d("MapActivity", "accessKeyNum : ${accessKeyNum.toString()}") // test 확인용
                        if (accessKeyNum == 0) mapAPI(1) // 재귀
                        else {
                            Toast.makeText(applicationContext, "주차장 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("MapActivity", response.toString())
                        }
                        return
                    }
                    response.body()?.let {
                        Log.d("MapActivity", it.toString())
                        Log.d("MapActivity", it.parkings.toString())
                        parkingDto = it
                        // 2. 마커를 이용해서 주차장 위치 설정
                        parkingMarking()
                        // 3. 뷰페이저2로 프래그먼트 전환 사용 - 구성 : 이미지, 주차장명, 카운팅수, 혼잡도, 예약버튼
                        // 여기선 프래그먼트가 아닌 뷰 전환을 사용(onCreate에서 적용)

                        // 그리고 데이터 적용
                        viewPagerAdapter.submitList(parkingDto.parkings)

                        // 예약버튼 -> 두환이형 xml로 넘어가기(Intent로 데이터 같이 넘겨주기)
                        // => ParkingViewPagerAdapter.kt에서 확인
                    }
                }

                override fun onFailure(call: Call<ParkingDto>, t: Throwable) {
                    // fail
                    Toast.makeText(applicationContext, "주차장 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }

            })
    }

    // 이 함수는 인터페이스 클래스이므로 구현후 사용
    override fun onMapReady(map: NaverMap) { // 네이버 맵 getMapAsync하면 자동으로 호출되는 함수
        naverMap = map

        // default : 동아대 위치
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(35.116712, 128.968449))
        naverMap.moveCamera(cameraUpdate)
        // 현위치 버튼(권한 필요)
        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = true
        // 위치 정보 받아옴(해당 함수 내부적으로 권한 확인도 포함)
        locationSource = FusedLocationSource(this@MapActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        val marker = Marker()
        marker.position = LatLng(35.116712, 128.968449)

        // 1. 주차장 api 데이터 저장 함수 실행
        // 2. 마커를 이용해서 주차장 위치 설정
        // 3. 마커 이후 bottom에 정보보여주는 형태로 xml구성해서(뷰페이저2로 프래그먼트 화면전환 사용) 간단한 카운팅수와 예약 버튼으로 구성
        mapAPI(0)
    }

    // 권한할 때 사용하는 함수 오버라이딩(boolean 타입)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 정상이면 FusedLocationSource 에서 인자로보낸 상태코드 1000이 requestCode로 가짐
        // 요청 코드 확인
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return // 권한 실패
        }
        // 권한 함수 정상 작동 판단
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                // 권한 함수는 실행되었지만, 권한 얻기 실패 -> 네이버 맵에 실패 상태 전송
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
    }

    private fun initFun() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser?.uid.toString()

        user_name = findViewById<TextView>(R.id.user_name)
        parking_lot_name = findViewById<TextView>(R.id.parking_lot_name)
        startTime = findViewById<TextView>(R.id.startTime)
        usedTimeLayout = findViewById<LinearLayout>(R.id.usingTimeLayout)
        remainingTime = findViewById<TextView>(R.id.usingTime)
        calcelReservationLayout = findViewById<LinearLayout>(R.id.cancelReservationLayout)
        btn_CancelReservation_ = findViewById<Button>(R.id.btn_CancelReservation)
        reservationInfoLayout = findViewById<LinearLayout>(R.id.reservationInfoLayout)
        btn_CancelReservation_.setOnClickListener(this)



    }

    private fun realtimeChangeInfo(){
        // 사용시간 실시간 변경
        Log.d("스레드", "스레드 시작")
        thread(start = true) {
            while (threadFlag) {
                runOnUiThread {
                    Log.d("스레드", "스레드 작동중")
                    // realtime db -> 없으면 알아서 생성
                    val parkingDB = FirebaseDatabase.getInstance().getReference().child("user")
                    parkingDB.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            user_name.text = snapshot.child(currentUser).child("name").value.toString()
                            // 주차장 예약 내역을 parking_id롤 통해 확인
                            if (snapshot.child(currentUser).child("parking_id").value != null) {
                                // 예약 내역이 있을 경우 예약 정보, 사용시간, 예약취소버튼을 표시
                                var calCurrent = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))

                                parking_lot_name.text = snapshot.child(currentUser).child("parking_name").value.toString()
                                startTime.text = snapshot.child(currentUser).child("reservation_time").value.toString()
                                calCurrent.timeInMillis = calCurrent.timeInMillis - snapshot.child(currentUser).child("reservation_time_mills").value.toString().toLong()

                                remainingTime.text = "${calCurrent.timeInMillis / (1000 * 60 * 60)} : " + "${calCurrent.timeInMillis / (1000 * 60) % 60}"

                                if (calCurrent.timeInMillis < 0) {
                                    // 아직 사용하지 않은 경우 사용시간 비표시 및 예약취소 표시
                                    usedTimeLayout.visibility = View.GONE
                                    calcelReservationLayout.visibility = View.VISIBLE
                                } else {
                                    // 이미 사용한 경우 사용시간 표시
                                    usedTimeLayout.visibility = View.VISIBLE
                                    calcelReservationLayout.visibility = View.GONE
                                }
                            } else {
                                // 예약내역이 없는 경우 예약정보 비표시
                                reservationInfoLayout.visibility = View.GONE
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
                Thread.sleep(1000)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        threadFlag = true
        realtimeChangeInfo()
        mapView.onStart()
    }

    override fun onRestart() {
        super.onRestart()
        threadFlag = true
        realtimeChangeInfo()
    }

    override fun onResume() {
        super.onResume()
        threadFlag = true
        realtimeChangeInfo()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        threadFlag = false
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        threadFlag = false
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        threadFlag = false
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        // 권한 상태코드
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    //뒤로가기버튼 클릭
    override fun onBackPressed() {
        finish() //액티비티 종료
    }
}