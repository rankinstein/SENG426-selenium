(function () {
	'use strict';

	angular
		.module('acmeApp')
		.controller('ACMEPassDialogController', ACMEPassDialogController);

	ACMEPassDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModal', '$uibModalInstance', 'entity', 'ACMEPass', 'User'];

	function ACMEPassDialogController($timeout, $scope, $stateParams, $uibModal, $uibModalInstance, entity, ACMEPass, User) {
		var vm = this;

		vm.acmePass = entity;
		vm.datePickerOpenStatus = {};
		vm.openCalendar = openCalendar;
		vm.openPwdGenModal = openPwdGenModal;
		vm.save = save;
		vm.clear = clear;
		vm.users = User.query();
		vm.toggleVisible = toggleVisble;
		vm.pwdVisible = false;

		$timeout(function () {
			angular.element('.form-group:eq(1)>input').focus();
		});

		function openPwdGenModal() {
			$uibModal.open({
				templateUrl: 'app/entities/acme-pass/acme-pass-pwd-gen.html',
				controller: 'ACMEPassPwdGenController',
				controllerAs: 'vm',
				backdrop: 'static',
				size: 'sm'
			}).result.then(function (password) {
				vm.acmePass.password = password;
			}, function () {
			});
		}

		function clear() {
			$uibModalInstance.dismiss('cancel');
		}

		function save() {
			vm.isSaving = true;
			if (vm.acmePass.id !== null) {
				ACMEPass.update(vm.acmePass, onSaveSuccess, onSaveError);
			} else {
				ACMEPass.save(vm.acmePass, onSaveSuccess, onSaveError);
			}
		}

		function onSaveSuccess(result) {
			$scope.$emit('acmeApp:ACMEPassUpdate', result);
			$uibModalInstance.close(result);
			vm.isSaving = false;
		}

		function onSaveError() {
			vm.isSaving = false;
		}

		vm.datePickerOpenStatus.createdDate = false;
		vm.datePickerOpenStatus.lastModifiedDate = false;

		function openCalendar(date) {
			vm.datePickerOpenStatus[date] = true;
		}

		function toggleVisble() {
            vm.pwdVisible = !vm.pwdVisible;
		}
	}
})();
