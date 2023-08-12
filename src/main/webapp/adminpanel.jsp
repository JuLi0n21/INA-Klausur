<!DOCTYPE html>
<html>
<%@ page contentType="text/html; charset=UTF-8" %>

  <head>
    <title>INA PRAKTIKUM</title>
    <link rel="stylesheet" type="text/css" href="style.css">
  </head>

  <body>

    <%@ page import="klausur.beans.Userdata" %>
      <h1 class="heading1">INA Klausur</h1>

    <% if (request.getSession().getAttribute("myUserdata") !=null) { %>
        <form action="/controller" method="post">
    <%= ((Userdata) session.getAttribute("myUserdata")).getUsername() %>
            <input type="submit" name="logout" value="logout">
        </form>
    <% if (request.getSession().getAttribute("admin") !=null) { %>
          <a href=index.jsp>Startseite</a>

    <% } else { %>
            <jsp:forward page="/login.jsp?error=Sie haben nicht die Berechtigung diese seite auf zurufen." />
    <% } } else { %>


    <jsp:forward page="/login.jsp?error=Sie haben nicht die Berechtigung diese seite auf zurufen." />

    <a href=login.jsp>Anmelden</a>

    <% } %>

    <!-- Gibt Nachricht aus so weit eine Nachricht zurück gekommen ist -->
    <% String message=request.getParameter("message"); if (message !=null) { %>
    <p style="color: green;">
    <%= message.toString() %>
    </p>
    <% } %>

    <!-- Gibt Fehler aus so weit ein erorr zurück gekommen ist -->
    <% String error=request.getParameter("error"); if (error !=null) { %>
    <p style="color: red;">
    Error: <%= error.toString() %>
    </p>
    <% } %>

    <hr>
    <div class="grid">

    <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.List" %>
    <%@ page import="klausur.beans.Itemdata" %>
    <%@ page import="java.io.IOException" %>

    <jsp:include page="/controller">
    <jsp:param name="itemlist" value="loaditems" />
    </jsp:include>

    <div class="colum">
    <h1>Datenbank bearbeiten</h1>

    <% List<Itemdata> warenkorb = new ArrayList(); %>
    <% if (session.getAttribute("warenkorb") !=null) { warenkorb=((ArrayList<Itemdata>
    ) session.getAttribute("warenkorb"));
    // out.println(request.getSession().getAttribute("warenkorb"));
    }
    %>

    <%List<Itemdata> items = ((ArrayList<Itemdata>) session.getAttribute("items"));
    if(!items.isEmpty()){
    for(Itemdata item : items){
    int currentCardAmount = 0;

    for(Itemdata ware : warenkorb){


    if(ware.getName().equals(item.getName())) {

    currentCardAmount = ware.getAmount();
    }
    // out.println(currentCardAmount);
    }
    %>
    <script>
    window.addEventListener('DOMContentLoaded', function () {
    createItem('<%= item.getName() %>', <%= item.getAmount() %>, <%= item.getPrice() %>)
    });
    </script>

    <% } } else { out.println("No Items Currently available"); } %>

  <label for="nameInput">Name:</label>
  <input type="text" name="Name" id="nameInput" placeholder="Enter name" required><br><br>

  <label for="amountInput">Amount:</label>
  <input type="number" name="Amount" id="amountInput" placeholder="Enter amount" required><br><br>

  <label for="priceInput">Price:</label>
  <input type="number" name="Price" id="priceInput" step="0.01" placeholder="Enter price" required><br><br>

  <div id="buttonContainer">
    <button type="button" onclick="addItem()" disabled>Add Item</button>
  </div>
<form id="itemTracker" action="controller" method="post">

  <table id="itemTable">
    <!-- Existing table rows or headers go here -->
  </table>

    <input type="submit" name="saveitems" value="saveitems">
</form>


<script>
  let nameInput = document.getElementById("nameInput");
  let amountInput = document.getElementById("amountInput");
  let priceInput = document.getElementById("priceInput");
  let addButton = document.querySelector("#buttonContainer button");

  nameInput.addEventListener("input", validateInputs);
  amountInput.addEventListener("input", validateInputs);
  priceInput.addEventListener("input", validateInputs);

  function validateInputs() {
    let name = nameInput.value.trim();
    let amount = parseInt(amountInput.value);
    let price = parseFloat(priceInput.value);

    let nameValid = name.length > 0;
    let amountValid = Number.isInteger(amount);
    let priceValid = !isNaN(price);

    addButton.disabled = !(nameValid && amountValid && priceValid);
  }

  function addItem() {
    let name = nameInput.value.trim();
    let amount = parseInt(amountInput.value);
    let price = parseFloat(priceInput.value);

    createItem(name, amount, price);

    // Clear input fields
    nameInput.value = "";
    amountInput.value = "";
    priceInput.value = "";

    addButton.disabled = true; // Disable the button again
  }

  function createItem(Name, Amount, Price) {
    let table = document.getElementById("itemTable");
    let newRow = table.insertRow();

    let nameCell = newRow.insertCell();
    let nameInput = document.createElement("input");
    nameInput.type = "text";
    nameInput.name = "Name";
    nameInput.value = Name;
    nameCell.appendChild(nameInput);

    let amountCell = newRow.insertCell();
    let amountInput = document.createElement("input");
    amountInput.type = "text";
    amountInput.name = "Amount";
    amountInput.value = Amount;
    amountCell.appendChild(amountInput);

    let priceCell = newRow.insertCell();
    let priceInput = document.createElement("input");
    priceInput.type = "text";
    priceInput.name = "Price";
    priceInput.value = Price;
    priceCell.appendChild(priceInput);

    let removeCell = newRow.insertCell();
    let removeButton = document.createElement("button");
    removeButton.type = "button";
    removeButton.textContent = "Remove";
    removeButton.onclick = function() {
      table.deleteRow(newRow.rowIndex);
    };
    removeCell.appendChild(removeButton);
  }

  function submitForm() {
    let form = document.getElementById("itemTracker");
    form.submit();
  }
</script>

    </div>
    </div>
    <hr>
  </body>

<script>
  let nameInput = document.getElementById("nameInput");
  let amountInput = document.getElementById("amountInput");
  let priceInput = document.getElementById("priceInput");
  let addButton = document.querySelector("#buttonContainer button");

  nameInput.addEventListener("input", validateInputs);
  amountInput.addEventListener("input", validateInputs);
  priceInput.addEventListener("input", validateInputs);

  function validateInputs() {
    let name = nameInput.value.trim();
    let amount = parseInt(amountInput.value);
    let price = parseFloat(priceInput.value);

    let nameValid = name.length > 0;
    let amountValid = Number.isInteger(amount);
    let priceValid = !isNaN(price);

    addButton.disabled = !(nameValid && amountValid && priceValid);
  }

  function addItem() {
    let name = nameInput.value.trim();
    let amount = parseInt(amountInput.value);
    let price = parseFloat(priceInput.value);

    createItem(name, amount, price);

    // Clear input fields
    nameInput.value = "";
    amountInput.value = "";
    priceInput.value = "";

    addButton.disabled = true; // Disable the button again
  }

  function createItem(Name, Amount, Price) {
    let table = document.getElementById("itemTable");
    let newRow = table.insertRow();

    let nameCell = newRow.insertCell();
    let nameInput = document.createElement("input");
    nameInput.type = "text";
    nameInput.name = "Name";
    nameInput.value = Name;
    nameCell.appendChild(nameInput);

    let amountCell = newRow.insertCell();
    let amountInput = document.createElement("input");
    amountInput.type = "text";
    amountInput.name = "Amount";
    amountInput.value = Amount;
    amountCell.appendChild(amountInput);

    let priceCell = newRow.insertCell();
    let priceInput = document.createElement("input");
    priceInput.type = "text";
    priceInput.name = "Price";
    priceInput.value = Price;
    priceCell.appendChild(priceInput);

    let removeCell = newRow.insertCell();
    let removeButton = document.createElement("button");
    removeButton.type = "button";
    removeButton.textContent = "Remove";
    removeButton.onclick = function() {
      table.deleteRow(newRow.rowIndex);
    };
    removeCell.appendChild(removeButton);
  }

  function submitForm() {
    let form = document.getElementById("itemTracker");
    form.submit();
  }
</script>


</html>