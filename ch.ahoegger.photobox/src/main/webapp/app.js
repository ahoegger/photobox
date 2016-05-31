(function(angular){
  'use strict';

  /**
   * @ngdoc overview
   * @name clientApp
   * @description
   * # clientApp
   *
   * Main module of the application.
   */
  angular
    .module('photoboxApp', [
      'module.picturebox',
      'modulePictureboxComponents',
      'module.picturebox.folderview',
      'ngAnimate',
      'ngCookies',
      'ngResource',
      'ui.router',
      'ngSanitize',
      'ngTouch'
      
    ])
    .config(_config);
  
  function _config($stateProvider, $urlRouterProvider){
    console.log('CONFIG start:');
    $urlRouterProvider.otherwise("/folder")
  }
  
  _config.$inject = ['$stateProvider', '$urlRouterProvider'];
  
//    .config(function ($routeProvider) {
//      $routeProvider
//        .when('/', {
//          templateUrl: 'views/main.html',
//          controller: 'photoListController'
//        })
//        .when('/picture/:path', {
//          templateUrl: 'views/picture.html',
//          controller: 'pictureController',
//          reloadOnSearch: false
//        })
//        .otherwise({
//          redirectTo: '/'
//        });
//    });  
})(angular);


