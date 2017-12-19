<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Staff - Redemption</title>

    <#include "stdmeta.inc.ftl">
    <#include "stdlinks.inc.ftl">
    <#include "internal.inc.ftl">
    <link rel="stylesheet" href="<@spring.url '/css/staffcreate.css'/>"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div id="staff-create-form">
                <h1>Create Staff</h1>
                <form action="<@spring.url '/api/v1/staff'/>" method="post">
                    <div id="error-box" class="form-group invisible">
                        <p class="text-danger"></p>
                    </div>
                    <div class="form-group">
                        <label for="">Username</label>
                        <input type="text" class="form-control" name="username" id="staff-username" placeholder="Username"">
                    </div>
                    <div class="form-group">
                        <label for="">Password</label>
                        <input type="password" class="form-control" name="password" id="staff-password" placeholder="Password">
                    </div>
                <#list permissions as permission>
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" name="permissions" value="${permission.unique}" id="perm-${permission.unique}"> ${permission.description}
                        </label>
                    </div>
                </#list>
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
<script src="<@spring.url '/js/staffcreate.js'/>"></script>
</body>
</html>