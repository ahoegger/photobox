(function(angular) {

  'use strict';

  angular.module('modulePictureboxComponents').directive('photoboxImageFullScreenSupport', FullScreenSupportDirective);

  function FullScreenSupportDirective(MouseEvent) {
    return {
    restrict : 'A',
    require : '^photoboxImageView',
    link : function($scope, $element, $attrs, imageViewController) {
      var self = this;
      var $image;

      var eventDetail;
      var onTouchStart = _touchStart.bind(self);
      var onMouseDown = _mouseDown.bind(self);
      var onMouseMove = _mouseMove.bind(self);
      var onTouchMove = _touchMove.bind(self);
      var onEnd = _moveEnd.bind(self);

      (function _init() {
        $image = angular.element($element[0].getElementsByTagName('img'));

        // listeners
        $image.bind('touchstart', onTouchStart);
        $image.bind('mousedown', onMouseDown);

      })();
 
      function _touchStart(event) {
        var touch = event.originalEvent.touches[0];
        _start(touch.pageX, touch.pageY, event);
      }
      function _mouseDown(event) {
        _start(event.pageX, event.pageY, event);
      }

      function _start(xOffset, yOffset, event) {
        if(imageViewController.getZoomFactor()){
          return;
        }
        eventDetail = new MouseEvent(xOffset, yOffset, event.timeStamp).withEnd(xOffset, yOffset, event.timeStamp);
        // attach listeners
        $image.on('mousemove', onMouseMove);
        $image.on('touchmove', onTouchMove);
        $image.on('mouseup touchend', onEnd);
      }

      function _mouseMove(event) {
        _move(event.pageX, event.pageY, event);
      }
      function _touchMove(event) {
        var touch = event.originalEvent.touches[0];
        _move(touch.pageX, touch.pageY, event);

      }
      function _move(xPos, yPos, event) {
        eventDetail.withEndPosition(xPos, yPos);
        if (!MouseEvent.isClickOffset(eventDetail)) {
          _removeListeners();
        }
      }
      function _moveEnd(event) {
        eventDetail.withEndTime(event.timeStamp);
        if (MouseEvent.isClick(eventDetail)) {
          $scope.$apply(function() {
            imageViewController.toggleFullScreen();
          });

        }
        _removeListeners();
      }

      function _removeListeners() {
        $image.off('mousemove', onMouseMove);
        $image.off('touchmove', onTouchMove);
        $image.off('mouseup touchend', onEnd);
      }

    }
    };
  }
  FullScreenSupportDirective.$inject = [ 'PhotoboxMouseEvent' ];

})(angular);