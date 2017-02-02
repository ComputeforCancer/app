package org.computeforcancer.android.fbLogin;

/**
 * Created by artem on 24.01.17.
 */

public interface ResultDispatcher<T,E> {

    void dispatchSuccessResult(T result);

    void dispatchError(E error);
}
