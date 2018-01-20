/*
* @file LoginActivity.java
* @auther 김대현
* @date 2017-12-21
* @update
* @comment
* - 프로그램 실행 시 가장 먼저 보여지는 액티비티
* - 로그인, 회원가입 기능이 있다.
 */

package com.example.jklae.stylepick.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.jklae.stylepick.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";

    // 객체 선언
    EditText etxtId;
    EditText etxtPw;

    // 설정
    private void setup(){
        // 객체 생성
        etxtId = (EditText) findViewById(R.id.loginId);
        etxtPw = (EditText) findViewById(R.id.loginPw);
    }

    // onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i(TAG, "onCreate()");
        setup();

    }

    /*
    * 로그인 시 서버와 통신하는 메소드
     */
    public void requestPost(String url, String id, String pw){
        //Request Body에 서버에 보낼 데이터 작성
        RequestBody requestBody = new FormBody.Builder()
                .add("phpSeq", "1")
                .add("loginId", id)
                .add("loginPw", pw).build();

        //작성한 Request Body와 데이터를 보낼 url을 Request에 붙임
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody).build();

        //request를 Client에 세팅하고 Server로 부터 온 Response를 처리할 Callback 작성
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("error", "Connect Server Error is " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("onResponse: ", "Response Body is " + response.body().string());
            }
        });
    }

    /*
    * 로그인 버튼 온클릭
     */
    public void login(View v){
        String loginId = etxtId.getText().toString();
        String loginPw = etxtPw.getText().toString();
        String url = "http://52.79.196.36/workspace/StylePick/member/login.php";
        requestPost(url, loginId, loginPw);
    }

    /*
    * 회원가입 버튼 온클릭
     */
    public void moveSignUp(View v){
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
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
