<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="java.math.BigDecimal"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.net.URLDecoder"%>

<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="/resources/css/bootstrap.min.css" />
<title>주문 정보</title>
</head>
<body>
	<!-- include 액션 태그 -->
	<jsp:include page="menu.jsp" />
	
	<div class="jumbotron">
		<!-- container : 이 안에 내용있다 -->
		<div class="container">
			<h1 class="display-3">주문 정보</h1>
		</div>
	</div>
	
	<!-- --------------------주문 정보 시작-------------------- -->
	<div class="container col-8 alert alert-info">
		<div class="text-center">
			<h1>영수증</h1>
		</div>
		<!-- 고객 정보 시작 : cookie사용-->
		<div class="row justify-content-between">
			<strong>배송 주소</strong><br />
			성명 : ${list[0]}<br />
			우편번호 : ${list[3]}<br />
			주소 : ${list[4]}&nbsp;${cartVO.addressDetail}&nbsp;${list[2]}
		</div>
		<div class="col-4" align="right">
			<p>
		
			</p>
		</div>
		<!-- 고객 정보 끝 -->
		<!-- 상품 정보 시작 : session 사용 -->
		<div>
			<table class="table table-hover">
				<tr>
					<th class="text-center">상품명</th>
					<th class="text-center">#</th>
					<th class="text-center">가격</th>
					<th class="text-center">소계</th>
				</tr>
				<c:forEach var="product" items="${myCart}">
				<tr>
					<td class="text-center"><em>${product.pname}</em></td>
					<td class="text-center">${product.quantity}</td>
					<td class="text-center"><fmt:formatNumber value="${product.unitPrice}" pattern="#,###" />원</td>
					<td class="text-center"><fmt:formatNumber value="${product.quantity*product.unitPrice}" pattern="#,###" />원</td>
				</tr>
				</c:forEach>
				
				<tr>
					<td></td>
					<td></td>
					<td class="text-right"><strong>총액:</strong></td>
					<td class="text-center text-danger"><strong>
						<fmt:formatNumber value="${total}" pattern="#,###" />원
					</strong></td>
				</tr>
			</table>
			
			<form action="thankCustomer" method="post">
			<a href="shippingInfo?cartId=${cartId}"
			class="btn btn-secondary" role="button">이전</a>
				<input type="hidden" name="cartId" value="${cartId}">
				<input type="hidden" name="shippingDate" value="${list[1]}">
				<input type="submit" class="btn btn-success" value="주문완료">
			<a href="checkOutCancelled" class="btn btn-secondary"
			role="button">취소</a>
			</form>
		</div>
		<!-- 상품 정보 끝 -->
	</div>
	<!-- --------------------주문 정보 끝-------------------- -->
	
	<jsp:include page="footer.jsp" />
</body>
</html>











