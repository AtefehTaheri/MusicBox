package ir.atefehtaheri.musicbox.feature.musiclist

import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.widget.Toast
import ir.atefehtaheri.musicbox.core.common.checkPermission
import ir.atefehtaheri.musicbox.core.common.launchPermissionDialog
import ir.atefehtaheri.musicbox.core.common.requestMultiPermissionCallback
import ir.atefehtaheri.musicbox.databinding.FragmentMusiclistBinding
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MusicListFragment() : Fragment() {

    private val musicListViewModel: MusicListViewModel by viewModels()
    lateinit var binding: FragmentMusiclistBinding
    private val adapter = MusicListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentMusiclistBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMusiclistBinding.bind(view)

        val isPermissionsDenied = getPermissionsList().map {
            checkPermission(requireContext(), it)
        }.any { it == false }

        if (isPermissionsDenied) {
            launchPermissionDialog(
                this.requestMultiPermissionCallback(::showListView, ::showError),
                getPermissionsList().toTypedArray()
            )
        } else {
            showListView()
        }

    }

    private fun showError() {
        Toast.makeText(
            requireContext(),
            "This app needs access to your music files.\nPlease grant the necessary permissions.",
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun showListView() {

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                musicListViewModel.uiState.collect {
                    when {
                        it.isLoading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                            binding.errorTextView.visibility = View.GONE
                        }

                        it.errorMessage != null -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            binding.errorTextView.visibility = View.VISIBLE
                            binding.errorTextView.text = it.errorMessage
                        }

                        else -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.errorTextView.visibility = View.GONE
                            adapter.submitList(it.musicList)
                        }
                    }
                }
            }
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
}