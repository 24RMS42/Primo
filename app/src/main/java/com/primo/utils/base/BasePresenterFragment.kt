package com.primo.utils.base


abstract  class BasePresenterFragment<V : BaseView, P : BasePresenter<V>> : BaseFragment() {

    protected var presenter : P? = null

    abstract protected fun initPresenter()

    override fun onResume() {
        super.onResume()
        presenter?.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }
}