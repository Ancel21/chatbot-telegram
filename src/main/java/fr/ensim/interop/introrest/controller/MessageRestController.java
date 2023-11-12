package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.telegram.ApiResponseTelegram;
import fr.ensim.interop.introrest.model.telegram.ApiResponseUpdateTelegram;
import fr.ensim.interop.introrest.model.telegram.Message;
import fr.ensim.interop.introrest.model.telegram.Joke;
import fr.ensim.interop.introrest.model.telegram.Update;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
public class MessageRestController {

	@Autowired
	JokeRestController jrc;

	@Autowired
	OpenWeatherRestController wrc;
	
	@Value("${telegram.api.url}")
	private String telegramApiUrl;
	@Value("${telegram.api.url.send}")
	private String telegramApiUrlSend;
	private static int offset=0;

	@GetMapping("getTelegram")
	@Scheduled(fixedRate=1000)
	public ApiResponseUpdateTelegram getTelegram(){
		RestTemplate restTemplate = new RestTemplate();

		ApiResponseUpdateTelegram response = restTemplate.getForObject("https://api.telegram.org/bot6189285093:AAF1wyfYFaare1o28Npv2qgfnqYlMMKcOcM/getUpdates?offset="+offset, ApiResponseUpdateTelegram.class);
		if(response.getResult().size()==0)
		{
			System.out.println("Pas de nouveaux messages");
			return null;

		}

		offset = response.getResult().get(response.getResult().size()-1).getUpdateId()+1;

		System.out.println("OFFSET : "+offset);

		//response = restTemplate.getForObject("https://api.telegram.org/bot6189285093:AAF1wyfYFaare1o28Npv2qgfnqYlMMKcOcM/getUpdates?offset="+offset, ApiResponseUpdateTelegram.class);

		for(Update update:response.getResult()){
			String text = update.getMessage().getText();

			if(text.toUpperCase().startsWith("METEO"))
			{
				String ville = text.substring(5, text.length());
				wrc.meteoByVilleV2(ville);
			}
			else if(text.toUpperCase().startsWith("JOKE")){
				jrc.sendJoke();
			}
			else if(text.toUpperCase().startsWith("HELP"))
			{
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				JSONObject messageJsonObject = new JSONObject();
				messageJsonObject.put("chat_id", "1850817748");
				messageJsonObject.put("text", "Commandes : \n JOKE : Demander une blague \n METEO [VILLE] : Meteo d'une ville");

				HttpEntity<String> request = new HttpEntity<String>(messageJsonObject.toString(), headers);

				try {
					String messageResultAsJsonStr = restTemplate.postForObject("https://api.telegram.org/bot6189285093:AAF1wyfYFaare1o28Npv2qgfnqYlMMKcOcM/sendMessage", request, String.class);
				} catch (RestClientException e) {
					e.printStackTrace();
				}
			}/*
			else
			{
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				JSONObject messageJsonObject = new JSONObject();
				messageJsonObject.put("chat_id", "1850817748");
				messageJsonObject.put("text", "Désolé, je ne comprends pas votre commande :'(\nEssayez la commande help ! ");

				HttpEntity<String> request = new HttpEntity<String>(messageJsonObject.toString(), headers);

				try {
					String messageResultAsJsonStr = restTemplate.postForObject("https://api.telegram.org/bot6189285093:AAF1wyfYFaare1o28Npv2qgfnqYlMMKcOcM/sendMessage", request, String.class);
				} catch (RestClientException e) {
					e.printStackTrace();
				}
			}*/
		}
		return response;
	}

	@GetMapping("/getApiUrl")
	public String getApiUrl()
	{
		return telegramApiUrl;
	}


}
