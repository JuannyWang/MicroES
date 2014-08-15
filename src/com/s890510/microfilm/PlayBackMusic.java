
package com.s890510.microfilm;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class PlayBackMusic
{
    private String          TAG                   = "Show_BackMusic";
    private boolean         isStop                = false;
    private MediaPlayer     player;
    private AssetFileDescriptor mSrcFd;

    public void prepareMusic(Context context, int musicId) {
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
        	mSrcFd = context.getAssets().openFd(MusicManager.getFilePath(musicId));
        	player.setDataSource(mSrcFd.getFileDescriptor(), mSrcFd.getStartOffset(), mSrcFd.getLength());
            player.prepare();
            player.setLooping(false);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getduration() {
        return ((int)player.getDuration()/100)*100;
    }

    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public void stopplay() {
        isStop = true;
    }

    public void start() {
        player.start();
    }

    public void pause() {
        player.pause();
    }

    public void destroy() {
    	if(player != null){
	        if(player.isPlaying())
	            player.stop();
	        player.release();
	        player = null;
    	}
    }

    public void seekTo(int progress) {
        player.seekTo(progress);
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public boolean CheckPlayer() {
        return player != null;
    }
}
