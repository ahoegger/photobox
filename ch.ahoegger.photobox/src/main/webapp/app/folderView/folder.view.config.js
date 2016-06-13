(function(angular) {

  'use strict';

  angular.module('module.picturebox.folderview').config(configuration);

  function configuration($stateProvider, $urlRouterProvider) {
    console.log('add folder state to stateprovider.');

    var folderState = {
        name: 'folderState',
        url : '/folder?id',
        params: {
          itemId: undefined,
        },
        views: {
            "main": {
              templateUrl : 'app/folderView/folder.view.html',
              controller : 'folderview.controller',
              controllerAs : 'folderViewController'
            }
        }
    };

    
    $stateProvider.state(folderState.name, folderState);
    // default
    $urlRouterProvider.otherwise("/folder")
  }

  configuration.$inject = [ '$stateProvider' ,'$urlRouterProvider'];

})(angular);