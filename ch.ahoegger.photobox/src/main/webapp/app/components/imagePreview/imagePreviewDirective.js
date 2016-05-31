(function(angular) {

  'use strict';

  var _name = 'photoboxImagePreview';

  angular.module('modulePictureboxComponents').directive(_name, PreviewDirective);

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
        $scope.imageUrl = $filter('imageSizeFilter')($scope.image.id, $element[0].getBoundingClientRect().width);
        if ($scope.imageSelection) {
          $scope.clickCallback = function(event) {
            console.log('clicked!!!');
            $scope.imageSelection($scope.image, event);
          }
        }

        if($scope.image.rotation){
          $image.css('transform', 'rotate('+$scope.image.rotation+'deg)');
          console.log('rotation: ',$scope.image.rotation );
        }

        // listeners
        $image.bind('load', function() {
          _centerImage();

        });
        $window.bind('resize', onWindowResize);

        $scope.$on('$destroy', function() {
          $window.unbind('resize', onWindowResize);
        });

      })();

      function _debouncedCenterImage() {
        console.log('DEBUONCED CENTER');
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
  PreviewDirective.$inject = [ '$timeout', '$window','$filter' ];

})(angular);