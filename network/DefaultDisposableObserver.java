package com.zhudai.network;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by xm on 2017/9/18.
 * 默认实现
 * 减少实际使用时不需要关注onError/onComplete时的代码量
 */
public class DefaultDisposableObserver<T> extends DisposableObserver<T>{

    @Override
    public void onNext(@NonNull T t) {

    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {

    }
}
