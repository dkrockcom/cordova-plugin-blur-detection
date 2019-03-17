/* global cordova:false */
/* globals window */

var exec = cordova.require('cordova/exec'),
    utils = cordova.require('cordova/utils');

var template = {
    checkImage: function(successCallback, errorCallback, message, forceAsync) {
        var action = 'checkImage';

        if (forceAsync) {
            action += 'Async';
        }

        exec(successCallback, errorCallback, 'BlurDetectPlugin', action, [message]);
    }
};

module.exports = template;
