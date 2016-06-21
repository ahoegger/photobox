(function(angular) {

  'use strict';

  angular.module('modulePictureboxComponents').directive('photoboxImageZoom', ZoomDirective);

  function ZoomDirective($document, $timeout, window, $filter, MouseEvent) {
    return {
    restrict : 'A',
    require : '^photoboxImageView',
    link : function($scope, $element, $attrs, imageViewController) {
      var self = this;
      var startZoomTimeout = 1000;
      var $image;
      var originalImageSize;
      var startTimeStamp;
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
        eventDetail = new MouseEvent(xOffset, yOffset, event.timeStamp).withEnd(xOffset, yOffset, event.timeStamp);
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
        _move(event.pageX, event.pageY, event);
      }
      function _touchMove(event) {
        // event.preventDefault();
        // event.stopPropagation();
        var touch = event.originalEvent.touches[0];
        _move(touch.pageX, touch.pageY, event);

      }
      function _move(xPos, yPos, event) {
        eventDetail.withEndPosition(xPos, yPos);
        if (isZoomed) {
          event.preventDefault();
          event.stopPropagation();
          imageViewController.move(eventDetail.getEndX() - eventDetail.getStartX(), eventDetail.getEndY() - eventDetail.getStartY());
        }
        if (!MouseEvent.isClickOffset(eventDetail)) {
          _stopZoom();
        }
      }

      function _moveEnd(event) {
        eventDetail.withEndTime(event.timeStamp);
        if (isZoomed) {
          event.preventDefault();
          event.stopPropagation();
          imageViewController.moveEnd();
        }
        $image.addClass('animated');
        _stopZoom();
        if (MouseEvent.isClick(eventDetail)) {
          $scope.$apply(function(){
            _handleClick();            
          });

        }
        _removeListeners();
      }

      function _zoom() {
        $image.removeClass('animated');
        // calc container relative
        imageViewController.startZoom(eventDetail.getEndX(), eventDetail.getEndY());
      }

      function _stopZoom() {
        if (startZoomPromise) {
          $timeout.cancel(startZoomPromise);
          startZoomPromise = undefined;
        }
        imageViewController.stopZoom();

      }

      function _removeListeners(){
        $image.off('mousemove', onMouseMove);
        $image.off('touchmove', onTouchMove);
        $image.off('mouseup touchend', onEnd);
      }
      function _handleClick() {
        imageViewController.resetZoom();
      }

    }
    };
  }
  ZoomDirective.$inject = [ '$document', '$timeout', '$window', '$filter', 'PhotoboxMouseEvent' ];

})(angular);