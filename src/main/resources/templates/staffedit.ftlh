<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Staff - Redemption</title>

    <#include "stdmeta.inc.ftlh">
    <#include "stdlinks.inc.ftlh">
    <#include "internal.inc.ftlh">
    <link rel="stylesheet" href="<@spring.url '/css/staffedit.css'/>"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col">
            <div id="staff-edit-form">
                <h1>Edit Staff</h1>
                <form action="<@spring.url '/api/v1/staff/${staff.id}'/>" method="post">
                    <div id="error-box" class="form-group invisible">
                        <p class="text-danger"></p>
                    </div>
                    <div class="form-group">
                        <label for="">Username</label>
                        <input type="text" class="form-control" name="username" id="staff-username" placeholder="Username" value="${staff.username}">
                    </div>
                    <div class="form-group">
                        <label for="">Password</label>
                        <input type="password" class="form-control" name="password" id="staff-password" placeholder="Password">
                    </div>
                <#list permissions as permission>
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" name="permissions" value="${permission.unique}" id="perm-${permission.unique}" ${staff.permissions?seq_contains(permission)?string("checked='true'", "")}> ${permission.description}
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
        <div class="col">
        <#include "copyright.inc.ftlh">
        </div>
    </div>
</div>

<#include "stdimports.inc.ftlh">
<script src="<@spring.url '/js/staffedit.js'/>"></script>
</body>
</html>