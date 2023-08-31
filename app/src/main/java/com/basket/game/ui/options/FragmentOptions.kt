package com.basket.game.ui.options

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.basket.game.R
import com.basket.game.core.library.shortToast
import com.basket.game.databinding.FragmentOptionsBinding
import com.basket.game.domain.SharedP
import com.basket.game.ui.other.ViewBindingFragment

class FragmentOptions : ViewBindingFragment<FragmentOptionsBinding>(FragmentOptionsBinding::inflate) {
    private val sp by lazy {
        SharedP(requireContext())
    }
    private val viewModel: OptionsViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setText()

        if (viewModel.currentBall.value!! == 0) {
            viewModel.setBall(sp.getSelected())
        }

        binding.close.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.left.setOnClickListener {
            viewModel.left()
        }

        binding.right.setOnClickListener {
            viewModel.right()
        }

        binding.selection.setOnClickListener {
            if (sp.isSymbolBought(viewModel.currentBall.value!!)) {
                if (sp.getSelected() != viewModel.currentBall.value!!) {
                    sp.setSelected(viewModel.currentBall.value!!)
                    viewModel.update()
                }
            } else {
                if (sp.getDiamonds() >= 100) {
                    sp.setDiamonds(-100)
                    sp.buySymbol(viewModel.currentBall.value!!)
                    setText()
                    viewModel.update()
                } else {
                    shortToast(requireContext(), "Not enough diamonds")
                }
            }
        }

        viewModel.currentBall.observe(viewLifecycleOwner) {
            binding.ball.setImageResource(
                when (it) {
                    1 -> R.drawable.ball01
                    2 -> R.drawable.ball02
                    3 -> R.drawable.ball03
                    4 -> R.drawable.ball04
                    5 -> R.drawable.ball05
                    6 -> R.drawable.ball06
                    7 -> R.drawable.ball07
                    else -> R.drawable.ball08
                }
            )
            val isSelected = it == sp.getSelected()
            val isBought = sp.isSymbolBought(it)
            val text = when {
                isSelected && isBought -> "selected"
                !isSelected && isBought -> "select"
                !isSelected && !isBought -> "buy (100)"
                else -> "error"
            }
            binding.selection.text = text
        }
    }

    private fun setText() {
        binding.diamonds.text = sp.getDiamonds().toString()
    }
}