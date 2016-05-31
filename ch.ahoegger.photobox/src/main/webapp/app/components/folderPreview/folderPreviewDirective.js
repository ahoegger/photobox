(function(angular) {

  'use strict';

  var _name = 'photoboxFolderPreview';

  angular.module('modulePictureboxComponents').directive(_name, FolderPreviewDirective);

  function FolderPreviewDirective($timeout, window, resourceService, $filter) {
    return {
    restrict : 'E',
    replace : false,
    templateUrl : 'app/components/folderPreview/folderPreviewTemplate.html',
    scope : {
    folder : '='
//    ngClick : '=?'
    },
    link : function($scope, $element, $attrs) {
      var $imageContainer;
      var $image;
      var $window;
      var onWindowResize = _debouncedCenterImage.bind(this);
      var debounce = 500;
      var debouncePromise;

      (function _init() {
        if ($scope.folder.childrenLoaded) {
          _createPreviewImages($scope.folder);
        } else {
          var apiUrl = 'rest/api/'+$scope.folder.id;
//          var apiUrl = $filter('imageLinkFilter')($scope.folder.links, 'api');
          if (apiUrl) {
            resourceService.getResources(apiUrl).then(function(response) {
//              $scope.$apply(function() {
                _createPreviewImages(response);
//              });

            });
          }
        }
        $imageContainer = angular.element($element[0].getElementsByClassName('img-preview-container'));
        $image = angular.element($element[0].getElementsByClassName('img-preview'));
        $window = angular.element(window);
//        $scope.clickCallback = function() {
//          console.log('clicked!!!');
//          $scope.ngClick($scope.folder);
//        }

      })();

      function _createPreviewImages(folder) {
        
        
        var images = folder.pictures;
        if (images.length < 3) {
          $scope.previewImages = images;
        } else {
          var previewImages = [];
          // pick random 3
          var i;
          for (i = 0; i < 3; i++) {
            previewImages.push(images.splice(Math.floor(Math.random() * images.length), 1)[0]);
          }
          $scope.previewImages = previewImages;
        }
      }

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
        var css = {

        };
        if (imageBounds.width < imageBounds.height) {
          css.width = containerSize + 'px';
          // css.margin = 'auto 0';
          css.top = -((containerSize / imageBounds.width) * imageBounds.height - containerSize) / 2 + 'px';
        } else {
          css.height = containerSize + 'px';
          // css.margin = '0 auto';
          css.left = -((containerSize / imageBounds.height) * imageBounds.width - containerSize) / 2 + 'px';
        }
        $image.css(css);
      }
    }
    };
  }
  FolderPreviewDirective.$inject = [ '$timeout', '$window', 'resource.service', '$filter' ];

})(angular);