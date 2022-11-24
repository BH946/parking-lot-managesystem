package softwareProject.parkingLot.Map

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import softwareProject.parkingLot.R
import softwareProject.parkingLot.User.ParkingInfoActivity
import softwareProject.parkingLot.User.ReservationActivity

// 뷰페이저 사용때 item관련 xml에 루트 레이아웃은 꼭 너비, 높이가 match여야 한다.
class ParkingViewPagerAdapter : ListAdapter<Parking, ParkingViewPagerAdapter.ItemViewHolder>(differ){
    // inner class로 class 내부에서 class 만듬
    inner class ItemViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        // 매개변수 view는 onCreateViewHolder에서 가져온 레이아웃
        fun bind(parking: Parking) {
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val countTextView = view.findViewById<TextView>(R.id.countTextView)
            val fullCheckTextView = view.findViewById<TextView>(R.id.fullCheckTextView)
            val reservationButton = view.findViewById<Button>(R.id.reservationButton)

//            thumbnailImageView.setImageResource() // 아쉽게 데이터에 이미지가 없음
            titleTextView.text = parking.name

            reservationButton.setOnClickListener{
                val intent = Intent(view.context, ParkingInfoActivity::class.java)
                intent.putExtra("parking", parking)
                view.context.startActivity(intent)
            }

            // realtime db -> 없으면 알아서 생성
            var parkingDB = FirebaseDatabase.getInstance().getReference().child("Parking")
            parkingDB.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(parking.id.toString()).child("user").value == null) {
                        countTextView.text = "0"+"/"+parking.num // 카운팅 수와 전체 자리수
                        fullCheckTextView.text = "여유" // 임의로 설정
                    }
                    else {
                        Log.d("test", snapshot.child(parking.id.toString()).child("user").value.toString())
                        var num = snapshot.child(parking.id.toString()).child("counting").value
                        countTextView.text = num.toString()+"/"+parking.num // 카운팅 수와 전체 자리수
                        if(num.toString().toInt()>=parking.num.toInt()) {
                            fullCheckTextView.text = "만차"
                        }else {
                            num = (parking.num.toInt() - num.toString().toInt()).toString()
                            fullCheckTextView.text = num+" 대 가능"
                        }

                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        }
    }

    // 뷰홀더에 담을 레이아웃 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.item_parking_viewpager, parent, false))
        // 리턴타입 : ItemViewHolder
    }

    // bind함수 실행 설정
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // holder는 위에서 만들어준 ItemViewHolder클래스
        // currentList는 Parking타입의 리스트임
        holder.bind(currentList[position])
    }

    companion object{
        val differ = object: DiffUtil.ItemCallback<Parking>() {
            override fun areItemsTheSame(oldItem: Parking, newItem: Parking): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Parking, newItem: Parking): Boolean {
                return oldItem == newItem
            }
        }
    }

}