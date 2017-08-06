<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Home - Redemption</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Permanent+Marker|Droid+Sans"/>
    <link rel="stylesheet" href="css/common.css"/>
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

<#include "footer.inc.ftl">