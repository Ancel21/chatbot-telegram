package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.City;
import fr.ensim.interop.introrest.model.Meteo;
import fr.ensim.interop.introrest.model.OpenWeather;
import org.primefaces.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
public class OpenWeatherRestController {
	
	private final static String API_KEY="2d651e3909ec4c16aa7cb661af0a0765";

	@GetMapping(value = "/meteo", params = {"lat", "long"})
	public ResponseEntity<OpenWeather> meteo(
			@RequestParam("lat") String lat,
			@RequestParam("long") String longitude) {

		RestTemplate restTemplate = new RestTemplate();
		OpenWeather openWeather = restTemplate.getForObject("http://api.openweathermap.org/data/2.5/weather?lat={lat}" 
																	+ "&lon={longitude}&appid=" + API_KEY, 
															OpenWeather.class, lat, longitude);
		
		return ResponseEntity.ok().body(openWeather);
	}

	@GetMapping("/meteo")
	public ResponseEntity<OpenWeather> meteoByVille(
			@RequestParam("ville") String nomVille) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<City[]> responseEntity = restTemplate.getForEntity("http://api.openweathermap.org/geo/1.0/direct?q={ville}&limit=3"
																				  + "&appid=" + API_KEY,
																		  City[].class, nomVille);
		City[] cities = responseEntity.getBody();
		City city = cities[0];

		OpenWeather openWeather = restTemplate.getForObject("http://api.openweathermap.org/data/2.5/weather?lat={lat}"
																	+ "&lon={longitude}&appid=" + API_KEY,
															OpenWeather.class, city.getLat(), city.getLon());

		return ResponseEntity.ok().body(openWeather);
	}

	@GetMapping("/v2/meteo")
	public ResponseEntity<Meteo> meteoByVilleV2(
			@RequestParam("ville") String nomVille) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<City[]> responseEntity = restTemplate.getForEntity("http://api.openweathermap.org/geo/1.0/direct?q={ville}&limit=3"
																				  + "&appid=" + API_KEY,
																		  City[].class, nomVille);
		City[] cities = responseEntity.getBody();
		City city = cities[0];

		OpenWeather openWeather = restTemplate.getForObject("http://api.openweathermap.org/data/2.5/weather?lat={lat}"
																	+ "&lon={longitude}&units=metric&lang=fr&appid=" + API_KEY,
															OpenWeather.class, city.getLat(), city.getLon());
		
		Meteo meteo = new Meteo();
		meteo.setMeteo(openWeather.getWeather().get(0).getMain());
		meteo.setDetails(openWeather.getWeather().get(0).getDescription());
		meteo.setTemperature(openWeather.getMain().getTemp());


		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject messageJsonObject = new JSONObject();
		messageJsonObject.put("chat_id", "1850817748");
		messageJsonObject.put("text", meteo.getTemperature());
		HttpEntity<String> request = new HttpEntity<String>(messageJsonObject.toString(), headers);

		try {
			String messageResultAsJsonStr = restTemplate.postForObject("https://api.telegram.org/bot6189285093:AAF1wyfYFaare1o28Npv2qgfnqYlMMKcOcM/sendMessage", request, String.class);
		} catch (RestClientException e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok().body(meteo);
	}

	@GetMapping("/position")
	public ResponseEntity<City> getCoord(
			@RequestParam("ville") String nomVille) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<City[]> responseEntity = restTemplate.getForEntity("http://api.openweathermap.org/geo/1.0/direct?q={ville}&limit=3"
																	+ "&appid=" + API_KEY,
															City[].class, nomVille);
		City[] cities = responseEntity.getBody();
		City city = cities[0];

		return ResponseEntity.ok().body(city);
	}

}
