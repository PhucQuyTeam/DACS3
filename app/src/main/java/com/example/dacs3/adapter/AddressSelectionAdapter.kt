package com.example.dacs3.ui.checkout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dacs3.R
import com.example.dacs3.model.AddressDTO

class AddressSelectionAdapter(
    private val addresses: List<AddressDTO>,
    private val onAddressSelected: (AddressDTO) -> Unit,
    private val onEditClick: (AddressDTO) -> Unit
) : RecyclerView.Adapter<AddressSelectionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvReceiverName)
        val tvPhone: TextView = view.findViewById(R.id.tvReceiverPhone)
        val tvAddress: TextView = view.findViewById(R.id.tvFullAddress)
        val tvEdit: TextView = view.findViewById(R.id.tvEditAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_address_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = addresses[position]

        holder.tvName.text = address.receiverName
        holder.tvPhone.text = " | ${address.receiverPhone}"
        holder.tvAddress.text = address.fullAddress


        // Sự kiện khi bấm vào cả dòng địa chỉ để chọn
        holder.itemView.setOnClickListener {
            addresses.forEach { it.isSelected = false }
            address.isSelected = true
            notifyDataSetChanged() // Cập nhật lại UI để RadioButton nhảy sang mục mới
            onAddressSelected(address) // Trả kết quả về Activity
        }

        // Sự kiện khi bấm vào chữ "Sửa"
        holder.tvEdit.setOnClickListener {
            onEditClick(address)
        }
    }

    override fun getItemCount() = addresses.size
}