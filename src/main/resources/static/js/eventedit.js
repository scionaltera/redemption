$(document).ready(function() {
    $("#event-edit-form button.btn-primary").click(function() {
        var form = $("#event-edit-form form");

        $.ajax({
            url: form.attr("action"),
            method: "POST",
            data: form.serialize()
        }).done(function() {
            window.location = "/dashboard";
        }).fail(function(jqXHR) {
            var errorBox = $("#event-error-box");

            errorBox.removeClass("invisible");
            errorBox.find("p").html(jqXHR.responseJSON.message.split("\n").join("<br/>"));
        });

        event.preventDefault();
        return false;
    });

    $("#event-edit-form button.btn-danger").click(function() {
        window.location = "/dashboard";

        event.preventDefault();
        return false;
    });

    $("#event-participants-edit-form button.btn-primary").click(function() {
        var form = $("#event-participants-edit-form form");
        var submittedEmail = $("#event-participant").val();

        $.ajax({
            url: form.attr("action"),
            method: "POST",
            data: form.serialize()
        }).done(function(event) {
            var matches = event.participants.filter(p => p.email === submittedEmail);

            if (matches.length > 0) {
                var participant = matches[0];
                var csrfToken = $("meta[name='_csrf']").attr("content");

                var row = $("#event-participant-table tr:last").after(`<tr><td>${participant.lastName}, ${participant.firstName} (${participant.email})</td><td><button data-csrf-param="_csrf" data-csrf-token="${csrfToken}" data-participant-id="${participant.id}" type="submit" class="btn btn-danger">Remove</button></td></tr>`);
                row.next().find("button").click(removeButtonClick);
            }
        }).fail(function(jqXHR) {
            var errorBox = $("#event-participant-error-box");

            errorBox.removeClass("invisible");
            errorBox.find("p").html(jqXHR.responseJSON.message.split("\n").join("<br/>"));
        });

        event.preventDefault();
        return false;
    });

    $("#event-participants-edit-form button.btn-danger").click(removeButtonClick);
});

var removeButtonClick = function() {
    var form = $("#event-participants-edit-form form");
    var participantId = $(this)[0].dataset.participantId;
    var payload = {};

    payload[$(this)[0].dataset.csrfParam] = $(this)[0].dataset.csrfToken;

    $.ajax({
        url: form.attr("action") + "/" + participantId,
        method: "DELETE",
        data: payload
    }).done(function() {
        $("#event-participant-table tr").has("button[data-participant-id=" + participantId + "]").remove();
    }).fail(function(jqXHR) {
        var errorBox = $("#event-participant-error-box");

        errorBox.removeClass("invisible");
        errorBox.find("p").html(jqXHR.responseJSON.message.split("\n").join("<br/>"));
    });

    event.preventDefault();
    return false;
};