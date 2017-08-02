package shike.mytakephtotodemo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private int PHOTO_REQUEST_GALLERY = 1;
    private int PHOTO_REQUEST_CAREMA = 2;
    private int PHOTO_PERMISSION = 21;


    private File tempFile;
    private Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopWindowOfUserImage();
            }
        });
    }

    //拍照
    private void initPopWindowOfUserImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            final String permission = Manifest.permission.CAMERA;  //相机权限
            final String permission1 = Manifest.permission.READ_EXTERNAL_STORAGE; //写入数据权限
            final String permission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE; //写入数据权限
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mContext, permission1) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mContext, permission2) != PackageManager.PERMISSION_GRANTED) {  //先判断是否被赋予权限，没有则申请权限
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PHOTO_PERMISSION);
            } else {  //赋予过权限，则直接调用相机拍照
                changeHeadIcon();
            }
        } else {
            changeHeadIcon();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PHOTO_PERMISSION) {
            if (grantResults.length > 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
                changeHeadIcon();
            }
        }
    }


    private void changeHeadIcon() {
        final CharSequence[] items = {"相册", "拍照"};
        AlertDialog dlg = new AlertDialog.Builder(mContext)
                .setTitle("选择图片")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // 这里item是根据选择的方式，
                        if (item == 0) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                        } else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                tempFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + "");

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    //如果是7.0及以上的系统使用FileProvider的方式创建一个Uri
                                            Log.e("77777777777777","");
                                    photoURI = FileProvider.getUriForFile(mContext, "cn.lovexiaoai.myapp.fileprovider", tempFile);
                                } else {
                                    Log.e("6666666666666666666","");
                                    //7.0以下使用这种方式创建一个Uri
                                    photoURI = Uri.fromFile(tempFile);
                                }
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(intent, PHOTO_REQUEST_CAREMA);

                            } else {
                                Toast.makeText(mContext, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).create();
        dlg.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                Uri uri = data.getData();
                Log.e("图片路径？？", uri.toString() + "");
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Uri uri = Uri.fromFile(tempFile);
                Log.e("图片路径？？", uri.toString() + "  ");
            } else {
                Toast.makeText(MainActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
