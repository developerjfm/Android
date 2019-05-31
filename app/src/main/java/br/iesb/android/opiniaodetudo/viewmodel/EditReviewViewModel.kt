package br.iesb.android.opiniaodetudo.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import br.iesb.android.opiniaodetudo.model.Review

class EditReviewViewModel : ViewModel() {
    var data: MutableLiveData<Review> = MutableLiveData()
}