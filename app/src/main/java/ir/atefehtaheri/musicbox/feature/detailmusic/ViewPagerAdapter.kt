package ir.atefehtaheri.musicbox.feature.detailmusic

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.atefehtaheri.musicbox.R
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto
import ir.atefehtaheri.musicbox.databinding.PagerItemBinding

class ViewPagerAdapter() :
    ListAdapter<MusicDto, ViewPagerAdapter.ViewPagerViewHolder>(MusicDiffCallback()) {


    class ViewPagerViewHolder(
        private val binding: PagerItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val title = binding.title
        private val artist = binding.artist
        private val imageView = binding.imageView

        fun bind(musicDto: MusicDto) {
            title.text = musicDto.title
            artist.text = musicDto.artist
            val imageUri: Uri = Uri.parse(musicDto.image)

            imageView.load(imageUri) {
                crossfade(true)
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewPagerViewHolder(
        PagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
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
