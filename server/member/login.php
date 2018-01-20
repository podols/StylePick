<?php
include '../connect/mysqlConnect.php';
$phpSeq = $_REQUEST['phpSeq'];
switch ($phpSeq) {
  case 1: login(); break;
}

function login(){
  global $mysqli;
  $id = $_POST['loginId'];
  $pw = $_POST['loginPw'];
  $sql = "SELECT id, password
          FROM member
          WHERE id='$id' AND password='$pw'";
  $result = $mysqli->query($sql);
  $row = $result->num_rows;
  if($row >= 1) echo "success";
  else echo "fail";
  mysqli_close($mysqli);
}

?>
