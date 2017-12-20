<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Participant - Redemption</title>

    <#include "stdmeta.inc.ftl">
    <#include "stdlinks.inc.ftl">
    <#include "internal.inc.ftl">
    <link rel="stylesheet" href="<@spring.url '/css/participantcreate.css'/>"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div id="participant-create-form">
                <h1>Create Participant</h1>
                <form action="<@spring.url '/api/v1/participant'/>" method="post">
                    <div id="error-box" class="form-group invisible">
                        <p class="text-danger"></p>
                    </div>
                    <div class="form-group">
                        <label for="">First Name</label>
                        <input type="text" class="form-control" name="firstName" id="participant-first-name" placeholder="First Name">
                    </div>
                    <div class="form-group">
                        <label for="">Last Name</label>
                        <input type="text" class="form-control" name="lastName" id="participant-last-name" placeholder="Last Name">
                    </div>
                    <div class="form-group">
                        <label for="">Email</label>
                        <input type="text" class="form-control" name="email" id="participant-email" placeholder="participant@somewhere.net">
                    </div>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button class="btn btn-danger">Cancel</button>
                    <button class="btn btn-primary">Submit</button>
                </form>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
        <#include "copyright.inc.ftl">
        </div>
    </div>
</div>

<#include "stdimports.inc.ftl">
<script src="<@spring.url '/js/participantcreate.js'/>"></script>
</body>
</html>