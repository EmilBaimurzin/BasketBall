package com.basket.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.basket.game.R
import com.basket.game.core.library.ViewBindingDialog
import com.basket.game.databinding.DialogPauseBinding
import com.basket.game.domain.SharedP
import com.basket.game.ui.basteball.CBViewModel

class DialogPause: ViewBindingDialog<DialogPauseBinding>(DialogPauseBinding::inflate) {
    private val callbackViewModel: CBViewModel by activityViewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack()
                callbackViewModel.callback?.invoke()
                true
            } else {
                false
            }
        }

        binding.play.setOnClickListener {
            findNavController().popBackStack()
            callbackViewModel.callback?.invoke()
        }
    }
}