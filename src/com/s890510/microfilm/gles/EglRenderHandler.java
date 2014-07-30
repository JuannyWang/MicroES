/**
 * Handler for RenderThread.  Used for messages sent from the UI thread to the render thread.
 * <p>
 * The object is created on the render thread, and the various "send" methods are called
 * from the UI thread.
 */

package com.s890510.microfilm.gles;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class EglRenderHandler extends Handler {
	private static final String TAG = "EglRanderHandler";
    
    private static final int MSG_SURFACE_CREATED = 0;
    private static final int MSG_SURFACE_CHANGED = 1;
    private static final int MSG_DO_FRAME = 2;
    private static final int MSG_SHUTDOWN = 3;

    // This shouldn't need to be a weak ref, since we'll go away when the Looper quits,
    // but no real harm in it.
    private WeakReference<EglRender> mWeakRenderThread;

    /**
     * Call from render thread.
     */
    public EglRenderHandler(EglRender rt) {
        mWeakRenderThread = new WeakReference<EglRender>(rt);
    }

    /**
     * Sends the "surface created" message.
     * <p>
     * Call from UI thread.
     */
    public void sendSurfaceCreated() {
        sendMessage(obtainMessage(EglRenderHandler.MSG_SURFACE_CREATED));
    }

    /**
     * Sends the "surface changed" message, forwarding what we got from the SurfaceHolder.
     * <p>
     * Call from UI thread.
     */
    public void sendSurfaceChanged(@SuppressWarnings("unused") int format,
            int width, int height) {
        // ignore format
        sendMessage(obtainMessage(EglRenderHandler.MSG_SURFACE_CHANGED, width, height));
    }

    /**
     * Sends the "do frame" message, forwarding the Choreographer event.
     * <p>
     * Call from UI thread.
     */
    public void sendDoFrame() {
        sendMessage(obtainMessage(EglRenderHandler.MSG_DO_FRAME));
    }

    /**
     * Sends the "shutdown" message, which tells the render thread to halt.
     * <p>
     * Call from UI thread.
     */
    public void sendShutdown() {
        sendMessage(obtainMessage(EglRenderHandler.MSG_SHUTDOWN));
    }

    @Override  // runs on EglRander
    public void handleMessage(Message msg) {
        int what = msg.what;
        //Log.d(TAG, "RenderHandler [" + this + "]: what=" + what);

        EglRender mEglRander = mWeakRenderThread.get();
        if (mEglRander == null) {
            Log.w(TAG, "RenderHandler.handleMessage: weak ref is null");
            return;
        }

        switch (what) {
            case MSG_SURFACE_CREATED:
                mEglRander.surfaceCreated();
                break;
            case MSG_SURFACE_CHANGED:
                mEglRander.surfaceChanged(msg.arg1, msg.arg2);
                break;
            case MSG_DO_FRAME:
                mEglRander.doFrame();
                break;
            case MSG_SHUTDOWN:
                mEglRander.shutdown();
                break;
           default:
                throw new RuntimeException("unknown message " + what);
        }
    }
}
