package softwareProject.parkingLot.Map

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ParkingService {
    @GET("api/15050093/v1/uddi:d19c8e21-4445-43fe-b2a6-865dff832e08")
    fun getParkingLocation(
        @Query("serviceKey") apiKey: String,
        @Query("page") page: Int,
        @Query("perPage") perPage: Int,
        @Query("cond[지역코드::EQ]") cond: String
    ): Call<ParkingDto>

}