<?php
include '../connect/mysqlConnect.php';
$phpSeq = $_REQUEST['phpSeq'];
switch ($phpSeq) {
  case 1: signup(); break;
  case 2: upload_profil_photo(); break;
}

function upload_profil_photo(){
  // 설정
  $uploads_dir = './profileImg';
  $allowed_ext = array('jpg','jpeg','png','gif');

  // 변수 정리
  $error = $_FILES['profilePhoto']['error'];
  $name = $_FILES['profilePhoto']['name'];
  $ext = array_pop(explode('.', $name));        // 확장자

  // 오류 확인
  if( $error != UPLOAD_ERR_OK ) {
  	switch( $error ) {
  		case UPLOAD_ERR_INI_SIZE:
  		case UPLOAD_ERR_FORM_SIZE:
  			echo "파일이 너무 큽니다. ($error)";
  			break;
  		case UPLOAD_ERR_NO_FILE:
  			echo "파일이 첨부되지 않았습니다. ($error)";
  			break;
  		default:
  			echo "파일이 제대로 업로드되지 않았습니다. ($error)";
  	}
  	exit;
  }

  // 확장자 확인
  if(!in_array($ext, $allowed_ext) ) {
  	echo "허용되지 않는 확장자입니다.";
  	exit;
  }

  // 파일 이동
  move_uploaded_file($_FILES['profilePhoto']['tmp_name'], "$uploads_dir/$name");
  echo "파일명:".$name.", 업로드 완료";
}

// 아이디 중복확인
function checkId($id){
  global $mysqli;
  $sql = "SELECT id FROM member WHERE id = '$id'";
  $result = $mysqli->query($sql);
  $row = $result->num_rows;

  if($row >= 1) {
    $data = array("flag"=>0, "result"=>"이미 가입되어 있는 아이디입니다."); // 첫글자는 영문자로 시작
    echo json_encode($data);
    mysqli_close($mysqli);
    exit;
  }
}

// 회원가입
function signup(){
  global $mysqli;
  $id = $_POST['id'];
  $pw1 = $_POST['pw1'];
  $pw2 = $_POST['pw2'];
  $email = $_POST['email'];
  $nickName = $_POST['nickName'];
  $profileImgName = $_POST['profileImgName'];

  checkId($id); // 아이디 중복확인

  // 예외처리 패턴
  $patternId = "/^[a-zA-Z]\w{4,11}$/u"; // 영문/숫자만 허용하며, 첫글자는 영문자로 시작하여야 하며, 5자리에서 12자리 이내로 입력받음
  $patternPw = "/^(?=.*[a-zA-Z])(?=.*[0-9]).{6,16}$/";  //6~16자 영문,숫자 조합
  $patternNickName = "/^[0-9a-zA-Z가-힣]{4,16}$/"; //한글2~8자, 영문4~16자, 한글,영문,숫자 사용가능

  // 예외처리
  if(!preg_match($patternId, $id)){
    $data = array("flag"=>1, "result"=>"5~12자 영문 또는 숫자만 입력하세요."); // 첫글자는 영문자로 시작
    echo json_encode($data);
  }
  else if(!preg_match($patternPw, $pw1)){
    $data = array("flag"=>2, "result"=>"6~16자 영문과 숫자를 조합해서 입력하세요.");
    echo json_encode($data);
  }
  else if(!($pw1 == $pw2)){
    $data = array("flag"=>2, "result"=>"비밀번호를 확인하세요.");
    echo json_encode($data);
  }
  else if(!filter_var($email, FILTER_VALIDATE_EMAIL)){
    $data = array("flag"=>3, "result"=>"이메일 형식에 맞게 입력하세요."); // 길이 상관없이 형식만 a@b.c 가능
    echo json_encode($data);
  }
  else if(!preg_match($patternNickName, $nickName)){
    $data = array("flag"=>4, "result"=>"한글 2~8자, 영문 4~16자로 입력하세요. (숫자가능)");
    echo json_encode($data);
  }
  // 예외처리 후 문제 없으면 회원가입 진행
  else{
    $sql = "INSERT INTO member(id, password, email, nickName, profileImgName, joinDate)
            VALUES('$id', '$pw1', '$email', '$nickName', '$profileImgName', now())";

    if($mysqli->query($sql)){
      // echo "회원가입을 완료하였습니다.";
      $data = array("flag"=>5, "result"=>"회원가입을 완료하였습니다.");
      echo json_encode($data);
    }
    else{
      $data = array("flag"=>6, "result"=>"가입 실패");
      echo json_encode($data);
    }
    mysqli_close($mysqli);
  }
}
?>
