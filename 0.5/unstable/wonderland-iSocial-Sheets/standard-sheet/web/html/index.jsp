<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<title>My Recorder</title>
<script src='http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.js'></script>
<script type="text/javascript" src="<% application.getContextPath(); %>html/js/swfobject.js"></script>
<script type="text/javascript" src="<% application.getContextPath(); %>html/js/recorder.js"></script>

<script type="text/javascript">
$(function() {
  var appWidth = 24;
  var appHeight = 24;
  var flashvars = {'event_handler': 'microphone_recorder_events', 'upload_image': 'html/images/upload.png'};
  var params = {};
  var attributes = {'id': "recorderApp", 'name':  "recorderApp"};
  swfobject.embedSWF("recorder.swf", "flashcontent", appWidth, appHeight, "10.1.0", "", flashvars, params, attributes);
});
</script>

<style>
#control_panel { white-space: nowrap; }
#control_panel a { outline: none; display: inline-block; width: 24px; height: 24px; }
#control_panel a img { border: 0; }
#save_button { position: absolute; padding: 0; margin: 0; }
#play_button { display: inline-block; }
</style>
</head>

<body>
  

  <div id="status">
   Recorder Status...
  </div>

  <div id="control_panel">
  <a id="record_button" onclick="Recorder.record('audio', 'audio.wav');" href="javascript:void(0);" title="Record"><img src="html/images/record.png" width="24" height="24" alt="Record"/></a>
  <span id="save_button">
    <span id="flashcontent">
      <p>Your browser must have JavaScript enabled and the Adobe Flash Player installed.</p>
    </span>
  </span>
  <a id="play_button" style="display:none;" onclick="Recorder.playBack('audio');" href="javascript:void(0);" title="Play"><img src="html/images/play.png" width="24" height="24" alt="Play"/></a>
  </div>

  <div id="upload_status">
  </div>

  <div>Activity Level: <span id="activity_level"></span></div>

  <form id="uploadForm" name="uploadForm" action="saveaudiofile">
    <input id="authenticity_token" name="authenticity_token" value="xxxxx" type="hidden">
    <input id="upload_file" name="upload_file[parent_id]" value="1" type="hidden">
    <input id="format" name="format" value="json" type="hidden">
        
  </form>

</body>
</html>


