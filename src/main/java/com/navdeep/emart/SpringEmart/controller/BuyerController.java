package com.navdeep.emart.SpringEmart.controller;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.navdeep.emart.SpringEmart.dto.CustomerDto;
import com.navdeep.emart.SpringEmart.dto.QuantityDto;
import com.navdeep.emart.SpringEmart.model.Order;
import com.navdeep.emart.SpringEmart.model.OrderProduct;
import com.navdeep.emart.SpringEmart.model.Product;
import com.navdeep.emart.SpringEmart.model.User;
import com.navdeep.emart.SpringEmart.repositories.OrderProductRepository;
import com.navdeep.emart.SpringEmart.repositories.OrderRepository;
import com.navdeep.emart.SpringEmart.repositories.ProductRepository;
import com.navdeep.emart.SpringEmart.service.ProductService;
import com.navdeep.emart.SpringEmart.service.UserService;
import com.navdeep.emart.SpringEmart.utils.ImageUtil;

@Controller
@RequestMapping("/buyer")
public class BuyerController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	OrderProductRepository orderProductrepository;
	
	@GetMapping("/home")
	public String showBuyerRegistrationForm(@PageableDefault(size = 5) Pageable pageable,
			@RequestParam(value="filter",required=false) String filter,
			@RequestParam(value="search",required=false) String search,
			Model model) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		
		if(search == null) {
			Page<Product> page = productRepository.findAll(pageable);
			model.addAttribute("page", page);
			model.addAttribute("userName", user.getFirstName() + " " + user.getLastName());
		}
		else {
			model.addAttribute("search", search);
			if(filter == null){
				Page<Product> page = productRepository.findByName(search, pageable);
				model.addAttribute("page", page);
			}
			else if(filter.equalsIgnoreCase("lowToHigh")) {
				Page<Product> page = productService.findBySearchAndLowToHigh(search, pageable);
				model.addAttribute("page",page);
				model.addAttribute("lowToHigh", "lowToHigh");
			}
			else if(filter.equalsIgnoreCase("highToLow")) {
				Page<Product> page = productService.findBySearchAndHighToLow(search, pageable);
				model.addAttribute("page",page);
				model.addAttribute("highToLow", "highToLow");
			}
			else if (filter.equalsIgnoreCase("newestArrivals")) {
				Page<Product> page = productService.findBySearchAndarrival(search, pageable);
				model.addAttribute("page",page);
				model.addAttribute("newestArrivals", "newestArrivals");
			}
		}
		model.addAttribute("imgUtil", new ImageUtil());
		return "buyer/index.html";
	}
	
	
	@GetMapping("/getProductDetails/{id}")
	public String getProductDetails(@PathVariable("id") Long id, Model model) {
		Product product = productRepository.findById(id).orElse(null);
		model.addAttribute("quantity", new QuantityDto());
		model.addAttribute("imgUtil", new ImageUtil());
		if(product != null) {
			model.addAttribute("product", product);
		}
		else {
			model.addAttribute("error","This product does not Exist :-");
		}
		return "buyer/productDetails";
	}
	
	@PostMapping("/getProductDetails/{id}")
	@Transactional
	public String addToCart(@ModelAttribute("quantity") @Valid QuantityDto quantityDto, BindingResult result, @PathVariable("id") Long id, RedirectAttributes redir) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		Product product = productRepository.findById(id).orElse(null);
		
		Order order = userService.getLatestOrder(user.getId());
		
		if ((product.getQuantityPosted()-product.getSales()) >= quantityDto.getNumber()) {
			if (order != null) {
				if (order.getStatus().equalsIgnoreCase("CONFIRMED")) {
					Order newOrder = new Order(new Date(), "PENDING");
					user.addOrder(newOrder);
					orderRepository.save(newOrder);
					OrderProduct orderProduct = new OrderProduct(newOrder, product, quantityDto.getNumber());
					product.setSales(product.getSales() + quantityDto.getNumber());
					productRepository.save(product);
					orderProductrepository.save(orderProduct);
				} else {
					List<OrderProduct> orderProducts = order.getOrderProducts();
					int flag = 0;
					for (OrderProduct orderProduct : orderProducts) {
						if (orderProduct.getProduct().getId() == id) {
							orderProduct.setQuantity(orderProduct.getQuantity() + quantityDto.getNumber());
							product.setSales(product.getSales() + orderProduct.getQuantity() + quantityDto.getNumber());
							productRepository.save(product);
							flag = 1;
							break;
						}
					}
					if (flag == 0) {
						OrderProduct orderProduct = new OrderProduct(order, product, quantityDto.getNumber());
						product.setSales(product.getSales() + quantityDto.getNumber());
						productRepository.save(product);
						orderProductrepository.save(orderProduct);
					}
				}
			} else {
				Order newOrder = new Order(new Date(), "PENDING");
				user.addOrder(newOrder);
				orderRepository.save(newOrder);
				OrderProduct orderProduct = new OrderProduct(newOrder, product, quantityDto.getNumber());
				product.setSales(product.getSales() + quantityDto.getNumber());
				productRepository.save(product);
				orderProductrepository.save(orderProduct);

			} 
		}
		else {
			redir.addFlashAttribute("outOfStock", "This Quantity required by you is not available :-(");
		}
		redir.addAttribute("id", id);
		return "redirect:/buyer/getProductDetails/{id}";
	}
	
	
	@GetMapping("/getCart")
	public String getCart(Model model) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		
		model.addAttribute("quantity",new QuantityDto());
		model.addAttribute("imgUtil", new ImageUtil());
		Double totalPrice = 0.00;
		Order order = userService.getLatestOrder(user.getId());
		if(order != null && order.getStatus().equalsIgnoreCase("PENDING")) {
			List<OrderProduct> orderProducts = order.getOrderProducts();
			for(OrderProduct orderProduct: orderProducts) {
				totalPrice += orderProduct.getTotalPrice();
			}
			model.addAttribute("orderProducts", orderProducts);
			model.addAttribute("totalPrice", totalPrice);
		}
		else {
			model.addAttribute("noProducts", "Look like your Cart is Empty please add some Items!!");
		}
		return "buyer/cartDetails";
	}
	
	@GetMapping("/getCart/details")
	public String updateCart(@RequestParam(value = "quantity", required = true) Integer quantity,
			@RequestParam(value = "productId", required = true) Long productId, RedirectAttributes redir) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		Order order = userService.getLatestOrder(user.getId());
		List<OrderProduct> orderProducts = order.getOrderProducts();
		
		search:
		for(OrderProduct orderProduct : orderProducts) {
			if(orderProduct.getProduct().getId() == productId) {
				if((orderProduct.getProduct().getQuantityPosted()-orderProduct.getProduct().getSales()) < quantity) {
					redir.addFlashAttribute("outOfStock", "This Quantity required by you is not available :-(");
					break search;
				}
				Product product = orderProduct.getProduct();
				product.setSales(product.getSales()-orderProduct.getQuantity()+quantity);
				productRepository.save(product);
				orderProduct.setQuantity(quantity);
				orderProductrepository.save(orderProduct);
				break;
			}
		}
		return "redirect:/buyer/getCart";
	}
	
	@GetMapping("/deleteItem")
	public String deleteItemInCart(@RequestParam(value = "productId", required = true) Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		User user = userService.findByEmail(auth.getName());
		Order order = userService.getLatestOrder(user.getId());
		List<OrderProduct> orderProducts = order.getOrderProducts();
		
		for(OrderProduct orderProduct: orderProducts) {
			if(orderProduct.getProduct().getId() == id) {
				Product product = orderProduct.getProduct();
				product.setSales(product.getSales()-orderProduct.getQuantity());
				productRepository.save(product);
				orderProductrepository.delete(orderProduct);
				break;
			}
		}
		return "redirect:/buyer/getCart";
	}
	
	@GetMapping("/checkOut")
	public String checkOut(Model model) {
		model.addAttribute("customer", new CustomerDto());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		Double totalPrice = 0.00;
		Order order = userService.getLatestOrder(user.getId());
		if(order != null) {
			List<OrderProduct> orderProducts = order.getOrderProducts();
			for(OrderProduct orderProduct: orderProducts) {
				totalPrice += orderProduct.getTotalPrice();
			}
			Double tax = totalPrice*(0.18);
			Double orderTotal = totalPrice + tax;
			model.addAttribute("orderProducts", orderProducts);
			model.addAttribute("totalPrice", totalPrice);
			model.addAttribute("tax", tax);
			model.addAttribute("orderTotal", orderTotal);
		}
		else {
			return "shopSomething";
		}
		return "buyer/checkOut";
	}
	
	@PostMapping("/checkOut")
	public String postCheckOut(@ModelAttribute("customer") @Valid CustomerDto customerDto, BindingResult result, RedirectAttributes redir) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		if(result.hasErrors()) {
			return "buyer/checkOut";
		}
		else {
			Order order = userService.getLatestOrder(user.getId());
			order.setStatus("CONFIRMED");
			user.setCountry(customerDto.getCountry());
			user.setCompany(customerDto.getCompany());
			user.setAddress(customerDto.getAddress());
			user.setPincode(customerDto.getCode());
			user.setPhone(customerDto.getNumber());
			userService.update(user);
			redir.addAttribute("orderId", order.getId());
			return "redirect:/buyer/thankYou/{orderId}";
		}
	}
	
	@GetMapping("/thankYou/{id}")
	public String getThankYouPage(@PathVariable("id") Long id, Model model) {
		model.addAttribute("orderNo", id);
		return "buyer/thankYou";
	}
	
	
	@GetMapping("/orderHistory")
	public String showOrderHistory(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		List<Order> orders = userService.findAllOrdersByDate(user.getId());
		model.addAttribute("orders", orders);
		model.addAttribute("username", user.getFirstName()+" "+ user.getLastName());
		return "buyer/orderHistory";
	}
	
	
}
