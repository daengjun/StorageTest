package com.snf.teststorageproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String Path_Full = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/testText";
    private String fileName, fileNumber ;
    private EditText editText;
    private Button save_button, copy_button, content_read_button, file_list_lookup, file_delete;
    private TextView content_read_tv, file_list_tv;
    private int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {

        editText = findViewById(R.id.editTxt);
        save_button = findViewById(R.id.file_save_txt);
        save_button.setOnClickListener(this);
        copy_button = findViewById(R.id.file_copy);
        copy_button.setOnClickListener(this);
        content_read_button = findViewById(R.id.content_read_button);
        content_read_button.setOnClickListener(this);
        content_read_tv = findViewById(R.id.content_read_tv);
        file_list_lookup = findViewById(R.id.file_list_lookup);
        file_list_lookup.setOnClickListener(this);
        file_delete = findViewById(R.id.file_delete);
        file_delete.setOnClickListener(this);
        file_list_tv = findViewById(R.id.file_list_tv);

        permission();
        getFileList(false);
    }

    /**
     * 초기 파일 저장,읽기 권한 부여
     **/
    public void permission() {

        String temp = ""; //파일 읽기 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }
        //파일 쓰기 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }
        if (!TextUtils.isEmpty(temp)) {
            // 권한 요청
            ActivityCompat.requestPermissions(this, temp.trim().split(" "), 1000);

        }
    }

    /**
     * 텍스트 파일 만드는 메서드
     **/
    public void writeLog() {

        if (editText.getText().equals("")) {
            Toast.makeText(this, "저장할 값을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        fileName = i + 1 + ".테스트 파일.txt";
        File file = new File(Path_Full + File.separator, fileName);

        //파일이 없는경우 파일을 생성한후 로그 기록
        if (!file.exists()) {
            //폴더가 없을 때 생성
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            try {
                file.createNewFile();
            } catch (Exception e) {
                Log.e("writeLog", e.toString());
            }
        }
        try {
            BufferedWriter bfw = new BufferedWriter(new FileWriter(Path_Full + File.separator + fileName, true));

            bfw.write(editText.getText().toString());
            bfw.write("\n");

            bfw.flush();
            bfw.close();

            ++i;

            Toast.makeText(this, "파일 생성이 완료 되었습니다.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e("writeLog", e.toString());
        }

    }

    /**
     * 내부저장소로 파일 이동하는 매서드 , 이미 파일이 있는 경우 덮어 쓰기
     **/
    private void copy(String path_Full, String name) throws IOException {
        InputStream in = new FileInputStream(path_Full + name);
        try {
            FileOutputStream fos = openFileOutput(name, MODE_PRIVATE);
            try {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
            } finally {
                fos.close();
            }
        } finally {
            new File(path_Full + File.separator + name).delete();
            in.close();
        }
        Toast.makeText(this, "파일 복사가 완료 되었습니다.", Toast.LENGTH_SHORT).show();

    }

    /**
     * 내부 저장소에서 파일 읽어오는 메서드
     **/
    private void readFile() {

        StringBuffer buffer = new StringBuffer();
        String data;
        FileInputStream fis;
        try {
            fis = openFileInput(fileNumber);

            if (fis.available() < 0) {
                Toast.makeText(this, "값을 다시 입력해주세요 " + buffer, Toast.LENGTH_SHORT).show();
                return;
            }

            BufferedReader iReader = new BufferedReader(new InputStreamReader((fis)));

            data = iReader.readLine();
            while (data != null) {
                buffer.append(data);
                data = iReader.readLine();
            }
            content_read_tv.setText(buffer.toString());
            iReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!buffer.toString().equals(""))
            Toast.makeText(this, "파일을 읽어 왔습니다. 값 : " + buffer, Toast.LENGTH_SHORT).show();


    }

    /**
     * 파일 삭제하는 메서드
     **/
    private void file_delete() {

        if (deleteFile(fileNumber)) {
            Toast.makeText(this, "파일 삭제 완료", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "파일 삭제 실패", Toast.LENGTH_SHORT).show();

        }


    }


    /**
     * 내부 저장소에 파일 만드는 메서드
     **/
    private void internalStorageWrite() {

        FileOutputStream fos;
        String strFileContents = editText.getText().toString();

        try {
            fos = openFileOutput("sample.txt", MODE_PRIVATE);
            fos.write(strFileContents.getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 내부저장소에 파일 목록 확인하는 매서드
     **/
    private void getFileList(boolean fileSearch) {

        String list = "";
        if (this.fileList().length > 0) {
            for (String a : this.fileList()) {
                list += a;
                list += "\n";
                if (fileSearch) {
                    if(a.contains(fileNumber)){
                        fileNumber = a;
                    };
                }
                Log.d("MainActivity", "파일 목록: " + a);
            }
            file_list_tv.setText(list);
        } else {
            file_list_tv.setText("파일 리스트가 없습니다.");
        }

        Log.d("MainActivity", "파일이름: " + fileName);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_save_txt:
                writeLog();
                break;

            case R.id.file_copy:

                try {
                    copy(Path_Full + File.separator, fileName);
                    getFileList(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.content_read_button:
                if(!editText.getText().toString().contains("번")){
                    Toast.makeText(this,"다시 입력해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                fileNumber = editText.getText().toString().substring(0,1);

                getFileList(true);
                readFile();
                break;

            case R.id.file_list_lookup:
                getFileList(false);
                break;

            case R.id.file_delete:
                if(!editText.getText().toString().contains("번")){
                    Toast.makeText(this,"다시 입력해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                fileNumber = editText.getText().toString().substring(0,1);
                getFileList(true);
                file_delete();
                getFileList(false);
                break;


        }
    }
}