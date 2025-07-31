function registerTaskGroupsDropdownEvents() {
    var groups = $("#task-groups-dropdown").find("li");
    groups.click(function() {
        var groupId = $(this)[0].id;
        selectTaskGroupFromDropdown(groupId);
    });
}

function selectTaskGroupFromDropdown(groupId) {
    $.post("/internals/action/switch-task-group", JSON.stringify({group: groupId, render: "tasks"}), function(data) {
        refreshTaskGroupDropDown();
        refreshTasksWithData(data);
    }).fail(function(response) {
        alert('Error sending request to switch task group: ' + response.responseText);
    });
}

function refreshTaskGroupDropDown() {
    $.get("/internals/get/rendered-task-groups-dropdown", function(data) {
        $("#task-groups-dropdown-container").html(data);
        registerTaskGroupsDropdownEvents();
    }).fail(function(response) {
        alert('Error sending request to get task group drop down: ' + response.responseText);
    });
}