/**
 * Created by Lucia on 6/5/2016.
 */
(function () {
    'use strict';
    angular.module('usbus').controller('CreateBranchController', CreateBranchController);
    CreateBranchController.$inject = ['$scope', 'localStorage', 'BranchResource', '$mdDialog', 'theme'];
    /* @ngInject */
    function CreateBranchController($scope, localStorage, BranchResource, $mdDialog, theme) {
        $scope.createBranch = createBranch;
        $scope.cancel = cancel;
        $scope.showAlert = showAlert;
        $scope.addWindow = addWindow;
        $scope.deleteWindow = deleteWindow;
        $scope.theme = theme;

        $scope.windows = [];

        if (typeof localStorage.getData('tenantId') !== 'undefined' && localStorage.getData('tenantId') != null) {
            $scope.tenantId = localStorage.getData('tenantId');
        }

        var token = null;
        if (localStorage.getData('token') != null && localStorage.getData('token') != '') {
            token = localStorage.getData('token');
        }

        function createBranch(branch) {
            if ($scope.windows != null && $scope.windows != []) {
                branch.windows = $scope.windows;
            }
            branch.tenantId = $scope.tenantId;
            branch.active = true;
            BranchResource.branches(token).save({
                tenantId: $scope.tenantId

            }, branch, function (resp) {
                console.log(resp);
                showAlert('Exito!', 'Se ha creado su sucursal de forma exitosa');
            }, function (error) {
                console.log(error);
                showAlert('Error!', 'Ocurrió un error al crear la sucursal');
            });
        }

        function addWindow() {
            var id = $scope.windows.length + 1;
            $scope.windows.push({id: id, tickets: false, parcels: false, active: true});
            console.log($scope.windows);
        }

        function deleteWindow(index) {
            $scope.windows.splice(index, 1);
            var i = 0;
            for (i = 0; i < $scope.windows.length; i++) {
                $scope.windows[i].index = i + 1;
            }
        }

        function showAlert(title, content) {
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
