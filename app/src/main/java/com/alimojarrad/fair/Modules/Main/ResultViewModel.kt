package com.alimojarrad.fair.Modules.Main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.alimojarrad.fair.Models.Result
import com.alimojarrad.fair.Models.ServerResponse
import com.alimojarrad.fair.Services.API.UseCases.GetCarResults
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

open class ResultViewModel : ViewModel() {
    var result = MutableLiveData<ArrayList<Result>>()
    var serverResponse = MutableLiveData<ServerResponse>()
    var disposable = CompositeDisposable()

    fun getResults(map : HashMap<String,String>){
        disposable.add(
                GetCarResults.execute(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    if(it.isSuccessful){
                                        it.body()?.results?.let {
                                            result.value = it
                                        }
                                    }else{
                                        serverResponse.value = ServerResponse(false,"We could not process your request at this time.")
                                        Timber.e("${it.errorBody()}")
                                    }
                                },
                                {
                                    Timber.e(it)
                                    serverResponse.value = ServerResponse(false,"We could not process your request at this time.")

                                }
                        )
        )
    }
}