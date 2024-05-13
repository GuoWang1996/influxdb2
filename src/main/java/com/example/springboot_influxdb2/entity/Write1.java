package com.example.springboot_influxdb2.entity;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import java.time.Instant;

public class Write1 {
	
	private static char[] token = "T6pFII5KIC5RPWstXnn5Zc2HMehv-THY2FP8QSrE2A2tVMUPOUyuncBWMJpTOtwhyEdCHwi-2VzD14Nz_glTWw==".toCharArray();

	private static String org = "test";
	private static String bucket = "Bucket02";
	
	private static String url = "http://127.0.0.1:8086/";
	
	public static void main(String[] args) {
		System.out.println("test");
		//1.创建客户端
		InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url, token, org, bucket);
		
		//2.创建写入对象
		WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
		
		//3.1 行协议方式写入数据
		//writeApiBlocking.writeRecord(WritePrecision.MS, "cpu_load_short,adress=上海 value=32");
		
		//3.2 Point方式写入
		Point point = Point.measurement("cpu_load_short")
				.addTag("adress", "上海")
				.addField("value", 0.64)
				.time(Instant.now(), WritePrecision.MS);
		writeApiBlocking.writePoint(point);
		System.out.println(point.toLineProtocol());
		//4.关闭客户端
		influxDBClient.close();
	}

}
