package kr.or.ddit.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import kr.or.ddit.service.ProductService;
import kr.or.ddit.util.FileUploadUtil;
import kr.or.ddit.vo.AttachVO;
import kr.or.ddit.vo.CartDetVO;
import kr.or.ddit.vo.CartVO;
import kr.or.ddit.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.proxy.annotation.GetCreator;

@Slf4j
@Controller
public class ProductController {
	
	@Autowired
	ProductService productService;
	
	@RequestMapping(value = "/products", method = RequestMethod.GET)
	public ModelAndView products(ModelAndView mav,@RequestParam(value="keyword",required=false) String keyword) {
		log.info(keyword);
		//Model
		List<ProductVO> data = this.productService.list(keyword);
		mav.addObject("data", data);
		//View
		mav.setViewName("product/products");
		
		return mav;
		
	}
	
	//URI : /addProduct
	//파라미터 : none
	@RequestMapping(value = "/addProduct", method = RequestMethod.GET)
	public ModelAndView addProduct() {
		
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("product/addProduct");
		//forwarding
		return mav;
		
	}
	
	@ResponseBody
	@PostMapping("/getProductId")
	public Map<String,String> getProductId(){
		Map<String,String> map = new HashMap<String, String>();
		
		String productId = this.productService.getProductId();
		
		map.put("ProductId",productId);
		
		return map;
	}
	
	@RequestMapping(value = "/addProduct", method = RequestMethod.POST)
	public ModelAndView addProductPost(ModelAndView mav, @ModelAttribute ProductVO productVO) {
		log.info("ProductVO : " + productVO.toString());
		log.info("여기는 옴");
		//PRODUCT 테이블에 insert
		//result > 0 => insert 성공, result == 0 => 실패
		
		
		
		int result = this.productService.insertProduct(productVO);
		log.info("result : " + result);
		
		if(result > 0) {//입력 성공
			mav.setViewName("redirect:/product?productId=" + productVO.getProductId());
		}else {//입력 실패
			mav.setViewName("redirect:/addproduct");
		}
		
		return mav;
	}
	
	@RequestMapping(value = "/product", method = RequestMethod.GET)
	public ModelAndView selectDetail(ModelAndView mav, @ModelAttribute ProductVO productVO) {
		log.info(productVO.toString());
		ProductVO vo = this.productService.selectDetail(productVO);
		
		mav.addObject("data", vo);
		mav.setViewName("product/product");
		return mav;
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public ModelAndView update(ModelAndView mav, @ModelAttribute ProductVO productVO) {
		ProductVO vo = this.productService.selectDetail(productVO);
		
		mav.addObject("data",vo);
		mav.setViewName("product/update");
		return mav;
	}
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ModelAndView detailUpdate(ModelAndView mav, @ModelAttribute ProductVO productVO) {
		int result = this.productService.update(productVO);
		
		if(result >0 ) {
			mav.setViewName("redirect:/product?productId="+productVO.getProductId());
		}else {
			mav.setViewName("redirect:/update?productId="+productVO.getProductId());
		}
		
		return mav;
	}
	
	//파라미터 받는 방법 => VO는  @ModelAttribute	/String/int는 @RequestParam
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ModelAndView delete(ModelAndView mav,@RequestParam String productId) {
		int result= this.productService.delete(productId);
		
		if(result > 0) {
			mav.setViewName("redirect:/products");
		}else {
			mav.setViewName("redirect:/product?productId="+productId);
		}
		return mav;
	}
	
	//spring에서 요청파라미터를 매개변수로 받을 수 있다
	@RequestMapping(value = "/addcart", method = RequestMethod.POST)
	public ModelAndView addCart(ModelAndView mav,ProductVO productVO,HttpSession session,String productId) {
		
		log.info("productId: "+productVO.getProductId());
		if(productVO.getProductId() == null || productVO.getProductId().trim().equals("")){
			mav.setViewName("redirect:/products");
		}
		
		ProductVO product = this.productService.selectDetail(productVO);
		
		if(product == null) {
			mav.setViewName("redirect:/exceptionNoProductId");
		}
		
		ArrayList<ProductVO> list = (ArrayList<ProductVO>)session.getAttribute("cartlist");
		
		//장바구니에 list가 없다면 
		if(list == null){
			list = new ArrayList<ProductVO>();
			session.setAttribute("cartlist", list);
		}
		
		
		int cnt = 0;

		for(int i=0;i<list.size();i++){

			if(list.get(i).getProductId().equals(productVO.getProductId() )){
				cnt++;
				list.get(i).setQuantity(list.get(i).getQuantity()+1);
			}
		}
		
		//장바구니에 해당 상품이 없다면
		if(cnt == 0){
			product.setQuantity(1);
			//최종목표 : 장바구니(list)에 상품을 추가
			list.add(product);
		}
		
		session.setAttribute("cartList", list);
		
		log.info("vo ]"+list.toString());
		mav.setViewName("redirect:/product?productId="+productVO.getProductId());
		return mav;
	}
	
	@RequestMapping(value = "/cart", method = RequestMethod.GET)
	public ModelAndView cart(ModelAndView mav,HttpSession session) {
		ArrayList<ProductVO> list = (ArrayList<ProductVO>)session.getAttribute("cartlist");
		if(list != null) {
			int total = 0;
			for (ProductVO productVO : list) {
				total +=productVO.getUnitPrice() * productVO.getQuantity();
			}
			mav.setViewName("product/cart");
			mav.addObject("myCart",list);
			mav.addObject("total", total);
			
		}else {
			mav.setViewName("product/cart");
			mav.addObject("myCart","");
		}
		
	
		return mav;
	}
	
	
	
	
	@RequestMapping(value = "/removecart", method = RequestMethod.GET)
	public ModelAndView removeCart(ModelAndView mav,ProductVO productVO,HttpSession session) {
		log.info("vo: "+productVO.getProductId());
		
		ArrayList<ProductVO> cartlist =
				(ArrayList<ProductVO>)session.getAttribute("cartlist");
		
		if(productVO.getProductId()==null || productVO.getProductId().trim().equals("")){
			mav.setViewName("redirect:/products");
		}


		if(cartlist==null){	//해당 상품이 없음
			mav.setViewName("redirect:/products");		
		}
			
		
			for(int i=0;i<cartlist.size();i++){

				if(cartlist.get(i).getProductId().equals(productVO.getProductId())){
					cartlist.remove(cartlist.get(i));
					mav.setViewName("redirect:/cart");
				}
			}
			
			if(cartlist.size() == 0) {
				session.invalidate();
			}
			
		return mav;
	}
	
	@RequestMapping(value="/deletecart", method = RequestMethod.GET )
	public ModelAndView deletecart(ModelAndView mav,HttpSession session) {
		session.invalidate();
		mav.setViewName("redirect:/cart");
		return mav;
	}
	
	@RequestMapping(value="/shippingInfo",method = RequestMethod.GET)
	public String shippingInfo(Model model,HttpSession session) {
		//jsp uri shippingInfo?cartId=session.getId()
		//를 통해 파라미터로 jsession아이디를 받아서 처리 할 수도 있음
		
		
		String id = session.getId();
		
		model.addAttribute("cartId",id);
		
		return "product/shippingInfo";
	}
	
	@RequestMapping(value="/processShippingInfo", method = RequestMethod.POST )
	public String processShippingInfo(Model model,HttpServletResponse response,CartVO cartVO,HttpSession session) throws IOException {
			Cookie cartId = 
				new Cookie("Shipping_cartId",
				URLEncoder.encode(cartVO.getCartId(),"UTF-8"));
			Cookie name = 
					new Cookie("Shipping_name",
					URLEncoder.encode(cartVO.getName(),"UTF-8"));
			Cookie shippingDate = 
					new Cookie("Shipping_shippingDate",
					URLEncoder.encode(cartVO.getShippingDate(),"UTF-8"));
			Cookie country = 
					new Cookie("Shipping_country",
					URLEncoder.encode(cartVO.getCountry(),"UTF-8"));
			Cookie zipCode = 
					new Cookie("Shipping_zipCode",
					URLEncoder.encode(cartVO.getZipCode(),"UTF-8"));
			Cookie addressName = 
					new Cookie("Shipping_addressName",
					URLEncoder.encode(cartVO.getAddressName(),"UTF-8"));
			Cookie addressDetail = 
					new Cookie("Shipping_addressDetail",
							URLEncoder.encode(cartVO.getAddressDetail(),"UTF-8"));
			
			//유효 기간 1일로 설정(초단위)
			cartId.setMaxAge(24 * 60 * 60);
			name.setMaxAge(24 * 60 * 60);
			shippingDate.setMaxAge(24 * 60 * 60);
			zipCode.setMaxAge(24 * 60 * 60);
			country.setMaxAge(24 * 60 * 60);
			addressName.setMaxAge(24 * 60 * 60);
			addressDetail.setMaxAge(24 * 60 * 60);
			
			//생성된 쿠키를 등록 
			response.addCookie(cartId);
			response.addCookie(name);
			response.addCookie(shippingDate);
			response.addCookie(zipCode);
			response.addCookie(country);
			response.addCookie(addressName);
			response.addCookie(addressDetail);
			
		
		
		
		List<String> list = new ArrayList<String>();
		list.add(cartVO.getCartId());
		list.add(cartVO.getShippingDate());
		list.add(cartVO.getCountry());
		list.add(cartVO.getZipCode());
		list.add(cartVO.getAddressName());
		
		log.info("cartVO: "+cartVO.toString());
		ArrayList<ProductVO> vo = (ArrayList<ProductVO>)session.getAttribute("cartlist");
		int total = 0;
		if(list != null) {
			for (ProductVO productVO : vo) {
				total +=productVO.getUnitPrice() * productVO.getQuantity();
			}
		}
		String cartId1 =session.getId();
		
		
		model.addAttribute("cartId", cartId1);
		model.addAttribute("total", total);
		model.addAttribute("myCart", vo);
		model.addAttribute("list",list);
		model.addAttribute("CartVO",cartVO);
		return "product/orderConfirmation";
	}
	
	
	
	
	
	@RequestMapping(value="/thankCustomer", method = RequestMethod.POST)
	public String thankCustomer(Model model,String cartId,String shippingDate,HttpSession session,HttpServletResponse response,HttpServletRequest request) throws IOException {
			String Shipping_name = "";
	      String Shipping_zipCode = "";
	      String Shipping_country = "";
	      String Shipping_addressName = "";
	      String Shipping_shippingDate = "";
	      String Shipping_cartId = "";
	      String addressDetail = "";
	      
	      Cookie[] cookies = request.getCookies();
	      CartVO cartVO = new CartVO();
	      //쿠키의 개수만큼 반복
	      for(int i=0;i<cookies.length;i++){
	         Cookie thisCookie = cookies[i];
	         //쿠키 이름 가져옴
//	          out.print(thisCookie.getName() + "<br />");
	         //쿠키 값 가져옴
//	          out.print(URLDecoder.decode(thisCookie.getValue(),"UTF-8")+"<br />");
	         if(thisCookie.getName().equals("Shipping_name")){
	            Shipping_name = URLDecoder.decode(thisCookie.getValue(),"UTF-8");
	            cartVO.setName(Shipping_name);
	         }
	         if(thisCookie.getName().equals("Shipping_zipCode")){
	            Shipping_zipCode = URLDecoder.decode(thisCookie.getValue(),"UTF-8");
	            cartVO.setZipCode(Shipping_zipCode);
	         }
	         if(thisCookie.getName().equals("Shipping_country")){
	            Shipping_country = URLDecoder.decode(thisCookie.getValue(),"UTF-8");
	            cartVO.setCountry(Shipping_country);
	         }
	         if(thisCookie.getName().equals("Shipping_addressName")){
	            Shipping_addressName = URLDecoder.decode(thisCookie.getValue(),"UTF-8");
	            cartVO.setAddressName(Shipping_addressName);
	         }
	         if(thisCookie.getName().equals("Shipping_shippingDate")){
	            Shipping_shippingDate = URLDecoder.decode(thisCookie.getValue(),"UTF-8");
	            cartVO.setShippingDate(Shipping_shippingDate);
	         }
	         if(thisCookie.getName().equals("Shipping_cartId")){
	            Shipping_cartId = URLDecoder.decode(thisCookie.getValue(),"UTF-8");
	            cartVO.setCartId(Shipping_cartId);
	         }
	         if(thisCookie.getName().equals("Shipping_cartId")){
	        	 Shipping_cartId = URLDecoder.decode(thisCookie.getValue(),"UTF-8");
	        	 cartVO.setCartId(Shipping_cartId);
	         }
	         if(thisCookie.getName().equals("Shipping_addressDetail")){
	        	 addressDetail = URLDecoder.decode(thisCookie.getValue(),"UTF-8");
	        	 cartVO.setAddressDetail(addressDetail);
	         }
	      }
	      log.info(cartVO.toString());
		
	      ArrayList<ProductVO> list = (ArrayList<ProductVO>)session.getAttribute("cartlist");

	      //3. CartVO : CartDetVO = 1: N
	      List<CartDetVO> cartDetVOList = new ArrayList<CartDetVO>();
	      for (ProductVO vo : list) {
			CartDetVO cartDetVO = new CartDetVO();
			cartDetVO.setCartId(cartVO.getCartId());
			cartDetVO.setProductId(vo.getProductId());
			cartDetVO.setUnitPrice(vo.getUnitPrice());
			cartDetVO.setQuantity(vo.getQuantity());
			cartDetVO.setAmount(vo.getUnitPrice() * vo.getQuantity());
			cartDetVOList.add(cartDetVO);
		}
	      cartVO.setCartDetVOList(cartDetVOList);
	      log.info(cartVO.getCartDetVOList().toString());
	      this.productService.thankCustomer(cartVO);
		model.addAttribute("cartId",cartId);
		model.addAttribute("shippingDate",shippingDate);
		session.invalidate();
		return "product/thankCustomer";
	}
	
	@RequestMapping(value="/checkOutCancelled", method = RequestMethod.GET)
	public String checkOutCancelled() {
		return "product/checkOutCancelled";
	}
	
	//쌤이 하신거
	@RequestMapping(value="/addcart2" ,method=RequestMethod.POST)
	public String addCart2(@RequestParam String productId,Model model,ProductVO productVO,HttpSession session) {
		
		if(productId == null) {
			return "redirect:/products";
		}
		
		ProductVO vo =	this.productService.selectDetail(productVO);
		if(vo == null) {
			return "redirect:/products";
		}
		
		ArrayList<ProductVO> list = (ArrayList<ProductVO>)session.getAttribute("cartlist");
		
		if(list == null){
			list = new ArrayList<ProductVO>();
			session.setAttribute("cartlist", list);
		}
		
		
		int cnt = 0;

		for(int i=0;i<list.size();i++){

			if(list.get(i).getProductId().equals(productId)){
				cnt++;
				list.get(i).setQuantity(list.get(i).getQuantity()+1);
			}
		}
		
		//장바구니에 해당 상품이 없다면
		if(cnt == 0){
			vo.setQuantity(1);
			//최종목표 : 장바구니(list)에 상품을 추가
			list.add(vo);
		}
		
		model.addAttribute("cart",list);
		return "redirect:/product?productId="+productId;
	}
	
	@RequestMapping(value="/cart2",method=RequestMethod.GET)
	public String cart2() {
		return "product/cart";
		//메서드가 void일때는 콘트롤러 내 다른 메서드에서 this.메서드명으로 실행 시켜도 된다
	}
@RequestMapping(value="/processShippingInfo2", method = RequestMethod.POST)	
public String processShippingInfo2(@ModelAttribute CartVO cartVO,HttpServletResponse response) throws IOException {
		
		//쿠키 쿠키 뉴~ 큐키 네임 밸류
		//요청 파라미터 정보를 쿠키에 넣음 
		Cookie cartId = 
			new Cookie("Shipping_cartId",
			URLEncoder.encode(cartVO.getCartId(),"UTF-8"));
		Cookie name = 
				new Cookie("Shipping_name",
				URLEncoder.encode(cartVO.getName(),"UTF-8"));
		Cookie shippingDate = 
				new Cookie("Shipping_shippingDate",
				URLEncoder.encode(cartVO.getShippingDate(),"UTF-8"));
		Cookie country = 
				new Cookie("Shipping_country",
				URLEncoder.encode(cartVO.getCountry(),"UTF-8"));
		Cookie zipCode = 
				new Cookie("Shipping_zipCode",
				URLEncoder.encode(cartVO.getZipCode(),"UTF-8"));
		Cookie addressName = 
				new Cookie("Shipping_addressName",
				URLEncoder.encode(cartVO.getAddressName(),"UTF-8"));
		Cookie addressDetail = 
				new Cookie("Shipping_addressDetail",
						URLEncoder.encode(cartVO.getAddressDetail(),"UTF-8"));
		
		//유효 기간 1일로 설정(초단위)
		cartId.setMaxAge(24 * 60 * 60);
		name.setMaxAge(24 * 60 * 60);
		shippingDate.setMaxAge(24 * 60 * 60);
		zipCode.setMaxAge(24 * 60 * 60);
		country.setMaxAge(24 * 60 * 60);
		addressName.setMaxAge(24 * 60 * 60);
		addressDetail.setMaxAge(24 * 60 * 60);
		
		//생성된 쿠키를 등록 
		response.addCookie(cartId);
		response.addCookie(name);
		response.addCookie(shippingDate);
		response.addCookie(zipCode);
		response.addCookie(country);
		response.addCookie(addressName);
		response.addCookie(addressDetail);
		
		//forwarding
		return "product/orderConfirmation";
	}
}
