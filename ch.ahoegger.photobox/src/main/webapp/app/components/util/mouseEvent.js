(function(angular) {

  'use strict';

  angular.module('modulePictureboxComponents').factory('PhotoboxMouseEvent', MouseEventFactory);

  function MouseEventFactory() {
    var timeDiffClick = 300;
    var deltaXYClick = 20;

    function MouseEvent(startX, startY, startTime) {
      this.startX = startX;
      this.startY = startY;
      this.endX;
      this.endY;
      this.startTime = startTime;
      this.endTime;
    }

    MouseEvent.prototype.getStartX = function() {
      return this.startX;
    };
    MouseEvent.prototype.getStartY = function() {
      return this.startY;
    };
    
    MouseEvent.prototype.getEndX = function() {
      return this.endX;
    };
    MouseEvent.prototype.getEndY = function() {
      return this.endY;
    };

    MouseEvent.prototype.getStartTime = function() {
      return this.startTime;
    };

    MouseEvent.prototype.withEndPosition = function(endX, endY) {
      this.endX = endX;
      this.endY = endY;
      return this;
    };

    MouseEvent.prototype.withEndTime = function( endTime) {
      this.endTime = endTime;
      return this;
    };
    
    MouseEvent.prototype.withEnd = function(endX, endY, endTime) {
      this.endTime = endTime;
      this.endX = endX;
      this.endY = endY;
      return this;
    };

    MouseEvent.isClickOffset = function(event) {
      if (!event || !angular.isDefined(event.endX) || !angular.isDefined(event.endY)) {
        return false;
      }
      if (Math.abs(event.endX - event.startX) > deltaXYClick) {
        return false;
      }
      if (Math.abs(event.endY - event.startY) > deltaXYClick) {
        return false;
      }
      return true;
    };

    MouseEvent.isClick = function(event) {
      if (!event || !angular.isDefined(event.endTime)) {
        return false;
      }
      if (event.endTime - event.startTime > timeDiffClick) {
        return false;
      }
      return MouseEvent.isClickOffset(event);
    }

    return MouseEvent;
  }

})(angular);