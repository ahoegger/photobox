(function(angular) {

  'use strict';

  var _controllerName = 'photobox.controller';

  angular.module('module.picturebox').controller(_controllerName, Controller);

  function Controller($state, $stateParams,$location,  logoutService) {
    var self = this;
     console.log('start controller ', _controllerName);
 
    (function _init() {
     
    })();
    
    // callback functions
    self.handleLogout = function(){
      logoutService.logout();
    };
    

  }
  
  Controller.$inject = [ '$state', '$stateParams', '$location', 'logout.service' ]
})(angular);