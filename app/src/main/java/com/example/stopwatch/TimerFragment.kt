package com.example.stopwatch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.stopwatch.databinding.FragmentTimerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.max

class TimerFragment : Fragment() {

    val model: TimeViewModel = TimeViewModel()
    var binding: FragmentTimerBinding? = null
    var watchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.setStartTime(0)
        model.setEndTime(0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)
        model.endTime.observe(viewLifecycleOwner,  {
            if (model.getStartTime() == 0L) {
                binding!!.arcPercentage = 1.0f
                binding!!.minutes = "00"
                binding!!.seconds = "00"
            } else {
                binding!!.arcPercentage = (model.getStartTime() - model.getEndTime()).toFloat() / model.getStartTime().toFloat()
                val time = model.getEndTime()
                val secs = time % 60
                val mins = time / 60
                binding!!.minutes = String.format("%02d", mins)
                binding!!.seconds = String.format("%02d", secs)
            }
        })

        model.setStartTime(0L)
        model.setEndTime(0L)

        binding!!.stopWatch.setOnClickListener({
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.main, StopWatch(), "stop_watch_fragment")
            fragmentTransaction.commit()
        })

        binding!!.startButton.setOnClickListener({
            startTimer()
        })

        binding!!.stopButton.setOnClickListener({
            stopTimer()
        })

        binding!!.resetButton.setOnClickListener({
            resetTimer()
        })
        return binding!!.root
    }

    fun startTimer() {
        if (watchJob != null && watchJob!!.isActive) {
            Toast.makeText(context, "stop current timer first", Toast.LENGTH_SHORT).show()
            return
        }

        val mins = binding!!.minuteEditText.text.toString().toLong()
        val secs = binding!!.secondEditText.text.toString().toLong()

        if (mins < 0 || secs < 0 || (mins == 0L && secs == 0L)) {
            Toast.makeText(context, "invalid input", Toast.LENGTH_SHORT).show()
            return
        }

        if (model.getStartTime() == 0L) {
            model.setStartTime(mins * 60 + secs)
            model.setEndTime(mins * 60 + secs)
        } else {
            model.setStartTime(model.getEndTime())
        }

        watchJob = CoroutineScope(Dispatchers.Default).launch {
            val initialTime = System.nanoTime()
            while (!watchJob!!.isCancelled && model.getEndTime() != 0L) {
                val curTime = System.nanoTime()
                val passedSeconds = TimeUnit.NANOSECONDS.toSeconds(curTime - initialTime)
                model.setEndTime(max(0, model.getStartTime() - passedSeconds))
                delay(100)
            }
            model.setEndTime(0)
            model.setStartTime(0)
        }
    }

    fun stopTimer() {
        watchJob?.cancel()
    }

    fun resetTimer() {
        if (watchJob == null) {
            Toast.makeText(context, "no timer running", Toast.LENGTH_SHORT).show()
            return
        }
        if (watchJob!!.isActive) {
            Toast.makeText(context, "stop the timer first", Toast.LENGTH_SHORT).show()
            return
        }
        model.setEndTime(0)
        model.setStartTime(0)
    }
}