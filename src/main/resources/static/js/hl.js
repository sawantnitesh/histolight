var hl_NAMESPACE = "http://localhost:8080/"

function init() {

    loadTables();

    addTablesDropdownListener();

    addRunButtonListener();

    addResetButtonListener();

    setOutputDiv();

    addUpdateValueListener();

    setGoogleCharts();
}


function loadTables() {

    $(document.body).loading();

    $.ajax({
        url: hl_NAMESPACE + "getAllTables",
        method: "GET",
        success: function (result) {

            for (i = 0; i < result.length; i++) {
                $('#tables').append($('<option>', {
                    value: result[i],
                    text: result[i]
                }));
            }

            $(document.body).loading('stop');
        }
    });
}

function addTablesDropdownListener() {

    $('#tables').on('change', function () {

        if (this.value == -1) {

            resetColumnsDropdown();

        } else {

            var jsonData = JSON.stringify({
                "table": this.value
            });

            $.ajax({
                url: hl_NAMESPACE + "getAllColumns",
                method: "POST",
                contentType: "application/json",
                dataType: "json",
                data: jsonData,
                success: function (result) {

                    resetColumnsDropdown();

                    for (i = 0; i < result.length; i++) {
                        $('#columns').append($('<option>', {
                            value: result[i],
                            text: result[i]
                        }));
                    }

                }
            });
        }

    });
}

function addRunButtonListener() {

    $('#go_button').on('click', function () {

        $("#outputDiv").loading();

        var tableName = $("#tables").val();
        var columnName = $("#columns").val();
        var classes = $("#classes").val();

        clearOutputDiv();

        var errorCondition = false;
        var errorMessage = null;
        if (tableName === "-1" || columnName === "-1" || classes === "") {
            errorCondition = true;
            errorMessage = 'Select All Inputs.';
        } else if (parseInt(classes) < 1) {
            errorCondition = true;
            errorMessage = 'Select classes greater than 0.';
        }
        if (errorCondition) {
            $("#errorTextDiv").append("<span class='errorText'>" + errorMessage + "</span>");
            $("#outputDiv").loading('stop');
            return;
        }

        var jsonData = JSON.stringify({
            "table": tableName,
            "column": columnName,
            "classes": classes
        });

        $.ajax({
            url: hl_NAMESPACE + "createDataQualityReport",
            method: "POST",
            contentType: "application/json",
            dataType: "json",
            data: jsonData,
            success: function (result) {

                if (result.appError) {

                    $("#errorTextDiv").append("<span class='errorText'>" + result.appError.errorLabel + "</span>");
                    $("#outputDiv").loading('stop');
                    return;
                }

                displayStatsTable(result);

                displayHistogram(result);

                $("#outputDiv").loading('stop');
            }
        });

    });
}

function addResetButtonListener() {

    $('#redo_button').on('click', function () {

        clearOutputDiv();

        resetColumnsDropdown();

        $("#tables").val("-1");

        $("#classes").val("5");

        $('#statsDiv').empty();

        $('#histoDiv').empty();

        $('#tables').val("-1");

        $('#columns').val("-1");

    });
}

function setOutputDiv() {

    document.getElementById('classes').oninput = function () {
        if (this.value.length > 2) {
            this.value = this.value.slice(0, 2);
        }
    }
}

function resetColumnsDropdown() {

    $("#columns option[value!='-1']").remove();//remove all values from columns except first
}

function clearOutputDiv() {

    $('#errorTextDiv').empty();

    $('#statsDiv').empty();

    $('#histoDiv').empty();
}

function displayStatsTable(result) {

    $('#statsDiv').append('<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#updateModal">Edit Value</button><div class="margin-10"></div>');

    $('#statsDiv').append("<table id='statsTable' />");

    $('#statsTable').append('<thead><th colspan="2">Range</th><th>Frequency</th></thead>');

    for (i = 0; i < result.intervalInfoList.length; i++) {

        $('#statsTable').append('<tr' + (i % 2 == 1 ? ' class="lightBlueRow"' : '') + '><td align="center" colspan="2">' + result.intervalInfoList[i].start + ' - ' + result.intervalInfoList[i].end + '</td>' +

            '<td align="center">' + result.intervalInfoList[i].frequency + '</td></tr>');
    }
}

function displayHistogram(result) {

    drawChart(result);
}

function setGoogleCharts() {

    google.charts.load("current", {packages: ["corechart"]});
}

function drawChart(result) {

    var dataArray = [['Range', 'Frequency', {role: 'style'}]];

    for (var i = 0; i < result.intervalInfoList.length; i++) {
        var label = result.intervalInfoList[i].start + " - " + result.intervalInfoList[i].end;
        var value = result.intervalInfoList[i].frequency;
        dataArray.push([label, value, 'color:#33A8FF;opacity:0.8']);
    }

    var data = google.visualization.arrayToDataTable(dataArray);

    var width = 800;
    if (result.intervalInfoList.length > 10) {
        width = 1000;
    }

    var options = {
        'title': result.table + "." + result.column,
        'width': width,
        'height': 550,
        bar: {groupWidth: "100%"},
        legend: {position: "none"}
    };

    var chart = new google.visualization.ColumnChart(document.getElementById('histoDiv'));
    chart.draw(data, options);
}

function addUpdateValueListener() {

    $("#updateModal").on("hide.bs.modal", function () {
        $("#updateValueMessageTextDiv").empty();
        $("#currentvalue").val("");
        $("#newvalue").val("");
    });

    $('#updateValue').on('click', function () {

        $("#updateValueMessageTextDiv").empty();

        $(document.body).loading();

        var currentvalue = $("#currentvalue").val();
        var newvalue = $("#newvalue").val();

        if (currentvalue === "" || newvalue === "-1") {
            $("#updateValueMessageTextDiv").append("<span class='errorText'>Select All Inputs.</span>");
            $(document.body).loading('stop');
            return;
        }

        var jsonData = JSON.stringify({
            "table": $("#tables").val(),
            "column": $("#columns").val(),
            "currentValue": currentvalue,
            "newValue": newvalue
        });

        $.ajax({
            url: hl_NAMESPACE + "updateValues",
            method: "POST",
            contentType: "application/json",
            dataType: "json",
            data: jsonData,
            success: function (result) {

                $("#updateValueMessageTextDiv").empty();

                if (result.appError) {

                    $("#updateValueMessageTextDiv").append("<span class='errorText'>" + result.appError.errorLabel + "</span>");
                    $(document.body).loading('stop');
                    return;
                }

                $("#updateValueMessageTextDiv").append("<span class='infoText'>Rows Updated : " + result.rowsUpdatedCount + "</span>");
                $(document.body).loading('stop');
            }
        });

    });
}
