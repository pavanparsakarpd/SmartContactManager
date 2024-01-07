package com.smart.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class MyOder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long myOrderId;
	
	private String orderId;
	
	private String amount;
	private String reciept;
	private String status;
	
	@ManyToOne
	private User user;
	
	private String paymentId;

	public Long getMyOrderId() {
		return myOrderId;
	}

	public void setMyOrderId(Long myOrderId) {
		this.myOrderId = myOrderId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getReciept() {
		return reciept;
	}

	public void setReciept(String reciept) {
		this.reciept = reciept;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	/**
	 * @param myOrderId
	 * @param orderId
	 * @param amount
	 * @param reciept
	 * @param status
	 * @param user
	 * @param paymentId
	 */
	public MyOder(Long myOrderId, String orderId, String amount, String reciept, String status, User user,
			String paymentId) {
		super();
		this.myOrderId = myOrderId;
		this.orderId = orderId;
		this.amount = amount;
		this.reciept = reciept;
		this.status = status;
		this.user = user;
		this.paymentId = paymentId;
	}

	/**
	 * 
	 */
	public MyOder() {
		
	}

	@Override
	public String toString() {
		return "MyOder [myOrderId=" + myOrderId + ", orderId=" + orderId + ", amount=" + amount + ", reciept=" + reciept
				+ ", status=" + status + ", user=" + user + ", paymentId=" + paymentId + "]";
	}
	
	

}
