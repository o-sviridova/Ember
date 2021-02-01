function reloadNewChiefSearchHandler() {
        document.querySelector('#search-form-edit-chief').addEventListener('submit', searchNewChiefHandler)
        var new_chief = document.getElementById("select-new-chief");
        new_chief.addEventListener("click", function() {
           var new_chief_option = new_chief.options[new_chief.selectedIndex];
           console.log( new_chief_option.value );
           console.log( new_chief_option.text );
           clickNewChiefHandler(new_chief_option.value,new_chief_option.text);
        });
        new_chief.addEventListener("change", function() {
           var new_chief_option = new_chief.options[new_chief.selectedIndex];
           console.log( new_chief_option.value );
           console.log( new_chief_option.text );
           clickNewChiefHandler(new_chief_option.value,new_chief_option.text);
        });

}

function clickNewChiefHandler(value, text) {
        var hidden_id = document.querySelector('#new-chief-hidden-id');
        var new_chief_form = document.querySelector('#new_chief_form');
        hidden_id.value = value;
        new_chief_form.value = text;
}

document.addEventListener('DOMContentLoaded', function () {
        reloadNewChiefSearchHandler()
});


function searchNewChiefHandler(e) {
        console.log( 'hello' );
        if (e.preventDefault) {
            e.preventDefault();
        }
        var query = document.getElementById('search-query-edit-chief').value

        if (query === '') {
            query = null;
        }

        searchLoafers(query)
        return false
}

function addDefaultOption(){
    var old_hidden_id = document.querySelector('#old-value-hidden-id').value;
    var old_name = document.querySelector('#old-value-name').value;
    if (old_hidden_id === '') {
        var opt = document.createElement('option');
        opt.value = null;
        opt.text = '';
        var select = document.querySelector('#select-new-chief');
        select.appendChild(opt);
        return
    }

    var opt = document.createElement('option');
    opt.value = old_hidden_id;
    opt.text = old_name;
    var select = document.querySelector('#select-new-chief');
    select.appendChild(opt);
}

function searchLoafers(query) {
        jsRoutes.controllers.HabitantController.searchLoafers(query).ajax( {
            success : function (data) {
                $("#edit-chief").html(data);
                addDefaultOption()
                reloadNewChiefSearchHandler()
            },
            error: function (data) {
                console.log( data.responseText);
            }
        })
}

