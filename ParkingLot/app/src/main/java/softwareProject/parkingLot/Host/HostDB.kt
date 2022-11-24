package softwareProject.parkingLot.Host

data class HostDB(
        var name:String="",
        var parkinglot_number:String="",
        var reservation_user:String="",
)

/*
* 남은 자리수 이상으로 예약자를 받으면?
* 예약자 리스트를 따로 만들어서 host db에 전달해야하는가
*/
