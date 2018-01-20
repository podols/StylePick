package com.example.jklae.stylepick.Util;

/*
* @file LoginActivity.java
* @auther 김대현
* @date 2017-12-28
* @update
* @comment
* 이미지 처리를 모아둔 유틸성 클래스
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ImgProcessing {
   

    /*
    * 이미지를 임시파일로 저장하는 메소드
     */
    public static HashMap saveBitmapToJpeg(Context context, Bitmap bitmap, String id){
        Date date = new Date();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyMMddHHmmss");
        String curTime = dayTime.format(date);

        File storage = context.getCacheDir(); // 이 부분이 임시파일 저장 경로
        String fileName = id + curTime + ".jpg";  // 파일이름은 마음대로!
        File tempFile = new File(storage,fileName);

        try{
            tempFile.createNewFile();  // 파일을 생성해주고
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , out);  // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌
            out.close(); // 마무리로 닫아줍니다.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 해시맵으로 파일객체와 파일명을 리턴해준다.
        HashMap map = new HashMap();
        map.put("fileObj", tempFile);
        map.put("fileName", fileName);

        return map;
    }


    /*
    * 이미지 리사이징을 하고, 리사이징한 bitmap을 임시파일(jpg)로 저장
    * 서버에 전송하기 전에 리사이징(bitmap)해서 임시파일(jpg)로 저장한다.
     */
    public static HashMap bitmapResizing(Context con, Uri uri, String id){
        if(uri == null) return null;  // 프로필 사진을 선택하지 않았으면 메소드를 종료시킨다.
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(
                    con.getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 리사이징한 프로필 사진을 임시파일로 생성하여('ID+현재시간.jpg' 로 임시파일 생성)
        // 파일객체와 파일명이 담긴 맵을 만들어 리턴받는다.
        HashMap map = saveBitmapToJpeg(con, bitmap, id);

        return map;
    }


}
