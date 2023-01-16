package com.auf.cea.beatsapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auf.cea.beatsapp.R
import com.auf.cea.beatsapp.databinding.ContentRealmTrackBinding
import com.auf.cea.beatsapp.models.realm.Track

class RealmTrackAdapter(private var trackDataList: ArrayList<Track>, private var callback: RealmTrackAdapterInterface) : RecyclerView.Adapter<RealmTrackAdapter.RealmViewHolder>(){

    interface RealmTrackAdapterInterface{
        fun removeTrack(id:String)
        fun viewLyrics(trackData:Track)
    }

    inner class RealmViewHolder(private val binding: ContentRealmTrackBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(itemData: Track){
            with(binding){
                txtTrackTitle.text = itemData.trackTitle
                txtTrackArtist.text = itemData.artist
                txtSource.text = String.format("From: %s",itemData.source)
                toggleFavorite.isChecked = true

                //Remove to Favorite
                toggleFavorite.setOnClickListener {
                    callback.removeTrack(itemData.id)
                }

                btnLyrics.setOnClickListener{
                    callback.viewLyrics(itemData)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealmViewHolder {
        val binding = ContentRealmTrackBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RealmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RealmViewHolder, position: Int) {
        val trackData = trackDataList[position]
        holder.bind(trackData)
    }

    override fun getItemCount(): Int {
        return trackDataList.size
    }

    fun updateData(trackList:ArrayList<Track>){
        this.trackDataList = arrayListOf()
        this.notifyDataSetChanged()
        this.trackDataList.addAll(trackList)
        this.notifyItemInserted(this.trackDataList.size)
    }
}