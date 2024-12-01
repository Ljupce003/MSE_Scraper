let csvData = []; // To store the parsed CSV data
let stockCodes = []; // To store unique stock codes

// Load CSV data from the local folder when the page loads
window.onload = function () {
  fetchCSVData("/csv/mega_data_example.csv"); // Adjust the filename as needed
};

// Function to fetch CSV data from the local folder
function fetchCSVData(fileName) {
  fetch(fileName)
    .then((response) => response.text())
    .then((csvText) => {
      Papa.parse(csvText, {
        header: false, // Assuming no headers in the CSV
        skipEmptyLines: true,
        complete: function (results) {
          csvData = results.data;
          stockCodes = getUniqueStockCodes(csvData);
          populateStockDropdown(stockCodes);
        },
        error: function (error) {
          console.error("Error parsing CSV:", error);
          alert("Failed to parse CSV. Check the console for more details.");
        },
      });
    })
    .catch((error) => {
      console.error("Error loading CSV:", error);
      alert(
        "Failed to load CSV. Please ensure the file is in the correct directory."
      );
    });
}

// Function to extract unique stock codes from the CSV data
function getUniqueStockCodes(data) {
  const codes = [];
  for (let i = 1; i < data.length; i++) {
    const code = data[i][0];
    if (codes.indexOf(code) === -1) {
      codes.push(code);
    }
  }
  return codes;
}

// Populate the dropdown with stock codes
function populateStockDropdown(codes) {
  const dropdown = document.getElementById("stockDropdown");
  dropdown.innerHTML = '<option value="">Одбери акција</option>'; // Clear previous options
  codes.forEach((code) => {
    const option = document.createElement("option");
    option.value = code;
    option.textContent = code;
    dropdown.appendChild(option);
  });
}

// Event listener for dropdown change
document
  .getElementById("stockDropdown")
  .addEventListener("change", function (event) {
    updateChart();
  });

// Event listener for date range change
document.getElementById("fromDate").addEventListener("change", updateChart);
document.getElementById("toDate").addEventListener("change", updateChart);

// Update chart based on the selected stock code and date range
function updateChart() {
  const selectedCode = document.getElementById("stockDropdown").value;
  const fromDate = document.getElementById("fromDate").value;
  const toDate = document.getElementById("toDate").value;

  if (selectedCode) {
    const filteredData = filterDataByStockCode(
      csvData,
      selectedCode,
      fromDate,
      toDate
    );
    renderChart(filteredData);
  } else {
    alert("Please select a stock code.");
  }
}

// Function to filter data based on stock code and date range
function filterDataByStockCode(data, code, fromDate, toDate) {
  return data
    .filter((row) => {
      const rowDate = row[1];
      const stockCode = row[0];
      if (stockCode !== code) return false;

      // Convert date from DD.MM.YYYY to a valid JavaScript Date object
      const dateParts = rowDate.split(".");
      const validDate = new Date(
        parseInt(dateParts[2]), // Year
        parseInt(dateParts[1]) - 1, // Month (0-based index)
        parseInt(dateParts[0]) // Day
      );

      // Check if the row date is within the selected range
      const isAfterFromDate = fromDate ? validDate >= new Date(fromDate) : true;
      const isBeforeToDate = toDate ? validDate <= new Date(toDate) : true;

      return isAfterFromDate && isBeforeToDate;
    })
    .map((row) => {
      const date = row[1];
      const open = parsePrice(row[2]);
      const high = parsePrice(row[3]);
      const low = parsePrice(row[4]);
      const close = parsePrice(row[5]);

      // Convert date from DD.MM.YYYY to a valid JavaScript Date object
      const dateParts = date.split(".");
      const validDate = new Date(
        parseInt(dateParts[2]), // Year
        parseInt(dateParts[1]) - 1, // Month (0-based index)
        parseInt(dateParts[0]) // Day
      );

      if (isNaN(validDate)) {
        console.error("Invalid date format in row:", date);
        return null;
      }

      return {
        x: validDate,
        y: [open, high, low, close],
      };
    })
    .filter((item) => item !== null); // Remove any null entries due to invalid dates
}

// Helper function to parse prices and convert them to numbers
function parsePrice(priceStr) {
  // Replace commas with periods and remove non-numeric characters
  priceStr = priceStr.replace(".", "").replace(",", ".");
  return parseFloat(priceStr);
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
        risingColor: "#ff4a68",
        fallingColor: "#339f93",
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
