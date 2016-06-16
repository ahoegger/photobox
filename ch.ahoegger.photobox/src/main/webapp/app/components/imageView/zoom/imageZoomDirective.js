(function(angular) {

  'use strict';

  angular.module('modulePictureboxComponents').directive('photoboxImageZoom', ZoomDirective);

  function ZoomDirective($document, $timeout, window, $filter) {
    return {
    restrict : 'A',
    require : '^photoboxImageView',
    link : function($scope, $element, $attrs, imageViewController) {
      var self = this;
      var startZoomTimeout = 1000;
      var $image;
      var originalImageSize;
      var startTimeStamp;
      var startPosition;
      var startZoomPromise;
      var zoomIntervalId;
      var isZoomed;

      var eventDetail;
      var onTouchStart = _touchStart.bind(self);
      var onMouseDown = _mouseDown.bind(self);
      var onMouseMove = _mouseMove.bind(self);
      var onTouchMove = _touchMove.bind(self);
      var onEnd = _moveEnd.bind(self);

      (function _init() {
        $image = angular.element($element[0].getElementsByTagName('img'));

        $scope.imageSrc = $filter('imageSizeFilter')($scope.imageId, $element[0].getBoundingClientRect());

        // listeners
        $image.bind('touchstart', onTouchStart);
        $image.bind('mousedown', onMouseDown);

        $image.bind('load', function() {
          var imageBounds = $image[0].getBoundingClientRect();
          console.log('after load: h:' + imageBounds.height + ' w:' + imageBounds.width)
          originalImageSize = {
          width : imageBounds.width,
          height : imageBounds.height
          };

        });

      })();

      function _touchStart(event) {
        startTimeStamp = event.timeStamp;
        var touch = event.originalEvent.touches[0];
        _start(touch.pageX, touch.pageY, event);
      }
      function _mouseDown(event) {
        startTimeStamp = event.timeStamp;
        // event.preventDefault();
        // event.stopPropagation();
        _start(event.pageX, event.pageY, event);
      }

      function _start(xOffset, yOffset, event) {
        startPosition = {

        };
        eventDetail = {
        xStart : xOffset,
        yStart : yOffset,
        xEnd : xOffset,
        yEnd : yOffset,
        timeStart : event.timeStamp,
        timeEnd : event.timeStamp
        };
        console.log('zoom start with: ', xOffset, yOffset);
        // attach listeners
        $image.on('mousemove', onMouseMove);
        $image.on('touchmove', onTouchMove);
        $image.on('mouseup touchend', onEnd);
        startZoomPromise = $timeout(function() {
          _zoom();
        }, startZoomTimeout);
        if (imageViewController.getZoomFactor()) {
          isZoomed = true;
          $image.removeClass('animated');
          event.preventDefault();
          event.stopPropagation();
        } else {
          isZoomed = false;
        }
      }

      function _mouseMove(event) {
        // event.preventDefault();
        // event.stopPropagation();
        _move(event.pageX, event.pageX, event);
      }
      function _touchMove(event) {
        // event.preventDefault();
        // event.stopPropagation();
        var touch = event.originalEvent.touches[0];
        _move(touch.pageX, touch.pageY, event);

      }
      function _move(xPos, yPos, event) {
        eventDetail.xEnd = xPos;
        eventDetail.yEnd = yPos;
        // console.log('move: ',xPos,yPos);
        if (!_isValidZoomDelta(eventDetail)) {
          _stopZoom();
        }
        if (isZoomed) {
          event.preventDefault();
          event.stopPropagation();
          imageViewController.move(eventDetail.xEnd - eventDetail.xStart, eventDetail.yEnd - eventDetail.yStart);
        }
      }

      function _moveEnd(event) {
        eventDetail.timeEnd = event.timeStamp;
        if (isZoomed) {
          event.preventDefault();
          event.stopPropagation();
          imageViewController.moveEnd();
        }
        $image.addClass('animated');
        _stopZoom();
        if (_isClick(eventDetail)) {
          _handleClick();
        }
      }

      function _zoom() {
        $image.removeClass('animated');
        imageViewController.startZoom(eventDetail.xEnd, eventDetail.yEnd);
      }

      function _stopZoom() {
        if (startZoomPromise) {
          $timeout.cancel(startZoomPromise);
          startZoomPromise = undefined;
        }
        imageViewController.stopZoom();

      }

      function _handleClick() {
        imageViewController.resetZoom();
      }

      function _isValidZoomDelta(eventDetail) {
        if (!eventDetail) {
          return false;
        }
        if (Math.abs(eventDetail.xEnd - eventDetail.xStart) > 10 || Math.abs(eventDetail.yEnd - eventDetail.yStart) > 10) {
          return false;
        }
        return true;

      }

      function _isClick(eventDetail) {
        if (!eventDetail) {
          return false;
        }
        if (Math.abs(eventDetail.timeStart - eventDetail.timeEnd) > 300) {
          return false;
        }
        if (Math.abs(eventDetail.xEnd - eventDetail.xStart) > 10 || Math.abs(eventDetail.yEnd - eventDetail.yStart) > 10) {
          return false;
        }
        return true;
      }
    }
    };
  }
  ZoomDirective.$inject = [ '$document', '$timeout', '$window', '$filter' ];

})(angular);