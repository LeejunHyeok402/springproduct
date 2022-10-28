<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- <jsp:useBean id="productDAO" class="kr.or.ddit.dao.ProductRepository" --%>
<%-- scope="session" /> --%>
<!DOCTYPE html>
<html>
<head>
<!-- <link rel="stylesheet" -->
<!-- 	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"> -->
<link rel="stylesheet" href="/resources/css/bootstrap.min.css" />
<title>상품 상세 정보</title>
<script type="text/javascript">
	function addToCart(){
		if(confirm("상품을 장바구니에 추가하시겠습니까?")){
			//addCart.jsp?id=P1234
			document.addForm.submit();
		}else{
			document.addForm.reset();
		}
	}
</script>
</head>
<body>
	<!-- include 액션 태그 -->
	<jsp:include page="menu.jsp" />
	
	<!-- --------------------상품상세 시작-------------------- -->
	<div class="jumbotron">
		<!-- container : 이 안에 내용있다 -->
		<div class="container">
			<h1 class="display-3">상품 정보</h1>
		</div>
	</div>
	<!-- 내용 -->
	<div class="container">
		<!-- 1건의 상품. 1행 -->
		<div class="row">
			<!-- 이미지 div -->
			<div class="col-md-5">
				<img src="/resources/images/${data.filename}" 
				alt="${data.pname}" title="${data.pname}" 
				style="width:100%;"/>
			</div>		
			<!-- 6크기의 1열 -->
			<div class="col-md-6">
				<h3>${data.pname}</h3>
				<p>${data.description}</p>
				<p>
					<b>상품 코드 : </b>
					<span class="badge badge-danger">
					${data.productId}
					</span>
				</p>
				<p><b>제조사 : </b>${data.manufacturer}</p>
				<p><b>분류 : </b>${data.category}</p>
				<p><b>재고 수 : </b>${data.unitsInStock}</p>
				<h4>${data.unitPrice}</h4>
				<p>
				<form name="addForm" action="addcart" method="post">
				<input type="hidden" name="productId" value="${data.productId}">
					<a href="#" class="btn btn-info" onclick="addToCart()">상품 주문&raquo;</a>
					<a href="/cart" class="btn btn-warning">장바구니&raquo;</a>
					<a href="products" class="btn btn-secondary">상품 목록&raquo;</a>
					<a href="update?productId=${data.productId}" class="btn btn-secondary">상품 수정&raquo;</a>
				</form>
				<form action="/delete" method="post">
					<input type="hidden" name="productId" value="${data.productId}">
					<input type="submit" value="삭제&raquo;" class="btn btn-warning">
				</form>
				</p>
			</div>
		</div>
	</div>
	<!-- --------------------상품상세 끝-------------------- -->
	
	<jsp:include page="footer.jsp" />
</body>
</html>






