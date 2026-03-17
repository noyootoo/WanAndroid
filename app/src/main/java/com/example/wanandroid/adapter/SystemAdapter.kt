package com.example.wanandroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.databinding.ItemSystemBinding
import com.example.wanandroid.model.SystemCategory

class SystemAdapter : ListAdapter<SystemCategory, SystemAdapter.ViewHolder>(SystemDiffCallback()) {

    class ViewHolder(private val binding: ItemSystemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: SystemCategory) {
            binding.tvName.text = category.name
            // 🌟 核心魔法：把 List<SystemChild> 变成用空格隔开的字符串
            binding.tvChildren.text = category.children.joinToString("   ") { it.name }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSystemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SystemDiffCallback : DiffUtil.ItemCallback<SystemCategory>() {
    override fun areItemsTheSame(oldItem: SystemCategory, newItem: SystemCategory) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: SystemCategory, newItem: SystemCategory) = oldItem == newItem
}