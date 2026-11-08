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

$event_id = isset($_POST['event_id']) ? (int)$_POST['event_id'] : 1;
$event_title = isset($_POST['event_title']) ? $_POST['event_title'] : '';
$student_name = isset($_POST['student_name']) ? $_POST['student_name'] : '';
$nrp = isset($_POST['nrp']) ? $_POST['nrp'] : '';
$faculty = isset($_POST['faculty']) ? $_POST['faculty'] : '';
$attendance_mode = isset($_POST['attendance_mode']) ? $_POST['attendance_mode'] : '';
$extras = isset($_POST['extras']) ? $_POST['extras'] : '';
$preferred_date = isset($_POST['preferred_date']) ? $_POST['preferred_date'] : date('Y-m-d');
$notes = isset($_POST['notes']) ? $_POST['notes'] : '';

$sql = "INSERT INTO registrations (event_id, event_title, student_name, nrp, faculty, attendance_mode, extras, preferred_date, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
$stmt = $c->prepare($sql);
$stmt->bind_param("issssssss", $event_id, $event_title, $student_name, $nrp, $faculty, $attendance_mode, $extras, $preferred_date, $notes);

if ($stmt->execute()) {
    echo json_encode(array('result' => 'SUCCESS', 'message' => 'Registration saved successfully'));
} else {
    echo json_encode(array('result' => 'ERROR', 'message' => 'Failed to insert data'));
}
$c->close();
?>
