package com.example.springboot_influxdb2;

import com.example.springboot_influxdb2.pojo.AreaTemperaturePOJO;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

@SpringBootTest
class SpringbootInfluxdb2ApplicationTests {
	

	@Value("${spring.influx.token:''}")
	private char[] token;
	
	@Value("${spring.influx.url:''}")
	private String url;
	
	@Value("${spring.influx.org:''}")
	private String org;
	
	@Value("${spring.influx.bucket:''}")
	private String bucket;
	private InfluxDBClient influxDBClient;
	private WriteApiBlocking writeApiBlocking;
	@BeforeEach
	void setUp() {
		// 创建客户端
		influxDBClient = InfluxDBClientFactory.create(url, token, org, bucket);
		// 创建写入对象
		writeApiBlocking = influxDBClient.getWriteApiBlocking();
	}
	@AfterEach
	void tearDown() {
		// 关闭客户端
		influxDBClient.close();
	}
	@Test
	void contextLoads() {
	}
	//行协议方式写入
	@Test
	void addByRecord(){
		//3.1 行协议方式写入数据
		/**
		 * @Param [WritePrecision,String] 1.时间精度 2.写入数据 格式:测量单位,测量值,标签,time（可忽略,默认当前系统日期）
		 **/
		writeApiBlocking.writeRecord(WritePrecision.MS, "areaTemperature,adress=上海 value=24 1715270399000");
		writeApiBlocking.writeRecord(WritePrecision.MS, "areaTemperature,adress=上海 value=29 1715356799000");
		writeApiBlocking.writeRecord(WritePrecision.MS, "areaTemperature,adress=上海 value=27 1715443199000");
		writeApiBlocking.writeRecord(WritePrecision.MS, "areaTemperature,adress=上海 value=17 1715529599000");
		writeApiBlocking.writeRecord(WritePrecision.MS, "areaTemperature,adress=上海 value=42 1715579999000");
		//4.关闭客户端
		influxDBClient.close();
		System.out.println("写入成功");
	}
	
	
	//Point方式写入
	@Test
	void addByPoint(){
		//3.1 行协议方式写入数据
		//writeApiBlocking.writeRecord(WritePrecision.MS, "cpu_load_short,adress=上海 value=32");
		
		//3.2 Point方式写入
		Point point = Point.measurement("cpu_load_short")
				.addTag("adress", "上海")
				.addField("value", 42.64)
				.time(Instant.now(), WritePrecision.MS);
		writeApiBlocking.writePoint(point);
		System.out.println(point.toLineProtocol());
		//4.关闭客户端
		influxDBClient.close();
		System.out.println("写入成功");
		
	}
	
	
	//POJO方式写入
	@Test
	void addByPOJO(){
		AreaTemperaturePOJO temperature = new AreaTemperaturePOJO();
		temperature.setAdress("南京");
		temperature.setValue(32.64);
		//设置时间为1715270399000
		temperature.setTime(Instant.ofEpochMilli(1715270399000L));
		writeApiBlocking.writeMeasurement( WritePrecision.NS, temperature);
	}
	
	//flux读
	@Test
	void read(){
		//重新创建连接,这里是测量单位级别，无需桶名。
		influxDBClient = InfluxDBClientFactory.create(url, token, org);
		QueryApi queryApi = influxDBClient.getQueryApi();
		
		//查询桶为Bucket02的areaTemperature下地区为上海,过去7天的数据
		String flux = "from(bucket: \"Bucket02\")\n" +
				"  |> range(start: -7d)\n" +
				"  |> filter(fn: (r) => r[\"_measurement\"] == \"areaTemperature\")\n" +
				"  |> filter(fn: (r) => r[\"_field\"] == \"value\")\n" +
				"  |> filter(fn: (r) => r[\"adress\"] == \"南京\" or r[\"adress\"] == \"上海\")\n" +
				"  |> aggregateWindow(every: 10s, fn: mean, createEmpty: false)\n" +
				"  |> yield(name: \"mean\")";
		String queryRaw = queryApi.queryRaw(flux);
		System.out.println(queryRaw);
	}
}
