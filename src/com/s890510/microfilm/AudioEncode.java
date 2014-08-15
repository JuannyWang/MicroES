package com.s890510.microfilm;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

public class AudioEncode {
    private static final String TAG="AudioEncode";

    private static final boolean VERBOSE = false;
    private static final int MAX_SAMPLE_SIZE = 256 * 1024;
    //private static int AUDIO_SOURCE = R.raw.theme_film_audio_track;
    //private static int AUDIO_SOURCE = R.raw.twilight_soundtrack;

    public static final int NO_TIME_LIMIT = -1;
    
    private MediaExtractor mExtractor;
    private AssetFileDescriptor mSrcFd;
    private HashMap<Integer, Integer> mIndexMap;
    private int mMusicId;
    private int mEncodeTime;

    //public static void doEncode(Context context, String dstMediaPath, MediaFormat videoFormat){
    public void doEncode(Context context, String dstMediaPath, MediaMuxer muxer){
        try {
            //cloneMediaUsingMuxer(context, videoFormat, source, dstMediaPath, 1, -1);
            cloneMediaUsingMuxer(context, muxer, dstMediaPath, 1, -1);

        } catch (IOException e) {
            e.printStackTrace();
            new File(dstMediaPath).delete();
        }
    }

    public void setupAudioMuxer(Context context, MediaMuxer muxer, int time){
        // Set up MediaExtractor to read from the source.
        //mSrcFd = context.getResources().openRawResourceFd(source);
        
        mExtractor = new MediaExtractor();
        try {
        	mSrcFd = context.getAssets().openFd(MusicManager.getFilePath(mMusicId));
            mExtractor.setDataSource(mSrcFd.getFileDescriptor(), mSrcFd.getStartOffset(), mSrcFd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int trackCount = mExtractor.getTrackCount();

        // Set up the tracks.
        mIndexMap = new HashMap<Integer, Integer>(trackCount);
        for (int i = 0; i < trackCount; i++) {
            mExtractor.selectTrack(i);
            MediaFormat format = mExtractor.getTrackFormat(i);
            int dstIndex = muxer.addTrack(format);
            mIndexMap.put(i, dstIndex);
        }
        
        if(time == NO_TIME_LIMIT)
        	mEncodeTime = time;
        else mEncodeTime = time * 1000000; // sec to microsecond
    }

    /**
     * Using the MediaMuxer to clone a media file.
     */
    //private static void cloneMediaUsingMuxer(Context context, MediaFormat videoFormat, int srcMedia, String dstMediaPath,
    private void cloneMediaUsingMuxer(Context context, MediaMuxer muxer, String dstMediaPath,
            int expectedTrackCount, int degrees) throws IOException {

        // Copy the samples from MediaExtractor to MediaMuxer.
        boolean sawEOS = false;
        int bufferSize = MAX_SAMPLE_SIZE;
        int frameCount = 0;
        int offset = 100;

        ByteBuffer dstBuf = ByteBuffer.allocate(bufferSize);
        BufferInfo bufferInfo = new BufferInfo();

        if (degrees >= 0) {
            muxer.setOrientationHint(degrees);
        }
        //muxer.start();
        while (!sawEOS) {
            bufferInfo.offset = offset;
            bufferInfo.size = mExtractor.readSampleData(dstBuf, offset);

            if (bufferInfo.size < 0) {
                if (VERBOSE) {
                    Log.d(TAG, "saw input EOS.");
                }
                sawEOS = true;
                bufferInfo.size = 0;
            } else {
                bufferInfo.presentationTimeUs = mExtractor.getSampleTime();
                
                if(mEncodeTime != NO_TIME_LIMIT){
	                if(bufferInfo.presentationTimeUs > mEncodeTime){
	                    sawEOS = true;
	                    bufferInfo.size = 0;
	                    continue;
	                }
                }
                
                bufferInfo.flags = mExtractor.getSampleFlags();
                int trackIndex = mExtractor.getSampleTrackIndex();
                muxer.writeSampleData(mIndexMap.get(trackIndex), dstBuf,
                        bufferInfo);
                //muxer.writeSampleData(audioIndex, dstBuf,
                //        bufferInfo);
                mExtractor.advance();

                frameCount++;
                if (VERBOSE) {
                    Log.d(TAG, "Frame (" + frameCount + ") " +
                            "PresentationTimeUs:" + bufferInfo.presentationTimeUs +
                            " Flags:" + bufferInfo.flags +
                            " TrackIndex:" + trackIndex +
                            " Size(KB) " + bufferInfo.size / 1024);
                }
            }
        }
        //muxer.stop();
        //muxer.release();
        mSrcFd.close();
        return;
    }
    
    public void setAudioSource(Context context, int musicId){
    	mMusicId = musicId;
    }
}
