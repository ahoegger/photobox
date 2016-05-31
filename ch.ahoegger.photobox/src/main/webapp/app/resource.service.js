(function (angular) {
    'use strict';

    angular.module('module.picturebox')
        .service('resource.service', ResourceService);
    
    function ResourceService($http, $q){
      var GLOBAL_TIMEOUT = 5000;
      var GLOBAL_JSON_TYPE = 'application/json';
      
      var getResourcesInternal = function(url){

        console.log('call resource for '+url+'.');
        var deferred = $q.defer();
        $http.get(url,
            {
                headers: {'Accept': GLOBAL_JSON_TYPE},
                timeout: GLOBAL_TIMEOUT
            }
        )
        .then(function(result){
          deferred.resolve(result.data);
        },
        function(error){
          deferred.resolve(undefined);
        });
        return deferred.promise;
      };
      
      var getFolder = function(id){
        var url = 'rest/api/';
        if(angular.isDefined(id)){
          url +=id;
        }
        console.log('call getFolder for '+url+'.');
        var deferred = $q.defer();
        $http.get(url,
            {
                headers: {'Accept': GLOBAL_JSON_TYPE},
                timeout: GLOBAL_TIMEOUT
            }
        )
        .then(function(result){
          deferred.resolve(result.data);
        },
        function(error){
          deferred.resolve(undefined);
        });
        return deferred.promise;
      }

      return {
          getResources: getResourcesInternal,
          getFolder: getFolder
      };
    }
    ResourceService.$inject = ['$http', '$q'];
    
})(angular);