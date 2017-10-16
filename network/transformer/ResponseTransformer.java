package com.zhudai.network.transformer;

import com.zhudai.network.ResponseResult;
import com.zhudai.network.exception.ServerException;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xm on 2016/8/18.
 */
public class ResponseTransformer {
    /**
     * 对结果进行预处理
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<ResponseResult<T>, T> handleResponse() {

        return new ObservableTransformer<ResponseResult<T>, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<ResponseResult<T>> upstream) {
                return upstream.flatMap(new Function<ResponseResult<T>, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(@NonNull ResponseResult<T> response) throws Exception {
                        if (response.success()) {
                            return createData(response.data);
                        } else {
                            return Observable.error(new ServerException(response.code, response.msg));
                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };

    }

    /**
     * 创建成功的数据
     *
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Observable<T> createData(final T data) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> e) throws Exception {
                try {
                    e.onNext(data);
                    e.onComplete();
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        });

    }
}
