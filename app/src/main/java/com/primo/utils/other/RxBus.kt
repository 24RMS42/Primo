package com.primo.utils.other

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

class RxBus {

    private val _bus : Subject<RxEvent, RxEvent> = SerializedSubject<RxEvent, RxEvent>(PublishSubject.create());

    fun send(o : RxEvent) {
        _bus.onNext(o);
    }

    fun toObserverable() : Observable<RxEvent> {
        return _bus;
    }

    fun hasObservers() : Boolean {
        return _bus.hasObservers();
    }

}
enum class Events {
    QR_NOT_FOUND_DIALOG_CLOSE,
    ADD_PRODUCT,
    ADD_PRODUCTS,
    CHANGE_COST,
    COST_REQUEST,
    CLEAR_PRODUCTS,
    UPDATE_PRODUCTS,
    CAMERA_PERMISSION,
    LOCATION_PERMISSION,
    SIGNED,
    CONFIRMED
}

data class RxEvent(var key: Any, var sentObject: Any? = null) {

}
