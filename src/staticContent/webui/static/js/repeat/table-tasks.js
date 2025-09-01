function registerTableTasks() {
    registerCells();

    $('#modal-task-name-save').click(newNameOnClick);
}

function registerCells() {
    utils_TableHighlight("table-tasks");
    utils_TableOnclick("table-tasks", tableTaskOnClick);

    /* Get all rows from your 'table' but not the first one 
    * that includes headers. */
    var table = document.getElementById("table-tasks");
    var rows = $('#table-tasks').find("tr").not(":first");
    if (rows.length > 0) {
        var lastCellIndex = table.rows[1].cells.length - 1;
        table.rows[1].cells[lastCellIndex].click();
    }
}

function tableTaskOnClick(cell, row, col) {
    var isFocused = utils_GetTableSelectedRowIndex("table-tasks") == row;

    if (!isFocused || col != 1) {
        fillSourceForTask(getIdForTaskIndex(row));
    }

    if (!isFocused) {
        return;
    }

    if (col == 0) { // Name.
        $("#modal-task-name-row").val(row);
        $("#new-task-name").val(cell.textContent);
        $("#modal-task-name").modal();
        utils_FocusInputForModal("new-task-name");
    } 
    if (col == 1) { // Activation.
        window.location.assign("/tasks/details?id=" + getIdForTaskIndex(row));
    }
    if (col == 2) { // Enable/disable.
        $.post("/internals/toggle/task-enabled", JSON.stringify({task: getIdForTaskIndex(row)}), function(data) {
            refreshTasksWithDataAndIndex(data, row);
        }).fail(function(response) {
            alert('Error toggling state: ' + response.responseText);
        });
    }
}

function newNameOnClick(e) {
    var row = $('#modal-task-name-row').val();
    var newName = $('#new-task-name').val();

    $.post("/internals/modify/task-name", JSON.stringify({task: getIdForTaskIndex(row), name: newName}), function(data) {
        refreshTasksWithDataAndIndex(data, row);
    }).fail(function(response) {
        alert('Error changing name: ' + response.responseText);
    });
}

function refreshTasksWithData(data) {
    var index = utils_GetTableSelectedRowIndex("table-tasks");
    refreshTasksWithDataAndIndex(data, index);
}

function refreshTasksWithDataAndIndex(data, index) {
    _rememberLastScrollPosition();
    var tableElement = $("#table-tasks-container");
    tableElement.html(data);
    registerCells();
    utils_SetTableSelectedIndex("table-tasks", index);
    _setLastToLastScrollPosition();
    _adjustTaskTableHeight();
}

function getIdForTaskIndex(index) {
    return $("div[id^=\'div-task-id-\']").eq(index)[0].id.substring("div-task-id-".length);
}

var _lastScrollPosition = 0;

function _rememberLastScrollPosition() {
    _lastScrollPosition = $("#table-tasks-body")[0].scrollTop;
}

function _setLastToLastScrollPosition() {
    $("#table-tasks-body")[0].scrollTop = _lastScrollPosition;
}

function _adjustTaskTableHeight() {
    var isExpanded = $("#button-task-table-expand-shrink").hasClass("repeat-btn-shrink");
    if (isExpanded) {
        _setTaskTableHeightPercentViewHeight(100);
    } else {
        _setTaskTableHeightPercentViewHeight(25);
    }
}

function toggleTaskTableExpandShrinkAction(e) {
    var body = document.querySelectorAll("#table-tasks-body")[0];
    var currentPercent = _taskTableHeightPercent(body);
    if (currentPercent > 50) {
        $("#button-task-table-expand-shrink").addClass("repeat-btn-expand");
        $("#button-task-table-expand-shrink").removeClass("repeat-btn-shrink");
        _setTaskTableHeightPercentViewHeight(25);
    } else {
        $("#button-task-table-expand-shrink").removeClass("repeat-btn-expand");
        $("#button-task-table-expand-shrink").addClass("repeat-btn-shrink");
        _setTaskTableHeightPercentViewHeight(100);
    }
}

function _setTaskTableHeightPercentViewHeight(percent) {
    var body = document.querySelectorAll('#table-tasks-body')[0];
    body.style.setProperty("height",  percent + "vh");
}

function _taskTableHeightPercent(bodyElement) {
    var heightPixel = _taskTableHeightPixel(bodyElement);
    return Math.round(_computeViewHeightPercent(heightPixel));
}

function _taskTableHeightPixel(bodyElement) {
    var heightPixel = getComputedStyle(bodyElement).getPropertyValue("height");
    var heightFloatString = heightPixel.substring(0, heightPixel.length - 2);
    return parseFloat(heightFloatString);
}

function _computeViewHeightPercent(heightPx) {
  var h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);
  return (heightPx / h) * 100;
}

function _computeViewHeightPx(percent) {
  var h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);
  return (percent * h) / 100;
}
