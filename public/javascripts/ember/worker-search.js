
function reloadSearchHandlers() {
        document.querySelector('#search-form').addEventListener('submit', searchHandler)
}

document.addEventListener('DOMContentLoaded', function () {
        reloadSearchHandlers()
});


function searchHandler(e) {
        if (e.preventDefault) {
            e.preventDefault();
        }
        var query = document.getElementById('search-worker-query').value

        if (query === '') {
            query = null;
        }

        var selectedCompany = document.getElementById('organization-id').value
        var workingCapacity = null

        searchHabitantsBy(query, selectedCompany, workingCapacity)
        return false
}

function searchHabitantsBy(query, selectedCompany, workingCapacity) {
        jsRoutes.controllers.HabitantController.searchHabitantsBy(query, selectedCompany, workingCapacity).ajax( {
            success : function (data) {
                $("#worker-results").html(data);
                reloadSearchHandlers()
            },
            error: function (data) {
                console.log( data.responseText);
            }
        })
}
