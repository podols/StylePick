<?php
  $hostname = "127.0.0.1";  // 내 pc?, aws 서버?
  $username= "root";  // aws: ubuntu, DB 사용자명?
  $password = "패스워드"; // db 비번
  $dbname = "style_pick";  
  $mysqli = new mysqli($hostname, $username, $password, $dbname);
  // mysqli_set_charset($conn,"utf8");   // 새로 추가
  if(mysqli_connect_errno()){
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
      exit();
  }else{
      // printf("DB Connect 성공!!");
  }
  // mysqli_close($mysqli);
?>
