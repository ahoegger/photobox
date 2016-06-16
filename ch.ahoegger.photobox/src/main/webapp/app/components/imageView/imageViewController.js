(function(angular) {

  'use strict';

  angular.module('modulePictureboxComponents').factory('zoomDataFactory', ZoomDataFactory).controller('imageViewController', Controller);

  function Controller($scope, ZoomData) {
    var self = this;
    var containerSize;
    var $image;
    var initalImageSize;
    var rotation;
    var zoomData;

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
      var width, height;

      if ((rotation / 90) % 2 == 0) {
        imgWidth = initalImageSize.width;
        imgHeight = initalImageSize.height;
        width = imgWidth;
        height = imgHeight;
      } else {
        imgWidth = initalImageSize.height;
        imgHeight = initalImageSize.width;
        width = imgHeight;
        height = imgWidth;
      }

      var scaleFactor = containerSize.width / imgWidth;
      scaleFactor = Math.min(scaleFactor, containerSize.height / imgHeight);
      if (zoomData) {
        scaleFactor = scaleFactor * zoomData.getFactor();
      }
      var width = width * scaleFactor;
      var height = height * scaleFactor;
      var top = Math.floor((containerSize.height - height) / 2);
      var left = Math.floor((containerSize.width - width) / 2);
      if (zoomData) {
        // ensure image bounds
        var deltaX = 0;
        var deltaY = 0;
        if (left < 0) {
          deltaX = Math.min(Math.abs(left), (zoomData.getOffsetX() + zoomData.getDeltaX()));
          deltaX = Math.max(left, (containerSize.width - width), deltaX);
        }
        if (top < 0) {
          deltaY = Math.min(Math.abs(top), (zoomData.getOffsetY() + zoomData.getDeltaY()));
          deltaY = Math.max(top, (containerSize.height - height), deltaY);
        }
        left += deltaX;
        top += deltaY;
      }

      $image.css({
      top : top + 'px',
      left : left + 'px',
      width : width + 'px',
      height : height + 'px',
      transform : 'rotate(' + $scope.rotation + 'deg)'
      });
    };

    self.move = function(deltaX, deltaY) {
      if (zoomData) {
        zoomData.setDeltaX(deltaX);
        zoomData.setDeltaY(deltaY);
        self.layout();
      }
    };
    self.moveEnd = function() {
      if (zoomData) {
        zoomData.setOffsetX(zoomData.getOffsetX() + zoomData.getDeltaX());
        zoomData.setOffsetY(zoomData.getOffsetY() + zoomData.getDeltaY());
        zoomData.setDeltaX(0);
        zoomData.setDeltaY(0);
      }
    };

    self.startZoom = function(positionX, positionY) {
      var imgBounds = $image[0].getBoundingClientRect();
      var originX = 1 / imgBounds.width * (positionX - imgBounds.left);
      var originY = 1 / imgBounds.height * (positionY - imgBounds.top);
      if (!zoomData) {
        zoomData = new ZoomData(originX, originY);
      }
      zoomData.setInterval(setInterval(function() {
        if (zoomData.getFactor() >= 6) {
          clearInterval(zoomData.getInterval());
          zoomData.setInterval();
          return;
        }
        zoomData.incrementFactor(0.015);
        self.layout();
      }, 15));
    };

    self.stopZoom = function() {
      if (zoomData && zoomData.getInterval()) {
        clearInterval(zoomData.getInterval());
        zoomData.setInterval();
      }
    };

    self.resetZoom = function() {
      if (zoomData) {
        zoomData = undefined;
        self.layout();
      }
    }

    self.getZoomFactor = function() {
      if (zoomData) {
        return zoomData.getFactor();
      }
      return undefined;
    };

  }

  Controller.$inject = [ '$scope', 'zoomDataFactory' ];

  // TODO ngDoc
  function ZoomDataFactory() {

    function ZoomData(originX, originY) {
      this.originX = originX;
      this.originY = originY;
      this.factor = 1;
      this.interval;
      this.offsetX = 0;
      this.offsetY = 0;
      this.deltaX = 0;
      this.deltaY = 0;
    }
    ZoomData.prototype.setOffsetX = function(offsetX) {
      this.offsetX = offsetX;
    };
    ZoomData.prototype.getOffsetX = function() {
      return this.offsetX;
    };

    ZoomData.prototype.setOffsetY = function(offsetY) {
      this.offsetY = offsetY;
    };
    ZoomData.prototype.getOffsetY = function() {
      return this.offsetY;
    };

    ZoomData.prototype.setDeltaX = function(deltaX) {
      this.deltaX = deltaX;
    };
    ZoomData.prototype.getDeltaX = function() {
      return this.deltaX;
    };
    ZoomData.prototype.setDeltaY = function(deltaY) {
      this.deltaY = deltaY;
    };
    ZoomData.prototype.getDeltaY = function() {
      return this.deltaY;
    };

    ZoomData.prototype.setFactor = function(factor) {
      this.factor = factor;
    };

    ZoomData.prototype.incrementFactor = function(delta) {
      this.factor += delta;
    };
    ZoomData.prototype.getFactor = function() {
      return this.factor;
    };

    ZoomData.prototype.setInterval = function(interval) {
      this.interval = interval;
    };

    ZoomData.prototype.getInterval = function() {
      return this.interval;
    };

    return ZoomData;
  }

})(angular);