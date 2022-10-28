package kr.or.ddit.dao;

import java.util.List;

import org.apache.ibatis.annotations.ConstructorArgs;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.or.ddit.vo.AttachVO;
import kr.or.ddit.vo.CartVO;
import kr.or.ddit.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ProductDao {
	//의존성 주입(Dependency Injection-DI)
	//제어의 역전(Inversion of Control - IoC)
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	//PRODUCT 테이블에 insert
	public int insertProduct(ProductVO productVO) {
		//.insert("namespace.id",파라미터)
		log.info("dao sqlsession: "+sqlSessionTemplate);
		return sqlSessionTemplate.insert("product.insert", productVO);
	}
	
	//상품 목록
	public List<ProductVO> list(String keyword){
		return this.sqlSessionTemplate.selectList("product.list",keyword);
	}
	
	public ProductVO selectDetail(ProductVO productVO) {
		return this.sqlSessionTemplate.selectOne("product.select_detail", productVO);
	}
	
	public int update(ProductVO productVO) {
		return this.sqlSessionTemplate.update("product.update", productVO);
	}
	
	public int delete(String productId) {
		return this.sqlSessionTemplate.delete("product.delete",productId);
	}
	
	public int insertCart(CartVO cartVO) {
		return this.sqlSessionTemplate.insert("product.insertCart", cartVO);
	}
	
	
	public int insertAttach(List<AttachVO> attachVOList) {
		log.info("dao: "+this.sqlSessionTemplate);
		return this.sqlSessionTemplate.insert("product.insertAttach", attachVOList);
	}
}
