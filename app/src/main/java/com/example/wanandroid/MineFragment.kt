package com.example.wanandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.wanandroid.databinding.FragmentMineBinding
import com.example.wanandroid.utils.DataStoreManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MineFragment : Fragment() {

    private var _binding: FragmentMineBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.llUserInfo.setOnClickListener {
            // 如果未登录才跳转
            if (binding.tvUsername.text == "点击登录") {
                LoginActivity.start(requireContext())
            }
        }
        
        binding.tvMyCollect.setOnClickListener {
            if (binding.tvUsername.text != "点击登录") {
                startActivity(android.content.Intent(requireContext(), CollectActivity::class.java))
            } else {
                LoginActivity.start(requireContext())
            }
        }
        
        binding.tvLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                DataStoreManager.clearUserData()
                com.example.wanandroid.db.AppDatabase.getDatabase().articleDao().clearAll()
            }
        }
        
        // 观察用户名变化
        viewLifecycleOwner.lifecycleScope.launch {
            DataStoreManager.usernameFlow.collectLatest { username ->
                if (username.isNotEmpty()) {
                    binding.tvUsername.text = username
                    binding.tvCoinInfo.text = "欢迎回来"
                    binding.tvLogout.visibility = View.VISIBLE
                    binding.vLogoutDivider.visibility = View.VISIBLE
                } else {
                    binding.tvUsername.text = "点击登录"
                    binding.tvCoinInfo.text = "登录后体验更多功能"
                    binding.tvLogout.visibility = View.GONE
                    binding.vLogoutDivider.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}