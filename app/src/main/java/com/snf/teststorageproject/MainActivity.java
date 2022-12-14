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
     * ?????? ?????? ??????,?????? ?????? ??????
     **/
    public void permission() {

        String temp = ""; //?????? ?????? ?????? ??????
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }
        //?????? ?????? ?????? ??????
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }
        if (!TextUtils.isEmpty(temp)) {
            // ?????? ??????
            ActivityCompat.requestPermissions(this, temp.trim().split(" "), 1000);

        }
    }

    /**
     * ????????? ?????? ????????? ?????????
     **/
    public void writeLog() {

        if (editText.getText().equals("")) {
            Toast.makeText(this, "????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
            return;
        }

        fileName = i + 1 + ".????????? ??????.txt";
        File file = new File(Path_Full + File.separator, fileName);

        //????????? ???????????? ????????? ???????????? ?????? ??????
        if (!file.exists()) {
            //????????? ?????? ??? ??????
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

            Toast.makeText(this, "?????? ????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e("writeLog", e.toString());
        }

    }

    /**
     * ?????????????????? ?????? ???????????? ????????? , ?????? ????????? ?????? ?????? ?????? ??????
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
        Toast.makeText(this, "?????? ????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();

    }

    /**
     * ?????? ??????????????? ?????? ???????????? ?????????
     **/
    private void readFile() {

        StringBuffer buffer = new StringBuffer();
        String data;
        FileInputStream fis;
        try {
            fis = openFileInput(fileNumber);

            if (fis.available() < 0) {
                Toast.makeText(this, "?????? ?????? ?????????????????? " + buffer, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "????????? ?????? ????????????. ??? : " + buffer, Toast.LENGTH_SHORT).show();


    }

    /**
     * ?????? ???????????? ?????????
     **/
    private void file_delete() {

        if (deleteFile(fileNumber)) {
            Toast.makeText(this, "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "?????? ?????? ??????", Toast.LENGTH_SHORT).show();

        }


    }


    /**
     * ?????? ???????????? ?????? ????????? ?????????
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
     * ?????????????????? ?????? ?????? ???????????? ?????????
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
                Log.d("MainActivity", "?????? ??????: " + a);
            }
            file_list_tv.setText(list);
        } else {
            file_list_tv.setText("?????? ???????????? ????????????.");
        }

        Log.d("MainActivity", "????????????: " + fileName);

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
                if(!editText.getText().toString().contains("???")){
                    Toast.makeText(this,"?????? ??????????????????",Toast.LENGTH_SHORT).show();
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
                if(!editText.getText().toString().contains("???")){
                    Toast.makeText(this,"?????? ??????????????????",Toast.LENGTH_SHORT).show();
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