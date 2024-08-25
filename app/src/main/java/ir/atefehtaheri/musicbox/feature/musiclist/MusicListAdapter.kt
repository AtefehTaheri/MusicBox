package ir.atefehtaheri.musicbox.feature.musiclist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto
import ir.atefehtaheri.musicbox.databinding.MusicItemBinding

class MusicListAdapter() :
    ListAdapter<MusicDto, MusicListAdapter.MusicListViewHolder>(MusicDiffCallback()) {


    class MusicListViewHolder(private val binding: MusicItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val title = binding.title
        private val artist = binding.artist
        fun bind(musicDto: MusicDto) {
            title.text = musicDto.title
            artist.text = musicDto.artist
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= MusicListViewHolder(
        MusicItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MusicListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MusicDiffCallback : DiffUtil.ItemCallback<MusicDto>() {
    override fun areItemsTheSame(oldItem: MusicDto, newItem: MusicDto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MusicDto, newItem: MusicDto): Boolean {
        return oldItem == newItem
    }
}