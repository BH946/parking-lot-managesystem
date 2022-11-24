package softwareProject.parkingLot.Map

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// "data" 내부
data class Parking (
    // @SerializedName를 통해 서버 키값 value값 바로 매핑
    @SerializedName("주차장관리번호") val id: Long,
    @SerializedName("주차장명") val name: String,
    @SerializedName("위도") val lat: String,
    @SerializedName("경도") val lon: String,
    @SerializedName("주차장구분") val category: String,
    @SerializedName("주차장유형") val way: String,
    @SerializedName("주차장지번주소") val area: String,
    @SerializedName("주차장도로명주소") val road: String,
    @SerializedName("주차구획수") val num: String,
    @SerializedName("운영요일") val day: String,
    @SerializedName("평일운영시작시각") val weekdayStartTime: String,
    @SerializedName("평일운영종료시각") val weekdayEndTime: String,
    @SerializedName("토요일운영시작시각") val saturdayStartTime: String,
    @SerializedName("토요일운영종료시각") val saturdayEndTime: String,
    @SerializedName("공휴일운영시작시각") val holidayStartTime: String,
    @SerializedName("공휴일운영종료시각") val holidayEndTime: String,
    @SerializedName("요금정보") val cost: String,
    @SerializedName("연락처") val tel: String,
) : Serializable