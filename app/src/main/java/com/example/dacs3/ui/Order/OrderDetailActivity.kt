package com.example.dacs3.ui.order

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.R
import com.example.dacs3.databinding.ActivityOrderDetailBinding
import com.example.dacs3.network.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private var selectedImageUri: Uri? = null
    private var previewCard: View? = null
    private var previewImageView: ImageView? = null

    // 1. Bộ chọn ảnh từ thư viện
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            previewImageView?.setImageURI(it)
            previewCard?.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Setup Toolbar & Back
        setSupportActionBar(binding.toolbarOrderDetail)
        binding.toolbarOrderDetail.setNavigationOnClickListener { finish() }

        // 3. Nhận dữ liệu từ màn hình Lịch sử đơn hàng
        val orderId = intent.getIntExtra("ORDER_ID", 0)
        val orderTotal = intent.getDoubleExtra("ORDER_TOTAL", 0.0)
        val orderAddress = intent.getStringExtra("ORDER_ADDRESS") ?: "Chưa rõ địa chỉ"
        val orderDate = intent.getStringExtra("ORDER_DATE") ?: "Chưa rõ ngày đặt"
        val paymentStatus = intent.getStringExtra("ORDER_PAYMENT_STATUS")
        val deliveryStatus = intent.getStringExtra("ORDER_DELIVERY_STATUS") ?: "pending"

        // 4. Đổ dữ liệu lên giao diện
        binding.tvDetailOrderId.text = "Mã đơn: #$orderId"
        binding.tvDetailDate.text = "Ngày đặt: $orderDate"
        binding.tvDetailAddress.text = orderAddress
        binding.tvDetailTotal.text = "${String.format("%,.0f", orderTotal)}đ"

        if (paymentStatus.equals("paid", ignoreCase = true) || paymentStatus.equals("Đã thanh toán", ignoreCase = true)) {
            binding.tvDetailPaymentStatus.text = "Đã thanh toán"
            binding.tvDetailPaymentStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
        } else {
            binding.tvDetailPaymentStatus.text = "Chưa thanh toán (COD)"
            binding.tvDetailPaymentStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
        }

        // 5. Cài đặt RecyclerView
        binding.rvOrderItems.layoutManager = LinearLayoutManager(this)

        // 6. Gọi API lấy danh sách món hàng
        fetchOrderItems(orderId, deliveryStatus)
    }

    private fun fetchOrderItems(orderId: Int, deliveryStatus: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getInstance(this@OrderDetailActivity).getOrderItems(orderId)
                if (response.isSuccessful && response.body() != null) {
                    val itemList = response.body()!!

                    // Khởi tạo Adapter với 3 tham số: List, Trạng thái giao, và Hàm xử lý khi bấm Đánh giá
                    val adapter = OrderDetailAdapter(itemList, deliveryStatus) { productId, position ->
                        showReviewBottomSheet(productId, orderId, position, itemList)
                    }
                    binding.rvOrderItems.adapter = adapter
                } else {
                    Toast.makeText(this@OrderDetailActivity, "Không thể tải chi tiết sản phẩm", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@OrderDetailActivity, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showReviewBottomSheet(productId: Int, orderId: Int, position: Int, itemList: List<com.example.dacs3.model.OrderItemDTO>) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_review, null)
        bottomSheetDialog.setContentView(view)

        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val edtComment = view.findViewById<EditText>(R.id.edtComment)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmitReview)
        val btnSelectImage = view.findViewById<View>(R.id.tvSelectImage)

        previewCard = view.findViewById(R.id.cardPreview)
        previewImageView = view.findViewById(R.id.imgReviewPreview)

        // Reset trạng thái ảnh mỗi lần mở
        selectedImageUri = null
        previewCard?.visibility = View.GONE

        btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnSubmit.setOnClickListener {
            val rating = ratingBar.rating.toInt()
            val comment = edtComment.text.toString().trim()

            // Chuẩn bị dữ liệu Multipart
            val pIdBody = productId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val oIdBody = orderId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val ratBody = rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val cmtBody = comment.toRequestBody("text/plain".toMediaTypeOrNull())

            var imagePart: MultipartBody.Part? = null
            selectedImageUri?.let { uri ->
                val file = getFileFromUri(uri)
                file?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", it.name, requestFile)
                }
            }

            btnSubmit.isEnabled = false
            btnSubmit.text = "Đang gửi..."

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.getInstance(this@OrderDetailActivity)
                        .addReview(pIdBody, oIdBody, ratBody, cmtBody, imagePart)

                    if (response.isSuccessful) {
                        Toast.makeText(this@OrderDetailActivity, "Cảm ơn sếp đã đánh giá!", Toast.LENGTH_SHORT).show()
                        bottomSheetDialog.dismiss()

                        // LOCK NÚT LẠI NGAY LẬP TỨC TRÊN GIAO DIỆN
                        itemList[position].isReviewed = true
                        binding.rvOrderItems.adapter?.notifyItemChanged(position)
                    } else {
                        Toast.makeText(this@OrderDetailActivity, "Gửi thất bại, thử lại sau!", Toast.LENGTH_SHORT).show()
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Gửi đánh giá"
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@OrderDetailActivity, "Lỗi kết nối!", Toast.LENGTH_SHORT).show()
                    btnSubmit.isEnabled = true
                }
            }
        }
        bottomSheetDialog.show()
    }

    private fun getFileFromUri(uri: Uri): File? {
        val file = File(cacheDir, "temp_review_image_${System.currentTimeMillis()}.jpg")
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            null
        }
    }
}