import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.houseconnect.R

class EmployeeAdapter(private var employees: List<Employee>) :
    RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    private var listener: OnItemClickListener? = null

    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val workTextView: TextView = itemView.findViewById(R.id.workTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val phoneButton: Button = itemView.findViewById(R.id.phoneButton)
        val smsButton: Button = itemView.findViewById(R.id.smsButton)

        init {
            cardView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onCardClick(position)
                }
            }

            phoneButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onPhoneButtonClick(position)
                }
            }

            smsButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onSmsButtonClick(position)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onCardClick(position: Int)
        fun onPhoneButtonClick(position: Int)
        fun onSmsButtonClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun submitList(newList: List<Employee>) {
        employees = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_employee, parent, false)
        return EmployeeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val currentItem = employees[position]

        // Bind data to views
        holder.nameTextView.text = currentItem.name
        holder.workTextView.text = currentItem.work
        holder.priceTextView.text = currentItem.price.toString()
        holder.locationTextView.text = currentItem.location

        holder.cardView.setOnClickListener {
            listener?.onCardClick(position)
        }
    }

    override fun getItemCount() = employees.size
}