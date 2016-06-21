(function(angular) {

  'use strict';

  angular.module('modulePictureboxComponents').factory('zoomDataFactory', ZoomDataFactory).controller('imageViewController', Controller);

  function Controller($scope, $filter, ZoomData) {
    var self = this;
    var containerSize;
    var $image;
    var initalImageSize;
    var rotation;
    var zoomData;

    (function _init() {
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

      var size = _calculateLayoutSize();
      var originOffset = _calculateLayoutOriginOffset(size);
      if (originOffset) {
        zoomData.setOffsetX(originOffset.left);
        zoomData.setDeltaX(0);
        zoomData.setOffsetY(originOffset.top);
        zoomData.setDeltaY(0);
      }
      var offset = _calculateMoveOffset();
      var position = _calculatePosition(size, offset);

      var css = {
      top : position.top + 'px',
      left : position.left + 'px',
      width : size.width + 'px',
      height : size.height + 'px',
      transform : 'rotate(' + $scope.rotation + 'deg)'
      };
      
      $image.css(css);
      return;

    };

    function _calculateLayoutSize() {
      var width = initalImageSize.width;
      var height = initalImageSize.height;
      var imgWidthTransformed, imgHeightTransformed;
      if ((rotation / 90) % 2 == 0) {
        imgWidthTransformed = width;
        imgHeightTransformed = height;
      } else {
        imgWidthTransformed = height;
        imgHeightTransformed = width;
      }

      var scaleFactor = containerSize.width / imgWidthTransformed;
      scaleFactor = Math.min(scaleFactor, containerSize.height / imgHeightTransformed);
      if (zoomData) {
        scaleFactor = scaleFactor * zoomData.getFactor();
      }
      var position = {
      width : width * scaleFactor,
      height : height * scaleFactor
      };
      return position;
    }

    function _calculateLayoutOriginOffset(size) {
      if (!zoomData || !zoomData.getOriginX() || !zoomData.getOriginY()) {
        return;
      }
      return {
        left:0,
        top:0
      };
//      var wTrans = size.width;
//      var hTrans = size.height;
//      var currentBoundery = $image[0].getBoundingClientRect();
//      var currSize = {
//          width : currentBoundery.width,
//          height: currentBoundery.height
//      };
//      if ((rotation / 90) % 2 !== 0) {
//        wTrans = size.height;
//        hTrans = size.width;
//        currSize = {
//            width : currentBoundery.height,
//            height: currentBoundery.width
//        };
//      }
//
//      var left = currentBoundery.left;
//      var top = currentBoundery.top;
//
//
//      var offset = {
//      left : -(left + zoomData.getOriginX() * currSize.width - wTrans/2),
//      top  : -(top + zoomData.getOriginY() * currSize.height - hTrans/2)
//      };
//      console.log('_calculateLayoutOriginOffset offset: ', offset);
//      return offset;
    }

    function _calculateMoveOffset() {
      var offset = {
      left : 0,
      top : 0
      };
      if (zoomData) {
        offset.left = zoomData.getOffsetX() + zoomData.getDeltaX();
        offset.top = zoomData.getOffsetY() + zoomData.getDeltaY();
      }
      return offset;
    }

    function _calculatePosition(size, offset) {
      var wTrans = size.width;
      var hTrans = size.height;
      var deltaTop = 0;
      var deltaLeft = 0;
      if ((rotation / 90) % 2 !== 0) {
        wTrans = size.height;
        hTrans = size.width;
        deltaTop = (size.width - size.height) / 2;
        deltaLeft = (size.height - size.width) / 2;
      }
      var left = Math.floor((containerSize.width - wTrans) / 2) + deltaLeft;
      var top = Math.floor((containerSize.height - hTrans) / 2) + deltaTop;

      // ensure in bounds
      if (wTrans > containerSize.width) {
        left += offset.left;
        left = Math.min(deltaLeft, left);
        left = Math.max((containerSize.width - wTrans) + deltaLeft, left);
      }
      if (hTrans > containerSize.height) {
        top += offset.top;
        top = Math.min(deltaTop, top);
        top = Math.max((containerSize.height - hTrans) + deltaTop, top);
      }

      return {
      left : left,
      top : top
      };

    }

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
        zoomData = new ZoomData();
        zoomData.setFullScreenBeforeZoom($scope.fullScreen);
        $scope.fullScreen = true;
      }
      zoomData.setOriginX(originX);
      zoomData.setOriginY(originY);
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
      if (zoomData) {
        if (zoomData.getInterval()) {
          clearInterval(zoomData.getInterval());
          zoomData.setInterval();
          $scope.imageSrc = $filter('imageSizeFilter')($scope.imageId, $image[0].getBoundingClientRect());
        }
        zoomData.setOriginX();
        zoomData.setOriginY();
      }
    };

    self.resetZoom = function() {
      if (zoomData) {
        $scope.fullScreen = zoomData.getFullScreenBeforeZoom();
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

  Controller.$inject = [ '$scope', '$filter', 'zoomDataFactory' ];

 function ZoomDataFactory() {

    function ZoomData() {
      this.originX;
      this.originY;
      this.factor = 1;
      this.interval;
      this.offsetX = 0;
      this.offsetY = 0;
      this.deltaX = 0;
      this.deltaY = 0;
      this.fullScreenBeforeZoom = false;
    }


    ZoomData.prototype.setOriginX = function(originX) {
      this.originX = originX;
    };
    ZoomData.prototype.getOriginX = function() {
      return this.originX;
    };
    ZoomData.prototype.setOriginY = function(originY) {
      this.originY = originY;
    };
    ZoomData.prototype.getOriginY = function() {
      return this.originY;
    };

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
    
    ZoomData.prototype.getFullScreenBeforeZoom = function() {
      return this.fullScreenBeforeZoom;
    };
    
    ZoomData.prototype.setFullScreenBeforeZoom = function(fullScreenBeforeZoom) {
      this.fullScreenBeforeZoom = fullScreenBeforeZoom;
    };
    

    return ZoomData;
  }

})(angular);