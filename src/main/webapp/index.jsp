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

    <% if (request.getSession().getAttribute("myUserdata") != null) {    %>
           <form action="/controller" method="post">
            <%= ((Userdata) session.getAttribute("myUserdata")).getUsername() %>
           <input type="submit" name="logout" value="logout">
           </form>
         <%  if (request.getSession().getAttribute("admin") != null) {  %>
             <a href=adminpanel.jsp>Datenbank Bearbeiten</a>
             <% } %>
     <%   } else {
    %>

       <a href=login.jsp>Anmelden</a>

   <% } %>

   <!-- Gibt Nachricht aus so weit eine Nachricht zurück gekommen ist -->
       <% String message = request.getParameter("message");
           if (message != null) { %>
               <p style="color: green;">
               <%= message.toString() %>
               </p>
           <% } %>

   <!-- Gibt Fehler aus so weit ein erorr zurück gekommen ist -->
       <% String error = request.getParameter("error");
          if (error != null) { %>
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
                <h1>Verfuegbare Items</h1>

                <% List<Itemdata> warenkorb = new ArrayList(); %>
                  <% if (session.getAttribute("warenkorb") !=null)
                  {
                    warenkorb=((ArrayList<Itemdata>) session.getAttribute("warenkorb"));
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
                            createItem('<%= item.getName() %>', <%= item.getAmount() %>, <%= item.getPrice() %>, <%= currentCardAmount %>)
                          });
                        </script>

                        <% } } else { out.println("No Items Currently available"); } %>

                          <!-- Items die zur auswahl stehen werden hier reingelegt. -->
                          <div id="itemTracker" class="itembox"></div>
              </div>
              <hr>
              <div class="colum">
                <h1>Warenkorb</h1>
                <form id="warenkorb-form" class="itembox" action="/controller" method="get">
                   <input id="confirmOrderButton" type="submit" name="action" value="save">
                  <input id="confirmOrderButton" type="submit" name="action" value="buy">
                  <p id=pricetag >Price: </p>
                  <hr>

                    <div id="warenkorb">
                        <!-- Items werden hier reingelegt sobald sie Ausgewaehlt wurden. -->
                    </div>

                </form>
              </div>
    </div>
    <hr>

    <script>
      /**
          handelt die Warenkorb erstellung
          Fuegt fuer jedes Potentielle item
          ein Element an und wird beim klicken
          in den Warenkorb gelegt, sofern ein
          item bereits im warenkorb liegt wird
          der zustand wieder hergestellt
      */

      var cardValue = 0;

      function createItem(name, initialAmount, price, _cardAmount) {

        let amount = initialAmount;
        let cardAmount = _cardAmount;
        let cardSpan;
        let inputitem;
        let inputamount;

        cardValue += cardAmount * price;

        const itemlistButton = document.createElement('button');
        itemlistButton.textContent = '-';
        itemlistButton.type = 'button';
        itemlistButton.addEventListener('click', () => {
          amount++;
          cardAmount--;
          itemSpan.textContent = price + '€ ' + name + ': ' + amount;
          cardSpan.textContent = name + ': ' + cardAmount;
          cardValue -= price;
          document.getElementById('pricetag').innerText = 'Price: ' + cardValue.toFixed(2) + '€';
          if (cardAmount === 0) {
            card.removeChild(cardSpan);
            card.removeChild(linebreak)
            card.removeChild(itemlistButton);
            card.removeChild(inputamount)
            card.removeChild(inputitem)
          }
          inputamount.value = cardAmount;
          updateButtonStates();
        });

        const shoppingcardButton = document.createElement('button');
        shoppingcardButton.textContent = '+';
        shoppingcardButton.type = 'button';
        shoppingcardButton.addEventListener('click', () => {
          if (amount > 0) {
            amount--;
            cardAmount++;
            movetocard();
            itemSpan.textContent = price + '€ ' + name + ': ' + amount;
            cardSpan.textContent = name + ': ' + cardAmount;
            inputamount.value = cardAmount;
            cardValue += price;
            updateButtonStates();
            document.getElementById('pricetag').innerText = 'Price: ' + cardValue.toFixed(2) + '€';
          }
        });

        const linebreak = document.createElement('br');

        const itemSpan = document.createElement('span');
        itemSpan.textContent = price + '€ ' + name + ': ' + amount;

        const itemTrackerDiv = document.getElementById('itemTracker');

        itemTrackerDiv.appendChild(itemSpan);
        itemTrackerDiv.appendChild(shoppingcardButton);
        itemTrackerDiv.appendChild(document.createElement('br'));

        const card = document.getElementById('warenkorb');

        if (cardAmount > 0) {
          movetocard();
        }

        function movetocard() {
          const existingItems = document.querySelectorAll('#warenkorb span');

          for (const item of existingItems) {
            if (item.textContent.startsWith(name)) {
              return;
            }
          }

          inputitem = document.createElement('input')
          inputitem.type = "hidden";
          inputitem.value = name;
          inputitem.name = "items"
          card.appendChild(inputitem);

          inputamount = document.createElement('input')
          inputamount.type = "hidden";
          inputamount.value = cardAmount;
          inputamount.name = "amount"
          card.appendChild(inputamount);

          cardSpan = document.createElement('span');
          cardSpan.textContent = name + ': ' + cardAmount;
          card.appendChild(cardSpan);
          card.appendChild(itemlistButton);
          card.appendChild(linebreak);
        }
          document.getElementById('pricetag').innerText = 'Price: ' + cardValue.toFixed(2) + '€';
            updateButtonStates();

        function updateButtonStates() {
          itemlistButton.disabled = cardAmount === 0;
          shoppingcardButton.disabled = amount === 0;
        }
      }

    </script>


  </body>

</html>