<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>webblog</title>
    <style>
        .wb-wrap { max-width: 1100px; margin: 0 auto; padding: 0 12px; box-sizing: border-box; }
        .wb-row { display: flex; flex-wrap: wrap; gap: 12px; }
        .wb-menu { flex: 1 1 200px; min-width: 180px; }
        .wb-body { flex: 3 1 400px; min-width: 0; }
        @media (max-width: 720px) {
            .wb-row { flex-direction: column; }
        }
        table.wb-table { width: 100%; border-collapse: collapse; }
        table.wb-table th, table.wb-table td { border: 1px solid #ccc; padding: 8px; word-break: break-word; }
    </style>
</head>
<body>
<div class="wb-wrap">
    <div>
        <tiles:insertAttribute name="header"/>
    </div>
    <hr/>
    <div class="wb-row">
        <div class="wb-menu">
            <tiles:insertAttribute name="menu"/>
        </div>
        <div class="wb-body">
            <tiles:insertAttribute name="body"/>
        </div>
    </div>
    <hr/>
    <div>
        <tiles:insertAttribute name="footer"/>
    </div>
</div>
</body>
</html>