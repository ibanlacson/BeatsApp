package com.auf.cea.beatsapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.auf.cea.beatsapp.databinding.ContentMxmTrackBinding
import com.auf.cea.beatsapp.models.MusixMatchModels.TrackSearchModel.Track

class TrackListAdapter(private var trackList: ArrayList<Track>, private var context:Context): RecyclerView.Adapter<TrackListAdapter.AdapterView>() {
    inner class AdapterView(private var binding: ContentMxmTrackBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(itemData: Track){
            with(binding){
                txtTrackTitle.text = itemData.track.track_name
                txtTrackArtist.text = itemData.track.artist_name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterView {
        val binding = ContentMxmTrackBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdapterView(binding)
    }

    override fun onBindViewHolder(holder: AdapterView, position: Int) {
        val trackData = trackList[position]
        return holder.bind(trackData)
    }

    override fun getItemCount(): Int {
        return  trackList.size
    }

    fun updateData(trackList: ArrayList<Track>){
        this.trackList = arrayListOf()
        this.notifyDataSetChanged()
        this.trackList.addAll(trackList)
        this.notifyItemInserted(this.trackList.size)
    }
}