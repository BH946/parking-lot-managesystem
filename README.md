# parking_lot_managesystem
<img src=".\images\image-20230105171320013.png" alt="image-20220824171926573" />

![image-20230105171320013](\parking_lot_managesystem\images\image-20230105171320013.png)

![image-20230105171353956](.\images\image-20230105171353956.png)

![image-20230105171426114](.\images\image-20230105171426114.png)

![image-20230105171501222](.\images\image-20230105171501222.png)

![image-20230105171443082](.\images\image-20230105171443082.png)

![image-20230105171524847](.\images\image-20230105171524847.png)

![image-20230105171554538](.\images\image-20230105171554538.png)

![image-20230105171612802](.\images\image-20230105171612802.png)

<br>

## check version for firebase

* [link](https://firebase.google.com/docs/android/setup)
  * 해당 링크에서 최소 요구조건 버전 체크
  * API 레벨 19는 Android 4.4를 의미하며 AVD의 API가 19이상을 의미
* [link](https://developer.android.com/about/versions/12/setup-sdk?hl=ko)
  * sdk설치 Tip

<br>

## using vesion

```
ext.kotlin_version = "1.4.32"
classpath "com.android.tools.build:gradle:4.1.2"
jvmTarget = '1.8'

compileSdkVersion 31
buildToolsVersion "30.0.3"
minSdkVersion 16
targetSdkVersion 31
```

```
dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation 'androidx.core:core-ktx:1.7.0'
    implementation 'androidx.appcompat:appcompat:1.4.1'
    implementation 'com.google.android.material:material:1.5.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.3'
    testImplementation 'junit:junit:4.+'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'

    // naver map sdk(api)
    implementation 'com.naver.maps:map-sdk:3.15.0'
    implementation 'com.google.android.gms:play-services-location:18.0.0'
}
```

```
AVD는 API24, pixel2 구동
안드로이드 스튜디오 4.1.2
```

<br>

## Architecture Document

![image-20230105171219152](.\images\image-20230105171219152.png)

<br>

## DB Structure

![firebase realtime database 구조](.\images\firebase realtime database 구조.png)

<br>

## Folder Structure

* [`/parkingLot/MainActivity.kt`](./ParkingLot/app/src/main/java/softwareProject/parkingLot/MainActivity.kt)
  * 루트 파일(제일 처음 실행)
  * 로딩 화면(`activity_main.xml`) -> 로그인 화면(`LoginActivity.kt`) 순으로 실행
* [`/parkingLot/Login/LoginActivity.kt`](./ParkingLot/app/src/main/java/softwareProject/parkingLot/Login/LoginActivity.kt)
  * 로그인 화면이며 `firebase` DB를 활용해서 구현
  * 로그인시 `MapActivity.kt` 로 이동
  * 관리자 로그인 화면 이동시 `HostLoginActivity.kt` 로 이동
* [`/parkingLot/Login/SignUpActivity.kt`](./ParkingLot/app/src/main/java/softwareProject/parkingLot/Login/SignUpActivity.kt)
  * 회원가입 화면이며 `firebase` DB를 활용해서 구현
* [`/parkingLot/Map/MapActivity.kt`](./ParkingLot/app/src/main/java/softwareProject/parkingLot/Map/MapActivity.kt)
  * 맵 화면을 구성하는 액티비티
    * `네이버 맵 API` 활용
    * 주차장 위치 데이터는 `공공 데이터 포털 API` 활용
    * `ParkingViewPagerAdapter.kt` 에 데이터 전달
  * [`/parkingLot/Map/Parking.kt`](./ParkingLot/app/src/main/java/softwareProject/parkingLot/Map/Parking.kt)
    * API 로 가져온 데이터를 가공하기 위한 모델
  * [`/parkingLot/Map/ParkingDto.kt`](./ParkingLot/app/src/main/java/softwareProject/parkingLot/Map/ParkingDto.kt)
    * API 로 가져온 데이터를 가공하기 위한 모델 - `Parking.kt 상위 모델`
  * [`/parkingLot/Map/ParkingService.kt`](./ParkingLot/app/src/main/java/softwareProject/parkingLot/Map/ParkingService.kt)
    * `retrofit2` 라이브러리로 API를 `GET 방식`으로 불러오는 파일
* [`/parkingLot/Map/ParkingViewPagerAdapter.kt`](./ParkingLot/app/src/main/java/softwareProject/parkingLot/Map/ParkingViewPagerAdapter.kt)
  * 슬라이드 형태의 UI를 사용하기 위해 `ViewPager` 기능을 사용하는 파일
  * `MapActivity.kt` 에서 데이터 받고, `firebase` DB의 데이터도 활용
* [`/parkingLot/User/ReservationActivity.kt`](./ParkingLot/app/src/main/java/softwareProject/parkingLot/User/ReservationActivity.kt)
  * 주차장 예약하기 위해 세부정보를 나타내는 파일
* [`/parkingLot/User/ParkingInfoActivity.kt`](./ParkingLot/app/src/main/java/softwareProject/parkingLot/User/ParkingInfoActivity.kt)
  * 주차장 예약하는 파일
  * `firebase` DB에 카운팅값을 추가하게 됨
* [`/parkingLot/Host/HostActivity.kt`](./ParkingLot/app/src/main/java/softwareProject/parkingLot/Host/HostActivity.kt)
  * 호스트 화면을 보여주는 파일
  * `firebase` DB의 데이터 활용
  * [`/parkingLot/Host/HostLoginActivity.kt`](./ParkingLot/app/src/main/java/softwareProject/parkingLot/Host/HostLoginActivity.kt)
    * 호스트 로그인 파일이며, 로그인은 개발자가 제공하는 키값으로 로그인
    * 로그인 -> `HostActivity.kt` 로 이동
* [`/layout/activity_main.xml`](./ParkingLot/app/src/main/res/layout/activity_main.xml)
  * 앱 실행 초기 로딩 화면 -> 이후 `activity_login.xml` 화면
* [`/layout/activity_login.xml`](./ParkingLot/app/src/main/res/layout/activity_login.xml)
  * 로그인 화면을 구성
* [`/layout/activity_signup.xml`](./ParkingLot/app/src/main/res/layout/activity_signup.xml)
  * 회원가입 화면을 구성
* [`/layout/activity_map.xml`](./ParkingLot/app/src/main/res/layout/activity_map.xml)
  * 맵 화면을 구성(주차장 위치 정보 제공)
  * [`/layout/item_parking_viewpager.xml`](./ParkingLot/app/src/main/res/layout/item_parking_viewpager.xml)
    * `viewpager` 기능을 사용하는데 구성한 item 화면
  * [`/layout/activity_reservation.xml`](./ParkingLot/app/src/main/res/layout/activity_reservation.xml)
    * 주차장 세부 정보로 구성
  * [`/layout/activity_parking_info.xml`](./ParkingLot/app/src/main/res/layout/activity_parking_info.xml)
    * 주차장 예약 화면으로 구성
* [`/layout/activity_hostlogin.xml`](./ParkingLot/app/src/main/res/layout/activity_hostlogin.xml)
  * 호스트 로그인 화면을 구성
  * [`/layout/activity_host.xml`](./ParkingLot/app/src/main/res/layout/activity_host.xml)
    * 호스트(관리자) 모드 화면을 구성
* [`/values/api_key.xml`](./ParkingLot/app/src/main/res/values/api_key.xml)
  * 네이버 맵, 공공 데이터 API 키로 구성
* [`/build.gradle`](./ParkingLot/app/build.gradle)
  * naver map sdk(api)
  * firebase - authentication, realtime db
  * multidex
  * retrofit
  * gson

<br>
