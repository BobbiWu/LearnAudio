package com.example.learnaudioandvideo.PlayAudio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.media.AudioTrack;
import android.util.Log;

public class AudioTrackPlay {
    private static final String TAG = AudioTrackPlay.class.getSimpleName();

    private AudioTrack mAudioTrack;

    private volatile boolean isPalying;

    private File mFile;

    private Thread mThread;

    private byte[] mBuffer;

    public void startPlay(File file) {
        mFile = file;
        if (isPalying) {
            Log.e(TAG, "Player already started !");
            return;
        }
        int minBufferSize = AudioTrack.getMinBufferSize(Config.SAMPLE_RATE_INHZ, Config.TRACK_CHANNEL_CONFIG, Config.ENCODING_FORMAT);
        if (minBufferSize == AudioTrack.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid parameter !");
            return;
        }
        Log.e(TAG, "getMinBufferSize = " + minBufferSize + " bytes !");
        mAudioTrack = new AudioTrack(Config.DEFAULT_STREAM_TYPE, Config.SAMPLE_RATE_INHZ,
            Config.TRACK_CHANNEL_CONFIG, Config.ENCODING_FORMAT, minBufferSize, Config.TRACK_PLAY_MODE);
        if (mAudioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioTrack initialize fail !");
            return;
        }

        mBuffer = new byte[minBufferSize];

        isPalying = true;

        mThread = new Thread(new AudioTrackRunnable());
        mThread.start();
    }

    public void stopPaly() {
        if (!isPalying) {
            return;
        }
        if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.stop();
        }
        //释放
        mAudioTrack.release();
        isPalying = false;
        Log.e(TAG, "Stop audio player success !");
    }

    private class AudioTrackRunnable implements Runnable {
        @Override
        public void run() {
            FileInputStream inStream = null;
            try {
                inStream = new FileInputStream(mFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                while (inStream.available() > 0) {
                    int readCount = inStream.read(mBuffer);
                    if (readCount == AudioTrack.ERROR_BAD_VALUE || readCount == AudioTrack.ERROR_INVALID_OPERATION) {
                        continue;
                    }
                    if (readCount != 0 && readCount != -1) {
                        mAudioTrack.write(mBuffer, 0, readCount);
                    }
                    mAudioTrack.play();
                    Log.d(TAG, "OK, Played " + mBuffer + " bytes !");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
