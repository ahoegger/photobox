'use strict';

/**
 * @ngdoc function
 * @name clientApp.controller:AboutCtrl
 * @description # AboutCtrl Controller of the clientApp
 */
var modContPicture = angular.module('modContPicture', [  'modServicesResource' ]);

var pictureController = modContPicture.controller('pictureController', [ '$scope','$rootScope',  
  function($scope,$rootScope, $routeParams) {
    if($rootScope.picture){
      $scope.picture = $rootScope.picture;
      console.log('found root scope picture with next: '+$scope.picture.nextResource);
      delete $rootScope.picture;
    }else{
      console.log('load picture from server...');
      // load picture
    }
} ]);
