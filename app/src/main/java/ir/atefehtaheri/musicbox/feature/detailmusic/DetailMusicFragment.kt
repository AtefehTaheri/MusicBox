package ir.atefehtaheri.musicbox.feature.detailmusic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.viewpager2.widget.ViewPager2
import ir.atefehtaheri.musicbox.databinding.FragmentDetailmusicBinding
import ir.atefehtaheri.musicbox.feature.musiclist.MusicListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class DetailMusicFragment : Fragment() {
    private lateinit var binding: FragmentDetailmusicBinding
    private val musicListViewModel: MusicListViewModel by activityViewModels()
    private var mediaController: MediaController? = null
    private val adapter = ViewPagerAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentDetailmusicBinding.inflate(inflater, container, false).root
    }

    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailmusicBinding.bind(view)
        binding.viewPager.adapter = adapter

        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (mediaController?.currentMediaItemIndex != position) {
                    mediaController?.seekToDefaultPosition(position)
                    mediaController?.play()
                }
            }
        })
        setMediaController()
        loadData()

    }

    @OptIn(UnstableApi::class)
    private fun setMediaController(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                musicListViewModel.mediaController.collectLatest {
                    mediaController = it
                    binding.mediaControls.player = it
                }
            }
        }
    }

    private fun loadData(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                musicListViewModel.uiState.collect {
                    adapter.submitList(it.musicList)
                    val positionToScroll = mediaController?.currentMediaItemIndex
                    val p = positionToScroll ?: 0
                    binding.viewPager.setCurrentItem(p, false)
                }
            }
        }
    }

}