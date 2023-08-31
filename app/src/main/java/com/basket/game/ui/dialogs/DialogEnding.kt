package com.basket.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.basket.game.R
import com.basket.game.core.library.ViewBindingDialog
import com.basket.game.databinding.DialogEndingBinding
import com.basket.game.domain.SharedP

class DialogEnding: ViewBindingDialog<DialogEndingBinding>(DialogEndingBinding::inflate) {
    private val sp by lazy {
        SharedP(requireContext())
    }
    private val args: DialogEndingArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack(R.id.fragmentMain, false, false)
                true
            } else {
                false
            }
        }

        binding.scores.text = args.scores.toString()
        binding.diamonds.text = "+" + args.scores.toString()

        binding.bestScore.text = sp.getBest().toString()

        binding.close.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }
    }
}