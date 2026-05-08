package com.example.dacs3.ui.checkout

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dacs3.api.ApiService
import com.example.dacs3.databinding.ActivityAddEditAddressBinding
import com.example.dacs3.model.AddressDTO
import com.example.dacs3.network.RetrofitClient
import kotlinx.coroutines.launch

class AddEditAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditAddressBinding
    private var addressId: Int = -1

    // Lưu trữ danh sách Tỉnh/Huyện lấy từ API
    private var provinceList: List<ApiService.ProvinceDTO> = emptyList()
    private var wardList: List<ApiService.WardDTO> = emptyList()

    // Lưu trữ ID đang được chọn
    private var selectedProvinceId: Int = -1
    private var selectedWardId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddEdit)
        binding.toolbarAddEdit.setNavigationOnClickListener { finish() }

        // Load danh sách Tỉnh ngay khi mở màn hình
        loadProvinces()

        val addressData = intent.getSerializableExtra("EXTRA_ADDRESS") as? AddressDTO
        if (addressData != null) {
            addressId = addressData.id
            binding.toolbarAddEdit.title = "Sửa địa chỉ"

            // Điền dữ liệu text vào trước
            binding.edtFullName.setText(addressData.receiverName)
            binding.edtPhone.setText(addressData.receiverPhone)
            binding.edtStreetDetail.setText(addressData.streetDetail) // ĐÃ SỬA: Chỉ hiển thị "1111"

            // Lưu lại ID Tỉnh/Huyện cũ để lát nữa API load xong sẽ tự động select
            selectedProvinceId = addressData.provinceId
            selectedWardId = addressData.wardId
        } else {
            binding.toolbarAddEdit.title = "Thêm địa chỉ mới"
        }

        binding.btnSaveAddress.setOnClickListener { performSave() }
    }

    private fun loadProvinces() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getInstance(this@AddEditAddressActivity).getProvinces()
                if (response.isSuccessful && response.body() != null) {
                    provinceList = response.body()!!

                    // Tạo list tên Tỉnh để hiển thị lên Dropdown
                    val provinceNames = provinceList.map { it.name }
                    val adapter = ArrayAdapter(this@AddEditAddressActivity, android.R.layout.simple_dropdown_item_1line, provinceNames)
                    binding.actProvince.setAdapter(adapter)

                    // Bắt sự kiện khi user Click chọn 1 Tỉnh
                    binding.actProvince.setOnItemClickListener { _, _, position, _ ->
                        selectedProvinceId = provinceList[position].id
                        binding.actWard.setText("") // Clear xã cũ
                        selectedWardId = -1
                        loadWards(selectedProvinceId) // Load lại danh sách xã theo tỉnh mới
                    }

                    // Nếu đang ở chế độ Sửa -> Tự động điền tên Tỉnh cũ vào Dropdown
                    if (selectedProvinceId != -1) {
                        val oldProvince = provinceList.find { it.id == selectedProvinceId }
                        if (oldProvince != null) {
                            binding.actProvince.setText(oldProvince.name, false)
                            loadWards(selectedProvinceId) // Load luôn xã của tỉnh đó
                        }
                    }
                }
            } catch (e: Exception) {
                // Xử lý lỗi mạng
            }
        }
    }

    private fun loadWards(provinceId: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getInstance(this@AddEditAddressActivity).getWards(provinceId)
                if (response.isSuccessful && response.body() != null) {
                    wardList = response.body()!!

                    val wardNames = wardList.map { it.name }
                    val adapter = ArrayAdapter(this@AddEditAddressActivity, android.R.layout.simple_dropdown_item_1line, wardNames)
                    binding.actWard.setAdapter(adapter)

                    binding.actWard.setOnItemClickListener { _, _, position, _ ->
                        selectedWardId = wardList[position].id
                    }

                    // Nếu đang ở chế độ Sửa -> Tự động điền tên Xã cũ vào Dropdown
                    if (selectedWardId != -1) {
                        val oldWard = wardList.find { it.id == selectedWardId }
                        if (oldWard != null) {
                            binding.actWard.setText(oldWard.name, false)
                        }
                    }
                }
            } catch (e: Exception) {
                // Lỗi
            }
        }
    }

    private fun performSave() {
        val street = binding.edtStreetDetail.text.toString().trim()
        val name = binding.edtFullName.text.toString().trim()
        val phone = binding.edtPhone.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || street.isEmpty() || selectedProvinceId == -1 || selectedWardId == -1) {
            Toast.makeText(this, "Vui lòng nhập và chọn đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSaveAddress.isEnabled = false

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.getInstance(this@AddEditAddressActivity)

                val body = HashMap<String, Any>()
                // Thêm Tên và SĐT (Sếp nhớ cập nhật API SpringBoot để nhận 2 trường này nếu cần nhé)
                body["receiverName"] = name
                body["receiverPhone"] = phone
                body["provinceId"] = selectedProvinceId
                body["wardId"] = selectedWardId
                body["streetDetail"] = street

                if (addressId != -1) body["id"] = addressId

                val response = if (addressId == -1) api.addAddress(body) else api.updateAddress(body)

                if (response.isSuccessful) {
                    Toast.makeText(this@AddEditAddressActivity, "Lưu thành công!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddEditAddressActivity, "Lỗi khi lưu", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddEditAddressActivity, "Lỗi mạng", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnSaveAddress.isEnabled = true
            }
        }
    }
}