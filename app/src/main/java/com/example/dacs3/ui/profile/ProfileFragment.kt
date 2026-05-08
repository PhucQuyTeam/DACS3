package com.example.dacs3.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.dacs3.R
import com.example.dacs3.databinding.FragmentProfileBinding
import com.example.dacs3.network.RetrofitClient
import com.example.dacs3.ui.auth.LoginActivity
import com.example.dacs3.Token.TokenManager
import com.example.dacs3.model.UserDTO
import com.example.dacs3.model.UserProfileDTO
import com.example.dacs3.ui.order.OrderHistoryActivity
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager
    private var currentUser: UserProfileDTO? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo TokenManager
        tokenManager = TokenManager(requireContext())

        // 1. GÁN SỰ KIỆN CHO CÁC NÚT BẤM (Ưu tiên nút Đăng xuất trước)
        setupListeners()

        // 2. GỌI API LẤY THÔNG TIN NGƯỜI DÙNG
        loadUserProfile()
    }override fun onResume() {
        super.onResume()
        // Mỗi khi đóng màn hình Edit, Fragment này hiện lên lại -> Nó sẽ tự gọi lại API để kéo dữ liệu mới nhất
        loadUserProfile()
    }


    private fun setupListeners() {
        // --- NÚT ĐĂNG XUẤT (Cứu cánh khỏi lỗi 403) ---
        binding.btnLogout.setOnClickListener {
            // 1. Xóa sạch Token trong điện thoại
            tokenManager.clearTokens()

            // 2. Chuyển về trang Đăng nhập
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            // Lệnh này cực kỳ quan trọng: Xóa sạch lịch sử các trang trước đó,
            // để người dùng không thể bấm phím Back quay lại app được nữa.
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Nút Thay đổi thông tin
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)

            intent.putExtra("EXTRA_NAME", currentUser?.name ?: "")
            intent.putExtra("EXTRA_EMAIL", currentUser?.email ?: "")
            intent.putExtra("EXTRA_PHONE", currentUser?.numberPhone ?: "")
            intent.putExtra("EXTRA_AVATAR", currentUser?.avatar ?: "")

            startActivity(intent)
        }
        // Nút Giỏ hàng
        binding.btnCart.setOnClickListener {
            val intent = android.content.Intent(requireContext(), com.example.dacs3.ui.cart.CartActivity::class.java)
            startActivity(intent)
        }
        binding.btnPending.setOnClickListener { // Nhớ đặt id btnPendingOrders trong XML
            val intent = Intent(requireContext(), OrderHistoryActivity::class.java)
            intent.putExtra("TARGET_TAB", 0) // Tab 0
            startActivity(intent)
        }

        // 3. Nút Icon "Đang giao"
        binding.btnShipping.setOnClickListener { // Nhớ đặt id btnShippingOrders trong XML
            val intent = Intent(requireContext(), OrderHistoryActivity::class.java)
            intent.putExtra("TARGET_TAB", 1) // Tab 1
            startActivity(intent)
        }

        // 4. Nút Icon "Hoàn thành"
        binding.btnCompleted.setOnClickListener { // Nhớ đặt id btnCompletedOrders trong XML
            val intent = Intent(requireContext(), OrderHistoryActivity::class.java)
            intent.putExtra("TARGET_TAB", 2) // Tab 2
            startActivity(intent)
        }

        // Nút Lịch sử đơn hàng
        binding.btnOrderHistory.setOnClickListener {
            val intent = Intent(requireContext(), OrderHistoryActivity::class.java)
            intent.putExtra("TARGET_TAB", 0) // Tab 0
            startActivity(intent)
        }
        binding.btnProfileInfo.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)

            intent.putExtra("EXTRA_NAME", currentUser?.name ?: "")
            intent.putExtra("EXTRA_EMAIL", currentUser?.email ?: "")
            intent.putExtra("EXTRA_PHONE", currentUser?.numberPhone ?: "")
            intent.putExtra("EXTRA_AVATAR", currentUser?.avatar ?: "")

            startActivity(intent)
        }
    }

    private fun loadUserProfile() {
        // Hiện Tên mặc định hoặc loading trong lúc chờ API
        binding.tvUserName.text = "Đang tải..."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Gọi API từ Retrofit (Đã tự động đính kèm Token trong Header)
                // Lưu ý: Đổi .getUserProfile() thành tên hàm thực tế trong ApiService của bạn
                val response = RetrofitClient.getInstance(requireContext()).getUserProfile()

                if (response.isSuccessful && response.body() != null) {
                    val userProfile = response.body()!!

                    currentUser = userProfile

                    // 1. Đổ dữ liệu Tên
                    binding.tvUserName.text = userProfile.name ?: "Chưa cập nhật tên"

                    // 2. Xử lý Avatar bo tròn bằng thư viện Glide
                    if (!userProfile.avatar.isNullOrEmpty()) {
                        // Nối URL gốc của backend với tên file ảnh trong DB
                        // VD: http://10.0.2.2:8081/uploads/1764724253_betta_hm_12.jpg
                        val imageUrl = "http://10.0.2.2:8081/upload/${userProfile.avatar}"

                        Glide.with(requireContext())
                            .load(imageUrl)
                            .circleCrop() // LỆNH NÀY SẼ ÉP ẢNH PHẢI CẮT TRÒN XOE 100%
                            .placeholder(R.drawable.logoaquariumshop)
                            .error(R.drawable.logoaquariumshop)
                            .into(binding.imgAvatar)
                    } else {
                        // Nếu DB trả về null, load ảnh mặc định và bo tròn
                        Glide.with(requireContext())
                            .load(R.drawable.logoaquariumshop)
                            .circleCrop()
                            .into(binding.imgAvatar)
                    }
                    val pending = userProfile.pendingCount ?: 0
                    if (pending > 0) {
                        binding.tvPendingCount.text = pending.toString()
                        binding.tvPendingCount.visibility = View.VISIBLE
                    } else {
                        binding.tvPendingCount.visibility = View.GONE
                    }

                    // Đang giao
                    val shipping = userProfile.shippingCount ?: 0
                    if (shipping > 0) {
                        binding.tvShippingCount.text = shipping.toString()
                        binding.tvShippingCount.visibility = View.VISIBLE
                    } else {
                        binding.tvShippingCount.visibility = View.GONE
                    }

                    // Hoàn thành
                    val completed = userProfile.completedCount ?: 0
                    if (completed > 0) {
                        binding.tvCompletedCount.text = completed.toString()
                        binding.tvCompletedCount.visibility = View.VISIBLE
                    } else {
                        binding.tvCompletedCount.visibility = View.GONE
                    }
                } else {
                    // Nếu dính 403 (Hết hạn Token), hệ thống sẽ nhảy vào đây
                    Log.e("Profile", "Lỗi API: ${response.code()}")
                    if(response.code() == 403) {
                        Toast.makeText(requireContext(), "Phiên đăng nhập hết hạn, vui lòng Đăng xuất!", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Profile", "Lỗi mạng: ${e.message}")
                binding.tvUserName.text = "Lỗi kết nối"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Tránh rò rỉ bộ nhớ (Memory Leak)
        _binding = null
    }
}