<?php
error_reporting(E_ERROR | E_PARSE);
$c = new mysqli("localhost", "root", "", "ubaya_events");
$c->set_charset("UTF8");

if ($c->connect_errno) {
    echo json_encode(array(
        'result' => 'ERROR',
        'message' => 'Failed to connect DB: ' . $c->connect_error
    ));
    die();
}

$sql = "SELECT * FROM registrations ORDER BY id DESC";
$stmt = $c->prepare($sql);
$stmt->execute();
$result = $stmt->get_result();
$array = array();

if ($result->num_rows > 0) {
    while ($obj = $result->fetch_assoc()) {
        $array[] = $obj;
    }
    echo json_encode(array('result' => 'SUCCESS', 'data' => $array));
} else {
    echo json_encode(array('result' => 'ERROR', 'message' => 'No registrations found'));
    die();
}
$c->close();
?>
