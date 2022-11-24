# parking_lot_managesystem

test..

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

## Folder Structure

* [`/src/App.jsx`](./src/App.jsx)
  * 

[`/parkingLot/MainActivity.kt`](./ParkingLot/app/src/main/java/softwareProject/parkingLot/MainActivity.kt)
