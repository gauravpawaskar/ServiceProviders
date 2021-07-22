var current_selected = null;

function show_service(service_name) {
  if (service_name == "none") {
    current_selected_div = document.getElementById(current_selected);
    current_selected_div.style.display = "none";
    current_selected = null;
  } else {
    service_div = document.getElementById(service_name);
    service_div.style.display = "block";
    if (current_selected != null) {
      current_selected_div = document.getElementById(current_selected);
      current_selected_div.style.display = "none";
    }
    current_selected = service_name;
  }
}

function set_up_service(service_name, client_id, client_secret) {
  var setup_xhr = new XMLHttpRequest();
  var setup_url = "setup";
  setup_xhr.open("POST", setup_url);
  setup_xhr.setRequestHeader("Content-Type", "application/json");
  setup_xhr.onreadystatechange = function () {
    if (
      setup_xhr.readyState === XMLHttpRequest.DONE &&
      setup_xhr.status === 200
    ) {
      // Request finished. Do processing here.
      var setup_resp_json = JSON.parse(setup_xhr.responseText);
      alert(setup_xhr.responseText);
      alert(setup_resp_json.status == "success");
      if (setup_resp_json.status != "success") {
        alert("Set up failed");
      } else {
        alert("Server Setup");
      }
    }
  };
  setup_xhr.send(
    JSON.stringify({
      service: service_name,
      client_id: client_id,
      client_secret: client_secret,
    })
  );
}

function redirectLogin(app) {
  if (app == "linkedin") {
    client_id = document.getElementById(app + "_clientid").value;
    client_secret = document.getElementById(app + "_clientsecret").value;
    checkboxes = document.getElementsByName(app + "_scope");
    checkboxesChecked_scope = [];
    for (var i = 0; i < checkboxes.length; i++) {
      if (checkboxes[i].checked) {
        checkboxesChecked_scope.push(checkboxes[i].value);
      }
    }
    set_up_service(app, client_id, client_secret);
    checkboxesChecked_scope_string = checkboxesChecked_scope.join("%20");
    url_string =
      "https://www.linkedin.com/oauth/v2/authorization?response_type=code&client_id=" +
      client_id +
      "&redirect_uri=https://flagprotectors.com/oauth/" +
      app +
      "&state=foobar&scope=" +
      checkboxesChecked_scope_string;
    location.replace(url_string);
  } else if (app == "auth0") {
    client_id = document.getElementById(app + "_clientid").value;
    client_secret = document.getElementById(app + "_clientsecret").value;
    checkboxes = document.getElementsByName(app + "_scope");
    checkboxesChecked_scope = [];
    for (var i = 0; i < checkboxes.length; i++) {
      if (checkboxes[i].checked) {
        checkboxesChecked_scope.push(checkboxes[i].value);
      }
    }
    set_up_service(app, client_id, client_secret);
    checkboxesChecked_scope_string = checkboxesChecked_scope.join("%20");
    url_string =
      "https://flagprotectors.us.auth0.com/authorize?response_type=code&client_id=" +
      client_id +
      "&redirect_uri=https://flagprotectors.com/oauth/" +
      app +
      "&scope=" +
      checkboxesChecked_scope_string +
      "&state=xyzABC123";
    alert(url_string);
    location.replace(url_string);
  }
}
