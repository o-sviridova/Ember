
function reloadSearchHandlers() {
        document.querySelector('#search-form').addEventListener('submit', searchHandler)
}

document.addEventListener('DOMContentLoaded', function () {
        reloadSearchHandlers()
});


function searchHandler(e) {
        const searchByCompanyOption = 'company'
        console.log( 'hello' );
        if (e.preventDefault) {
            e.preventDefault();
        }
        var query = document.getElementById('search-query').value

        console.log( 'hello2' );
        var select = document.getElementById('search-type-select');
        var selectedCompany = select.options[select.selectedIndex].value;

        var select = document.getElementById('wc-select');
        var workingCapacity = select.options[select.selectedIndex].value;

        if (query === '') {
            query = null;
        }

        if (selectedCompany === '') {
            selectedCompany = null;
        }

        if (workingCapacity === '') {
            workingCapacity = null;
        }

        searchHabitantsBy(query, selectedCompany, workingCapacity)
        return false
}

function searchHabitantsBy(query, selectedCompany, workingCapacity) {
        jsRoutes.controllers.HabitantController.searchHabitantsBy(query, selectedCompany, workingCapacity).ajax( {
            success : function (data) {
                $("#accordion").html(data);
                reloadSearchHandlers()
            },
            error: function (data) {
                console.log( data.responseText);
            }
        })
}

function searchByCompany(selectedCompany) {
        jsRoutes.controllers.HabitantController.searchByOrganization(selectedCompany).ajax( {
            success : function (data) {
                $("#accordion").html(data);
                reloadSearchHandlers()
            },
            error: function (data) {
                console.log( data.responseText);
            }
        })
}