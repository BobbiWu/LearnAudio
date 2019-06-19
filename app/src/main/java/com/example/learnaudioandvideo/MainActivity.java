package com.example.learnaudioandvideo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.learnaudioandvideo.PlayAudio.AudioRecorderHelper;
import com.example.learnaudioandvideo.PlayAudio.AudioTrackPlay;
import com.example.learnaudioandvideo.PlayAudio.ConvertHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PERMISSIONS_REQUEST = 1;

    private static final String PATH = Environment.getExternalStorageDirectory() + "/Insane" + "/test.pcm";

    private static final String WAVPATH = Environment.getExternalStorageDirectory() + "/Insane" + "/test.wav";

    private Button vStartRecord, vConvert, vPlay, vWavPlay;

    private AudioRecorderHelper recorderHelper;

    private AudioTrackPlay mAudioTrackPlay;

    private ConvertHelper mConvertHelper;

    /*
     * 需要申请的权限
     * */
    private String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * 被用户拒绝的权限
     */
    private List<String> refusePermissionsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();

        vStartRecord = findViewById(R.id.btnStartRecord);
        vConvert = findViewById(R.id.btnConvert);
        vPlay = findViewById(R.id.btnPlay);
        vWavPlay = findViewById(R.id.btnWavPlay);

        vStartRecord.setOnClickListener(this);
        vConvert.setOnClickListener(this);
        vPlay.setOnClickListener(this);
        vWavPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartRecord:
                if (vStartRecord.getText().equals("开始录音")) {
                    vStartRecord.setText("停止录音");
                    File file = createFile(PATH);
                    if (file != null) {
                        recorderHelper = AudioRecorderHelper.getInstance();
                        recorderHelper.startRecorder(file);
                    }
                } else {
                    vStartRecord.setText("开始录音");
                    recorderHelper.stopRecorder();
                }
                break;
            case R.id.btnPlay:
                if (vPlay.getText().equals("播放")) {
                    vPlay.setText("停止播放");
                    mAudioTrackPlay = new AudioTrackPlay();
                    File pcmFilePath = filePath(PATH);
                    if (pcmFilePath == null) return;
                    mAudioTrackPlay.startPlay(pcmFilePath);
                } else {
                    vPlay.setText("播放");
                    mAudioTrackPlay.stopPaly();
                }
                break;
            case R.id.btnConvert:
                mConvertHelper = new ConvertHelper();
                File pcmFilePath = filePath(PATH);
                if (pcmFilePath == null) return;
                mConvertHelper.pcmToWav(pcmFilePath, createFile(WAVPATH));
                break;
            case R.id.btnWavPlay:
                if (vWavPlay.getText().equals("WAV播放")) {
                    vWavPlay.setText("停止播放");
                    mAudioTrackPlay = new AudioTrackPlay();
                    File wavFilePath = filePath(WAVPATH);
                    if (wavFilePath == null) return;
                    mAudioTrackPlay.startPlay(wavFilePath);
                } else {
                    vWavPlay.setText("WAV播放");
                    mAudioTrackPlay.stopPaly();
                }
                break;
        }
    }

    private File createFile(String path) {
        File tempFile = new File(path);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        } else {
            tempFile.delete();
        }
        Log.e("TAG--", String.valueOf(tempFile));
        return tempFile;
    }

    private File filePath(String path) {
        File tempFile = new File(path);
        if (!tempFile.exists()) {
            Toast.makeText(this, "请先录制PCM", Toast.LENGTH_SHORT).show();
            return null;
        }
        Log.e("TAG--", String.valueOf(tempFile));
        return tempFile;
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) !=
                        PackageManager.PERMISSION_GRANTED) {
                    refusePermissionsList.add(permissions[i]);
                }
            }
            if (!refusePermissionsList.isEmpty()) {
                String[] permissions = refusePermissionsList.toArray(new String[refusePermissionsList.size()]);
                ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // Permission Denied 权限被拒绝
                    Toast.makeText(MainActivity.this, "Permission Denied",
                            Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
