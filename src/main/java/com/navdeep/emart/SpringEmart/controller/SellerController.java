package com.navdeep.emart.SpringEmart.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.navdeep.emart.SpringEmart.dto.ProductDto;
import com.navdeep.emart.SpringEmart.dto.SalesDto;
import com.navdeep.emart.SpringEmart.model.Product;
import com.navdeep.emart.SpringEmart.model.User;
import com.navdeep.emart.SpringEmart.repositories.ProductRepository;
import com.navdeep.emart.SpringEmart.service.ProductService;
import com.navdeep.emart.SpringEmart.service.UserService;
import com.navdeep.emart.SpringEmart.utils.ImageUtil;


@Controller
@RequestMapping("/seller")
public class SellerController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	ProductRepository productRepository;
	
	@GetMapping("/home")
	public String showSellerRegistrationForm(@PageableDefault(size = 5) Pageable pageable, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		model.addAttribute("userName", user.getFirstName() + " " + user.getLastName());
		Long id = user.getId();
		Page<Product> page = productRepository.getAllProductsByDate(id, pageable);
		model.addAttribute("page", page);
		model.addAttribute("imgUtil", new ImageUtil());
		return "seller/index.html";
	}
	
	@GetMapping("/addProduct")
	public String addProduct(Model model) {
		model.addAttribute("product", new ProductDto());
		return "seller/addProduct";
	}
	
	@PostMapping("/addProduct")
	public String postAddProduct(@RequestParam("file") MultipartFile file, @ModelAttribute("product") @Valid ProductDto productDto, BindingResult result, Model model) throws IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		if (file.isEmpty()) {
            model.addAttribute("error", "Please upload an image");
            return "seller/addProduct";
        }
		else if(result.hasErrors()){
			model.addAttribute("error",result.getFieldError());
			return "seller/addProduct";
		}
		else {
			Product product = new Product(new Date(), new Date(), productDto.getProductDescription(), file.getBytes(), productDto.getProductName(),
					productDto.getPrice(), productDto.getQuantity(), productDto.getDiscount());
			product.setCode(productDto.getProductCode());
			product.setSales(0);
			user.addProduct(product);
			productService.save(product);
			return "redirect:/seller/addProduct";
		}
	}
	
	@GetMapping("/getProduct/{id}")
	public String editProductDetails(@PathVariable("id") Long id, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		model.addAttribute("editProduct", new Product());
		model.addAttribute("imgUtil", new ImageUtil());
		Product product = productRepository.findById(id).orElse(null);
		if(product != null && product.getUser().getId() == user.getId()) {
			model.addAttribute("product", product);
		}
		else {
			model.addAttribute("error", "This Product does not Exists.");
		}
		return "seller/editProduct";
	}
	
	@PostMapping("/getProduct/{id}")
	public String postEditProduct(@RequestParam("file") MultipartFile file, @PathVariable("id") Long id, @ModelAttribute("editProduct") @Valid Product formProduct, BindingResult result, RedirectAttributes redir) throws IOException {
		Product product = productRepository.findById(id).orElse(null);
		product.setName(formProduct.getName());
		product.setQuantityPosted(formProduct.getQuantityPosted());
		product.setPrice(formProduct.getPrice());
		product.setDescription(formProduct.getDescription());
		product.setDiscount(formProduct.getDiscount());
		product.setCode(formProduct.getCode());
		if(file.getContentType().equalsIgnoreCase("image/jpeg")
				|| file.getContentType().equalsIgnoreCase("image/png")) {
			product.setPic(file.getBytes());
		}
		productRepository.save(product);
		redir.addAttribute("id", id);
		return "redirect:/seller/getProduct/{id}";
	}
	
	@GetMapping("/salesReport")
	public String getSalesReport(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		Long id = user.getId();
		List<Product> products = productRepository.getAllProductsByDate(id);
		model.addAttribute("sales", new SalesDto());
		model.addAttribute("products", products);
		return "seller/salesReport";
	}
	
	@PostMapping("/salesReport")
	public String postSalesReport(@ModelAttribute("sales") @Valid SalesDto salesDto, BindingResult result, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		Long id = user.getId();
		if(salesDto.getStartDate() != null && salesDto.getEndDate() != null) {
			Date startDate = salesDto.getStartDate();
			Date endDate = salesDto.getEndDate();
			
			if(startDate.compareTo(endDate) < 0) {
				DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String startDateString = outputFormatter.format(startDate);
				
				String endDateString = outputFormatter.format(endDate);
				List<Product> products = productService.findBetweenDates(startDateString, endDateString,id);
				model.addAttribute("products", products);
			}
			else {
				model.addAttribute("error","Start Date should be less than End Date");
			}
		}
		else {
			List<Product> products = productRepository.getAllProductsByDate(id);
			model.addAttribute("products", products);
		}
		return "seller/salesReport";
	}
	
	@GetMapping("/deleteProduct")
	public String deleteProduct(@RequestParam(value = "id", required = true) Long productId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findByEmail(auth.getName());
		
		Product product = productRepository.findById(productId).orElse(null);
		if(product != null && product.getUser().getId() == user.getId()) {
			productRepository.delete(product);
		}
		return "redirect:/seller/home";
	}
}
