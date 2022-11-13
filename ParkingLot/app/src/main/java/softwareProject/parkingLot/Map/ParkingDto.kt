package softwareProject.parkingLot.Map

import com.google.gson.annotations.SerializedName

// 전체 데이터
data class ParkingDto (
    @SerializedName("currentCount") val dataSize: Int,
    @SerializedName("data") val parkings: List<Parking>
)