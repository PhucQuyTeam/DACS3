package com.example.dacs3.ui.blog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dacs3.R
import com.example.dacs3.adapter.BlogAdapter
import com.example.dacs3.databinding.FragmentBlogBinding
import com.example.dacs3.network.RetrofitClient
import com.example.dacs3.repository.BlogRepository
import com.example.dacs3.viewmodel.BlogViewModel
import com.example.dacs3.viewmodel.BlogViewModelFactory

class BlogFragment : Fragment() {

    private var _binding: FragmentBlogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BlogViewModel
    private lateinit var blogAdapter: BlogAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBlogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        blogAdapter = BlogAdapter { selectedBlog ->

            val bundle = Bundle().apply {
                putSerializable("BLOG_DATA", selectedBlog)
            }
            findNavController().navigate(R.id.action_blogFragment_to_blogDetailFragment, bundle)
        }

        binding.rvBlogs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = blogAdapter
        }


        val apiService = RetrofitClient.getInstance(requireContext())
        val repository = BlogRepository(apiService)
        viewModel = ViewModelProvider(this, BlogViewModelFactory(repository))[BlogViewModel::class.java]


        viewModel.blogs.observe(viewLifecycleOwner) { listBlogs ->
            blogAdapter.submitList(listBlogs)
        }


        viewModel.fetchBlogs()

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}