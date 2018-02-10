package com.binarybricks.coinhood.components.historicalchartmodule

import CoinDetailContract
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import com.binarybricks.coinhood.network.schedulers.BaseSchedulerProvider
import com.binarybricks.coinhood.stories.BasePresenter
import com.binarybricks.coinhood.stories.CryptoCompareRepository
import timber.log.Timber

/**
 * Created by pranay airan on 1/17/18.
 */

class CoinDetailPresenter(private val schedulerProvider: BaseSchedulerProvider) : BasePresenter<CoinDetailContract.View>()
        , CoinDetailContract.Presenter, LifecycleObserver {

    private val coinRepo by lazy {
        CryptoCompareRepository(schedulerProvider)
    }

    /**
     * Get the current price of a coin say btc or eth
     */
    override fun loadCurrentCoinPrice(fromCurrency: String, toCurrency: String) {
        compositeDisposable.add(coinRepo.getCoinPriceFull(fromCurrency, toCurrency)
                .filter { it.size > 0 }
                .observeOn(schedulerProvider.ui())
                .subscribe({
                    currentView?.onCoinDataLoaded(it[0])
                }, { Timber.e(it.localizedMessage) }))
    }


    // cleanup
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanYourSelf() {
        detachView()
    }
}