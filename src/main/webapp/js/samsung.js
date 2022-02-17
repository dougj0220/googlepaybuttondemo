function onBuyClicked() {
   const SAMSUNG_PAY = 'https://spay.samsung.com';
   if (!window.PaymentRequest) {
      // PaymentRequest API is not available - forwarding to legacy form based experience.
      location.href = '/checkout';
   }

   // Setup
   var supportedInstruments = [
      {
          supportedMethods: [ SAMSUNG_PAY ], // 'https://spay.samsung.com'
          data: {
              "version": "1",
              "productId": "c61e6295373140e581cf08", //Service id from partner portal
              "allowedCardNetworks": ['mastercard','visa'],
              "orderNumber": "1233123",
              "merchantName": "Blackhawk Network", //Merchant name in partner portal
              //"merchantGatewayParameter": {"userId": "acct_17irF7F6yPzJ7wOR"},
          },
          "isRecurring": false,
          "billingAddressRequired": false,
          "paymentProtocol": "PROTOCOL_3DS"
      },
      {
          supportedMethods: 'basic-card',
          data: {
              supportedNetworks: ['visa', 'mastercard']
          }
      }
   ];

   var details = {
      displayItems: [{
         label: 'Original donation amount',
         amount: { currency: 'USD', value: '65.00' }
      }, {
         label: 'Friends and family discount',
         amount: { currency: 'USD', value: '-10.00' }
      }],
      total: {
         label: 'Total due',
         amount: { currency: 'USD', value : '55.00' }
      }
   };

   var options = {
      requestShipping: false,
      requestPayerEmail: true,
      requestPayerPhone: true,
      requestPayerName: true
   };

   // Initialization
   var request = new PaymentRequest(supportedInstruments, details, options);

   // When user selects a shipping address
   request.addEventListener('shippingaddresschange', e => {
      e.updateWith(((details, addr) => {
         var shippingOption = {
            id: '',
            label: '',
            amount: { currency: 'USD', value: '0.00' },
            selected: true
         };
        // Shipping to US is supported
        if (addr.country === 'US') {
           shippingOption.id = 'us';
           shippingOption.label = 'Standard shipping in US';
           shippingOption.amount.value = '0.00';
           details.total.amount.value = '55.00';
        // Shipping to JP is supported
        } else if (addr.country === 'JP') {
           shippingOption.id = 'jp';
           shippingOption.label = 'International shipping';
           shippingOption.amount.value = '10.00';
           details.total.amount.value = '65.00';
        // Shipping to elsewhere is unsupported
        } else {
           // Empty array indicates rejection of the address
           details.shippingOptions = [];
           return Promise.resolve(details);
        }
        // Hardcoded for simplicity
        if (details.displayItems.length === 2) {
           details.displayItems[2] = shippingOption;
        } else {
           details.displayItems.push(shippingOption);
        }
        details.shippingOptions = [shippingOption];
        return Promise.resolve(details);
     })(details, request.shippingAddress));
   });

   // When user selects a shipping option
   request.addEventListener('shippingoptionchange', e => {
      e.updateWith(((details) => {
         // There should be only one option. Do nothing.
         return Promise.resolve(details);
      })(details));
   });

   // Show UI then continue with user payment info
   request.show().then(result => {
      // POST the result to the server
      console.log(result);
      return fetch('/samsung', {
         method: 'POST',
         credentials: 'include',
         headers: {
            'Content-Type': 'application/json'
         },
         body: JSON.stringify(result.toJSON())
      }).then(res => {
         console.log(res);
         // Only if successful
         if (res.status === 200) {
            result.complete('success');
            return res.json();
         } else {
            result.complete('fail');
            throw 'Failure';
         }
      }).then(response => {
         console.log(response);
         document.getElementById('samsungPayHeader').innerHTML = 'Decrypted Samsung Pay Payload';
         document.getElementById('samsungPayResponse').innerHTML = response.paymentResponse;
         console.log("end");
      });
   }).catch(function(err) {
      console.error('Uh oh, something bad happened: ' + err.message);
   });
}

document.querySelector('#spaybutton').addEventListener('click', onBuyClicked);
