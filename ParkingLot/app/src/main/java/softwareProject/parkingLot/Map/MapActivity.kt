package softwareProject.parkingLot.Map

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
import softwareProject.parkingLot.User.ReservationActivity

class MapActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener {

    // lazy : lateinit과 동일. 다만, val에 사용
    private lateinit var naverMap : NaverMap
    private lateinit var locationSource : FusedLocationSource // 위치 반환 객체
    private lateinit var btn1 : Button

    private val mapView: MapView by lazy {
        findViewById<MapView>(R.id.mapView)
    }
    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.mapViewPager2)
    }
    private lateinit var parkingDto : ParkingDto
    private val viewPagerAdapter = ParkingViewPagerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mapView.onCreate(savedInstanceState) // 액티비티 만들때 onCreate()는 반드시 호출
        title = "붕붕이"

        // 네이버 맵 객체 가져오기(콜백 방식 사용)
        mapView.getMapAsync(this)

        btn1 = findViewById<Button>(R.id.btn1)
        btn1.setOnClickListener {
            val intent = Intent(applicationContext, ReservationActivity::class.java)
            startActivity(intent)
        }
        // viewpager적용
        viewPager.adapter = viewPagerAdapter

        // viewPager에 보이는 페이지가 변화될때 내용
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 페이지 바뀌면 데이터 이용해 맵 위치도 이동
                val selectedParking = viewPagerAdapter.currentList[position]
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(selectedParking.lat.toDouble(), selectedParking.lon.toDouble()))
                    .animate(CameraAnimation.Easing)
                naverMap.moveCamera(cameraUpdate)
            }
        })

        // 감천2 공영 주차장으로 테스트 - id : 11063
        // reference는 db의 root를 의미
//        val parkingDB = FirebaseDatabase.getInstance().getReference().child("Parking").child("11063")
//        val parking = mutableMapOf<String, Any>()
//        parking["user"] = "ccc@ccc.ccc"
//        parking["counting"] = 0
//        parkingDB.updateChildren(parking)

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


    private fun mapAPI(accessKeyNum : Int) {
        // api test
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.odcloud.kr/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val parkingService = retrofit.create(ParkingService::class.java)
        var apiKey : String
        if (accessKeyNum == 0) apiKey = "noPG7qG7dO3ftTu/rjrDZxXLyQ3EwSpIUGmkLTfbCleCIgtBkt2GQFv+bcGhS/+65IKs4pdQ0VqWlRrDiDVmpw=="
        else apiKey = "noPG7qG7dO3ftTu%2FrjrDZxXLyQ3EwSpIUGmkLTfbCleCIgtBkt2GQFv%2BbcGhS%2F%2B65IKs4pdQ0VqWlRrDiDVmpw%3D%3D"
        parkingService.getParkingLocation(apiKey,
            1,10,"26380") // 사하구청 : 26380, 테스트로 데이터 10개만
            .enqueue(object: Callback<ParkingDto> {
                override fun onResponse(call: Call<ParkingDto>, response: Response<ParkingDto>) {
                    // success
                    if(!response.isSuccessful){
                        Log.d("MapActivity", "accessKeyNum : ${accessKeyNum.toString()}") // test 확인용
                        if (accessKeyNum == 0) mapAPI(1) // 재귀
                        else {
                            Toast.makeText(applicationContext, "주차장 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("MapActivity", response.toString())
                        }
                        return
                    }
                    response.body()?.let{
                        Log.d("MapActivity",it.toString())
                        Log.d("MapActivity",it.parkings.toString())
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
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(35.116712,128.968449))
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
            if(!locationSource.isActivated) {
                // 권한 함수는 실행되었지만, 권한 얻기 실패 -> 네이버 맵에 실패 상태 전송
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
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

}