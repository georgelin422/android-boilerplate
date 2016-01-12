package uk.co.ribot.androidboilerplate.ui.base;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * attachView() and detachView(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */
public class BasePresenter<T extends MvpView> implements Presenter<T> {

    private final BehaviorSubject<PresenterEvent> subject = BehaviorSubject.create();

    private T mMvpView;

    @Override
    public void attachView(T mvpView) {
        mMvpView = mvpView;
        subject.onNext(PresenterEvent.ATTACH_VIEW);
    }

    @Override
    public void detachView() {
        subject.onNext(PresenterEvent.DETACH_VIEW);
        mMvpView = null;
    }

    public boolean isViewAttached() {
        return mMvpView != null;
    }

    public T getMvpView() {
        return mMvpView;
    }

    public void checkViewAttached() {
        if (!isViewAttached())
            throw new MvpViewNotAttachedException();
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before" +
                " requesting data to the Presenter");
        }
    }

    /**
     * Returns an {@link Observable} of {@link PresenterEvent}.
     *
     * @param presenterEvent
     * @return
     */
    public Observable<PresenterEvent> presenterEvent(final PresenterEvent presenterEvent) {
        return subject.takeFirst(new Func1<PresenterEvent, Boolean>() {
            @Override public Boolean call(PresenterEvent event) {
                return event == presenterEvent;
            }
        });
    }

    protected enum PresenterEvent {
        ATTACH_VIEW, DETACH_VIEW
    }

}

