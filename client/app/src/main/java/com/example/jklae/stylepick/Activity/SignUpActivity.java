/*
* @file SignUpActivity.java
* @auther 김대현
* @date 2017-12-22
* @update
* @comment
* - 회원가입을 하는 액티비티
* - 프로필 사진을 선택하고, 회원정보를 입력하여 회원가입을 할 수 있다.
 */

package com.example.jklae.stylepick.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.jklae.stylepick.R;
import com.example.jklae.stylepick.Util.ImgProcessing;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.overrideOf;

public class SignUpActivity extends AppCompatActivity {

    private final String TAG = "SignUpActivity";

    // 프로필 이미지 관련 변수
    private ImageView profileImgView;
    private ImageView editProfileImgView;
    private boolean editProfile = false;
    private Uri profileUri;

    // 회원가입용 객체
    private EditText etxtId;
    private EditText etxtPw1;
    private EditText etxtPw2;
    private EditText etxtEmail;
    private EditText etxtNickName;
    private TextView exceptionId;
    private TextView exceptionPw;
    private TextView exceptionEmail;
    private TextView exceptionNickName;


//    설정
    private void setup(){
        // 툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);   // 액션바가 없으니 툴바로 대체하겠다는 뜻
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // 뒤로가기 버튼 생성
        getSupportActionBar().setDisplayShowTitleEnabled(false);    // 제목 제거

    // 객체 생성
        // 프로필 이미지 뷰
        profileImgView = (ImageView) findViewById(R.id.profile_img);
        editProfileImgView = (ImageView) findViewById(R.id.edit_profile_img);
        // 회원정보 EditTxt
        etxtId = (EditText) findViewById(R.id.id2);
        etxtPw1 = (EditText) findViewById(R.id.pw2);
        etxtPw2 = (EditText) findViewById(R.id.check_pw2);
        etxtEmail = (EditText) findViewById(R.id.email2);
        etxtNickName = (EditText) findViewById(R.id.nick_name2);
        // 회원정보 예외처리 결과
        exceptionId = (TextView) findViewById(R.id.exception_id);
        exceptionPw = (TextView) findViewById(R.id.exception_pw);
        exceptionEmail = (TextView) findViewById(R.id.exception_email);
        exceptionNickName = (TextView) findViewById(R.id.exception_nickName);
    }

//    onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Log.i(TAG, "onCreate()");
        setup();
    }

    /*
    * 가입완료 온클릭
    * 이미지 리사이징, 서버로 이미지 전송
     */
    public void completeSignUp(View v){
        String id = etxtId.getText().toString();
        String pw1 = etxtPw1.getText().toString();
        String pw2 = etxtPw2.getText().toString();
        String email = etxtEmail.getText().toString();
        String nickName = etxtNickName.getText().toString();
        File profileImgFile = null;
        String profileImgName = "";
        HashMap map;
        // 프로필 사진 리사이징 후 임시파일로 저장한 파일객체와 파일명을 해시맵으로 리턴받는다.
        if(profileUri != null) {    // 프로필 사진을 선택했을 때 조건
            map = ImgProcessing.bitmapResizing(this, profileUri, id);
            profileImgFile = (File) map.get("fileObj");
            profileImgName = (String) map.get("fileName");
        }

        // 회원정보를 서버로 전송하여 예외처리 후 회원가입을 하고,
        // 프로필 사진이 있다면 서버에 전송하여 업로드를 한다.
        sendToServer(id,pw1,pw2,email,nickName,profileImgName,profileImgFile);

    }

    /*
    * 회원정보를 서버로 전송하는 메소드
     */
    public void sendToServer(String id, String pw1, String pw2, String email, String nickName, final String profileImgName, final File profileImgFile){
        class SendTask extends AsyncTask<String, Void, String>{ // prams, progress, result
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SignUpActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String res) {
                super.onPostExecute(res);
                loading.dismiss();

                int flag = 0;
                String result = null;
                try {
                    JSONObject jsonObj = new JSONObject(res);
                    flag = jsonObj.getInt("flag");
                    result = jsonObj.getString("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 이전에 예외처리한 결과의 UI를 초기화한다.
                exceptionId.setVisibility(exceptionId.GONE);
                exceptionPw.setVisibility(exceptionPw.GONE);
                exceptionEmail.setVisibility(exceptionEmail.GONE);
                exceptionNickName.setVisibility(exceptionNickName.GONE);

                // 입력한 회원정보에 예외처리한 결과를 UI에서 보여준다.
                // 예외처리에 모두 통과하였다면, 프로필 사진을 리사이징하고, 서버에 업로드 한다. (case:5)
                switch (flag){
                    case 0: exceptionId.setText(result);    // 아이디 중복확인 결과
                            exceptionId.setVisibility(exceptionId.VISIBLE); break;
                    case 1: exceptionId.setText(result);
                            exceptionId.setVisibility(exceptionId.VISIBLE); break;
                    case 2: exceptionPw.setText(result);
                            exceptionPw.setVisibility(exceptionPw.VISIBLE); break;
                    case 3: exceptionEmail.setText(result);
                            exceptionEmail.setVisibility(exceptionEmail.VISIBLE); break;
                    case 4: exceptionNickName.setText(result);
                            exceptionNickName.setVisibility(exceptionNickName.VISIBLE); break;
                    case 5:
                        if(profileImgFile != null && !(profileImgName.equals(""))){
                            sendFile(profileImgFile, profileImgName);         // 서버로 프로필 사진 전송
                        }
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);  break;
                    case 6: Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show(); break;
                }
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    String id = params[0];
                    String pw1 = params[1];
                    String pw2 = params[2];
                    String email = params[3];
                    String nickName = params[4];
                    String profileImgName = params[5];
                    String phpSeq = "1";

                    String link = "http://52.79.196.36/workspace/StylePick/member/signup.php";
                    String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                    data += "&" + URLEncoder.encode("pw1", "UTF-8") + "=" + URLEncoder.encode(pw1, "UTF-8");
                    data += "&" + URLEncoder.encode("pw2", "UTF-8") + "=" + URLEncoder.encode(pw2, "UTF-8");
                    data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    data += "&" + URLEncoder.encode("nickName", "UTF-8") + "=" + URLEncoder.encode(nickName, "UTF-8");
                    data += "&" + URLEncoder.encode("profileImgName", "UTF-8") + "=" + URLEncoder.encode(profileImgName, "UTF-8");
                    data += "&" + URLEncoder.encode("phpSeq", "UTF-8") + "=" + URLEncoder.encode(phpSeq, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();     // onPostExecute로 리턴할 값
                    String json;

                    // Read Server Response
                    while ((json = reader.readLine()) != null) {
                        sb.append(json+"\n");
                        break;
                    }
                    return sb.toString().trim();   // onPostExecute의 파라미터로 리턴하는 거 같음 (trim()추가)
                }
                catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        SendTask task = new SendTask();
        task.execute(id, pw1, pw2, email, nickName, profileImgName);
    }

    /*
    * 프로필 사진 서버에 전송
    * okhttp3를 이용하여 서버에 전송하는 메소드이다.
     */
    public void sendFile(File profileImgFile, String profileImgName){
        // Request Body 생성
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("profilePhoto", profileImgName,  // 서버에서 받을 변수명, 서버에 저장할 파일명
                        RequestBody.create(MultipartBody.FORM, profileImgFile))  // 실제 파일을 객체로 만들어서 추가
                .build();

        // Request 생성
        Request request = new Request.Builder()
                .url("http://52.79.196.36/workspace/StylePick/member/signup.php?phpSeq=2")
                .post(requestBody)
                .build();

        // 서버로 부터 결과를 받는다.
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: 응답 실패,"+call);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    /*
    * 프로필 사진 온클릭
    * 프로필 사진이 설정 되어 있으면 메뉴창에서 새 프로필사진을 추가할건지 기본 이미지로 변경할건지 선택한다.
    * 프로필 사진이 설정이 되어 있지 않으면 메뉴창 없이 바로 갤러리앱으로 이동한다.
     */
    public void addingProfile(View v){
        if(editProfile){
            final String[] items = {"새 프로필 사진", "기본 이미지로 변경"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("프로필 사진 설정").setItems(items, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == 0){         // 새 프로필 사진
                        newProfileImg();
                    }
                    else if(which == 1){    // 기본 이미지로 변경
                        profileImgView.setImageResource(R.drawable.img_profile_addition);
                        editProfileImgView.setVisibility(editProfileImgView.GONE);
                        editProfile = false;
                    }
                }
            });
            AlertDialog dialog = builder.create();      // 알림창 객체 생성
            dialog.show();      // 알림창 띄우기
        }
        else{
            newProfileImg();
        }
    }

    /*
    * 갤러리앱 실행
    * 새 프로필 사진을 선택할 수 있다.
     */
    private void newProfileImg(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, 1);
    }

    /*
    * 갤러리앱에서 이미지 선택 결과를 프로필 사진으로 가져온다.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        profileUri = data.getData();

        // Glide Transformations CircleCrop, Uri로 이미지를 원형으로 수정
        Glide.with(SignUpActivity.this)
                .load(profileUri)
                .apply(bitmapTransform(new CircleCrop()))
                .apply(overrideOf(500,500))
                .into(profileImgView);
        // 프로필 이미지를 수정할 수 있는 버튼 보여주기
        editProfileImgView.setVisibility(editProfileImgView.VISIBLE);
        editProfile = true;

    }

    /*
    *뒤로가기 버튼
    *툴바에 있는 뒤로가기 버튼을 누르면 이전화면으로 돌아간다.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //    생명주기
    @Override
    protected void onRestart(){
        super.onRestart();
        Log.i(TAG, "onRestart() called");
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.i(TAG, "onStart() called");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG, "onResume() called");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG, "onPause() called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "onDestroy() called");
    }
}
