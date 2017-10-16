package com.zhudai.network.transformer;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public class SchedulersCompat {

    private static final ObservableTransformer computationTransformer = new ObservableTransformer() {
                @Override
                public ObservableSource apply(@NonNull Observable upstream) {
                    return upstream.subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread());
                }
            };

    private static final ObservableTransformer ioTransformer = new ObservableTransformer() {
        public ObservableSource apply(@NonNull Observable upstream) {
            return upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final ObservableTransformer newTransformer = new ObservableTransformer() {
        public ObservableSource apply(@NonNull Observable upstream) {
            return upstream.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final ObservableTransformer trampolineTransformer = new ObservableTransformer() {
        public ObservableSource apply(@NonNull Observable upstream) {
            return upstream.subscribeOn(Schedulers.trampoline())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    /**
     * 用自定义管理的线程池
     */
    private static final ObservableTransformer executorTransformer = new ObservableTransformer() {
        public ObservableSource apply(@NonNull Observable upstream) {
            return upstream.subscribeOn(Schedulers.from(JobExecutor.eventExecutor))
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final ObservableTransformer mainThreadTransformer = new ObservableTransformer() {
        public ObservableSource apply(@NonNull Observable upstream) {
            return upstream.observeOn(AndroidSchedulers.mainThread());
        }
    };

    /**
     * Don't break the chain: use RxJava's compose() operator
     */
    public static <T> ObservableTransformer<T, T> applyComputationSchedulers() {
        return (ObservableTransformer<T, T>) computationTransformer;
    }

    public static <T> ObservableTransformer<T, T> applyIoSchedulers() {
        return (ObservableTransformer<T, T>) ioTransformer;
    }

    public static <T> ObservableTransformer<T, T> applyNewSchedulers() {
        return (ObservableTransformer<T, T>) newTransformer;
    }

    public static <T> ObservableTransformer<T, T> applyTrampolineSchedulers() {
        return (ObservableTransformer<T, T>) trampolineTransformer;
    }

    public static <T> ObservableTransformer<T, T> applyExecutorSchedulers() {
        return (ObservableTransformer<T, T>) executorTransformer;
    }

    public static <T> ObservableTransformer<T, T> observeOnMainThread() {
        return (ObservableTransformer<T, T>) mainThreadTransformer;
    }
}
