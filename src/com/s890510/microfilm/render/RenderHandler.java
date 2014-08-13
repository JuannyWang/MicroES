package com.s890510.microfilm.render;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Handler for RenderThread. Used for messages sent from the UI thread to the
 * render thread.
 * <p>
 * The object is created on the render thread, and the various "send" methods
 * are called from the UI thread.
 */
public class RenderHandler extends Handler {
    private static final String         TAG                 = "RenderHandler";

    private static final int            MSG_SURFACE_CREATED = 0;
    private static final int            MSG_SURFACE_CHANGED = 1;
    private static final int            MSG_DO_FRAME        = 2;
    private static final int            MSG_SHUTDOWN        = 3;

    // This shouldn't need to be a weak ref, since we'll go away when the Looper
    // quits,
    // but no real harm in it.
    private WeakReference<RenderThread> mWeakRenderThread;

    /**
     * Call from render thread.
     */
    public RenderHandler(RenderThread rt) {
        mWeakRenderThread = new WeakReference<RenderThread>(rt);
    }

    /**
     * Sends the "surface created" message.
     * <p>
     * Call from UI thread.
     */
    public void sendSurfaceCreated() {
        sendMessage(obtainMessage(RenderHandler.MSG_SURFACE_CREATED));
    }

    /**
     * Sends the "surface changed" message, forwarding what we got from the
     * SurfaceHolder.
     * <p>
     * Call from UI thread.
     */
    public void sendSurfaceChanged(int format, int width, int height) {
        // ignore format
        sendMessage(obtainMessage(RenderHandler.MSG_SURFACE_CHANGED, width, height));
    }

    /**
     * Sends the "do frame" message, forwarding the Choreographer event.
     * <p>
     * Call from UI thread.
     */
    public void sendDoFrame(long frameTimeNanos) {
        sendMessage(obtainMessage(RenderHandler.MSG_DO_FRAME, (int) (frameTimeNanos >> 32), (int) frameTimeNanos));
    }

    /**
     * Sends the "shutdown" message, which tells the render thread to halt.
     * <p>
     * Call from UI thread.
     */
    public void sendShutdown() {
        sendMessage(obtainMessage(RenderHandler.MSG_SHUTDOWN));
    }

    @Override
    // runs on RenderThread
    public void handleMessage(Message msg) {
        int what = msg.what;
        // Log.d(TAG, "RenderHandler [" + this + "]: what=" + what);

        RenderThread renderThread = mWeakRenderThread.get();
        if(renderThread == null) {
            Log.w(TAG, "RenderHandler.handleMessage: weak ref is null");
            return;
        }

        switch (what) {
            case MSG_SURFACE_CREATED:
                renderThread.surfaceCreated();
                break;
            case MSG_SURFACE_CHANGED:
                renderThread.surfaceChanged(msg.arg1, msg.arg2);
                break;
            case MSG_DO_FRAME:
                long timestamp = (((long) msg.arg1) << 32) | (((long) msg.arg2) & 0xffffffffL);
                renderThread.doFrame(timestamp);
                break;
            case MSG_SHUTDOWN:
                renderThread.shutdown();
                break;
            default:
                throw new RuntimeException("unknown message " + what);
        }
    }
}
