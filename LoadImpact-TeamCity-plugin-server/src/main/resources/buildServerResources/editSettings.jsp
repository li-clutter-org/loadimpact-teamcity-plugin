<%--suppress CheckValidXmlInScriptTagBody --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ include file="/include.jsp" %>

<script type="text/javascript">
    function sendAjax(task) {
        var apiToken = $('apiToken').value;
        apiToken = apiToken.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
        if (!apiToken || apiToken.length == 0) {
            alert("The API token cannot be blank.");
            return false;
        }

        BS.ajaxRequest($('loadImpactSettings').action, {
            parameters: 'task=' + task + '&apiToken=' + apiToken,
            onComplete: function (res) {
                if (res.responseXML) {
                    $('refreshContainer').refresh();
                }
            }
        });

        return false;
    }
</script>

<bs:refreshable containerId="refreshContainer" pageUrl="${pageUrl}">
    <h2>Load Impact Settings</h2>

    <form id="loadImpactSettings" method="post" action="${actionUri}" onsubmit="">
        <p style="font-size: 95%;">
            Before you create any build steps with Load Impact tests, you need to enter your
            own API Token below. This token is used to logon to your account and perform
            the load tests.
            When you create a new Load Impact build step, the first that happens is that the
            names of or all your tests are fetched and presented in a drop-list, so you can
            choose which test to run.
        </p>
        <bs:messages key="error" className="attentionRed" />
        <bs:messages key="result" className="successMessage" />

        <table class="runnerFormTable">
            <tr>
                <th style="width: 6em;">API Token</th>
                <td>
                    <input type="text" id="apiToken" name="apiToken" value="<c:out value="${apiToken}"/>" size="70" max="100"/>
                    <input onclick="return sendAjax('checkToken');" type="button" name="checkToken" value="Check API Token" class="btn submitButton"/>
                    <span class="smallNote">Get the API Token from your accounts page. <em>(Consists of 64 HEX digits)</em></span>
                </td>
            </tr>
        </table>
        <div class="saveButtonsBlock">
            <input onclick="return sendAjax('saveSettings');" type="button" name="saveSettings" value="Save Settings" class="btn btn_primary submitButton"/>
        </div>
    </form>
</bs:refreshable>
