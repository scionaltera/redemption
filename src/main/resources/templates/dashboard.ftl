<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Dashboard - Redemption</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Droid+Sans"/>
    <link rel="stylesheet" href="css/index.css"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <form action="<@spring.url '/logout'/>" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn btn-danger">Sign Out</button>
            </form>
        </div>
    </div>
</div>

<#include "footer.inc.ftl">