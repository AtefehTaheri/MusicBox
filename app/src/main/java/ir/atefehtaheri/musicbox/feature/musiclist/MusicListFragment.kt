package ir.atefehtaheri.musicbox.feature.musiclist

import android.Manifest
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import ir.atefehtaheri.musicbox.R
import ir.atefehtaheri.musicbox.core.common.checkPermission
import ir.atefehtaheri.musicbox.core.common.launchIntentSender
import ir.atefehtaheri.musicbox.core.common.launchPermissionDialog
import ir.atefehtaheri.musicbox.core.common.requestIntentSenderCallback
import ir.atefehtaheri.musicbox.core.common.requestMultiPermissionCallback
import ir.atefehtaheri.musicbox.data.musiclist.local.models.MusicDto
import ir.atefehtaheri.musicbox.databinding.FragmentMusiclistBinding
import ir.atefehtaheri.musicbox.feature.musiclist.uistate.MusicListUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MusicListFragment() : Fragment() {

    @Inject
    lateinit var player: ExoPlayer
    private val musicListViewModel: MusicListViewModel by viewModels()
    private lateinit var binding: FragmentMusiclistBinding
    private lateinit var intentSenderCallback: ActivityResultLauncher<IntentSenderRequest>
    private var id_deleteMusic: Long? = null

    private val adapter = MusicListAdapter(
        onDeleteClick = { idMusic ->
            id_deleteMusic = idMusic
            musicListViewModel.deleteMusic(idMusic) { intentSender ->
                launchIntentSender(
                    intentSender,
                    intentSenderCallback
                )
            }
        },
        onPlayClick = { positionItem ->
            musicListViewModel.onMusicItemClick(positionItem)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentMusiclistBinding.inflate(inflater, container, false).root
    }

    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMusiclistBinding.bind(view)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.mediaControls.player = player

        checkPermissions()


//        val currentMusicTitle = binding.exoTitle
//        val currentMusicArtWork = binding.exoArtwork
//
//
//        player.addListener(object : Player.Listener {
//            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
//                mediaItem?.let {
//                    if (mediaItem.mediaMetadata.title != null) {
//                        val title = mediaItem.mediaMetadata.displayTitle
//                        val image = mediaItem.mediaMetadata.artworkUri
//                        currentMusicTitle.setText(title)
//                        currentMusicArtWork.load(image)
//                    } else {
//                        currentMusicTitle.setText("Unknown Title")
//                    }
//                }
//            }
//        })


    }

    private fun checkPermissions() {
        intentSenderCallback = this.requestIntentSenderCallback(
            onGranted = { musicListViewModel.deleteMusic(id_deleteMusic!!, {}) },
            onDenied = ::showToast,
            requireContext()
        )
        val isPermissionsDenied = getPermissionsList().map {
            checkPermission(requireContext(), it)
        }.any { it == false }

        if (isPermissionsDenied) {
            launchPermissionDialog(
                this.requestMultiPermissionCallback(
                    permissionsGranted = ::showListView,
                    permissionsDenied = ::showToast,
                    context = requireContext()
                ),
                getPermissionsList().toTypedArray()
            )
        } else {
            showListView()
        }
    }

    private fun showListView() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                musicListViewModel.uiState.collect {
                    when {
                        it.isLoading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                            binding.errorTextView.visibility = View.GONE
                            binding.playerCardview.visibility = View.GONE
                        }

                        it.errorMessage != null -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            binding.playerCardview.visibility = View.GONE
                            binding.errorTextView.visibility = View.VISIBLE
                            binding.errorTextView.text = it.errorMessage
                        }

                        else -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.errorTextView.visibility = View.GONE
                            adapter.submitList(it.musicList)
                            if (it.currentMusic != null) {
                                showPlayBar(it.currentMusic)
                            }

                        }
                    }
                }
            }
        }
    }

    private fun showPlayBar(currentMusic: MusicDto) {
        binding.playerCardview.visibility = View.VISIBLE
        val currentMusicTitle = binding.exoTitle
        val currentMusicArtWork = binding.exoArtwork
        currentMusicTitle.setText(currentMusic.title)
        currentMusicArtWork.load(currentMusic.image) {
            crossfade(true)
            placeholder(R.drawable.placeholder)
            error(R.drawable.placeholder)
        }
    }

    private fun getPermissionsList(): List<String> {

        val permissionsList: MutableList<String> = mutableListOf()
        if (Build.VERSION.SDK_INT >= TIRAMISU) {
            permissionsList.add(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        return permissionsList
    }

    private fun showToast(statusMessage: String) {
        Toast.makeText(
            requireContext(),
            statusMessage,
            Toast.LENGTH_SHORT
        ).show()
    }
}