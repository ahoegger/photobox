(function(angular) {

  'use strict';

  var _name = 'photoboxImageView';

  angular.module('modulePictureboxComponents').directive(_name, ViewDirective);

  function ViewDirective($timeout, window,$filter) {
    return {
    restrict : 'E',
    replace : false,
    templateUrl : 'app/components/imageView/imageViewTemplate.html',
    scope : {
    imageId : '@',
    rotation : '@',
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

      (function _init() {
        $image = angular.element($element[0].getElementsByTagName('img'));
        $window = angular.element(window);

        $scope.imageSrc = $filter('imageSizeFilter')($scope.imageId, $element[0].getBoundingClientRect().width);
        
        if($scope.rotation){
          $image.css('transform', 'rotate('+$scope.rotation+'deg)');
         
        }
        // listeners
        
        if($scope.imageSelection){
          $scope.clickCallback = function(event){
            $scope.imageSelection(event);
          }
        }
        
        $image.bind('load', function() {
          _layoutImage();

        });
        $window.bind('resize', onWindowResize);

        $scope.$on('$destroy', function() {
          $window.unbind('resize', onWindowResize);
        });
      })();

      function _debouncedLayoutImage() {
        if (debouncePromise) {
          $timeout.cancel(debouncePromise);

        }
        debouncePromise = $timeout(function() {
          _layoutImage();
          debouncePromise = undefined;
        }, debounce);
      }

      function _layoutImage() {
        if (fit) {
          _fitImage();
        } else {
          // TODO
        }

      }
      function _fitImage() {
        var containerSize = $element[0].getBoundingClientRect();
        var imageBounds = $image[0].getBoundingClientRect();
        var imgWidth, imgHeight;
        if (($scope.rotation / 90) % 2 == 0) {
          imgWidth = imageBounds.width;
          imgHeight = imageBounds.height;
        } else {
          imgWidth = imageBounds.height;
          imgHeight = imageBounds.width;
        }
        
        var scaleFactor = containerSize.width / imgWidth;
        scaleFactor = Math.min(scaleFactor, containerSize.height / imgHeight);
        console.log('fit image!!!', scaleFactor);
        var width = imgWidth * scaleFactor;
        var height = imgHeight * scaleFactor;
        var top = Math.floor((containerSize.height - height) / 2);
        var left = Math.floor((containerSize.width - width) / 2);
        var css = {
          top : top + 'px',
          left : left + 'px',
          height : height + 'px',
          width : width + 'px'
        };

        $image.css(css);
      }
    }
    };
  }
  ViewDirective.$inject = [ '$timeout', '$window','$filter' ];

})(angular);