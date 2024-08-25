package ir.atefehtaheri.musicbox.feature.musiclist

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.atefehtaheri.musicbox.R
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto
import ir.atefehtaheri.musicbox.databinding.MusicItemBinding

class MusicListAdapter() :
    ListAdapter<MusicDto, MusicListAdapter.MusicListViewHolder>(MusicDiffCallback()) {


    class MusicListViewHolder(private val binding: MusicItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val title = binding.title
        private val artist = binding.artist
        private val duration = binding.duration
        private val imageView = binding.imageView

        fun bind(musicDto: MusicDto) {
            title.text = musicDto.title
            artist.text = musicDto.artist
            duration.text = musicDto.duration.toString()
            val imageUri: Uri = Uri.parse(musicDto.image)

            imageView.load(imageUri) {
                crossfade(true)
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }
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