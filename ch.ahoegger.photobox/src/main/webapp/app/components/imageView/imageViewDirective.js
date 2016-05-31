(function(angular) {

  'use strict';

  var _name = 'photoboxImageView';

  angular.module('modulePictureboxComponents').directive(_name, ViewDirective);

  function ViewDirective($timeout, window, $filter) {
    return {
    restrict : 'E',
    replace : false,
    templateUrl : 'app/components/imageView/imageViewTemplate.html',
    scope : {
    imageId : '@',
    rotation : '=',
    imageSelection : '=?',
    debounce : '@?'
    },
    link : function($scope, $element, $attrs) {
      var $window;
      var $image;
      var fit = true;
      var debouncePromise;
      var debounce = $scope.debounce || 200;
      var onWindowResize = _debouncedLayoutImage.bind(this);
      var originalImageSize;

      (function _init() {
        $image = angular.element($element[0].getElementsByTagName('img'));
        $window = angular.element(window);

        $scope.imageSrc = $filter('imageSizeFilter')($scope.imageId, $element[0].getBoundingClientRect().width);

        // listeners
        $scope.$watch('rotation', function(newVal) {
          if (angular.isDefined($scope.rotation)) {
            $image.css('transform', 'rotate(' + $scope.rotation + 'deg)');
            _debouncedLayoutImage();
          }
        });
        if ($scope.imageSelection) {
          $scope.clickCallback = function(event) {
            $scope.imageSelection(event);
          }
        }

        $image.bind('load', function() {
          var imageBounds = $image[0].getBoundingClientRect();
          console.log('after load: h:' + imageBounds.height + ' w:' + imageBounds.width)
          originalImageSize = {
          width : imageBounds.width,
          height : imageBounds.height
          };
          _layoutImage();

        });
        $window.bind('resize', onWindowResize);

        $scope.$on('$destroy', function() {
          $window.unbind('resize', onWindowResize);
        });
      })();

      function _debouncedLayoutImage(timeout) {
        if (debouncePromise) {
          $timeout.cancel(debouncePromise);

        }
        timeout = timeout || debounce;
        debouncePromise = $timeout(function() {
          _layoutImage();
          debouncePromise = undefined;
        }, timeout);
      }

      function _layoutImage() {
        if (!originalImageSize) {
          return;
        }
        if (fit) {
          _fitImage();
        } else {
          // TODO
        }

      }
      function _fitImage() {
        var containerSize = $element[0].getBoundingClientRect();

        var imgWidth, imgHeight;

        if (($scope.rotation / 90) % 2 == 0) {
          imgWidth = originalImageSize.width;
          imgHeight = originalImageSize.height;
        } else {
          imgWidth = originalImageSize.height;
          imgHeight = originalImageSize.width;
        }

        var scaleFactor = containerSize.width / imgWidth;
        scaleFactor = Math.min(scaleFactor, containerSize.height / imgHeight);
        console.log('fit image!!!', scaleFactor);
        var width = imgWidth * scaleFactor;
        var height = imgHeight * scaleFactor;
        var top = Math.floor((containerSize.height - height) / 2);
        var left = Math.floor((containerSize.width - width) / 2);
        var css;
        if (($scope.rotation / 90) % 2 == 0) {
          css = {
          top : top + 'px',
          left : left + 'px',
          width : width + 'px',
          height : height + 'px'
          };
        } else {
          css = {
          top : Math.floor((containerSize.height - width) / 2) + 'px',
          left : Math.floor((containerSize.width - height) / 2) + 'px',
          width : height + 'px',
          height : width + 'px'
          };
        }
        $image.css(css);
      }
    }
    };
  }
  ViewDirective.$inject = [ '$timeout', '$window', '$filter' ];

})(angular);