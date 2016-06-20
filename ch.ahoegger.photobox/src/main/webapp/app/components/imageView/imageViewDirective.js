(function(angular) {

  'use strict';

  var _name = 'photoboxImageView';

  angular.module('modulePictureboxComponents').directive(_name, ViewDirective);

  function ViewDirective($timeout, window, $filter) {
    return {
    restrict : 'E',
    replace : false,
    templateUrl : 'app/components/imageView/imageViewTemplate.html',
    controller: 'imageViewController',
    require: '^photoboxImageView',
    scope : {
    imageId : '@',
    rotation : '=',
    fullScreen: '=?',
    imageSelection : '=?',
    debounce : '@?'
    },
    link : function($scope, $element, $attrs, imageViewController) {
      var $window;
      var $image;
      var fit = true;
      var debouncePromise;
      var debounce = $scope.debounce || 200;
      var onWindowResize = function(){
        console.log('['+$scope.imageId+']: resized!!!');
        var containerSize = $element[0].getBoundingClientRect();
        imageViewController.setContainerSize(containerSize.width, containerSize.height);
        _debouncedLayoutImage();
      }.bind(this);

      (function _init() {
        $image = angular.element($element[0].getElementsByTagName('img'));
        imageViewController.setImage($image);
        $window = angular.element(window);

        $scope.imageSrc = $filter('imageSizeFilter')($scope.imageId, $element[0].getBoundingClientRect());
        var containerSize = $element[0].getBoundingClientRect();
        imageViewController.setContainerSize(containerSize.width, containerSize.height);
        // listeners
        $scope.$watch(function(){
          var containerSize = $element[0].getBoundingClientRect();
          return {
            width: containerSize.width,
            height: containerSize.height
          }
        }, function(newValue, oldValue){
          if(!newValue){
            return;
          }
          if(oldValue){
            if(oldValue.width === newValue.width 
                && oldValue.height === newValue.height){
              return;
            }
          }
          // do it
          imageViewController.setContainerSize(newValue.width, newValue.height);
        },true);
        
        $scope.$watch('rotation', function(newVal) {
          if (angular.isDefined($scope.rotation)) {
            console.log('['+$scope.imageId+']: rotation changed!!!');
            imageViewController.setRotation($scope.rotation);
            _debouncedLayoutImage();
          }
        });
        if ($scope.imageSelection) {
          $scope.clickCallback = function(event) {
            $scope.imageSelection(event);
          }
        }

        $image.bind('load', function() {
          imageViewController.setInitialSize(this.naturalWidth, this.naturalHeight);
          console.log('['+$scope.imageId+']: after load!!!');
          _layoutImage();
          $timeout(function(){
            $image.addClass('animated visible');
          },100);

        });

        $window.bind('resize', onWindowResize);
        $scope.$on('$destroy', function() {
          $window.unbind('resize', onWindowResize);
        });
      })();

      function _debouncedLayoutImage(timeout) {
        if (debouncePromise) {
          $timeout.cancel(debouncePromise);
          debouncePromise = undefined;
        }
        timeout = timeout || debounce;
        debouncePromise = $timeout(function() {
          debouncePromise = undefined;
          _layoutImage();
        }, timeout);
      }

      function _layoutImage() {
        if (debouncePromise) {
          $timeout.cancel(debouncePromise);
          debouncePromise = undefined;
        }
        imageViewController.layout();
      }
      
    }
    };
  }
  ViewDirective.$inject = [ '$timeout', '$window', '$filter' ];

})(angular);