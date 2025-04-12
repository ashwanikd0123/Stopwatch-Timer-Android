package com.example.stopwatch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimeViewModel {
    val startTime: MutableLiveData<TimeData> = MutableLiveData(TimeData(0))
    val endTime: MutableLiveData<TimeData> = MutableLiveData(TimeData(0))

    fun setStartTime(time: Long) {
        startTime.postValue(TimeData(time))
    }

    fun setEndTime(time: Long) {
        endTime.postValue(TimeData(time))
    }

    fun getStartTime() : Long {
        return startTime.value!!.time;
    }

    fun getEndTime() : Long {
        return endTime.value!!.time;
    }
}