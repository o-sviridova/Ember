activeButtonClass="list-group-item list-group-item-action active"
inactiveButtonClass="list-group-item list-group-item-action active"

function changeActiveButton(vacancyId) {
    var newActiveButton = "#button-open-vacancy-" + vacancyId
    $("#switcher-open-vacancies").children().removeClass('active');
    $(newActiveButton).addClass('active');
}

function searchCandidatesByVacancy(vacancyId) {
        jsRoutes.controllers.VacancyController.searchCandidatesByVacancy(vacancyId).ajax( {
            success : function (data) {
                $("#container_for_candidates").html(data);
            },
            error: function (data) {
                console.log(data.responseText);
            }
        })
}