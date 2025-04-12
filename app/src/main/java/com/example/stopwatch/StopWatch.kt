package com.example.stopwatch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.stopwatch.databinding.FragmentStopWatchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class StopWatch : Fragment() {

    val model: TimeViewModel = TimeViewModel()
    var binding: FragmentStopWatchBinding? = null
    var watchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.setStartTime(0)
        model.setEndTime(0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stop_watch, container, false)

        model.endTime.observe(viewLifecycleOwner) {
            val startTime = model.getStartTime() ?: 0
            val endTime = model.getEndTime() ?: 0
            val elapsedTimeNs: Long = endTime - startTime

            val seconds = TimeUnit.NANOSECONDS.toSeconds(elapsedTimeNs)
            val milliseconds = TimeUnit.NANOSECONDS.toMillis(elapsedTimeNs) % 1000
            val minutes = TimeUnit.SECONDS.toMinutes(seconds)

            val formattedTime = if (minutes > 0) {
                String.format("%02d:%02d:%02d", minutes, seconds % 60, milliseconds / 10)
            } else {
                String.format("%02d:%02d", seconds % 60, milliseconds / 10)
            }

            binding?.curTime = formattedTime
        }

        binding!!.startButton.setOnClickListener {
            startStopWatch()
        }

        binding!!.stopButton.setOnClickListener {
            stopStopWatch()
        }

        binding!!.resetButton.setOnClickListener {
            reset()
        }

        binding!!.timerButton.setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.main, TimerFragment(), "timer_fragment")
            fragmentTransaction.commit()
        }

        return binding!!.root
    }

    fun startStopWatch() {
        watchJob?.cancel()
        if (model.getStartTime() == 0L) {
            model.setStartTime(System.nanoTime())
            model.setEndTime(System.nanoTime())
        }
        watchJob = CoroutineScope(Dispatchers.Default).launch {
            while (!watchJob!!.isCancelled) {
                model.setEndTime(System.nanoTime())
                delay(10)
            }
        }
    }

    fun stopStopWatch() {
        watchJob?.cancel()
    }

    private fun reset() {
        if (watchJob != null && watchJob!!.isActive) {
            Toast.makeText(context, "stop current stopwatch first!", Toast.LENGTH_SHORT).show()
            return
        }
        model.setStartTime(0)
        model.setEndTime(0)
    }
}