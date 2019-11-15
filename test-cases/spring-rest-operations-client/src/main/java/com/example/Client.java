package com.example;

import com.fasterxml.jackson.core.JsonFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

//@SpringBootApplication
public class Client {
    public static void main(String[] args) {
        List<HttpMessageConverter<?>> messageConverters = Collections.singletonList(new SimpleMessageConverter(new JsonFactory()));
        RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(messageConverters).build();
        SimpleClient simpleClient = new SimpleClient(restTemplate, "http://localhost:8080");
        ResponseEntity<Item> itemResponseEntity = simpleClient.get("1");
        Item item = itemResponseEntity.getBody();
        ResponseEntity<String> stringResponseEntity = simpleClient.create(item);
        System.out.println(stringResponseEntity.getBody());
    }
}
