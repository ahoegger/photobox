(function(angular) {

  'use strict';

  angular.module('module.picturebox.pictureview').config(configuration);

  function configuration($stateProvider, $urlRouterProvider) {
    console.log('add folder state to stateprovider.');

    var picutreState = {
        name: 'picture',
        url : '/picture?folderId?index',
        reloadOnSearch: false,
        views: {
            "main": {
              templateUrl : 'app/pictureView/pictureView.html',
              controller : 'pictureview.controller',
              controllerAs : 'pictureViewController'
            }
        }
    };

    
    $stateProvider.state(picutreState.name, picutreState);
  }

  configuration.$inject = [ '$stateProvider' ,'$urlRouterProvider'];

})(angular);