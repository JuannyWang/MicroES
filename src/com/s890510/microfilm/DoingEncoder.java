package com.s890510.microfilm;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import com.s890510.microfilm.draw.GLDraw;
import com.s890510.microfilm.gles.EglCore;
import com.s890510.microfilm.gles.WindowSurface;

public class DoingEncoder {
	private static final String TAG = "DoingEncoder";
    private static final boolean VERBOSE = false;

	private MediaCodec.BufferInfo mBufferInfo;

	private static final String MIME_TYPE = "video/avc";
    private static final int WIDTH = 480;       // note 480x640, not 640x480
    private static final int HEIGHT = 640;
    private static final int BIT_RATE = 5000000;
    private static final int FRAMES_PER_SECOND = 30;
	private static final int IFRAME_INTERVAL = 5;
	
	private MediaCodec mEncoder;
    private MediaMuxer mMuxer;
    private EglCore mEglCore;
    private WindowSurface mInputSurface;
    private int mTrackIndex;
    private boolean mMuxerStarted;
    
    private GLDraw mGLDraw;
    
    public DoingEncoder() {
    	//mGLDraw = new GLDraw();
	}
    
    public void Start(/*SaveCallback callback*/) {
    	create(new File(Environment.getExternalStorageDirectory().toString(), "test.mp4"));
    	//callback.onSaveDone();
	}

    public void create(File outputFile) {
        final int NUM_FRAMES = 240;

        try {
            prepareEncoder(MIME_TYPE, WIDTH, HEIGHT, BIT_RATE, FRAMES_PER_SECOND, outputFile);
            mGLDraw.setView(WIDTH, HEIGHT);
            mGLDraw.setEye();
            mGLDraw.prepare();

            for (int i = 0; i < NUM_FRAMES; i++) {
                // Drain any data from the encoder into the muxer.
                drainEncoder(false);

                // Generate a frame and submit it.
                //generateFrame(i);
                mGLDraw.draw();
                submitFrame(computePresentationTimeNsec(i));
            }

            // Send end-of-stream and drain remaining output.
            drainEncoder(true);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            releaseEncoder();
        }

        Log.d(TAG, "MovieEightRects complete: " + outputFile);
    }

    /**
     * Generates the presentation time for frame N, in nanoseconds.  Fixed frame rate.
     */
    private static long computePresentationTimeNsec(int frameIndex) {
        final long ONE_BILLION = 1000000000;
        return frameIndex * ONE_BILLION / FRAMES_PER_SECOND;
    }

    /**
     * Prepares the video encoder, muxer, and an EGL input surface.
     */
    protected void prepareEncoder(String mimeType, int width, int height, int bitRate,
            int framesPerSecond, File outputFile) throws IOException {
        mBufferInfo = new MediaCodec.BufferInfo();

        MediaFormat format = MediaFormat.createVideoFormat(mimeType, width, height);

        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, framesPerSecond);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        if (VERBOSE) Log.d(TAG, "format: " + format);

        // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
        // we can use for input and wrap it with a class that handles the EGL work.
        mEncoder = MediaCodec.createEncoderByType(mimeType);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        Log.v(TAG, "encoder is " + mEncoder.getCodecInfo().getName());
        Surface surface;
        try {
            surface = mEncoder.createInputSurface();
        } catch (IllegalStateException ise) {
            // This is generally the first time we ever try to encode something through a
            // Surface, so specialize the message a bit if we can guess at why it's failing.
            // TODO: failure message should come out of strings.xml for i18n
            if (isSoftwareCodec(mEncoder)) {
                throw new RuntimeException("Can't use input surface with software codec: " +
                        mEncoder.getCodecInfo().getName(),
                        ise);
            } else {
                throw new RuntimeException("Failed to create input surface", ise);
            }
        }
        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);
        mInputSurface = new WindowSurface(mEglCore, surface, true);
        mInputSurface.makeCurrent();
        mEncoder.start();

        // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
        // obtained from the encoder after it has started processing data.
        //
        // We're not actually interested in multiplexing audio.  We just want to convert
        // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
        if (VERBOSE) Log.d(TAG, "output will go to " + outputFile);
        mMuxer = new MediaMuxer(outputFile.toString(),
                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        mTrackIndex = -1;
        mMuxerStarted = false;
    }

    /**
     * Returns true if the codec has a software implementation.
     */
    private static boolean isSoftwareCodec(MediaCodec codec) {
        String codecName = codec.getCodecInfo().getName();

        return ("OMX.google.h264.encoder".equals(codecName));
    }

    /**
     * Releases encoder resources.  May be called after partial / failed initialization.
     */
    protected void releaseEncoder() {
        if (VERBOSE) Log.d(TAG, "releasing encoder objects");
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }

    /**
     * Submits a frame to the encoder.
     *
     * @param presentationTimeNsec The presentation time stamp, in nanoseconds.
     */
    protected void submitFrame(long presentationTimeNsec) {
        // The eglSwapBuffers call will block if the input is full, which would be bad if
        // it stayed full until we dequeued an output buffer (which we can't do, since we're
        // stuck here).  So long as the caller fully drains the encoder before supplying
        // additional input, the system guarantees that we can supply another frame
        // without blocking.
        mInputSurface.setPresentationTime(presentationTimeNsec);
        mInputSurface.swapBuffers();
    }

    /**
     * Extracts all pending data from the encoder.
     * <p>
     * If endOfStream is not set, this returns when there is no more data to drain.  If it
     * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
     * Calling this with endOfStream set should be done once, right before stopping the muxer.
     */
    protected void drainEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 10000;
        if (VERBOSE) Log.d(TAG, "drainEncoder(" + endOfStream + ")");

        if (endOfStream) {
            if (VERBOSE) Log.d(TAG, "sending EOS to encoder");
            mEncoder.signalEndOfInputStream();
        }

        ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();
        while (true) {
            int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break;      // out of while
                } else {
                    if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS");
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = mEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mMuxerStarted) {
                    throw new RuntimeException("format changed twice");
                }
                MediaFormat newFormat = mEncoder.getOutputFormat();
                Log.d(TAG, "encoder output format changed: " + newFormat);

                // now that we have the Magic Goodies, start the muxer
                mTrackIndex = mMuxer.addTrack(newFormat);
                mMuxer.start();
                mMuxerStarted = true;
            } else if (encoderStatus < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                        encoderStatus);
                // let's ignore it
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }

                    // adjust the ByteBuffer values to match BufferInfo
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                    if (VERBOSE) Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer");
                }

                mEncoder.releaseOutputBuffer(encoderStatus, false);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.w(TAG, "reached end of stream unexpectedly");
                    } else {
                        if (VERBOSE) Log.d(TAG, "end of stream reached");
                    }
                    break;      // out of while
                }
            }
        }
    }
}
