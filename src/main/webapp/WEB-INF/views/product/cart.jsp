<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.util.ArrayList"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="/resources/css/bootstrap.min.css" />
<title>장바구니</title>
</head>
<body>
	<!-- include 액션 태그 -->
	<jsp:include page="menu.jsp" />
	
	<div class="jumbotron">
		<!-- container : 이 안에 내용있다 -->
		<div class="container">
			<h1 class="display-3">장바구니</h1>
		</div>
	</div>
	
	<!-- ------------장바구니 상세 내용 시작 ------------------- -->
	<div class="container">
		<div class="row">
			<table width="100%">
				<tr>
					<td align="left">
						<a href="deletecart" 
						class="btn btn-danger">삭제하기</a>
					</td>
					<c:if test="${myCart ne ''}">
					<td align="right">
						<a href="shippingInfo" 
						class="btn btn-success">주문하기</a>
					</td>
					</c:if>
				</tr>
			</table>
		</div>
		<!-- 장바구니 출력 시작 -->
		<!-- padding-top : 영역의 위쪽 여백 50px -->
		<div style="padding-top:50px;">
			<table class="table table-hover">
				<tr>
					<th>상품</th><th>가격</th><th>수량</th>
					<th>금액</th><th>비고</th>
				</tr>
				<c:if test="${myCart ne ''}">
				<c:forEach var="product" items="${myCart}">
				<tr>
					<td>${product.productId} - ${product.pname}</td>
					<td>${product.unitPrice}</td>
					<td>${product.quantity}</td>
					<td>${product.quantity*product.unitPrice}</td>
					<td>
						<a href="removecart?productId=${product.productId}"
						class="badge badge-danger">삭제</a>
					</td>
				</tr>
				</c:forEach>
				<tr>
					<th></th>
					<th></th>
					<th>총액</th>
					<th>${total}</th>
					<th></th>
				</tr>
				</c:if>
				<c:if test="${myCart eq ''}">
				<tr style="text-align:center;">
						<td colspan="5" style="text-align:center;">장바구니에 상품이 없습니다.</th>
				</tr>
				</c:if>

			</table>
			<a href="products" class="btn btn-secondary">&laquo;쇼핑 계속하기</a>
		</div>
		<!-- 장바구니 출력 끝 -->
	</div>
	<!-- ------------장바구니 상세 내용 끝 ------------------- -->
	
	<jsp:include page="footer.jsp" />
</body>
</html>





