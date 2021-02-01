organization_id=-1

function uploadVacancyTypes(index) {
    jsRoutes.controllers.VacancyTypeController.getVacancyTypesByOrganization(index).ajax({
        success: function (data) {
            organization_id = index.toString();
            var main = document.getElementById("exampleFormControlSelect2");
            while (main.firstChild) {
                main.removeChild(main.firstChild);
            }
            var vacancyTypes = data.vacancyTypes
            for (var vacancy_type_no in vacancyTypes) {
                var option = document.createElement('option')
                option.textContent = vacancyTypes[vacancy_type_no].name
                option.value = vacancyTypes[vacancy_type_no].id
                main.appendChild(option)
            }
        },
        error: function (data) {
            console.log(data.responseText);
        }
    })
}


$('#autocomplete_organization').autocomplete({
    lookup: function (query, done) {
        // Do Ajax call or lookup locally, when done,
        // call the callback and pass your results:
        var sug = [];

        jsRoutes.controllers.OrganizationController.getOrganizations().ajax( {
            success : function (data) {
                var organizations = data.organizations;
                var i=0;
                for (var row_num in organizations) {
                    if(organizations[row_num].name.toLowerCase().indexOf(query.toLowerCase()) + 1) {
                        sug[i] = {"value": organizations[row_num].name, "data": organizations[row_num].id}
                        i++;
                    }
                }
                var result = {
                    suggestions: sug
                };

                done(result);
            },
            error: function (data) {
                console.log( data.responseText );
            }
        })
    },
    noCache: true,
    onSelect: function (suggestion) {
        if(parseInt(organization_id) === parseInt(suggestion.data)) return;
        uploadVacancyTypes(suggestion.data)
    }
});