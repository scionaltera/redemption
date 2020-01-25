<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Home - Redemption</title>

    <#include "stdmeta.inc.ftlh">
    <#include "stdlinks.inc.ftlh">
    <link rel="stylesheet" href="<@spring.url '/css/index.css'/>"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div id="title" class="col text-center">
            <span>Redemption</span>
        </div>
    </div>
    <div class="row">
        <div class="col-2 offset-5">
            <form id="login-form" action="<@spring.url '/login'/>" method="post">
            <#if message??>
                <div class="form-group">
                    <p class="text-danger">${message}</p>
                </div>
            </#if>
                <div class="form-group">
                    <input type="text" name="username" id="username-input" placeholder="Username"/>
                </div>
                <div class="form-group">
                    <input type="password" name="password" id="password-input" placeholder="Password"/>
                </div>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn btn-primary">Sign In</button>
            </form>
        </div>
    </div>
</div>

<#include "copyright.inc.ftlh">
<#include "stdimports.inc.ftlh">
</body>
</html>