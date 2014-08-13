package com.s890510.microfilm.util;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.util.Log;

public class ThreadPool {
    private static final String TAG             = "ThreadPool";
    public static final int     CORE_POOL_SIZE  = 4;
    public static final int     MAX_POOL_SIZE   = 8;
    private static final int    KEEP_ALIVE_TIME = 10;                    // 10
                                                                          // seconds

    // Resource type
    public static final int     MODE_NONE       = 0;
    public static final int     MODE_CPU        = 1;
    public static final int     MODE_NETWORK    = 2;

    ResourceCounter             mCpuCounter     = new ResourceCounter(2);
    ResourceCounter             mNetworkCounter = new ResourceCounter(2);

    private final Executor      mExecutor;

    public interface Job<T> {
        public T run(JobContext jc);
    }

    public interface JobContext {
        boolean isCancelled();

        void setCancelListener(CancelListener listener);

        boolean setMode(int mode);
    }

    public interface CancelListener {
        public void onCancel();
    }

    // Submit a job to the thread pool. The listener will be called when the
    // job is finished (or cancelled).
    public <T> Future<T> submit(Job<T> job, FutureListener<T> listener) {
        Worker<T> w = new Worker<T>(job, listener);
        mExecutor.execute(w);
        return w;
    }

    public ThreadPool(int initPoolSize, int maxPoolSize, String name) {
        mExecutor = new ThreadPoolExecutor(initPoolSize, maxPoolSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                new PriorityThreadFactory(name, android.os.Process.THREAD_PRIORITY_BACKGROUND));
    }

    private static class ResourceCounter {
        public int value;

        public ResourceCounter(int v) {
            value = v;
        }
    }

    private class Worker<T> implements Runnable, Future<T>, JobContext {
        @SuppressWarnings("hiding")
        private static final String TAG = "Worker";
        private Job<T>              mJob;
        private FutureListener<T>   mListener;
        private CancelListener      mCancelListener;
        private ResourceCounter     mWaitOnResource;
        private volatile boolean    mIsCancelled;
        private boolean             mIsDone;
        private T                   mResult;
        private int                 mMode;

        public Worker(Job<T> job, FutureListener<T> listener) {
            mJob = job;
            mListener = listener;
        }

        // This is called by a thread in the thread pool.
        @Override
        public void run() {
            T result = null;

            // A job is in CPU mode by default. setMode returns false
            // if the job is cancelled.
            if(setMode(MODE_CPU)) {
                try {
                    result = mJob.run(this);
                } catch(Throwable ex) {
                    Log.w(TAG, "Exception in running a job", ex);
                }
            }

            synchronized(this) {
                setMode(MODE_NONE);
                mResult = result;
                mIsDone = true;
                notifyAll();
            }
            if(mListener != null)
                mListener.onFutureDone(this);
        }

        // Below are the methods for Future.
        @Override
        public synchronized void cancel() {
            if(mIsCancelled)
                return;
            mIsCancelled = true;
            if(mWaitOnResource != null) {
                synchronized(mWaitOnResource) {
                    mWaitOnResource.notifyAll();
                }
            }
            if(mCancelListener != null) {
                mCancelListener.onCancel();
            }
        }

        @Override
        public boolean isCancelled() {
            return mIsCancelled;
        }

        @Override
        public synchronized boolean isDone() {
            return mIsDone;
        }

        @Override
        public synchronized T get() {
            while(!mIsDone) {
                try {
                    wait();
                } catch(Exception ex) {
                    Log.w(TAG, "ingore exception", ex);
                    // ignore.
                }
            }
            return mResult;
        }

        @Override
        public void waitDone() {
            get();
        }

        // Below are the methods for JobContext (only called from the
        // thread running the job)
        @Override
        public synchronized void setCancelListener(CancelListener listener) {
            mCancelListener = listener;
            if(mIsCancelled && mCancelListener != null) {
                mCancelListener.onCancel();
            }
        }

        @Override
        public boolean setMode(int mode) {
            // Release old resource
            ResourceCounter rc = modeToCounter(mMode);
            if(rc != null)
                releaseResource(rc);
            mMode = MODE_NONE;

            // Acquire new resource
            rc = modeToCounter(mode);
            if(rc != null) {
                if(!acquireResource(rc)) {
                    return false;
                }
                mMode = mode;
            }

            return true;
        }

        private ResourceCounter modeToCounter(int mode) {
            if(mode == MODE_CPU) {
                return mCpuCounter;
            } else if(mode == MODE_NETWORK) {
                return mNetworkCounter;
            } else {
                return null;
            }
        }

        private boolean acquireResource(ResourceCounter counter) {
            while(true) {
                synchronized(this) {
                    if(mIsCancelled) {
                        mWaitOnResource = null;
                        return false;
                    }
                    mWaitOnResource = counter;
                }

                synchronized(counter) {
                    if(counter.value > 0) {
                        counter.value--;
                        break;
                    } else {
                        try {
                            counter.wait();
                        } catch(InterruptedException ex) {
                            // ignore.
                        }
                    }
                }
            }

            synchronized(this) {
                mWaitOnResource = null;
            }

            return true;
        }

        private void releaseResource(ResourceCounter counter) {
            synchronized(counter) {
                counter.value++;
                counter.notifyAll();
            }
        }
    }
}
