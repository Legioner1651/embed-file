window.RT_WIDGET_CONFIG = {
  "web-component-1": {
    "ready": true,
    "installmentsSettings": { "limitRest": null },
    onInit: function (resultType, errors, warnings) {
      switch (resultType) {
        case "SUCCESS":
          console.log("SUCCESS");
          break;
        case "WARNING":
          console.log("WARNING", warnings);
          break;
        case "ERROR":
          console.log("ERROR", errors, warnings);
          break;
        default:
          break;
      }
    },
    outputData: {
      status: false,
      getProductOfferCfg: function () {
        return "данные пока не готовы";
      },
      getProductOfferCfgAsync: function () {
        return "данные пока не готовы";
      }
    },
    outerFuncs: [{
      id: "NNumberschoose",
      handler: function (maxCount, params, resolve, reject) {
        var nums = prompt("Введите номер:", "9123456789");
        var numarr = nums.split(",", maxCount);
        let response = numarr.map(function (num) {
          return {
            "action": "add",
            "instId": ""
          };
        });
        resolve(response);
      }
    }]
  }
};
var createXHRRequest = function (method, url, param, token, resolve, reject) {
  resolve = resolve || function () { };
  reject = reject || function () { };
  var xhr = new XMLHttpRequest();
  if (method === "GET" && param) {
    url += param;
  }
  xhr.open(method, url);
  if (token) {
    xhr.setRequestHeader("Authorization", token);
  }
  if (method === "POST") {
    xhr.setRequestHeader("Content-Type", "application/json");
  }
  xhr.onload = function () {
    if (this.status >= 200 && this.status < 300) {
      resolve(xhr.response);
    } else {
      reject({
        status: this.status,
        statusText: xhr.statusText
      });
    }
  };
  xhr.onerror = function () {
    reject({
      status: this.status,
      statusText: xhr.statusText
    });
  };
  if (method === "POST") {
    xhr.send(param);
  } else {
    xhr.send();
  }
};