package com.example.learnaudioandvideo.PlayAudio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecorderHelper {
    private static final String TAG = AudioRecorderHelper.class.getSimpleName();

    private static AudioRecorderHelper mAudioRecorderHelper;

    private AudioRecord mAudioRecorder;

    private volatile boolean isRecording;

    private Thread mThread;

    private File file;

    private byte[] mBufferByte;

    public static AudioRecorderHelper getInstance() {
        if (mAudioRecorderHelper == null) {
            mAudioRecorderHelper = new AudioRecorderHelper();
        }
        return mAudioRecorderHelper;
    }

    public void startRecorder(File file) {
        this.file = file;
        if (isRecording) {
            Log.e(TAG, "Recorder already started !");
            return;
        }
        /*
          audioSource:    音频采集的输入源，可选的值以常量的形式定义在 MediaRecorder.AudioSource 类中,例如：MIC（由手机麦克风输入），VOICE_COMMUNICATION（用于VoIP应用）等等。
          sampleRateInHz: 采样率，注意，目前44100Hz是唯一可以保证兼容所有Android手机的采样率。
          channelConfig:  通道数的配置，可选的值以常量的形式定义在 AudioFormat 类中，常用的是 CHANNEL_IN_MONO（单通道），CHANNEL_IN_STEREO（双通道）
          audioFormat:    这个参数是用来配置“数据位宽”的，可选的值也是以常量的形式定义在 AudioFormat 类中，常用的是 ENCODING_PCM_16BIT（16bit），ENCODING_PCM_8BIT（8bit），注意，前者是可以保证兼容所有Android手机的。
          bufferSizeInBytes:  AudioRecord 内部的音频缓冲区的大小，该缓冲区的值不能低于一帧“音频帧”（Frame）的大小
         */
        int minBufferSize = AudioRecord.getMinBufferSize(Config.SAMPLE_RATE_INHZ, Config.CHANNEL_CONFIG, Config.ENCODING_FORMAT);
        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid parameter !");
            return;
        }

        mBufferByte = new byte[minBufferSize];

        mAudioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, Config.SAMPLE_RATE_INHZ,
            Config.CHANNEL_CONFIG, Config.ENCODING_FORMAT, minBufferSize);

        if (mAudioRecorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioRecord initialize fail !");
            return;
        }
        mAudioRecorder.startRecording();
        mThread = new Thread(new AudioRunnable());
        mThread.start();
        isRecording = true;
    }

    public void stopRecorder() {
        if (!isRecording) {
            return;
        }
        mAudioRecorder.release();
        isRecording = false;
        Log.e(TAG, "Stop AudioRecorder success !");
    }

    private class AudioRunnable implements Runnable {
        @Override
        public void run() {
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (os != null) {
                while (isRecording) {
                    int ret = mAudioRecorder.read(mBufferByte, 0, mBufferByte.length);
                    if (ret == AudioRecord.ERROR_INVALID_OPERATION) {
                        Log.e(TAG, "Error ERROR_INVALID_OPERATION");
                    } else if (ret == AudioRecord.ERROR_BAD_VALUE) {
                        Log.e(TAG, "Error ERROR_BAD_VALUE");
                    } else {
                        Log.e("TAG", "Audio captured: " + mBufferByte.length);
                        try {
                            os.write(mBufferByte);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
