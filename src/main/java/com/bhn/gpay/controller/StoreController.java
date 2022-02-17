package com.bhn.gpay.controller;

import com.bhn.gpay.domain.ApiResponse;
import com.bhn.gpay.domain.Product;
import com.bhn.gpay.service.DecryptService;
import com.bhn.gpay.service.impl.CybersourceServiceImpl;
import com.bhn.gpay.service.impl.VantivServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StoreController {

	@Autowired
	private DecryptService decryptService;

	@Autowired
	private CybersourceServiceImpl cybersourceService;

	@Autowired
	private VantivServiceImpl vantivService;

	@GetMapping("/item")
	@ModelAttribute
	public String item(Model model) {
		
		Product item = new Product("Item");
		item.setImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS0R-e7TjTN0duf_EWojsx79HpWW9p4wpxkFA&usqp=CAU");
		item.setPrice(20);
		
		model.addAttribute("name", item.getName());
		model.addAttribute("imgLink", item.getImage());
		model.addAttribute("price", item.getPrice());
		return "itemView";
	}

	@PostMapping("/item/{gateway}")
	public ApiResponse handlePaymentToken(@PathVariable String gateway, @RequestBody String paymentToken) throws Exception {
		String decryptedPayload = decryptService.decryptGooglePayload(paymentToken);

		String gatewayResponse = "";
		if (gateway.equals("cybersource")) {
			gatewayResponse = cybersourceService.processGooglePayload(decryptedPayload);
		}
		else if (gateway.equals("vantiv")) {
			gatewayResponse = vantivService.processGooglePayload(decryptedPayload);
		}
		return new ApiResponse(decryptedPayload, gatewayResponse);
	}

	@GetMapping("/samsung")
	@ModelAttribute
	public String samsungPage(Model model) {

		Product item = new Product("Item");
		item.setImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS0R-e7TjTN0duf_EWojsx79HpWW9p4wpxkFA&usqp=CAU");
		item.setPrice(20);

		model.addAttribute("name", item.getName());
		model.addAttribute("imgLink", item.getImage());
		model.addAttribute("price", item.getPrice());
		return "itemView";
	}

	@PostMapping("/samsung")
	public ApiResponse samsungPayment(@RequestBody String paymentToken) throws Exception {
		System.out.println(paymentToken);
		String decryptedPayload = decryptService.decryptSamsungPayload(paymentToken);
		System.out.println(decryptedPayload);
		return new ApiResponse(decryptedPayload, "Empty");

		/*String response = cybersourceService.processBasicCard(paymentToken);
		return new ApiResponse(paymentToken, response);*/

	}
}
