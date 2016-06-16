(function(angular) {

  'use strict';

  angular.module('modulePictureboxComponents').controller('imageViewController', Controller);

  function Controller($scope) {
    var self = this;
    var containerSize;
    var $image;
    var initalImageSize;
    var rotation;
    var zoom;

    (function _init() {
      console.log('imageViewController init', $scope.rotation);
    })();

    self.setImage = function($img) {
      $image = $img;
    };
    self.setInitialSize = function(w, h) {
      initalImageSize = {
      width : w,
      height : h
      };
    };
    self.setContainerSize = function(w, h) {
      containerSize = {
      width : w,
      height : h
      };
    };

    self.setRotation = function(rot) {
      rotation = rot;
    };

    self.layout = function() {
      if (!initalImageSize || !$image) {
        return;
      }
      console.log('[' + $scope.imageId + ']: DO layout');

      var imgWidth, imgHeight;

      if ((rotation / 90) % 2 == 0) {
        imgWidth = initalImageSize.width;
        imgHeight = initalImageSize.height;
      } else {
        imgWidth = initalImageSize.height;
        imgHeight = initalImageSize.width;
      }

      var scaleFactor = containerSize.width / imgWidth;
      scaleFactor = Math.min(scaleFactor, containerSize.height / imgHeight);
      if (zoom) {
        scaleFactor = scaleFactor * zoom.factor;
      }
      var width = imgWidth * scaleFactor;
      var height = imgHeight * scaleFactor;
      var top = Math.floor((containerSize.height - height) / 2);
      var left = Math.floor((containerSize.width - width) / 2);
      var css;
      if ((rotation / 90) % 2 == 0) {
        css = {
        top : top + 'px',
        left : left + 'px',
        width : width + 'px',
        height : height + 'px',
        transform : 'rotate(' + $scope.rotation + 'deg)'
        };
      } else {
        css = {
        top : Math.floor((containerSize.height - width) / 2) + 'px',
        left : Math.floor((containerSize.width - height) / 2) + 'px',
        width : height + 'px',
        height : width + 'px',
        transform : 'rotate(' + $scope.rotation + 'deg)'
        };
      }
      $image.css(css);
    };

    self.move = function(deltaX, deltaY){
      if(zoom){
        zoom.deltaX = deltaX;
        zoom.deltaY = deltaY;
        console.log('move: ', zoom);
        self.layout();
      }
    };
    
    self.startZoom = function(positionX, positionY) {
      var imgBounds = $image[0].getBoundingClientRect();
      var originX = 100 / imgBounds.width * (positionX - imgBounds.left);
      var originY = 100 / imgBounds.height * (positionY - imgBounds.top);
      if (!zoom) {
        zoom = {
        interval : undefined,
        originX : originX,
        originY : originY,
        factor : 1
        };
      }
      zoom.interval = setInterval(function() {
        console.log('zoooom', zoom);
        if (zoom.factor >= 6) {
          clearInterval(zoom.interval);
          zoom.interval = undefined;
          return;
        }
        zoom.factor = zoom.factor + 0.1;
        self.layout();
      }, 100);
      console.log('start zooom at: ', zoom);
    };

    self.stopZoom = function() {
      if (zoom && zoom.interval) {
        clearInterval(zoom.interval);
        zoom.interval = undefined;
      }
      console.log('stop zooom ');
    };
    
    self.resetZoom = function(){
      if(zoom){
        zoom = undefined;
        self.layout();
      }
    }
    
    self.getZoomFactor = function(){
      if(zoom){
        return zoom.factor;
      }
      return undefined;
    };
    
  }

  Controller.$inject = [ '$scope' ]
})(angular);