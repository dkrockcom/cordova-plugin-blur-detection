# cordova-plugin-blur-detection


Plugin for the identify blurred image.

****************************
Installation
****************************

```
cordova plugin add cordova-plugin-blur-detection
```

**********************
Sample Implementation.
**********************

```
window.BlurDetect.checkImage(function(evt){
	console.log(evt);
}, function(err){
	console.log(err);
}, "{String}"); // - storage/emulated/0/Demo.jpg
```

**********************
Output
**********************

```
BLUR/NOT BLUR
```

Report security issues to our `mail <mailto:admin@dkrock.com>`_
