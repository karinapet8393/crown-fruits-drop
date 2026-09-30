package com.WqNzVmK.rJpLtF.presentation.recipes

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
import com.WqNzVmK.rJpLtF.core.assets.AppAssets
import com.WqNzVmK.rJpLtF.core.assets.loadAsset
import com.WqNzVmK.rJpLtF.core.navigation.Navigator
import com.WqNzVmK.rJpLtF.databinding.FragmentRecipesBinding
import com.WqNzVmK.rJpLtF.presentation.menu.MenuFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * The royal menu card. Secondary screen: it deliberately has no PLAY button so the
 * main navigation path stays Menu -> Game.
 */
class RecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: RecipesViewModel by viewModels { RecipesViewModel.Factory }

    private val adapter = RecipeAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recipesBg.loadAsset(AppAssets.BG_MENU)
        binding.recipesList.layoutManager = LinearLayoutManager(requireContext())
        binding.recipesList.adapter = adapter

        binding.recipesBack.setOnClickListener { goBack() }
        binding.recipesMenu.setOnClickListener { goBack() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collectLatest { adapter.submit(it) }
            }
        }
    }

    private fun goBack() {
        Navigator.back(parentFragmentManager, MenuFragment())
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        _binding?.recipesList?.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
