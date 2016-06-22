(function(angular) {

  'use strict';

  var _name = 'photoboxImageSwipe';

  angular.module('modulePictureboxComponents').directive(_name, SwipeDirective);

  function SwipeDirective($timeout, window, $document, $compile, $filter) {
    return {
    restrict : 'E',
    replace : false,
    templateUrl : 'app/components/imageView/swipe/imageSwipeTemplate.html',
    scope : {
    files : '=',
    index : '=',
    controller : '=',
    debounce : '@?'
    },
    link : function($scope, $element, $attrs) {
      var $window;
      var $container;
      var $images = [];
      var $currentImage;
      var currentIndex;
      var debouncePromise;
      var debouncePromiseUpdateImages;
      var debounce = $scope.debounce || 200;
      var linkFilter = $filter('imageLinkFilter');
      var onResize = function() {
        _updatePositionsDebounced();
      }.bind(this);
      var onKeyDown = _handleKeyDown.bind(this);

      (function _init() {
        currentIndex = parseInt($scope.index);
        $window = angular.element(window);
        $container = angular.element($element[0].getElementsByClassName('swipe-container'));

        // var imageSrc=linkFilter($scope.files[$scope.index], 'Desktop');
        // $image02[0].setAttribute('image-src', imageSrc);
        _initialUpdateImagesDebounced();

        // handler
        $scope.navigateNext = _snapNext.bind(this);
        $scope.hasNext = function() {
          return $scope.files && $scope.files.length > currentIndex + 1;
        }.bind(this);
        $scope.navigatePrevious = _snapPrevious.bind(this);
        $scope.hasPrevious = function() {
          return $scope.files && currentIndex > 0;
        }.bind(this);
        // listeners

        $scope.$watch('files', function(newFiles) {
          _initialUpdateImagesDebounced();
        });
        $scope.$watch('index', function(newIndexRaw) {
          var newIndex = undefined;
          if (angular.isDefined(newIndexRaw)) {
            newIndex = parseInt(newIndexRaw);
          }
          if (currentIndex !== newIndex) {
            _initialUpdateImagesDebounced();
          }
        });
        $window.on('keyup', onKeyDown);
        $window.on('resize', onResize);
        $element.on('destroy', function() {
          $window.off('resize', onResize)
          $window.off('keyup', onKeyDown);
        });

      })();

      function _initialUpdateImagesDebounced() {
        _setAnimated(false);
        _updateImagesDebounced().then(function() {
          _setAnimated(true);
        });
      }

      function _updateImagesDebounced() {
        if (debouncePromiseUpdateImages) {
          $timeout.cancel(debouncePromiseUpdateImages);
          debouncePromiseUpdateImages = undefined;
        }
        debouncePromiseUpdateImages = $timeout(function() {
          _updateImages();
          debouncePromiseUpdateImages = undefined;
        }, debounce);
        return debouncePromiseUpdateImages;
      }

      function _updateImages() {
        var files = $scope.files;
        if (!files || angular.isUndefined($scope.index)) {
          return;
        }
        var newIndex = parseInt($scope.index);
        newIndex = Math.min(files.length - 1, newIndex);
        newIndex = Math.max(0, newIndex);
        if (currentIndex !== newIndex) {
          currentIndex = newIndex;
          _notifyIndexUpdated(currentIndex);
        }
        // remove old
        $images.forEach(function(img) {
          _removeImage(img);
        });
        var source;
        if (angular.isDefined($scope.index) && files) {
          // create images
          var i;
          var left = -100;
          for (i = -1; i < 2; i++) {
            if (i + currentIndex >= 0 && i + currentIndex < files.length) {
              source = '<photobox-image-view photobox-image-zoom photobox-image-full-screen-support class="animated" controller="controller" image="::image"></photobox-image-view>';
              // source = '<photobox-image-view image-selection="clickCallback"
              // image-src="' + linkFilter($scope.files[i + currentIndex].links,
              // 'Desktop') + '"></photobox-image-view>';
              var childScope = $scope.$new();
              childScope.image = $scope.files[i + currentIndex];
              // var childScope = $scope;
              var $img = $compile(source)(childScope);
              $images[i + 1] = {
              $element : $img,
              $scope : childScope
              };
              $img.appendTo($container[0]);

            }
            left += 100;
          }
          _updatePositions(0);
          _setCurrentImage($images[1]);
        }
      }

      function _removeImage(image) {
        if (image && image.$element) {
          image.$scope.$destroy();
          image.$element.remove();
        }
      }

      function _setAnimated(animated) {
        var fun = function($img) {
          if (animated) {
            $img.addClass('animated');
          } else {
            $img.removeClass('animated');
          }
        };
        $images.forEach(function($img) {
          if ($img && $img.$element) {
            fun($img.$element);
          }
        });
      }

      function _setCurrentImage(image) {
        if ($currentImage && $currentImage.$element) {
          _removeDragListener($currentImage);
          $currentImage.$element.removeClass('current');
        }
        $currentImage = image;
        if ($currentImage && $currentImage.$element) {
          $currentImage.$element.addClass('current');
          _addDragListeners($currentImage);
        }
      }

      function _updatePositionsDebounced(deltaX) {
        if (debouncePromise) {
          $timeout.cancel(debouncePromise)
          debouncePromise = undefined;
        }
        debouncePromise = $timeout(function() {
          _updatePositions(deltaX);
        }, debounce);
      }

      function _updatePositions(deltaXRaw) {
        var elementWidth = $container[0].getBoundingClientRect().width;
        var deltaX = deltaXRaw || 0;
        deltaX = Math.min(Math.abs(deltaX), elementWidth) * Math.sign(deltaX);
        var index = -1;
        var i;
        for (i = -1; i < 2; i++) {
          if ($images[i + 1] && $images[i + 1].$element) {
            var left = (i * elementWidth) + deltaX;
            var opacity = (elementWidth - Math.abs(left / 1.5)) / elementWidth;

            $images[i + 1].$element.css({
            left : left + 'px',
            opacity : opacity + ''
            });
          }
        }

      }

      function _snapPrevious() {
        if (currentIndex <= 0) {
          _updatePositions(0);
          return;
        }
        // update index
        currentIndex--;
        // remove last
        if ($images[2]) {
          _removeImage($images[2]);
        }
        // shift
        $images[2] = $images[1];
        $images[1] = $images[0]
        // create new
        if (currentIndex > 0) {
          $images[0] = _createImageView(currentIndex - 1);
        } else {
          $images[0] = undefined;
        }
        _updatePositions(0);
        _setCurrentImage($images[1]);
        _notifyIndexUpdated(currentIndex);
      }

      function _snapNext() {
        if (currentIndex >= $scope.files.length - 1) {
          _updatePositions(0);
          return;
        }
        // update index
        currentIndex++;
        // remove last
        if ($images[0]) {
          _removeImage($images[0]);
        }
        // shift
        $images[0] = $images[1];
        $images[1] = $images[2]
        // create new
        if ($scope.files.length > (currentIndex + 1)) {
          $images[2] = _createImageView(currentIndex + 1, true);
        } else {
          $images[2] = undefined;
        }
        _updatePositions(0);
        _setCurrentImage($images[1]);
        _notifyIndexUpdated(currentIndex);
      }

      function _createImageView(fileIndex, append) {
        var source = '<photobox-image-view photobox-image-zoom photobox-image-full-screen-support class="animated"  controller="controller" image="::image"></photobox-image-view>';
        // var source = '<photobox-image-view image-selection="clickCallback"
        // image-src="' + linkFilter($scope.files[fileIndex].links, 'Desktop') +
        // '"></photobox-image-view>';
        var childScope = $scope.$new();
        childScope.image = $scope.files[fileIndex];
        // var childScope = $scope;

        var $image = $compile(source)(childScope);
        if (append) {
          $image.appendTo($container[0]);
        } else {
          $image.prependTo($container[0]);
        }
        return {
        $element : $image,
        $scope : childScope
        };
      }

      function _notifyIndexUpdated(index) {
        $scope.controller.setCurrentIndex(index);
      }

      // drag support
      var onTouchStart = _touchStart.bind(self);
      var onMouseDown = _mouseDown.bind(self);
      var swiping = false;
      var swipeStartX;
      var swipeCurrentX;
      var onMouseMove = _mouseMove.bind(self);
      var onTouchMove = _touchMove.bind(self);
      var onMoveEnd = _moveEnd.bind(self);
      var startTimeStamp;
      var endTimeStamp;

      function _addDragListeners($image) {
        if ($image && $image.$element) {
          $image.$element.on('mousedown', onMouseDown);
          $image.$element.on('touchstart', onTouchStart);
        }
      }

      function _removeDragListener($image) {
        if ($image && $image.$element) {
          $image.$element.off('mousedown', onMouseDown);
          $image.$element.off('touchstart', onTouchStart);
        }
      }

      function _touchStart(event) {
        startTimeStamp = event.timeStamp;
        event.preventDefault();
        // event.stopPropagation();
        var touch = event.originalEvent.touches[0];
        _dragStart(touch.pageX);
      }
      function _mouseDown(event) {
        startTimeStamp = event.timeStamp;
        event.preventDefault();
        // event.stopPropagation();
        _dragStart(event.pageX);
      }
      function _dragStart(xPosition) {
        if (swiping) {
          return;
        }
        _setAnimated(false);
        swipeStartX = xPosition;
        $document.on('mousemove', onMouseMove);
        $document.on('touchmove', onTouchMove);
        $document.on('mouseup touchend', onMoveEnd);
        swiping = true;
      }

      function _mouseMove(event) {
        event.preventDefault();
        // event.stopPropagation();
        _move(event.pageX);
      }
      function _touchMove(event) {
        event.preventDefault();
        // event.stopPropagation();
        var touch = event.originalEvent.touches[0];
        _move(touch.pageX);

      }
      function _move(xPosition) {
        swipeCurrentX = xPosition;
        var deltaX = swipeCurrentX - swipeStartX;
        if (deltaX < 0) {
          if (!$images[2]) {
            deltaX = deltaX / 5;
          }
        } else if (deltaX > 0) {
          if (!$images[0]) {
            deltaX = deltaX / 5;
          }
        }
        _updatePositions(deltaX);
      }
      function _moveEnd(event) {
        var timeDiff = event.timeStamp - startTimeStamp;
        event.preventDefault();
        event.stopPropagation();
        if (!swiping) {
          return;
        }
        $document.off('mousemove', onMouseMove);
        $document.off('touchmove', onTouchMove);
        $document.off('mouseup touchend', onMoveEnd);
        _setAnimated(true);
        swiping = false;
        var deltaX;
        if (angular.isUndefined(swipeCurrentX)) {
          swipeCurrentX = swipeStartX;
        }
        deltaX = swipeCurrentX - swipeStartX;

        swipeCurrentX = undefined;
        swipeStartX = undefined;
        $scope.$apply(function() {
          _snap(deltaX);
        });
      }

      var selectionPromise;

      function _snap(deltaX) {
        var elementWidth = $container[0].getBoundingClientRect().width;
        if (Math.abs(deltaX) > elementWidth * 0.25) {
          if (deltaX > 0 && $images[0]) {
            _snapPrevious();
          } else if (deltaX < 0 && $images[2]) {
            _snapNext();
          } else {
            _updatePositions(0);
          }
        } else {
          _updatePositions(0);
        }
      }

      function _handleImageDoubleSelection() {
      }

      function _handleKeyDown(event) {
        if (event.shiftKey || event.altKey) {
          return;
        }
        if (event.ctrlKey) {
          if (event.keyCode === 37) {
            $scope.files[$scope.index].rotateLeft();
            event.preventDefault();
          } else if (event.keyCode === 39) {
            $scope.files[$scope.index].rotateRight();
            event.preventDefault();
          }
          return;
        }
        if (event.keyCode === 37) {
          $scope.$apply(function() {
            _snapPrevious();
          });
          event.preventDefault();
        } else if (event.keyCode === 39) {
          $scope.$apply(function() {
            _snapNext();
          });

          event.preventDefault();
        }

      }

    }
    };
  }
  SwipeDirective.$inject = [ '$timeout', '$window', '$document', '$compile', '$filter' ];

})(angular);