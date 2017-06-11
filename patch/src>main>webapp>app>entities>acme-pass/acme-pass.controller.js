(function () {
    'use strict';

    angular
        .module('acmeApp')
        .controller('ACMEPassController', ACMEPassController);

    ACMEPassController.$inject = ['$scope', '$state', 'ACMEPass', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ACMEPassController($scope, $state, ACMEPass, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.toggleVisible = toggleVisible;

        loadAll();

        function loadAll() {
            ACMEPass.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.acmePasses = data.map(function(item) {
                    item.visible = false;
                    return item;
                });
                vm.page = pagingParams.page;
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function toggleVisible(id) {
            vm.acmePasses = vm.acmePasses.map(function(item) {
                if(item.id === id) item.visible = !item.visible;
                return item;
            })
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }
    }
})();
