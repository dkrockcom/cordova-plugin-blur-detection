/* global cordova:false */
/* globals window */

var exec = cordova.require('cordova/exec'),
    utils = cordova.require('cordova/utils');

var template = {
    chcekImage: function(successCallback, errorCallback, message, forceAsync) {
        var action = 'chcekImage';

        if (forceAsync) {
            action += 'Async';
        }

        exec(successCallback, errorCallback, 'BlurDetectPlugin', action, [message]);
    }
};

module.exports = template;
