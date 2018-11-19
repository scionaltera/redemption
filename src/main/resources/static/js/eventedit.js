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

                $("#event-participant-table tr:last").after(`<tr><td>${participant.lastName}, ${participant.firstName} (${participant.email})</td><td><button type="submit" class="btn btn-danger" disabled>Remove</button></td></tr>`);
            }
        }).fail(function(jqXHR) {
            var errorBox = $("#event-participant-error-box");

            errorBox.removeClass("invisible");
            errorBox.find("p").html(jqXHR.responseJSON.message.split("\n").join("<br/>"));
        });

        event.preventDefault();
        return false;
    });
});