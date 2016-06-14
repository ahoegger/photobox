(function(angular) {

  'use strict';

  var _controllerName = 'photobox.controller';

  angular.module('module.picturebox').controller(_controllerName, Controller);

  function Controller($scope, $state, $stateParams, $location, overlayService, logoutService) {
    var self = this;
    console.log('start controller ', _controllerName);

    (function _init() {

    })();

    // callback functions
    self.handleLogout = function() {
      var overlayId;
      var yesCallback = function() {
        console.log('do logout!!!');
        overlayService.close(overlayId);
        logoutService.logout().then(function() {
          location.reload();
        }, function() {
          location.reload();
        });
      };
      var noCallback = function() {
        overlayService.close(overlayId);
      };
      overlayId = overlayService.showConfirmation('Are U sure to logout?', yesCallback, noCallback, $scope, [ 'logout-confirmation-overlay' ]);
    };

  }

  Controller.$inject = [ '$scope', '$state', '$stateParams', '$location', 'overlay.service', 'logout.service' ]
})(angular);