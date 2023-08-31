package com.basket.game.ui.basteball

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.basket.game.R
import com.basket.game.ui.other.ViewBindingFragment
import com.basket.game.databinding.FragmentBasketballBinding
import com.basket.game.domain.SharedP
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FragmentBasketball :
    ViewBindingFragment<FragmentBasketballBinding>(FragmentBasketballBinding::inflate) {
    private val viewModel: BasketballViewModel by viewModels()
    private val callBackViewModel: CBViewModel by activityViewModels()
    private val sp by lazy {
        SharedP(requireContext())
    }
    private val xy by lazy {
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        Pair(size.x, size.y)
    }
    private var ballImg = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ballImg = when (sp.getSelected()) {
            1 -> R.drawable.ball01
            2 -> R.drawable.ball02
            3 -> R.drawable.ball03
            4 -> R.drawable.ball04
            5 -> R.drawable.ball05
            6 -> R.drawable.ball06
            7 -> R.drawable.ball07
            else -> R.drawable.ball08
        }

        binding.menu.setOnClickListener {
            findNavController().popBackStack()
        }

        callBackViewModel.callback = {
            viewModel.pauseState = false
            lifecycleScope.launch {
                delay(20)
                viewModel.start(
                    binding.basket.width,
                    xy.first.toFloat(),
                    0f,
                    dpToPx(50),
                    binding.basketWeb.width,
                    binding.basketWeb.height,
                    dpToPx(600)
                )
            }
        }

        binding.pause.setOnClickListener {
            viewModel.pauseState = true
            viewModel.stop()
            findNavController().navigate(R.id.action_fragmentBasketball_to_dialogPause)
        }

        binding.root.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN && !viewModel.isIntersected) {
                viewModel.spawnBall(motionEvent.x - dpToPx(25), 0f)
            }
            true
        }

        viewModel.basketXY.observe(viewLifecycleOwner) {
            binding.basket.x = it.x
            binding.basket.y = it.y

            viewModel.setBasketWeb(it.x + dpToPx(35), it.y + dpToPx(70))
        }

        viewModel.ballXY.observe(viewLifecycleOwner) {
            binding.ballLayout.removeAllViews()
            if (it != null) {
                val ballView = ImageView(requireContext())
                ballView.layoutParams = ViewGroup.LayoutParams(dpToPx(50), dpToPx(50))
                ballView.setImageResource(ballImg)
                ballView.x = it.x
                ballView.y = it.y
                binding.ballLayout.addView(ballView)
            }
        }

        viewModel.scores.observe(viewLifecycleOwner) {
            binding.scores.text = it.toString()
        }

        viewModel.energy.observe(viewLifecycleOwner) {
            binding.energyContainer2.removeAllViews()
            binding.energyContainer1.removeAllViews()

            repeat(it) {
                val energyView = ImageView(requireContext())
                val energyView2 = ImageView(requireContext())
                energyView.layoutParams = LinearLayout.LayoutParams(dpToPx(6), dpToPx(16)).apply {
                    marginEnd = dpToPx(1)
                }
                energyView2.setImageResource(R.drawable.energy_point)
                energyView2.layoutParams = LinearLayout.LayoutParams(dpToPx(6), dpToPx(16)).apply {
                    marginEnd = dpToPx(1)
                }
                energyView.setImageResource(R.drawable.energy_point)
                binding.energyContainer1.addView(energyView)
                binding.energyContainer2.addView(energyView2)
            }

            if (it == 0 && viewModel.gameState) {
                end()
            }
        }

        lifecycleScope.launch {
            delay(20)
            if (viewModel.basketXY.value!!.y == 0f) {
                viewModel.initBasket(
                    (xy.first / 2 - (binding.basket.width / 2)).toFloat(),
                    binding.basket.height.toFloat() + binding.basket.height.toFloat() / 2
                )
            }
            if (viewModel.gameState && !viewModel.pauseState) {
                viewModel.start(
                    binding.basket.width,
                    xy.first.toFloat(),
                    0f,
                    dpToPx(50),
                    binding.basketWeb.width,
                    binding.basketWeb.height,
                    dpToPx(600)
                )
            }
        }
    }

    private fun end() {
        viewModel.gameState = false
        viewModel.stop()
        if (sp.getBest() < viewModel.scores.value!!) {
            sp.setBest(viewModel.scores.value!!)
        }
        sp.setDiamonds(viewModel.scores.value!!)
        findNavController().navigate(FragmentBasketballDirections.actionFragmentBasketballToDialogEnding(viewModel.scores.value!!))
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
    }
}