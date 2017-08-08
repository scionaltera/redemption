<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Home - Redemption</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <#include "stdlinks.inc.ftl">
    <link rel="stylesheet" href="css/index.css"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div id="title" class="col-md-12 text-center">
            <span class="visible-lg-block">Redemption</span>
            <span class="visible-md-block">Redemption</span>
            <span class="visible-sm-block">Redemption</span>
            <span class="visible-xs-block">Redemption</span>
        </div>
    </div>
    <div class="row">
        <div class="col-md-2 col-md-offset-5">
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

<#include "copyright.inc.ftl">

<#include "stdimports.inc.ftl">
</body>
</html>