
function parsePrice(priceStr) {
    // Replace commas with periods and remove non-numeric characters
    priceStr = priceStr.replace(".", "").replace(",", ".");
    return parseFloat(priceStr);
}

function filterDataByStockCode(data) {
    let previousClose = null; // За следење на затворањето на претходниот ред

    return data.map((row, index) => {
        const date = row[1];
        const open = parsePrice(row[2]); // Тековното затворање е `row[2]`
        let close;

        // Ако е првиот ред, постави почетната цена со default offset
        if (index === 0) {
            close = parsePrice(row[4]);
        } else {
            close = previousClose; // Почетната цена е затворањето на претходниот ред
        }

        const high = parsePrice(row[3]);
        const low = parsePrice(row[4]);

        // Конвертирај го датумот во валиден JavaScript датум
        const dateParts = date.split(".");
        const validDate = new Date(
            parseInt(dateParts[2]), // Година
            parseInt(dateParts[1]) - 1, // Месец (0-базиран индекс)
            parseInt(dateParts[0]) // Ден
        );

        // Ажурирај ја затворачката цена за следниот ред
        previousClose = open;

        return {
            x: validDate,
            y: [open, high, low, close], // Open, High, Low, Close
        };
    });
}




// Function to render the candlestick chart using CanvasJS
function renderChart(chartData) {
  const chart = new CanvasJS.Chart("chartContainer", {
    animationEnabled: true,
    theme: "light2", // Using a light theme to avoid dark blue color
    zoomEnabled: true,
    exportEnabled: true,
    axisX: {
      title: "Date",
      valueFormatString: "DD MMM YYYY",
      gridColor: "#313748",
      labelFontColor: "#9a9ea3",
      titleFontColor: "#ffffff",
    },
    axisY: {
      gridColor: "#313748",
      labelFontColor: "#9a9ea3",
      titleFontColor: "#ffffff",
      title: "Price",
      prefix: "MKD ",
      labelFormatter: function (e) {
        // Format Y-axis labels as "16.750,00"
        return formatCurrency(e.value);
      },
    },
    data: [
      {
        type: "candlestick",
        risingColor: "#339c90",
        fallingColor: "#ff1a00",
        yValueFormatString: "MKD #,##0.00", // Default formatting
        dataPoints: chartData,
      },
    ],
  });

  changeBorderColor(chart);
  chart.render();
}

// Helper function to format numbers as "16.750,00" (with periods as thousand separators and commas as decimal separator)
function formatCurrency(value) {
  return value.toLocaleString("de-DE", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}

// Function to change border color
function changeBorderColor(chart) {
  var dataSeries;
  for (var i = 0; i < chart.options.data.length; i++) {
    dataSeries = chart.options.data[i];
    for (var j = 0; j < dataSeries.dataPoints.length; j++) {
      dataSeries.dataPoints[j].color =
        dataSeries.dataPoints[j].y[0] <= dataSeries.dataPoints[j].y[3]
          ? dataSeries.risingColor
            ? dataSeries.risingColor
            : dataSeries.color
          : dataSeries.fallingColor
          ? dataSeries.fallingColor
          : dataSeries.color;
    }
  }
}
