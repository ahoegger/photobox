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
    imageSelection : '=?',
    indexUpdated : '=?',
    rotateLeft : '=?',
    rotateRight : '=?',
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
        // listeners

        if ($scope.imageSelection) {
          $scope.clickCallback = function(event) {
            $scope.imageSelection(event);
          }
        }
        $scope.$watch('files', function(newFiles) {
          console.log('files changed: ', newFiles);
          _initialUpdateImagesDebounced();
        });
        $scope.$watch('index', function(newIndexRaw) {
          console.log('index changed: ', newIndexRaw);
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
        $container.removeClass('animated');
        _updateImagesDebounced().then(function() {
          $container.addClass('animated');
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
        $images.forEach(function($img) {
          if ($img) {
            $img.remove();
          }
        });
        var source;
        if (angular.isDefined($scope.index) && files) {
          // create images
          var i;
          var left = -100;
          for (i = -1; i < 2; i++) {
            if (i + currentIndex >= 0 && i + currentIndex < files.length) {
              source = '<photobox-image-view ' + 'image-selection="clickCallback" ' + 'image-id="' + $scope.files[i + currentIndex].id + '" ' + 'rotation="files[' + (i + currentIndex) + '].rotation"' + '></photobox-image-view>';
              // source = '<photobox-image-view image-selection="clickCallback"
              // image-src="' + linkFilter($scope.files[i + currentIndex].links,
              // 'Desktop') + '"></photobox-image-view>';
              $images[i + 1] = $compile(source)($scope);
              $images[i + 1].appendTo($container[0]);

            }
            left += 100;
          }
          _updatePositions(0);
          _setCurrentImage($images[1]);
        }
      }

      function _setCurrentImage($image) {
        if ($currentImage) {
          _removeDragListener($currentImage);
          $currentImage.removeClass('current');
        }
        $currentImage = $image;
        $image.addClass('current');
        _addDragListeners($currentImage);
      }

      function _updatePositionsDebounced(deltaX) {
        console.log('debounced update positions...');
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
        console.log('update positions: ' + elementWidth);
        var deltaX = deltaXRaw || 0;
        deltaX = Math.min(Math.abs(deltaX), elementWidth) * Math.sign(deltaX);
        var index = -1;
        var i;
        for (i = -1; i < 2; i++) {
          if ($images[i + 1]) {
            var left = (i * elementWidth) + deltaX;
            var opacity = (elementWidth - Math.abs(left / 1.5)) / elementWidth;

            $images[i + 1].css({
            left : left + 'px',
            opacity : opacity + ''
            });
            console.log('css img ' + (i + 1) + ' = ' + ((i * elementWidth) + deltaX) + 'px');
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
          $images[2].remove();
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
          $images[0].remove();
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
        var source = '<photobox-image-view  image-selection="clickCallback"  image-id="' + $scope.files[fileIndex].id + '" rotation="files[' + fileIndex + '].rotation"></photobox-image-view>';
        // var source = '<photobox-image-view image-selection="clickCallback"
        // image-src="' + linkFilter($scope.files[fileIndex].links, 'Desktop') +
        // '"></photobox-image-view>';
        var $image = $compile(source)($scope);
        if (append) {
          $image.appendTo($container[0]);
        } else {
          $image.prependTo($container[0]);
        }
        return $image;
      }

      function _notifyIndexUpdated(index) {
        if ($scope.indexUpdated) {
          $scope.indexUpdated(index);
        }
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

      function _addDragListeners($image) {
        $image.on('mousedown', onMouseDown);
        $image.on('touchstart', onTouchStart);
      }

      function _removeDragListener($image) {
        $image.off('mousedown', onMouseDown);
        $image.off('touchstart', onTouchStart);
      }

      function _touchStart(event) {
        event.preventDefault();
        event.stopPropagation();
        console.log('touch start!!!');
        var touch = event.originalEvent.touches[0];
        _dragStart(touch.pageX);
      }
      function _mouseDown(event) {
        event.preventDefault();
        event.stopPropagation();
        console.log('mouse down!!!');
        _dragStart(event.pageX);
      }
      function _dragStart(xPosition) {
        if (swiping) {
          return;
        }
        console.log('add listeners...');
        $container.removeClass('animated');
        swipeStartX = xPosition;
        console.log('dragStart');
        $document.on('mousemove', onMouseMove);
        $document.on('touchmove', onTouchMove);
        $document.on('mouseup touchend', onMoveEnd);
        swiping = true;
      }

      function _mouseMove(event) {
        event.preventDefault();
        event.stopPropagation();
        _move(event.pageX);
      }
      function _touchMove(event) {
        event.preventDefault();
        event.stopPropagation();
        var touch = event.originalEvent.touches[0];
        _move(touch.pageX);

      }
      function _move(xPosition) {
        swipeCurrentX = xPosition;
        var deltaX = swipeCurrentX - swipeStartX;
        if (deltaX < 0 && $images[2]) {
          _updatePositions(deltaX);
        } else if (deltaX > 0 && $images[0]) {
          _updatePositions(deltaX);
        }
        console.log('moved: ', xPosition, swipeStartX);
      }
      function _moveEnd(event) {
        event.preventDefault();
        event.stopPropagation();
        if (!swiping) {
          return;
        }
        console.log('move end!!! remove listeners')
        $document.off('mousemove', onMouseMove);
        $document.off('touchmove', onTouchMove);
        $document.off('mouseup touchend', onMoveEnd);
        $container.addClass('animated');
        swiping = false;
        var deltaX;
        if (angular.isUndefined(swipeCurrentX)) {
          swipeCurrentX = swipeStartX;
        }

        deltaX = swipeCurrentX - swipeStartX;
        swipeCurrentX = undefined;
        swipeStartX = undefined;
        _snap(deltaX);
      }

      function _snap(deltaX) {
        var elementWidth = $container[0].getBoundingClientRect().width;
        if (Math.abs(deltaX) > elementWidth * 0.30) {
          if (deltaX > 0) {
            _snapPrevious();
          } else {
            _snapNext();
          }
        } else {
          _updatePositions(0);
          if (Math.abs(deltaX) < 10) {
            console.log('img selection ' + $scope.files[currentIndex]);
            $scope.$apply(function() {
              $scope.imageSelection($scope.files[currentIndex]);
            });
          }
        }
      }

      function _handleKeyDown(event) {
        if (event.shiftKey || event.altKey) {
          return;
        }
        if (event.ctrlKey) {
          if (event.keyCode === 37) {
            if ($scope.rotateLeft) {
              $scope.rotateLeft();
              event.preventDefault();
            }
          } else if (event.keyCode === 39) {
            if ($scope.rotateRight) {
              $scope.rotateRight();
              event.preventDefault();
            }
          }
          return;
        }
        if (event.keyCode === 37) {
          _snapPrevious();
          event.preventDefault();
        } else if (event.keyCode === 39) {
          _snapNext();
          event.preventDefault();
        }

      }

    }
    };
  }
  SwipeDirective.$inject = [ '$timeout', '$window', '$document', '$compile', '$filter' ];

})(angular);