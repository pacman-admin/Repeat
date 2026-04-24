<#macro fragment>
<#if task.hasStatistics == "true">
    <h1> Statistics </h1>
    <div class="row col-xs-12">
        <p><b>Created:</b> ${task.statistics.created}</p>
        <p><b>Last used:</b> ${task.statistics.lastUsed}</p>
        <p><b>Total execution time:</b> ${task.statistics.totalExecutionTime}</p>
        <p><b>Average execution time (success only):</b> ${task.statistics.averageExecutionTime}</p>
    </div>
</#if>
</#macro>

<@fragment/>