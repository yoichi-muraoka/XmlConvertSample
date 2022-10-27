package com.example.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.example.demo.domain.City;
import com.example.demo.domain.Pref;

@RestController
public class AreaController {

	// 参考: https://www.bold.ne.jp/engineer-club/java-xml-read
	@GetMapping("/areas")
	public List<Pref> getPrefectures() throws Exception {
		// XMLの取得
		String uri = "https://weather.tsukumijima.net/primary_area.xml";
		HttpClient client = HttpClient.newBuilder().build();
		HttpRequest request = HttpRequest.newBuilder(URI.create(uri)).build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		
		// Documentの生成
		InputStream inputStream = new ByteArrayInputStream(response.body().getBytes(StandardCharsets.UTF_8));
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inputStream);
		
		// pref要素の取得
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("//pref");
		NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		
		// pref要素をPrefオブジェクトのリストに変換
		List<Pref> prefList = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element elm = (Element) nodeList.item(i);
			
			// pref要素のtitle属性の値を取得
			String title = elm.getAttribute("title");
			
			// pref要素の子要素のうち、city要素を取得
			NodeList children = elm.getElementsByTagName("city");
			List<City> cityList = new ArrayList<>();
			for(int j = 0; j < children.getLength(); j++) {
				Element cityElm = (Element) children.item(j);
				cityList.add(new City(cityElm.getAttribute("id"), cityElm.getAttribute("title")));
			}
			
			prefList.add(new Pref(title, cityList));
		}
		return prefList;
	}

}





