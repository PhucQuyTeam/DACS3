package com.example.jit_vphuc_54

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.jit_vphuc_54.Viewmodel.ProductViewModel
import com.example.jit_vphuc_54.databinding.FragmentHomedetailBinding
import com.example.jit_vphuc_54.model.Product
import java.io.File

class HomeDetailFragment : Fragment() {

    private lateinit var binding: FragmentHomedetailBinding
    private lateinit var adapter: AdapterProduct
    private lateinit var viewModel: ProductViewModel

    private var selectedImageUri: Uri? = null
    private var currentImgView: ImageView? = null

    // launcher pick ảnh
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            selectedImageUri = uri
            currentImgView?.setImageURI(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomedetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // khởi tạo ViewModel
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), factory)[ProductViewModel::class.java]

        // khởi tạo Adapter, truyền viewModel vào để checkbox update db
        adapter = AdapterProduct(mutableListOf(), viewModel)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // observe data
        viewModel.readAllData.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }

        // delete 1 item
        adapter.onDeleteClick = {
            viewModel.deleteProduct(it)
        }

        // edit item
        adapter.onEditClick = {
            showDialogEdit(it)
        }

        // delete all ticked
        binding.btnDeleteSelected.setOnClickListener {
            viewModel.deleteSelected()
        }
        binding.btnSelectAllDelete.setOnClickListener {
//            adapter.selectAllAndUpdateDB(viewModel)
            viewModel.deleteAll()
        }
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    // lưu ảnh vào internal storage
    private fun saveImage(uri: Uri): String {
        val file = File(requireContext().filesDir, "${System.currentTimeMillis()}.jpg")
        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath
    }

    // thêm sản phẩm
    fun showDialogAdd() {
        selectedImageUri = null
        val view = layoutInflater.inflate(R.layout.dialog_product, null)

        val edtName = view.findViewById<EditText>(R.id.edtName)
        val edtPrice = view.findViewById<EditText>(R.id.edtPrice)
        val edtDesc = view.findViewById<EditText>(R.id.edtDesc)
        val img = view.findViewById<ImageView>(R.id.imgProduct)
        val btnImg = view.findViewById<Button>(R.id.btnSelectImage)

        btnImg.setOnClickListener {
            currentImgView = img
            pickImageLauncher.launch("image/*")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Thêm sản phẩm")
            .setView(view)
            .setPositiveButton("OK") { _, _ ->

                val imagePath = selectedImageUri?.let { saveImage(it) }

                val product = Product(
                    name = edtName.text.toString(),
                    price = edtPrice.text.toString(),
                    description = edtDesc.text.toString(),
                    imageUri = imagePath
                )

                viewModel.addProduct(product)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // sửa sản phẩm
    fun showDialogEdit(product: Product) {
        selectedImageUri = null
        val view = layoutInflater.inflate(R.layout.dialog_product, null)

        val edtName = view.findViewById<EditText>(R.id.edtName)
        val edtPrice = view.findViewById<EditText>(R.id.edtPrice)
        val edtDesc = view.findViewById<EditText>(R.id.edtDesc)
        val img = view.findViewById<ImageView>(R.id.imgProduct)
        val btnImg = view.findViewById<Button>(R.id.btnSelectImage)

        edtName.setText(product.name)
        edtPrice.setText(product.price)
        edtDesc.setText(product.description)

        product.imageUri?.let {
            val file = File(it)
            if (file.exists()) img.setImageURI(Uri.fromFile(file))
        }

        btnImg.setOnClickListener {
            currentImgView = img
            pickImageLauncher.launch("image/*")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Sửa sản phẩm")
            .setView(view)
            .setPositiveButton("OK") { _, _ ->

                val newImage = selectedImageUri?.let { saveImage(it) } ?: product.imageUri

                val updated = product.copy(
                    name = edtName.text.toString(),
                    price = edtPrice.text.toString(),
                    description = edtDesc.text.toString(),
                    imageUri = newImage
                )

                viewModel.updateProduct(updated)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}