package com.example.wanandroid.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.wanandroid.R
import com.example.wanandroid.widget.LoadingDialog

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: Inflate<VB>
) : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!
    
    private var loadingDialog: LoadingDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected open fun initView() {}
    protected open fun initData() {}
    protected open fun initObserver() {}

    protected fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    protected fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = context?.let { LoadingDialog(it) }
        }
        if (loadingDialog?.isShowing != true) {
            loadingDialog?.show()
        }
    }

    protected fun hideLoading() {
        if (loadingDialog?.isShowing == true) {
            loadingDialog?.dismiss()
        }
    }

    protected fun showEmptyState(message: String = "暂无内容") {
        binding.root.findViewById<View>(R.id.emptyStateLayout)?.visibility = View.VISIBLE
        binding.root.findViewById<TextView>(R.id.tvEmptyState)?.text = message
        binding.root.findViewById<Button>(R.id.btnRetry)?.visibility = View.GONE
    }

    protected fun showErrorState(message: String = "加载失败", retryAction: () -> Unit) {
        binding.root.findViewById<View>(R.id.emptyStateLayout)?.visibility = View.VISIBLE
        binding.root.findViewById<TextView>(R.id.tvEmptyState)?.text = message
        binding.root.findViewById<Button>(R.id.btnRetry)?.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                hideStateLayout()
                retryAction()
            }
        }
    }

    protected fun hideStateLayout() {
        binding.root.findViewById<View>(R.id.emptyStateLayout)?.visibility = View.GONE
    }
}
