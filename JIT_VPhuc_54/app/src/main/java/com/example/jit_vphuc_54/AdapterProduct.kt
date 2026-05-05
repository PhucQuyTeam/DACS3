package com.example.jit_vphuc_54

import android.net.Uri
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.jit_vphuc_54.model.Product
import com.example.jit_vphuc_54.Viewmodel.ProductViewModel
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdapterProduct(
    private val list: MutableList<Product>,
    private val viewModel: ProductViewModel // thêm viewModel
) : RecyclerView.Adapter<AdapterProduct.ViewHolder>() {

    var onDeleteClick: ((Product) -> Unit)? = null
    var onEditClick: ((Product) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val img: ImageView = view.findViewById(R.id.imgProduct)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]

        holder.tvName.text = product.name
        holder.tvPrice.text = "Giá: ${product.price}"
        holder.tvDesc.text = product.description

        holder.img.setImageDrawable(null)
        product.imageUri?.let {
            val file = File(it)
            if (file.exists()) {
                holder.img.setImageURI(Uri.fromFile(file))
            }
        }

        // checkbox
        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = product.isSelected
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            product.isSelected = isChecked

            CoroutineScope(Dispatchers.IO).launch {
                viewModel.updateProduct(product)
            }
        }

        // delete
        holder.btnDelete.setOnClickListener {
            onDeleteClick?.invoke(product)
        }

        // edit
        holder.btnEdit.setOnClickListener {
            onEditClick?.invoke(product)
        }
    }

    fun setData(newList: List<Product>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
//    fun selectAllAndUpdateDB(viewModel: ProductViewModel) {
//        list.forEach {
//            it.isSelected = true
//            viewModel.updateProduct(it)
//        }
//        notifyDataSetChanged()
//    }
}