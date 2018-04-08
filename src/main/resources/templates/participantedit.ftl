<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Participant - Redemption</title>

    <#include "stdmeta.inc.ftl">
    <#include "stdlinks.inc.ftl">
    <#include "internal.inc.ftl">
    <link rel="stylesheet" href="<@spring.url '/css/participantedit.css'/>"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col">
            <div id="participant-edit-form">
                <h1>Edit Participant</h1>
                <form action="<@spring.url '/api/v1/participant/${participant.id}'/>" method="post">
                    <div id="error-box" class="form-group invisible">
                        <p class="text-danger"></p>
                    </div>
                    <div class="form-group">
                        <label for="">First Name</label>
                        <input type="text" class="form-control" name="firstName" id="participant-first-name" placeholder="First Name" value="${participant.firstName}">
                    </div>
                    <div class="form-group">
                        <label for="">Last Name</label>
                        <input type="text" class="form-control" name="lastName" id="participant-last-name" placeholder="Last Name" value="${participant.lastName}">
                    </div>
                    <div class="form-group">
                        <label for="">Email</label>
                        <input type="text" class="form-control" name="email" id="participant-email" placeholder="Email" value="${participant.email}">
                    </div>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button class="btn btn-danger">Cancel</button>
                    <button class="btn btn-primary">Submit</button>
                </form>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col">
        <#include "copyright.inc.ftl">
        </div>
    </div>
</div>

<#include "stdimports.inc.ftl">
<script src="<@spring.url '/js/participantedit.js'/>"></script>
</body>
</html>