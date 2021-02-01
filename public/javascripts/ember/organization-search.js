
function reloadOrganizationSearchHandler() {
        document.querySelector('#search-form-organizations').addEventListener('submit', searchOrganizationHandler)
}

document.addEventListener('DOMContentLoaded', function () {
        reloadOrganizationSearchHandler()
});


function searchOrganizationHandler(e) {
        if (e.preventDefault) {
            e.preventDefault();
        }
        var query = document.getElementById('search-query-organizations').value

        if (query === '') {
            query = null;
        }

        searchOrganizationBy(query)
        return false
}

function searchOrganizationBy(query) {
        jsRoutes.controllers.OrganizationController.searchOrganizationsBy(query).ajax( {
            success : function (data) {
                $("#accordion_organization").html(data);
                reloadOrganizationSearchHandler()
            },
            error: function (data) {
                console.log( data.responseText);
            }
        })
}
