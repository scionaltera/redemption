$(document).ready(function() {
    $.getJSON("/api/v1/audit?count=15", function(data) {
        var messages = [];

        $.each(data, function() {
            messages.push("<li>" + new Date(this.timestamp) + " : " + shorten(this.username) + " : " + this.remoteAddress + " : " + this.message + "</li>")
        });

        $("<ul/>", {
            "id": "log-box-items",
            html: messages.join("")
        }).appendTo("#log-box-content");
    });
});

function shorten(str) {
    if (str.length > 20) {
        return str.substring(0, 20) + '&hellip;';
    } else {
        return str;
    }
}