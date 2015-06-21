<%@ page import="com.loadimpact.teamcity_plugin.Debug" %>
<%@ page import="java.util.Enumeration" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<%
    Debug debug = new Debug("LoadTestSummaryTab.jsp");
//    Enumeration<String> names = request.getAttributeNames();
//    while (names.hasMoreElements()) {
//        String key = names.nextElement();
//        debug.print("model: %s: %s", key, request.getAttribute(key));
//    }
%>

<div id="load-impact">
    <img src="${teamcityPluginResourcesPath}image/loadimpact-logo-300x50.png" width="300">

    <c:if test="${noResults}">
        <p>No results to show</p>
    </c:if>

    <c:if test="${hasResults}">
        <h2>Load Test Summary</h2>
        <table>
            <tr>
                <th>Name</th>
                <td>${testName} (ID ${testId})</td>
            </tr>
            <c:if test="${status != 'success'}">
                <tr>
                    <th>Failure Reason</th>
                    <td class="error">${reason}</td>
                </tr>
            </c:if>
            <c:if test="${not empty targetUrl}">
                <tr>
                    <th>Target</th>
                    <td><a href="${targetUrl}">${targetUrl}</a></td>
                </tr>
            </c:if>
            <tr>
                <th>Elapsed Time</th>
                <td>${elapsedTime}</td>
            </tr>
            <c:if test="${not empty responseTime}">
                <tr>
                    <th>User Load Time</th>
                    <td>${responseTime}</td>
                </tr>
            </c:if>
            <tr>
                <th>Clients</th>
                <td>${clientsCount}</td>
            </tr>
            <tr>
                <th>Requests</th>
                <td>${requestsCount}</td>
            </tr>
            <tr>
                <th>Bandwidth</th>
                <td>${bandwidth}</td>
            </tr>
            <tr>
                <th>Test Results</th>
                <td>
                    <c:if test="${empty resultUrl}">
                        <a href="https://app.loadimpact.com/account/login" target="_blank" class="btn">Login to Load
                                                                                                       Impact</a>
                    </c:if>
                    <c:if test="${not empty resultUrl}">
                        <a href="${resultUrl}" target="_blank">View the LoadImpact Results Page</a>
                    </c:if>
                </td>
            </tr>
        </table>
    </c:if>
</div>
