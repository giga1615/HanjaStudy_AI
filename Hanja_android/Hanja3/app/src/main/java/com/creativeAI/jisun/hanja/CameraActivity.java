package com.creativeAI.jisun.hanja;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {
    String mCurrentPhotoPath, imageFileName;
    String email, theme, voca_place;
    String cmd = null;

    Uri imageURI;
    Uri photoURI, albumURI;
    File tempFile;
    Boolean isCamera = false;

    ImageView iv_view;
    Button btn_capture, btn_album;
    ImageView back_Arrow, send;
    TextView menu_title;

    ImageView yes_button, no_button, window_close2;
    TextView explain, theme_voca;
    RelativeLayout no_matching;

    private CustomAnimationDialog customAnimationDialog;

    private final int MY_PERMISSION_CAMERA = 1111;
    private final int PICK_FROM_CAMERA = 2222;
    private final int PICK_FROM_ALBUM = 3333;
    private final int REQUEST_IMAGE_CROP = 4444;

    private static MediaPlayer buttonmusic;
    private SharedPreferences backgroundData;
    boolean _effect_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ((VocabularyActivity) VocabularyActivity.musicContext).next = false;
        // 설정값 불러오기
        backgroundData = getSharedPreferences("backgroundData", MODE_PRIVATE);
        _effect_sound = backgroundData.getBoolean("EFFECT_SOUND", true);

        // 이전 엑티비티에서 클릭한 테마 번호 받아와서 title설정
        Intent intent = getIntent();
        cmd = "detect";
        email = intent.getExtras().getString("email");
        theme = intent.getExtras().getString("theme_num");
        voca_place = intent.getExtras().getString("voca_place");
        explain = (TextView) findViewById(R.id.explain);
        no_matching = (RelativeLayout) findViewById(R.id.no_matching);
        yes_button = (ImageView) findViewById(R.id.yes_button);
        no_button = (ImageView) findViewById(R.id.no_button);
        window_close2 = (ImageView) findViewById(R.id.window_close2);
        theme_voca = (TextView) findViewById(R.id.theme_voca);



        yes_button = (ImageView) findViewById(R.id.yes_button);
        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();


                Intent intent = new Intent(CameraActivity.this, DrawActivity.class);
                intent.putExtra("imageURI", imageURI);
                intent.putExtra("albumURI", albumURI);
                intent.putExtra("email", email);
                intent.putExtra("theme_num", theme);
                intent.putExtra("voca_place", voca_place);
                intent.putExtra("mCurrentPhotoPath", mCurrentPhotoPath);
                intent.putExtra("imageFileName", imageFileName);
                ((VocabularyActivity) VocabularyActivity.musicContext).next = true;
                startActivity(intent);
                //  yes_button.setVisibility(View.INVISIBLE);
                //   no_button.setVisibility(View.INVISIBLE);
            }
        });

        no_button = (ImageView) findViewById(R.id.no_button);
        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
//                buttonmusic.setLooping(false);
//                buttonmusic.start();

                window_close2.performClick();
                yes_button.setVisibility(View.INVISIBLE);
                no_button.setVisibility(View.INVISIBLE);
            }
        });

        window_close2 = (ImageView) findViewById(R.id.window_close2);
        window_close2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                no_matching.setVisibility(View.INVISIBLE);
                yes_button.setVisibility(View.INVISIBLE);
                no_button.setVisibility(View.INVISIBLE);
                explain.setVisibility(View.INVISIBLE);
            }
        });

        switch (theme) {
            case "t1":
                switch (voca_place) {
                    case "v1":
                        theme_voca.setText("귀");
                        break;
                    case "v2":
                        theme_voca.setText("손");
                        break;
                    case "v3":
                        theme_voca.setText("눈");
                        break;
                    case "v4":
                        theme_voca.setText("눈썹");
                        break;
                    case "v5":
                        theme_voca.setText("발");
                        break;
                    case "v6":
                        theme_voca.setText("머리");
                        break;
                    case "v7":
                        theme_voca.setText("코");
                        break;
                    case "v8":
                        theme_voca.setText("손톱");
                        break;
                    case "v9":
                        theme_voca.setText("입");
                        break;
                    case "v10":
                        theme_voca.setText("혀");
                        break;
                }
                break;

            case "t2":
                switch (voca_place) {
                    case "v1":
                        theme_voca.setText("말");
                        break;
                    case "v2":
                        theme_voca.setText("양");
                        break;
                    case "v3":
                        theme_voca.setText("고양이");
                        break;
                    case "v4":
                        theme_voca.setText("닭");
                        break;
                    case "v5":
                        theme_voca.setText("물고기");
                        break;
                    case "v6":
                        theme_voca.setText("새");
                        break;
                    case "v7":
                        theme_voca.setText("돼지");
                        break;
                    case "v8":
                        theme_voca.setText("개");
                        break;
                    case "v9":
                        theme_voca.setText("소");
                        break;
                    case "v10":
                        theme_voca.setText("조개");
                        break;
                }
                break;

            case "t3":
                switch (voca_place) {
                    case "v1":
                        theme_voca.setText("돌");
                        break;
                    case "v2":
                        theme_voca.setText("풀");
                        break;
                    case "v3":
                        theme_voca.setText("구름");
                        break;
                    case "v4":
                        theme_voca.setText("하늘");
                        break;
                    case "v5":
                        theme_voca.setText("산");
                        break;
                    case "v6":
                        theme_voca.setText("꽃");
                        break;
                    case "v7":
                        theme_voca.setText("눈");
                        break;
                    case "v8":
                        theme_voca.setText("물");
                        break;
                    case "v9":
                        theme_voca.setText("나무");
                        break;
                    case "v10":
                        theme_voca.setText("바다");
                        break;
                }
                break;
        }

        send = (ImageView) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                if (mCurrentPhotoPath != null) {
                    int idx = mCurrentPhotoPath.indexOf("/JPEG");
                    String Pathcut = mCurrentPhotoPath.substring(0, idx);

                    String img_path = Pathcut;
                    String img_name = imageFileName;
                    NetworkTask networkTask = new NetworkTask(cmd, email, theme, voca_place, img_path, img_name);
                    networkTask.execute();
                    initView();
                } else
                    Toast.makeText(getApplicationContext(), "사진이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
            }
        });
        back_Arrow = (ImageView) findViewById(R.id.backArrow);
        back_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

//                Intent intent = new Intent(CameraActivity.this, VocabularyActivity.class);
//                startActivity(intent);
//                finish();
                ((VocabularyActivity) VocabularyActivity.musicContext).next = false;
                onBackPressed();
            }
        });

        menu_title = (TextView) findViewById(R.id.profileName);
        menu_title.setText("사진찍기");

        btn_capture = (Button) findViewById(R.id.btn_capture);
        btn_album = (Button) findViewById(R.id.btn_album);
        iv_view = (ImageView) findViewById(R.id.iv_view);

        // 이전 엑티비티에서 클릭한 단어장 번호 받아오기 ----------------> 나중에 사진 서버에서 받아온다음에 이 테마 번호에 사진띄우기..
//        Intent intent = getIntent();
//        voca_place = intent.getStringExtra("voca_place");  //*String형(https://coding-factory.tistory.com/203)
//        Toast.makeText(getApplicationContext(), voca_place, Toast.LENGTH_SHORT).show();  // 잘 받아오는지 보려고 임시로 넣음

        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                isCamera = true;
                takePhoto();
                send.setVisibility(View.VISIBLE);
            }
        });

        btn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonmusic = MediaPlayer.create(getApplicationContext(), R.raw.buttonmusic);
                buttonmusic.setLooping(false);
                if (_effect_sound)
                    buttonmusic.start();

                isCamera = false;
                goToAlbum();
            }
        });

        checkPermission();

    }

    private void initView(){
                customAnimationDialog = new CustomAnimationDialog(CameraActivity.this);
                customAnimationDialog.show();
    }

    //파일의 실제 경로를 가져오기 위한 메소드
    public static String getPath(Context cxt, Uri uri) {
        Cursor cursor = cxt.getContentResolver().query(uri, null, null, null, null );
        cursor.moveToNext();
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
        cursor.close();

        return path;
    }

    private void takePhoto() {
        String state = Environment.getExternalStorageState();
        //외장 메모리 검사
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    tempFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("captureCamera Error", ex.toString());
                }
                if (tempFile != null) {
                    Uri providerURL = FileProvider.getUriForFile(this, getPackageName(), tempFile);
                    imageURI = providerURL;

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURL);
                    startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
                }
            }
        } else {
            Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "hanja");

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void goToAlbum() {
        Log.i("getAlbum", "Call");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        ((VocabularyActivity) VocabularyActivity.musicContext).next = false;
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private void galleryAddPic() {
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
        send.setVisibility(View.VISIBLE);
    }

    // 카메라 전용 crop
    public void cropImage() {
        Log.i("cropImage", "Call");
        Log.i("cropImage", "photoURI:" + photoURI + "/albumURI:" + albumURI);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");

        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumURI);
        ((VocabularyActivity) VocabularyActivity.musicContext).next = false;
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Log.i("REQUEST_TAKE_PHOTO", "OK");

                        setImage();
                        galleryAddPic();
                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                    send.setVisibility(View.INVISIBLE);
                }

                break;

            case PICK_FROM_ALBUM:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getData() != null) {
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            cropImage();
                        } catch (Exception e) {
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;

            case REQUEST_IMAGE_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    galleryAddPic();
                    iv_view.setImageURI(albumURI);
                }
                break;
        }
    }


    private void setImage() {

        ImageView imageView = findViewById(R.id.iv_view);

        ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, isCamera);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
       // Log.d(TAG, "setImage : " + tempFile.getAbsolutePath());

        imageView.setImageBitmap(originalBm);

    }



    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                ((VocabularyActivity) VocabularyActivity.musicContext).next = false;
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSION_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //동의 했을 경우



                } else {
                    //거부했을 경우
                    Toast toast = Toast.makeText(this,
                            "기능 사용을 위한 권한 동의가 필요합니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }

                break;
        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String cmd;
        private String user;
        private String theme;
        private String word;
        private String img_path;
        private String img_name;

        public NetworkTask(String cmd, String user, String theme, String word, String img_path, String img_name) {

            this.cmd = cmd;
            this.user = user;
            this.theme = theme;
            this.word = word;
            this.img_path = img_path;
            this.img_name = img_name;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnectionImg requestHttpURLConnection = new RequestHttpURLConnectionImg();
            result = requestHttpURLConnection.request(cmd, user, theme, word, img_path, img_name);

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s) {
                case "matching":
                 //   Toast.makeText(getApplication(), "사물인식 성공", Toast.LENGTH_LONG).show();

                    customAnimationDialog.dismiss();
                    Intent intent = new Intent(CameraActivity.this, DrawActivity.class);
                    intent.putExtra("imageURI", imageURI);
                    intent.putExtra("albumURI", albumURI);
                    intent.putExtra("email", email);
                    intent.putExtra("theme_num", theme);
                    intent.putExtra("voca_place", voca_place);
                    intent.putExtra("mCurrentPhotoPath", mCurrentPhotoPath);
                    intent.putExtra("imageFileName", imageFileName);
                    ((VocabularyActivity) VocabularyActivity.musicContext).next = true;
                    startActivity(intent);
                    break;
                case "no-matching":
                 //   Toast.makeText(getApplication(), "사물인식 결과없음", Toast.LENGTH_LONG).show();
                    explain.setText("'" + theme_voca.getText().toString() + "' 사진이 맞습니까?");
                    explain.setVisibility(View.VISIBLE);
                    no_matching.setVisibility(View.VISIBLE);
                    yes_button.setVisibility(View.VISIBLE);
                    no_button.setVisibility(View.VISIBLE);
                    customAnimationDialog.dismiss();
                    break;
            }
          //  Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        send.setVisibility(View.INVISIBLE);
        ((VocabularyActivity) VocabularyActivity.musicContext).next = false;
        //back_Arrow.performClick();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //KeyguardManager islock = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        if (islock.inKeyguardRestrictedInputMode() && !pm.isInteractive())
        if (((VocabularyActivity) VocabularyActivity.musicContext).next == false)
            ((VocabularyActivity) VocabularyActivity.musicContext).stopMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (((VocabularyActivity) VocabularyActivity.musicContext).next == false)
            ((VocabularyActivity) VocabularyActivity.musicContext).stopMusic();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (((VocabularyActivity) VocabularyActivity.musicContext).next == false && ((MainActivity) MainActivity.musicContext)._background_sound) {
            ((VocabularyActivity) VocabularyActivity.musicContext).stopMusic();
            ((VocabularyActivity) VocabularyActivity.musicContext).startMusic();
        }
    }
}