package at.stefangaller.reactivex.ui.main

import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {

    private val searchResult = BehaviorSubject.create<String>()
    private val latestQuery: BehaviorSubject<String> = BehaviorSubject.create()
    private val loadingSubject = PublishSubject.create<Boolean>()
    private val errorSubject = PublishSubject.create<String>()

    // IN Streams
    val clickSubject: PublishSubject<Any> = PublishSubject.create()
    val textSubject: PublishSubject<CharSequence> = PublishSubject.create()

    // OUT Streams
    val searchResultLiveData =
        LiveDataReactiveStreams.fromPublisher(searchResult.toFlowable(BackpressureStrategy.LATEST))
    val showLoadingLiveData =
        LiveDataReactiveStreams.fromPublisher(loadingSubject.toFlowable(BackpressureStrategy.LATEST))
    val errorLiveData =
        LiveDataReactiveStreams.fromPublisher(errorSubject.toFlowable(BackpressureStrategy.LATEST))

    init {
        clickSubject.withLatestFrom(textSubject) { _, text -> text }
            .map { it.toString() }
            .subscribe(latestQuery)

        latestQuery.distinctUntilChanged()
            .doOnEach { loadingSubject.onNext(true) }
            .switchMap { query ->
                simulateQuery(query)
                    .doOnEach { loadingSubject.onNext(false) }
                    .doOnError { errorSubject.onNext("Ooops. An error occured") }
                    .doOnComplete { errorSubject.onNext("") }
                    .onErrorReturnItem("")

            }
            .subscribe(searchResult)

    }


    private fun simulateQuery(query: String): Observable<String> =
        Observable.just("result for '$query'")
            .delay(500, TimeUnit.MILLISECONDS)
            .map { if (query == "error") throw Exception("oh no") else it }

}