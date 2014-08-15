package com.s890510.microfilm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import com.s890510.microfilm.util.MD5Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.media.videoeditor.AudioTrack;
import android.media.videoeditor.MediaItem;
import android.media.videoeditor.MediaProperties;
import android.media.videoeditor.MediaVideoItem;
import android.media.videoeditor.VideoEditor;
import android.media.videoeditor.VideoEditor.ExportProgressListener;
import android.media.videoeditor.VideoEditorImpl;
import android.os.RemoteCallbackList;
import android.os.RemoteException;


public class AudioMux {
    private final String TAG = "EncodeMux";
    private VideoEditorImpl mVideoEditorImpl;
    private String mProjectPath;

    private AudioTrack mAudioTrack;

    private char[] sNumbersAndLetters =
            ("0123456789abcdefghijklmnopqrstuvwxyz0123456789").toCharArray();
    private Random sRandGen = new Random(System.currentTimeMillis());
    
    private ProgressDialog mProgressDialog;
    private int mProgress = 0;

    public AudioMux(String path, ProgressDialog progressDialog) throws IOException {
    	mProgressDialog = progressDialog;
        mProjectPath = path;
        mVideoEditorImpl = new VideoEditorImpl(mProjectPath);
    }

    public void setAudio(Context context, int musicId, String filename) throws IOException {
    	String filePath = null;
		filePath = getAudioFilePath(context, musicId, filename);
		if(filePath != null) {
			mAudioTrack = new AudioTrack(mVideoEditorImpl, randomString(6), filePath);
            mVideoEditorImpl.addAudioTrack(mAudioTrack);
		}else{
			throw new IOException();
		}			
    }

    public void setVideo(String filename) throws IOException {
        MediaItem mediaItem = new MediaVideoItem(
                mVideoEditorImpl,
                randomString(6),
                filename,
                MediaItem.RENDERING_MODE_BLACK_BORDER);

        mVideoEditorImpl.insertMediaItem(mediaItem, null);
    }

    public void doExport(String path) throws IOException {
        mVideoEditorImpl.export(path, 720, MediaProperties.BITRATE_5M, new ExportProgressListener() {
            @Override
            public void onProgress(VideoEditor videoEditor, String filename, int progress) {
                //Log.e(TAG, "progress:" + progress);
            	mProgressDialog.incrementProgressBy(progress - mProgress);
                mProgress = progress;
            }
        });
    }

    public String randomString(int length) {
        if (length < 1) {
            return null;
        }
        // Create a char buffer to put random letters and numbers in.
        final char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = sNumbersAndLetters[sRandGen.nextInt(sNumbersAndLetters.length - 1)];
        }

        return new String(randBuffer);
    }

    public void release() {
        mVideoEditorImpl.release();
    }
    
    private String getAudioFilePath(Context context, int musicId, String filename) throws IOException{
        if(filename == null)
        	return null;

        final File mf = new File(context.getFilesDir(), filename);
        if (!mf.exists()) {
            FileOutputStream fos = null;
            InputStream is = null;
            try {
            	is = context.getAssets().open(MusicManager.getFilePath(musicId));
                fos = context.openFileOutput(filename, 0);
                final byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, bytesRead);
                }
            } catch(Exception e){
            	if(mf.exists())
            		mf.delete();
            	e.printStackTrace();
            } finally {
                if (is != null) {
                    is.close();
                }

                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            }
        }
        
        if(mf.exists()){
        	String correctMD5 = MD5Util.getCorrectMD5(musicId);
        	String fileMD5 = MD5Util.fileToMD5(mf.getAbsolutePath());
        	
        	if(correctMD5!=null && fileMD5!=null && correctMD5.equals(fileMD5)){
        		return mf.getAbsolutePath();
        	}else{
        		mf.delete();
        	}
        }
        return null;
    }
}
