(function(angular) {

  'use strict';

  var _name = 'photoboxImagePreview';

  angular.module('modulePictureboxComponents').directive(_name, PreviewDirective).directive('photoboxFixedHeight', FixedHeight).directive('photoboxFixedWidth', FixedWidth);

  function PreviewDirective($timeout, window, $filter) {
    return {
    restrict : 'E',
    replace : false,
    templateUrl : 'app/components/imagePreview/imagePreviewTemplate.html',
    scope : {
    imageSrc : '@',
    image : '=',
    imageSelection : '=?'
    },
    link : function($scope, $element, $attrs) {
      var $imageContainer;
      var $image;
      var $window;
      var onWindowResize = _debouncedCenterImage.bind(this);
      var debounce = 500;
      var debouncePromise;

      (function _init() {
        $imageContainer = angular.element($element[0].getElementsByClassName('img-preview-container'));
        $image = angular.element($element[0].getElementsByClassName('img-preview'));
        $window = angular.element(window);
        $scope.imageUrl = $filter('imageSizeFilter')($scope.image.id, $element[0].getBoundingClientRect());


        if ($scope.imageSelection) {
          $scope.clickCallback = function(event) {
            $scope.imageSelection($scope.image, event);
          }
        }

        if ($scope.image.rotation) {
          $image.css('transform', 'rotate(' + $scope.image.rotation + 'deg)');
        }

        // listeners
        $image.bind('load', function() {
          _centerImage();

        });
        
        $scope.$watch(function(){
          return $element[0].getBoundingClientRect().width;
        }, function(newValue, oldValue){
          if(newValue && newValue != oldValue){
            _debouncedCenterImage();
          }
        })

      })();

      function _debouncedCenterImage() {
        if (debouncePromise) {
          $timeout.cancel(debouncePromise);

        }
        debouncePromise = $timeout(function() {
          _centerImage();
          debouncePromise = undefined;
        }, debounce);
      }

      function _centerImage() {
        var containerSize = $imageContainer[0].getBoundingClientRect().width;
        var imageBounds = $image[0].getBoundingClientRect();
        var imgWidth, imgHeight;
        if (($scope.image.rotation / 90) % 2 == 0) {
          imgWidth = imageBounds.width;
          imgHeight = imageBounds.height;
        } else {
          imgWidth = imageBounds.height;
          imgHeight = imageBounds.width;
        }

        var css = {

        };
        if (imgWidth < imgHeight) {
          css.width = containerSize + 'px';
          // css.margin = 'auto 0';
          css.top = -((containerSize / imgWidth) * imgHeight - containerSize) / 2 + 'px';
        } else {
          css.height = containerSize + 'px';
          // css.margin = '0 auto';
          css.left = -((containerSize / imgHeight) * imgWidth - containerSize) / 2 + 'px';
        }
        $image.css(css);
      }
    }
    };
  }
  PreviewDirective.$inject = [ '$timeout', '$window', '$filter' ];

  function FixedHeight($timeout, window, $filter) {
    return {
    restrict : 'A',
    link : function($scope, $element, $attrs) {
      var debounce = 500;
      var debouncePromise;

      (function _init() {

        _setWidth($element[0].getBoundingClientRect().height);
        $scope.$watch(function() {
          return $element[0].getBoundingClientRect().height;
        }, function(newValue, oldValue) {
          if (newValue && newValue !== oldValue) {
            _debouncedUpdateWidth(newValue);
          }
        });

      })();

      function _debouncedUpdateWidth(width) {
        if (debouncePromise) {
          $timeout.cancel(debouncePromise);

        }
        debouncePromise = $timeout(function() {
          _setWidth(width);
          debouncePromise = undefined;
        }, debounce);
      }

      function _setWidth(width) {
        $element.css('width', width);
      }
    }
    };
  }
  FixedHeight.$inject = [ '$timeout', '$window', '$filter' ];
  
  function FixedWidth($timeout, window, $filter) {
    return {
    restrict : 'A',
    link : function($scope, $element, $attrs) {
      var debounce = 500;
      var debouncePromise;

      (function _init() {

        _setHeight($element[0].getBoundingClientRect().width);
        $scope.$watch(function() {
          return $element[0].getBoundingClientRect().width;
        }, function(newValue, oldValue) {
          if (newValue && newValue !== oldValue) {
            _debouncedUpdateHeight(newValue);
          }
        });

      })();

      function _debouncedUpdateHeight(height) {
        if (debouncePromise) {
          $timeout.cancel(debouncePromise);

        }
        debouncePromise = $timeout(function() {
          _setHeight(height);
          debouncePromise = undefined;
        }, debounce);
      }

      function _setHeight(height) {
        $element.css('height', height);
      }
    }
    };
  }
  FixedWidth.$inject = [ '$timeout', '$window', '$filter' ];
  
  
})(angular);