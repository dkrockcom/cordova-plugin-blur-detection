###############################
# cordova-plugin-blur-detection
###############################

Plugin for the identify blurred image.

****************************
Temporary Installation Steps
****************************

1. Download Unzip plugin
2. Go to  Cordova project directory.
3. Open cmd from current cordova project directory.
4. Run a command on CMD as `cordova plugin add path-for-cordova-plugin-blur-detection directory`

Example: cordova plugin add d:/cordova-plugin-blur-detection


**********************
Sample Implementation.
**********************

```
window.BlurDetect.chcekImage(function(evt){
	console.log(evt);
}, function(err){
	console.log(err);
}, "{String}"); // - storage/emulated/0/Demo.jpg
```

Report security issues to our `mail <mailto:admin@dkrock.com>`_
