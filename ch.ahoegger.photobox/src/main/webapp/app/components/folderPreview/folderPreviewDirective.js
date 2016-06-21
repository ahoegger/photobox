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
    // ngClick : '=?'
    },
    link : function($scope, $element, $attrs) {
      var $previewContainer;
      var $image;
      var $window;
      var onWindowResize = _debouncedUpdateHeight.bind(this);
      var debounce = 500;
      var debouncePromise;

      (function _init() {
        $previewContainer = angular.element($element[0].getElementsByClassName('preview-container'));
        _updatePreviewContainerHeight();
        
        $scope.pictureCount = $scope.folder.totalPictureCount;
        $scope.previewImages = $scope.folder.previewPictures;
//        if ($scope.folder.childrenLoaded) {
//          $scope.pictureCount = response.totalPictureCount;
//          $scope.previewImages = response.previewPictures;
//        } else {
//          var apiUrl = 'rest/api/' + $scope.folder.id;
//          if (apiUrl) {
//            resourceService.getResources(apiUrl).then(function(response) {
//              $scope.pictureCount = response.totalPictureCount;
//              $scope.previewImages = response.previewPictures;
//            });
//          }
//        }
        $image = angular.element($element[0].getElementsByClassName('img-preview'));
        $window = angular.element(window);
        // listeners
        $window.bind('resize', onWindowResize);

        $scope.$on('$destroy', function() {
          $window.unbind('resize', onWindowResize);
        });

      })();

      function _updatePreviewContainerHeight() {
        var previewContainerWidth = $previewContainer[0].getBoundingClientRect().width;
        var windowWidth = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
        var height = 0;
        if (windowWidth < 400) {
          // small
          height = Math.min(previewContainerWidth / 4, 100);
        } else if (windowWidth < 1000) {
          height = Math.min(previewContainerWidth / 5, 150);
        } else {
          height = Math.min(previewContainerWidth / 5, 200);
        }
        $previewContainer.css('height', height + 'px');
      }

      function _debouncedUpdateHeight() {
        if (debouncePromise) {
          $timeout.cancel(debouncePromise);

        }
        debouncePromise = $timeout(function() {
          _updatePreviewContainerHeight();
          debouncePromise = undefined;
        }, debounce);
      }

    }
    };
  }
  FolderPreviewDirective.$inject = [ '$timeout', '$window', 'resource.service', '$filter' ];

})(angular);