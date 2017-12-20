$(document).ready(function() {
    $("button.btn-primary").click(function() {
        var form = $("#participant-create-form form");

        $.ajax({
            url: form.attr("action"),
            method: "POST",
            data: form.serialize()
        }).done(function() {
            window.location = "/dashboard";
        }).fail(function(jqXHR) {
            var errorBox = $("#error-box");

            errorBox.removeClass("invisible");
            errorBox.find("p").html(jqXHR.responseJSON.message.split("\n").join("<br/>"));
        });

        event.preventDefault();
        return false;
    });

    $("button.btn-danger").click(function() {
        window.location = "/dashboard";

        event.preventDefault();
        return false;
    });
});