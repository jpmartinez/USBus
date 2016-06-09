/**
 * Created by Lucia on 6/5/2016.
 */
(function () {
    'use strict';
    angular.module('usbus').controller('EditServiceController', EditServiceController);
    EditServiceController.$inject = ['$scope', 'ServiceResource', '$mdDialog', 'serviceToEdit'];
    /* @ngInject */
    function EditServiceController($scope, ServiceResource, $mdDialog, serviceToEdit) {
        $scope.service = serviceToEdit;
        $scope.tenantId = 0;
        

        $scope.cancel = cancel;
        $scope.showAlert = showAlert;
/*
        $scope.route = ServiceResource.get({
            id: $scope.routeId,
            tenantId: $scope.tenantId
        });
*/

        function updateService(item) {
            ServiceResource.update({id: item.id, tenantId: $scope.tenantId}, item).$promise.then(function(data){
                showAlert('Exito!','Se ha editado su almac&eacute;n virtual de forma exitosa');
            }, function(error){
                showAlert('Error!','Ocurri&oacute; un error al procesar su petici&oacute;n');
            });
        }



        function showAlert(title,content) {
            $mdDialog
                .show($mdDialog
                    .alert()
                    .parent(
                        angular.element(document
                            .querySelector('#popupContainer')))
                    .clickOutsideToClose(true)
                    .title(title)
                    .content(content)
                    .ariaLabel('Alert Dialog Demo').ok('Cerrar'));

        };

        function cancel() {
            $mdDialog.cancel();
        };


    }
})();
