(function(angular) {

  'use strict';

  angular.module('modulePictureboxComponents').directive('photoboxOverlay', OverlayDirective);

  function OverlayDirective($timeout, window, $filter) {
    return {
    restrict : 'E',
    templateUrl : 'app/components/overlay/overlayTemplate.html',
    scope : {
      templateUrl : '@'
    },
    link : function($scope, $element, $attrs) {
      $scope.model  = $scope.$parent.model;;

    }
    };
  }
  OverlayDirective.$inject = [ '$timeout', '$window', '$filter' ];

})(angular);