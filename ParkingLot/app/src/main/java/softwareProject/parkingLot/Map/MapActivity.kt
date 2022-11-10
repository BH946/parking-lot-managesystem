package softwareProject.parkingLot.Map

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import softwareProject.parkingLot.Login.LoginActivity
import softwareProject.parkingLot.R
import softwareProject.parkingLot.User.ReservationActivity

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    // lazy : lateinit과 동일. 다만, val에 사용
    private lateinit var naverMap : NaverMap
    private lateinit var locationSource : FusedLocationSource // 위치 반환 객체
    private lateinit var btn1 : Button
    private val mapView: MapView by lazy {
        findViewById<MapView>(R.id.mapView)
    }

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



        // 주차장 api test
        val key = "noPG7qG7dO3ftTu%2FrjrDZxXLyQ3EwSpIUGmkLTfbCleCIgtBkt2GQFv%2BbcGhS%2F%2B65IKs4pdQ0VqWlRrDiDVmpw%3D%3D"
        val page = "&page=1"
        val perPage = "&perPage=10"
        val cond = "&cond=26380"
        val url = ""

    }

    // 이 함수는 인터페이스 클래스이므로 구현후 사용
    override fun onMapReady(map: NaverMap) {
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
        marker.map = naverMap
        marker.icon = MarkerIcons.BLACK
        marker.iconTintColor = Color.RED
        // 1. 주차장 데이터 오픈 api 찾아서 데이터 가공
        Log.d("api", "데이터 확인")
        // 사하구청 : 26380





        // 2. 마커를 이용해서 주차장 위치 설정
        // 3. 마커 이후 bottom에 xml구성해서 상세정보 버튼, 예약 버튼으로 구성(xml 필요)
        // 상세정보 -> 공공 데이터 활용(xml 필요)
        // 예약버튼 -> 두환이형 xml로 넘어가기
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