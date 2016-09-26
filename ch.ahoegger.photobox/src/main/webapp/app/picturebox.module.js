(function(angular) {
  'use strict';

  var app = angular
      .module('module.picturebox', [ 'modulePictureboxComponents', 'module.picturebox.folderview', 'ngAnimate', 'ngCookies', 'ngResource', 'ui.router', 'ngSanitize', 'ngTouch', 'smoothScroll' ]);

  

  app.factory('timeoutHttpIntercept', function() {
    return {
      'request' : function(config) {
        config.timeout = 10000;
        config.cache = true;
        return config;
      }
    };
  });
  
  app.config( HttpInterceptor);

  function HttpInterceptor($httpProvider) {
    $httpProvider.interceptors.push('timeoutHttpIntercept');
    $httpProvider.defaults.cache = true;
  }
  
  HttpInterceptor.$inject = [ '$httpProvider' ];

})(angular);