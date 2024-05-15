package com.example.springboot_influxdb2.pojo;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "areaTemperature")
public class AreaTemperaturePOJO {
	
	@Column(tag = true)
	String adress;
	
	@Column
	Double value;
	
	@Column(timestamp = true)
	Instant time;
	
	public String getAdress() {
		return adress;
	}
	
	public void setAdress(String adress) {
		this.adress = adress;
	}
	
	public Double getValue() {
		return value;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	
	public Instant getTime() {
		return time;
	}
	
	public void setTime(Instant time) {
		this.time = time;
	}
}
