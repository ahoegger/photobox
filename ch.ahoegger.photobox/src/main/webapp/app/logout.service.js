(function (angular) {
    'use strict';

    angular.module('module.picturebox')
        .service('logout.service', LogoutService);
    
    function LogoutService($http, $q){
      var GLOBAL_TIMEOUT = 5000;
      var GLOBAL_JSON_TYPE = 'application/json';
      
      
      
      var logout = function(image){
        var url = 'rest/logout';
        console.log('call postImage for '+url+'.');
        var deferred = $q.defer();
        $http.post(url, image,
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
        logout: logout
      };
    }
    LogoutService.$inject = ['$http', '$q'];
    
})(angular);