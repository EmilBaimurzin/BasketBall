package com.basket.game.ui.pre

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.basket.game.R
import com.basket.game.ui.other.ViewBindingFragment
import com.basket.game.databinding.FragmentPreGameBinding
import com.basket.game.domain.SharedP

class FragmentPreGame : ViewBindingFragment<FragmentPreGameBinding>(FragmentPreGameBinding::inflate) {
    private val sp by lazy {
        SharedP(requireContext())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sp.buySymbol(1)

        binding.apply {
            play.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentBasketball)
            }
            settings.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentOptions)
            }
            exit.setOnClickListener {
                requireActivity().finish()
            }
        }

        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }
    }
}