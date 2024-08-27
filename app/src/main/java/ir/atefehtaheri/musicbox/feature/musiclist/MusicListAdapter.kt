package ir.atefehtaheri.musicbox.feature.musiclist

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ir.atefehtaheri.musicbox.R
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto
import ir.atefehtaheri.musicbox.databinding.MusicItemBinding

class MusicListAdapter(
    private val onPlayClick:()->Unit,
    private val onDeleteClick:(Long)->Unit
) :ListAdapter<MusicDto, MusicListAdapter.MusicListViewHolder>(MusicDiffCallback()) {


    class MusicListViewHolder(
        private val binding: MusicItemBinding,
        private val onPlayClick:()->Unit,
        private val onDeleteClick:(Long)->Unit
    ) :RecyclerView.ViewHolder(binding.root) {

        private val title = binding.title
        private val artist = binding.artist
        private val duration = binding.duration
        private val imageView = binding.imageView
        private val moreOptionBtn = binding.moreOption

        fun bind(musicDto: MusicDto) {
            title.text = musicDto.title
            artist.text = musicDto.artist
            duration.text = durationToMinutes(musicDto.duration)
            val imageUri: Uri = Uri.parse(musicDto.image)

            imageView.load(imageUri) {
                crossfade(true)
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }
            moreOptionBtn.setOnClickListener { view ->
                showPopupMenu(view, musicDto,onPlayClick,onDeleteClick)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MusicListViewHolder(
        MusicItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onPlayClick,
        onDeleteClick)

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

private fun durationToMinutes(duration: Long): String {
    val totalSeconds = duration / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}


private fun showPopupMenu(
    view: View,
    musicDto: MusicDto,
    onPlayClick:()->Unit,
    onDeleteClick:(Long)->Unit) {

    val popupMenu = PopupMenu(view.context, view)
    popupMenu.inflate(R.menu.menu_item)
    popupMenu.setForceShowIcon(true)
    popupMenu.setOnMenuItemClickListener { menuItem ->
        when (menuItem.itemId) {
            R.id.play -> {
                onPlayClick()
                true
            }
            R.id.delete -> {
                onDeleteClick(musicDto.id)
                true
            }
            else -> false
        }
    }
    popupMenu.show()
}