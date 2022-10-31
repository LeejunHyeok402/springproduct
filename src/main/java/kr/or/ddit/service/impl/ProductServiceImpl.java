package kr.or.ddit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import kr.or.ddit.dao.ProductDao;
import kr.or.ddit.service.ProductService;
import kr.or.ddit.util.FileUploadUtil;
import kr.or.ddit.vo.AttachVO;
import kr.or.ddit.vo.CartVO;
import kr.or.ddit.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService{
	//의존성 주입(DI)
	//제어의 역전(IoC)
	@Autowired
	ProductDao productDao;
	
	//PRODUCT 테이블에 insert
	@Override
	public int insertProduct(ProductVO productVO) {	
		//product 테이블에 insert
		int result = this.productDao.insertProduct(productVO);
		//attach 테이블에 다중 insert
		if(result > 0 ) {
			List<AttachVO> list = FileUploadUtil.fileUploadAction(productVO.getProductImage(), productVO.getProductId());
			this.productDao.insertAttach(list);
		}
		return result;
	}
	
	
	//상품 목록
	@Override
	public List<ProductVO> list(String keyword){
		return this.productDao.list(keyword);
	}
	
	@Override
	public ProductVO selectDetail(ProductVO productVO) {
		return this.productDao.selectDetail(productVO);
	}
	
	@Override
	public int update(ProductVO productVO) {
		return this.productDao.update(productVO);
	}
	
	@Override
	public int delete(String productId) {
		return this.productDao.delete(productId);
	}
	//cart 및 cart_det 테이블에 insert
	@Override
	public int thankCustomer(CartVO cartVO) {
		//1.cart 테이블에 insert
		int cartInCnt = this.productDao.insertCart(cartVO);
		log.info("cartInCnt: "+cartInCnt);
		//2. cart_det 테이블에 insert
		
		return 0;
	}
	
	@Override
	public String getProductId() {
		return this.productDao.getProductId();
	}
}


