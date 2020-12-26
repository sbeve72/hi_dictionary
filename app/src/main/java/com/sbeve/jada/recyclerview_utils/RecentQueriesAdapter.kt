package com.sbeve.jada.recyclerview_utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sbeve.jada.databinding.QueryLayoutBinding
import com.sbeve.jada.retrofit_utils.RetrofitInit
import com.sbeve.jada.room_utils.RecentQuery
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class RecentQueriesAdapter(private val viewHolderClickListener: ViewHolderClickListener) :
    ListAdapter<RecentQuery, RecentQueriesAdapter.RecentQueryViewHolder>(RecentQueriesDiffUtil()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentQueryViewHolder {
        return RecentQueryViewHolder.inflateLayout(this, parent)
    }
    
    override fun onBindViewHolder(holder: RecentQueryViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.provideCurrentItem(currentItem)
    }
    
    //custom interface to be implemented by the main activity to set up onClickListeners
    interface ViewHolderClickListener {
        fun onItemClick(query: String, queryLanguageIndex: Int)
        fun onDeleteButtonClick(query: String, queryLanguageIndex: Int)
    }
    
    class RecentQueryViewHolder(myItemView: QueryLayoutBinding, private val viewHolderClickListener: ViewHolderClickListener) :
        RecyclerView.ViewHolder(myItemView.root) {
        
        //setting on click listeners for each item and the delete button in each item
        init {
            myItemView.root.setOnClickListener { viewHolderClickListener.onItemClick(queryTextValue, queryLanguageValue) }
            myItemView.deleteButton.setOnClickListener { viewHolderClickListener.onDeleteButtonClick(queryText.text.toString(), queryLanguageValue) }
        }
        
        companion object {
            fun inflateLayout(recentQueriesAdapter: RecentQueriesAdapter, parent: ViewGroup): RecentQueryViewHolder {
                val binding = QueryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return RecentQueryViewHolder(binding, recentQueriesAdapter.viewHolderClickListener)
            }
        }
        
        private val queryText = myItemView.queryText
        private lateinit var queryTextValue: String
        private val queryLanguage = myItemView.language
        private var queryLanguageValue by Delegates.notNull<Int>()
        private val timeDate = myItemView.timeDate
        
        //function to be called from onBindViewHolder() to provide the current item to the ViewHolder.
        fun provideCurrentItem(currentItem: RecentQuery) {
            bindQuery(currentItem.queryText)
            bindTime(currentItem.timeDate)
            bindLanguage(currentItem.queryLanguage)
        }
        
        private fun bindLanguage(languageValue: Int) {
            queryLanguageValue = languageValue
            queryLanguage.text = RetrofitInit.supportedLanguages.first[languageValue]
        }
        
        private fun bindQuery(queryValue: String) {
            queryTextValue = queryValue
            queryText.text = queryValue
        }
        
        private fun bindTime(timeValue: Long) {
            timeDate.text = SimpleDateFormat("hh:mm a; dd MMMM, yyyy", Locale.getDefault()).format(timeValue)
        }
    }
}

class RecentQueriesDiffUtil : DiffUtil.ItemCallback<RecentQuery>() {
    
    override fun areItemsTheSame(oldItem: RecentQuery, newItem: RecentQuery): Boolean {
        return oldItem.queryText == newItem.queryText
    }
    
    override fun areContentsTheSame(oldItem: RecentQuery, newItem: RecentQuery): Boolean {
        return oldItem.timeDate == newItem.timeDate
    }
}
