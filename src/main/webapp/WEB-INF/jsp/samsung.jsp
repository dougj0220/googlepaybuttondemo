<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
	<meta charset="UTF-8">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/item.css">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/samsung.js" defer></script>
	<title>Purchase ${name}</title>
</head>

<body>
    <div id="header">
        <h1>Store</h1>
    </div>
    <div id="main">
        <div id="store" class="row">
            <div class="column-left">
                <img alt="Image" src="${imgLink}">
            </div>
            <div class="column-right">
                <h1>${name}</h1>
                <h3>$${price}</h3>
                <img id="spaybutton" alt="SAMSUNG PAY" src="${pageContext.request.contextPath}/images/samsung-pay.inline.svg" />
                <!--
                <div id="gateway">
                    <p>Select which payment gateway to use:</p>
                    <input type="radio" id="cybersource" name="gatewaySelection" value="cybersource" checked>
                    <label for="cybersouce">CyberSource</label><br>
                    <input type="radio" id="vantiv" name="gatewaySelection" value="vantiv">
                    <label for="vantiv">Vantiv</label><br>
                    <button type="button" onclick="setDemoGateway()">Apply</button>
                </div>
                -->
            </div>
        </div>

        <div id="results" style="margin-top: 50px;">
            <h4 id="samsungPayHeader"></h4>
            <div id="samsungPayResponse"></div>
            <div id="cyberSourceResults" style="display:none;">
                <h4 id="cyberSourceHeader"></h4>
                <pre id="cyberSourceResponse"></pre>
            </div>
            <!--
            <div id="vantivResults" style="display:none;">
                <h4 id="vantivHeader"></h4>
                <pre id="vantivResponse"></pre>
            </div>
            -->
        </div>
    </div>
</body>

</html>